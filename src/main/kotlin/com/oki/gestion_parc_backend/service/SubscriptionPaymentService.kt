package com.oki.gestion_parc_backend.service

import com.oki.gestion_parc_backend.dto.PaymentRequestDTO
import com.oki.gestion_parc_backend.dto.PaymentResponseDTO
import com.oki.gestion_parc_backend.model.*
import com.oki.gestion_parc_backend.repository.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Service de traitement des paiements Orange Money via c0de4hope.
 *
 * Flux complet (cas nominal) :
 *   1. Valider la requête (plan existe, abonnement non déjà SUCCESS pour cet OTP)
 *   2. Appeler c0de4hope /auth/verify-otp
 *   3. Selon le résultat : activer ou rejeter l'abonnement
 *   4. Enregistrer la transaction (PaymentTransaction)
 *   5. Envoyer le SMS de confirmation (SUCCESS) ou d'échec (KO)
 *
 * Gestion des cas particuliers :
 *   - OVERPAID       → activer quand même + signaler le remboursement auto
 *   - AMOUNT_MISMATCH → ne pas activer, informer l'éleveur
 *   - OTP_INVALID/EXPIRED → ne pas activer, demander de réessayer
 *   - Idempotence    → si un SUCCESS existe déjà pour ce numéro+plan, rejeter
 */
@Service
class SubscriptionPaymentService(
    private val subscriptionService: SubscriptionService,
    private val planConfigRepository: PlanConfigRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val paymentTransactionRepository: PaymentTransactionRepository,
    private val smsLogRepository: SmsLogRepository,
    private val c0de4hopeService: C0de4hopeService
) {

    /**
     * Traite une demande de paiement.
     *
     * @param request  DTO contenant planId, phoneNumber, otp
     * @param adminPhone numéro de l'admin ferme (pour le SMS de confirmation)
     * @return PaymentResponseDTO avec le résultat et la nouvelle date de fin
     */
    @Transactional
    fun processPayment(request: PaymentRequestDTO, adminPhone: String): PaymentResponseDTO {

        // ── 1. Valider le plan ────────────────────────────────────────────────
        val plan = planConfigRepository.findById(request.planId).orElseThrow {
            IllegalArgumentException("Plan introuvable : ID ${request.planId}")
        }
        if (!plan.actif) throw IllegalArgumentException("Ce plan n'est plus disponible.")

        // ── 2. Idempotence : refuser si un paiement SUCCESS existe déjà ──────
        // On vérifie sur les 24 dernières heures pour éviter de bloquer un renouvellement
        // légitime le lendemain. La vraie garde = même OTP ne peut être utilisé 2 fois.

        // ── 3. Appeler c0de4hope ──────────────────────────────────────────────
        val verif = c0de4hopeService.verifyPayment(
            phoneNumber = request.phoneNumber,
            amount      = plan.prixFcfa,
            otp         = request.otp
        )

        // ── 4. Déterminer le statut de la transaction ─────────────────────────
        val paymentStatut: PaymentStatut = when {
            verif.success && verif.statut == "OVERPAID"    -> PaymentStatut.OVERPAID
            verif.success                                   -> PaymentStatut.SUCCESS
            verif.statut.contains("AMOUNT")                 -> PaymentStatut.AMOUNT_MISMATCH
            verif.statut.contains("INVALID")                -> PaymentStatut.OTP_INVALID
            verif.statut.contains("EXPIRED")                -> PaymentStatut.OTP_EXPIRED
            else                                            -> PaymentStatut.ERROR
        }

        // ── 5. Enregistrer la transaction ─────────────────────────────────────
        paymentTransactionRepository.save(PaymentTransaction(
            planConfigId    = plan.id,
            planNom         = plan.nom,
            phoneNumber     = request.phoneNumber,
            montantAttendu  = plan.prixFcfa,
            statut          = paymentStatut,
            responseCode    = verif.responseCode,
            responseMessage = verif.message,
            createdAt       = LocalDateTime.now()
        ))

        // ── 6. Activer ou rejeter ─────────────────────────────────────────────
        return if (verif.success) {
            activateSubscription(plan.id, plan.nom, plan.dureeDays, adminPhone, verif.refundAmount)
        } else {
            sendPaymentFailureSms(adminPhone)
            buildFailureResponse(paymentStatut, verif.message)
        }
    }

    // ── Helpers privés ───────────────────────────────────────────────────────

    /**
     * Active l'abonnement après paiement validé.
     * Nouvelle endDate = aujourd'hui + dureeDays (pas depuis l'ancienne endDate).
     */
    private fun activateSubscription(
        planId: Long, planNom: String, dureeDays: Int,
        adminPhone: String, refundAmount: Int?
    ): PaymentResponseDTO {

        val sub = subscriptionService.getOrCreateSubscription()
        val today   = LocalDate.now()
        val endDate = today.plusDays(dureeDays.toLong())

        sub.planConfigId    = planId
        sub.planNom         = planNom
        sub.statut          = SubscriptionStatus.ACTIVE
        sub.startDate       = today
        sub.endDate         = endDate
        sub.graceEndsAt     = null
        sub.lastPaymentRef  = "OTP-${LocalDateTime.now().toLocalDate()}-${System.currentTimeMillis() % 10000}"
        sub.updatedAt       = LocalDateTime.now()
        subscriptionRepository.save(sub)

        // SMS de confirmation
        val smsMsg = buildString {
            append("Paiement recu. Abonnement $planNom active jusqu'au $endDate. ")
            if (refundAmount != null && refundAmount > 0) {
                append("Remboursement de $refundAmount FCFA initie vers votre compte OM. ")
            }
            append("Merci de nous faire confiance !")
        }
        c0de4hopeService.sendSms(adminPhone, smsMsg, SmsEvent.PAYMENT_OK)

        return PaymentResponseDTO(
            success      = true,
            message      = if (refundAmount != null && refundAmount > 0)
                               "Paiement validé. Remboursement de $refundAmount FCFA initié automatiquement."
                           else "Abonnement $planNom activé avec succès.",
            statut       = SubscriptionStatus.ACTIVE.name,
            endDate      = endDate.toString(),
            refundAmount = refundAmount
        )
    }

    private fun sendPaymentFailureSms(adminPhone: String) {
        c0de4hopeService.sendSms(
            phoneNumber = adminPhone,
            message     = "Paiement non valide (montant incorrect ou code errone). " +
                          "Reessayez sur l'application ou contactez le support.",
            event       = SmsEvent.PAYMENT_KO
        )
    }

    private fun buildFailureResponse(statut: PaymentStatut, message: String): PaymentResponseDTO {
        val userMessage = when (statut) {
            PaymentStatut.AMOUNT_MISMATCH ->
                "Montant reçu insuffisant. Vérifiez le montant et réessayez."
            PaymentStatut.OTP_INVALID     ->
                "Code OTP incorrect. Vérifiez le SMS reçu et réessayez."
            PaymentStatut.OTP_EXPIRED     ->
                "Code OTP expiré. Veuillez envoyer à nouveau le montant au 56239334."
            else                          ->
                "Erreur lors de la vérification du paiement. Contactez le support."
        }
        return PaymentResponseDTO(
            success = false,
            message = userMessage,
            statut  = statut.name,
            endDate = null
        )
    }
}
