package com.oki.gestion_parc_backend.dto

data class IngredientDto(
    val id: Long,
    val nom: String,
    val typeAlimentId: Long,
    val typeAlimentLibelle: String
)