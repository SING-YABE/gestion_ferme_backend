package com.oki.gestion_parc_backend.dto

data class IngredientAlimentationDto(
    val ingredientId: Long,
    val ingredientNom: String? = null,
    val typeAlimentId: Long? = null,
    val typeAlimentLibelle: String? = null,
    val quantiteKg: Double,
    val prixUnitaire: Double,
    val sousTotal: Double? = null
)