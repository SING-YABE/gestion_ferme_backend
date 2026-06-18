package com.oki.gestion_parc_backend.model

/**
 * États possibles d'un abonnement ferme.
 *
 * Machine à états :
 *   TRIAL     → ACTIVE (si paiement avant fin essai)
 *   TRIAL     → EXPIRED (fin essai sans paiement — job nuit)
 *   ACTIVE    → GRACE   (date de fin dépassée — job nuit, grace = +3 jours)
 *   GRACE     → EXPIRED (fin grace sans renouvellement — job nuit)
 *   GRACE     → ACTIVE  (paiement pendant la grace)
 *   EXPIRED   → ACTIVE  (renouvellement : endDate = aujourd'hui + dureeDays)
 *   *         → SUSPENDED (action manuelle Super-Admin)
 *   SUSPENDED → ACTIVE  (levée de suspension — attribution manuelle)
 */
enum class SubscriptionStatus {
    /** Période d'essai gratuite en cours. Accès fonctionnalités de base. */
    TRIAL,

    /** Abonnement payé et valide. Accès complet au plan souscrit. */
    ACTIVE,

    /**
     * Abonnement expiré depuis moins de 3 jours.
     * Accès maintenu en lecture seule. Bandeau d'avertissement affiché.
     */
    GRACE,

    /** Abonnement expiré (au-delà de la grace). Accès bloqué — paywall. */
    EXPIRED,

    /** Suspendu manuellement par le Super-Admin (fraude, litige). Accès bloqué. */
    SUSPENDED,

    /** Annulé par le Super-Admin. Historique conservé. Accès bloqué. */
    CANCELLED
}
