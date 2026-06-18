package com.oki.gestion_parc_backend.model

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Abonnement actif d'une ferme (tenant).
 *
 * Singleton par tenant : 1 seule ligne par schéma ferme.
 * L'état évolue selon la machine à états définie dans SubscriptionStatus.
 *
 * Entrées :
 *   - Créée automatiquement à l'inscription en statut TRIAL
 *   - Mise à jour via SubscriptionPaymentService (paiement OTP)
 *   - Mise à jour via SubscriptionExpiryJob (transitions automatiques)
 *   - Mise à jour manuelle par le Super-Admin
 *
 * Sortie :
 *   - Lue par SubscriptionService.getStatus() → renvoyé au frontend/mobile
 *   - Lue par AnimalServiceImpl, BatimentService, etc. pour vérifier les limites
 */
@Entity
@Table(name = "subscription")
class Subscription(

    @Id
    val id: Long = 1L,

    // ── Plan souscrit ─────────────────────────────────────────────────────────

    /**
     * ID du plan souscrit (référence public.plan_config.id).
     * Null uniquement à l'initialisation avant le premier choix de plan.
     */
    @Column(nullable = true)
    var planConfigId: Long? = null,

    /**
     * Nom du plan — copié pour éviter une jointure cross-schema à chaque lecture.
     * Mis à jour à chaque changement de plan.
     */
    @Column(nullable = true)
    var planNom: String? = null,

    // ── État ─────────────────────────────────────────────────────────────────

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var statut: SubscriptionStatus = SubscriptionStatus.TRIAL,

    // ── Dates ────────────────────────────────────────────────────────────────

    /** Fin de la période d'essai gratuit (null si pas d'essai). */
    @Column(nullable = true)
    var trialEndsAt: LocalDate? = null,

    /** Début du dernier abonnement payé. Null si jamais payé. */
    @Column(nullable = true)
    var startDate: LocalDate? = null,

    /** Fin du dernier abonnement payé. Null si jamais payé ou si TRIAL en cours. */
    @Column(nullable = true)
    var endDate: LocalDate? = null,

    /**
     * Fin de la période de grâce = endDate + 3 jours.
     * Calculée automatiquement lors du passage en statut GRACE.
     */
    @Column(nullable = true)
    var graceEndsAt: LocalDate? = null,

    // ── Traçabilité ───────────────────────────────────────────────────────────

    /** Référence du dernier paiement validé via c0de4hope. Ex : "OTP-20260617-001". */
    @Column(nullable = true)
    var lastPaymentRef: String? = null,

    /**
     * Notes internes — renseignées lors d'une attribution manuelle Super-Admin
     * ou d'une suspension. Ex : "Attribution manuelle partenariat - 17/06/2026".
     */
    @Column(nullable = true, length = 500)
    var notes: String? = null,

    /** Réservé v2 : paiement récurrent automatique. */
    @Column(nullable = false)
    var autoRenew: Boolean = false,

    @Column(nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    /**
     * Vérifie si l'abonnement donne accès à l'application.
     * TRIAL, ACTIVE et GRACE permettent l'accès (GRACE = lecture seule côté frontend).
     */
    fun isAccessAllowed(): Boolean =
        statut == SubscriptionStatus.TRIAL ||
        statut == SubscriptionStatus.ACTIVE ||
        statut == SubscriptionStatus.GRACE

    /**
     * Vérifie si l'abonnement est pleinement actif (pas de restrictions lecture seule).
     */
    fun isFullyActive(): Boolean =
        statut == SubscriptionStatus.TRIAL || statut == SubscriptionStatus.ACTIVE
}
