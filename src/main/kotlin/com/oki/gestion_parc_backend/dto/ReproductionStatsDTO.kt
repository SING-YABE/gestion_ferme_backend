package com.oki.gestion_parc_backend.dto


data class ReproductionStatsDTO(
    val truiesGestantes: Long,
    val misesBasMois: Long,
    val porceletsSevres: Long,
    val tauxReussite: Double,
    val totalSaillies: Long
)
