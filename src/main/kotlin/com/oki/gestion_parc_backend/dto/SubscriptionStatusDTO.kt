package com.oki.gestion_parc_backend.dto

/**
 * Réponse retournée par GET /subscription/status.
 * Consommé par le mobile Flutter au moment du login pour connaître
 * le plan actif et les limites en vigueur.
 *
 * Champs :
 *   plan          : "FREE" ou "PRO"
 *   isPro         : raccourci booléen pour simplifier les conditions côté mobile
 *   dateDebut     : date de début du plan courant (ISO yyyy-MM-dd)
 *   dateFin       : date d'expiration (null = pas d'expiration)
 *   active        : true si l'abonnement est actif
 *   limits        : limites en vigueur + compteurs actuels
 */
data class SubscriptionStatusDTO(
    val plan: String,
    val isPro: Boolean,
    val dateDebut: String?,
    val dateFin: String?,
    val active: Boolean,
    val limits: LimitsDTO
)

/**
 * Détail des limites pour l'affichage mobile (ex: "3 / 5 animaux").
 *
 *   maxAnimaux     : -1 = illimité (plan PRO), sinon la limite configurée
 *   currentAnimaux : nombre d'animaux actifs (non vendus) actuellement
 *   limitAtteinte  : true si currentAnimaux >= maxAnimaux — déclenche le paywall mobile
 */
data class LimitsDTO(
    val maxAnimaux: Int,
    val currentAnimaux: Long,
    val limitAtteinte: Boolean
)
