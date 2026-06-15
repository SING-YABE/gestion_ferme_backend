package com.oki.gestion_parc_backend.model

/**
 * Plans d'abonnement disponibles pour la ferme.
 *
 * FREE → utilisation limitée (nombre d'animaux plafonné, configurable par l'admin)
 * PRO  → utilisation illimitée (activé manuellement jusqu'à intégration paiement)
 */
enum class SubscriptionPlan {
    FREE,
    PRO
}
