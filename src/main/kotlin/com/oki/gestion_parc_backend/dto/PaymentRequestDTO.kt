package com.oki.gestion_parc_backend.dto

/**
 * Corps de la requête POST /api/subscriptions/pay.
 *
 * L'éleveur envoie ces 3 champs après avoir effectué le virement Orange Money :
 *   planId      : ID du plan choisi (validé côté backend)
 *   phoneNumber : son numéro Orange Money (+226XXXXXXXX)
 *   otp         : code reçu par SMS de c0de4hope
 */
data class PaymentRequestDTO(
    val planId: Long,
    val phoneNumber: String,
    val otp: String
)

/**
 * Réponse retournée par POST /api/subscriptions/pay.
 */
data class PaymentResponseDTO(
    /** true si l'abonnement a été activé avec succès. */
    val success: Boolean,

    /** Message lisible pour l'utilisateur. */
    val message: String,

    /** Nouveau statut de l'abonnement (ACTIVE, etc.). */
    val statut: String?,

    /** Date de fin du nouvel abonnement (ISO yyyy-MM-dd). */
    val endDate: String?,

    /** Montant remboursé en FCFA si OVERPAID (null sinon). */
    val refundAmount: Int? = null
)
