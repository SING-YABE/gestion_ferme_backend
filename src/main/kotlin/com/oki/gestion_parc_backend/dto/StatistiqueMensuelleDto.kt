package com.oki.gestion_parc_backend.dto

data class StatistiqueMensuelleDto(
    val annee: Int,
    val mois: Int,
    val coutTotal: Double,
    val typeAnimal: String? = null
)
