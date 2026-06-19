package com.oki.gestion_parc_backend.service

import com.oki.gestion_parc_backend.model.SmsEvent
import com.oki.gestion_parc_backend.model.SmsLog
import com.oki.gestion_parc_backend.model.SmsStatut
import com.oki.gestion_parc_backend.repository.SmsLogRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.time.LocalDateTime

/**
 * Service d'intégration avec l'API c0de4hope.
 *
 * Responsabilités :
 *   1. verifyPayment() — vérifie un paiement Orange Money via OTP
 *   2. sendSms()       — envoie un SMS et le logue dans SmsLog
 *
 * La clé API est lue depuis la variable d'environnement CODEHOPE_API_KEY.
 * Elle n'apparaît jamais en dur dans le code source.
 *
 * Timeout : 10 secondes sur chaque appel (configuré dans le RestTemplate).
 */
@Service
class C0de4hopeService(
    private val smsLogRepository: SmsLogRepository,
    private val restTemplate: RestTemplate
) {

    @Value("\${codehope.api.key}")
    private lateinit var apiKey: String

    @Value("\${codehope.api.url}")
    private lateinit var apiUrl: String

    // ── Vérification de paiement ─────────────────────────────────────────────

    /**
     * Vérifie un paiement Orange Money auprès de c0de4hope.
     *
     * Entrées :
     *   @param phoneNumber  numéro OM de l'éleveur (ex: "+22670123456")
     *   @param amount       montant attendu en FCFA
     *   @param otp          code OTP reçu par SMS par l'éleveur
     *
     * Sortie : [PaymentVerificationResult] avec statut et message
     *
     * En cas d'échec réseau : retourne ERROR avec le message d'exception.
     */
    fun verifyPayment(phoneNumber: String, amount: Int, otp: String): PaymentVerificationResult {
        val endpoint = "$apiUrl/auth/verify-otp"
        val reqBody  = mapOf("number" to phoneNumber, "amount" to amount, "otp" to otp)
        println("[C0de4hope] ▶ verifyPayment → POST $endpoint")
        println("[C0de4hope]   body : $reqBody")

        return try {
            val headers = buildHeaders()
            val entity  = HttpEntity(reqBody, headers)

            val response = restTemplate.postForEntity(endpoint, entity, Map::class.java)

            println("[C0de4hope] ◀ HTTP ${response.statusCode} | body : ${response.body}")
            parseVerifyResponse(response)

        } catch (ex: Exception) {
            println("[C0de4hope] ❌ Exception : ${ex.javaClass.name} — ${ex.message}")
            ex.printStackTrace()
            PaymentVerificationResult(
                success       = false,
                statut        = "ERROR",
                message       = "Erreur réseau lors de la vérification : ${ex.message}",
                responseCode  = "NETWORK_ERROR",
                refundAmount  = null
            )
        }
    }

    // ── Envoi de SMS ─────────────────────────────────────────────────────────

    /**
     * Envoie un SMS via c0de4hope et logue le résultat dans SmsLog.
     *
     * Entrées :
     *   @param phoneNumber  numéro destinataire
     *   @param message      texte du SMS (max ~160 caractères recommandés)
     *   @param event        type d'événement (pour déduplication et historique)
     *
     * Sortie : true si envoyé avec succès, false sinon.
     * L'échec est loggé mais ne remonte pas d'exception (non bloquant).
     */
    fun sendSms(phoneNumber: String, message: String, event: SmsEvent): Boolean {
        val log = SmsLog(
            phoneNumber = phoneNumber,
            message     = message,
            event       = event,
            createdAt   = LocalDateTime.now()
        )

        return try {
            val headers = buildHeaders()
            val body = mapOf(
                "to"      to phoneNumber,
                "message" to message
            )
            val entity = HttpEntity(body, headers)

            val response = restTemplate.postForEntity(
                "$apiUrl/send",
                entity,
                Map::class.java
            )

            val success = response.statusCode.is2xxSuccessful
            log.statut = if (success) SmsStatut.SENT else SmsStatut.FAILED
            if (!success) log.errorMessage = "HTTP ${response.statusCode}"
            smsLogRepository.save(log)
            success

        } catch (ex: Exception) {
            log.statut       = SmsStatut.FAILED
            log.errorMessage = ex.message
            smsLogRepository.save(log)
            false
        }
    }

    // ── Helpers privés ───────────────────────────────────────────────────────

    private fun buildHeaders(): HttpHeaders = HttpHeaders().apply {
        contentType = MediaType.APPLICATION_JSON
        set("Authorization", "Bearer $apiKey")
    }

    /**
     * Interprète la réponse HTTP de /auth/verify-otp.
     *
     * L'API c0de4hope peut retourner plusieurs formats :
     *   Format A : { "status": "success"|"error", "code": "...", "message": "...", "refundAmount": null|Int }
     *   Format B : { "success": true|false, "message": "...", "data": {...} }
     *   Format C : { "code": "SUCCESS"|"OTP_INVALID"|..., "message": "..." }
     *
     * On tente de détecter le format et d'extraire les informations pertinentes.
     */
    private fun parseVerifyResponse(
        response: ResponseEntity<Map<*, *>>
    ): PaymentVerificationResult {

        val body = response.body ?: return PaymentVerificationResult(
            success = false, statut = "ERROR", message = "Réponse vide de c0de4hope"
        ).also { println("[C0de4hope] ⚠ body null") }

        println("[C0de4hope] parseVerify body keys : ${body.keys}")

        // --- Extraire les champs avec fallbacks multi-format ---
        val statusRaw   = body["status"]?.toString()
        val codeRaw     = body["code"]?.toString()
        val successBool = body["success"]?.let {
            it == true || it.toString().equals("true", ignoreCase = true)
        }
        val message     = body["message"]?.toString() ?: ""
        val refund      = (body["refundAmount"] as? Number)?.toInt()
            ?: (body["refund_amount"] as? Number)?.toInt()

        // Normaliser le statut en majuscules
        val statusUp = statusRaw?.uppercase()
        val codeUp   = codeRaw?.uppercase()

        println("[C0de4hope] status='$statusUp' code='$codeUp' success=$successBool message='$message'")

        val isSuccess = when {
            successBool == true                           -> true
            statusUp == "SUCCESS" || statusUp == "OK"     -> true
            codeUp == "SUCCESS" || codeUp == "OK"         -> true
            codeUp == "OVERPAID"                          -> true
            statusUp == "OVERPAID"                        -> true
            // HTTP 200 sans champ statut = succès implicite
            response.statusCode.is2xxSuccessful &&
            statusUp == null && codeUp == null &&
            successBool == null && message.isNotBlank()   -> true
            else                                          -> false
        }

        // Statut canonique pour notre logique interne
        val canonicalStatut = codeUp ?: statusUp ?: if (isSuccess) "SUCCESS" else "ERROR"

        println("[C0de4hope] → isSuccess=$isSuccess statut='$canonicalStatut' refund=$refund")

        return PaymentVerificationResult(
            success      = isSuccess,
            statut       = canonicalStatut,
            message      = message,
            responseCode = canonicalStatut,
            refundAmount = refund
        )
    }
}

/**
 * Résultat d'une vérification de paiement c0de4hope.
 *
 * success       : true si le paiement est valide (SUCCESS ou OVERPAID)
 * statut        : code retourné par c0de4hope (SUCCESS, AMOUNT_MISMATCH, OVERPAID, OTP_INVALID...)
 * message       : message lisible retourné par l'API
 * responseCode  : code brut pour logging
 * refundAmount  : montant remboursé en FCFA (null si pas de remboursement)
 */
data class PaymentVerificationResult(
    val success: Boolean,
    val statut: String,
    val message: String,
    val responseCode: String? = null,
    val refundAmount: Int? = null
)
