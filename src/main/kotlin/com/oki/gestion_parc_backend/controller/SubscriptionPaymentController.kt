package com.oki.gestion_parc_backend.controller

import com.oki.gestion_parc_backend.dto.PaymentRequestDTO
import com.oki.gestion_parc_backend.dto.PaymentResponseDTO
import com.oki.gestion_parc_backend.repository.UtilisateurRepository
import com.oki.gestion_parc_backend.service.SubscriptionPaymentService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*

/**
 * Controller de paiement d'abonnement via Orange Money (c0de4hope).
 *
 * Endpoint :
 *   POST /api/subscriptions/pay
 *     → Vérifie l'OTP, active l'abonnement, envoie le SMS de confirmation.
 *     → Protégé JWT : seul un utilisateur authentifié de la ferme peut payer.
 *
 * Le numéro Orange Money de l'admin est récupéré depuis son profil en base
 * (champ telephone dans Utilisateur) pour l'envoi du SMS de confirmation.
 * Si le profil n'a pas de numéro, on utilise celui fourni dans la requête.
 */
@RestController
@RequestMapping("/api/subscriptions")
class SubscriptionPaymentController(
    private val subscriptionPaymentService: SubscriptionPaymentService,
    private val utilisateurRepository: UtilisateurRepository
) {

    /**
     * Traite le paiement Orange Money et active l'abonnement si succès.
     *
     * Corps attendu :
     *   { "planId": 2, "phoneNumber": "+22670123456", "otp": "123456" }
     *
     * Réponse succès :
     *   { "success": true, "message": "...", "statut": "ACTIVE", "endDate": "2026-07-17" }
     *
     * Réponse échec :
     *   { "success": false, "message": "Code OTP incorrect...", "statut": "OTP_INVALID" }
     */
    @PostMapping("/pay")
    fun pay(
        @RequestBody request: PaymentRequestDTO,
        authentication: Authentication
    ): ResponseEntity<PaymentResponseDTO> {

        // Récupérer le numéro de téléphone de l'utilisateur connecté (pour SMS)
        // Si absent en base, utiliser le numéro fourni dans la requête
        val adminPhone = try {
            val user = utilisateurRepository.findByEmail(authentication.name)
            user?.telephone?.takeIf { it.isNotBlank() } ?: request.phoneNumber
        } catch (e: Exception) {
            request.phoneNumber
        }

        val result = subscriptionPaymentService.processPayment(request, adminPhone)

        return if (result.success) {
            ResponseEntity.ok(result)
        } else {
            // 402 Payment Required — le frontend affiche le message d'erreur
            ResponseEntity.status(402).body(result)
        }
    }
}
