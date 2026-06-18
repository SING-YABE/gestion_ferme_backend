package com.oki.gestion_parc_backend.model

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * Trace chaque tentative de paiement Orange Money via c0de4hope.
 * Stockée dans le schéma tenant (une table par ferme).
 *
 * Chaque appel à POST /api/subscriptions/pay génère une ligne,
 * qu'il soit succès ou échec — pour audit et déduplication.
 *
 * Entrée  : créée par SubscriptionPaymentService après appel à c0de4hope
 * Sortie  : consultée par le Super-Admin dans l'historique des transactions
 */
@Entity
@Table(name = "payment_transactions")
class PaymentTransaction(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    /** ID du plan visé par ce paiement. */
    @Column(nullable = false)
    val planConfigId: Long = 0L,

    /** Nom du plan au moment du paiement (copie pour historique immuable). */
    @Column(nullable = false)
    val planNom: String = "",

    /** Numéro Orange Money de l'éleveur (ex: "+22670123456"). */
    @Column(nullable = false)
    val phoneNumber: String = "",

    /** Montant attendu selon le plan (FCFA). */
    @Column(nullable = false)
    val montantAttendu: Int = 0,

    /**
     * Résultat de la vérification c0de4hope.
     * SUCCESS / AMOUNT_MISMATCH / OVERPAID / OTP_INVALID / OTP_EXPIRED
     * / MANUAL_ATTRIBUTION / ERROR
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val statut: PaymentStatut = PaymentStatut.ERROR,

    /** Code brut retourné par l'API c0de4hope (pour débogage). */
    @Column(nullable = true)
    val responseCode: String? = null,

    /** Message brut retourné par l'API c0de4hope. */
    @Column(nullable = true, length = 500)
    val responseMessage: String? = null,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)

/**
 * Statuts possibles d'une transaction de paiement.
 */
enum class PaymentStatut {
    /** Paiement validé — abonnement activé. */
    SUCCESS,

    /** Montant reçu insuffisant. c0de4hope rembourse. */
    AMOUNT_MISMATCH,

    /** Montant excédentaire. c0de4hope rembourse le surplus. Abonnement activé quand même. */
    OVERPAID,

    /** OTP saisi incorrect. Peut réessayer. */
    OTP_INVALID,

    /** OTP expiré. Doit recommencer le paiement. */
    OTP_EXPIRED,

    /** Attribution manuelle par le Super-Admin (sans paiement réel). */
    MANUAL_ATTRIBUTION,

    /** Erreur réseau ou API inattendue. */
    ERROR
}
