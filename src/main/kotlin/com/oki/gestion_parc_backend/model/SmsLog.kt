package com.oki.gestion_parc_backend.model

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * Journal de tous les SMS envoyés via c0de4hope.
 * Stocké dans le schéma tenant (une table par ferme).
 *
 * Utilisé pour :
 *   - Audit : qui a reçu quoi et quand
 *   - Déduplication : éviter d'envoyer deux fois le même SMS (ex: EXPIRY_7D)
 *
 * Entrée  : créé par C0de4hopeService.sendSms()
 * Sortie  : consulté par SubscriptionExpiryJob avant chaque envoi
 */
@Entity
@Table(name = "sms_logs")
class SmsLog(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    /** Numéro destinataire (numéro Orange Money de l'admin ferme). */
    @Column(nullable = false)
    val phoneNumber: String = "",

    /** Texte complet du SMS envoyé. */
    @Column(nullable = false, length = 500)
    val message: String = "",

    /**
     * Type d'événement déclencheur.
     * WELCOME / EXPIRY_7D / EXPIRY_3D / EXPIRY_0D / PAYMENT_OK / PAYMENT_KO / MANUAL
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val event: SmsEvent = SmsEvent.WELCOME,

    /** SENT si l'envoi c0de4hope a réussi, FAILED sinon. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var statut: SmsStatut = SmsStatut.SENT,

    /** Message d'erreur si statut = FAILED (null si succès). */
    @Column(nullable = true, length = 500)
    var errorMessage: String? = null,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)

enum class SmsEvent {
    /** SMS de bienvenue à l'inscription. */
    WELCOME,

    /** Alerte 7 jours avant expiration. */
    EXPIRY_7D,

    /** Alerte 3 jours avant expiration. */
    EXPIRY_3D,

    /** SMS le jour de l'expiration. */
    EXPIRY_0D,

    /** Confirmation de paiement réussi. */
    PAYMENT_OK,

    /** Notification d'échec de paiement. */
    PAYMENT_KO,

    /** Attribution manuelle par Super-Admin. */
    MANUAL
}

enum class SmsStatut {
    SENT, FAILED
}
