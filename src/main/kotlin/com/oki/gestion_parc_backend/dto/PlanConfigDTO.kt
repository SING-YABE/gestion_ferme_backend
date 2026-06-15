package com.oki.gestion_parc_backend.dto

/**
 * DTO pour lire et modifier la configuration des limites du plan FREE.
 * Utilisé pour GET /subscription/config et PUT /subscription/config.
 *
 *   maxAnimauxFreePlan : nombre max d'animaux actifs autorisés en plan FREE
 */
data class PlanConfigDTO(
    val maxAnimauxFreePlan: Int
)
