package com.oki.gestion_parc_backend.dto

import java.time.LocalDate

data class AlimentationDto(
    val id: Long? = null,
    val date: LocalDate,
    val mode: String, // "ACHAT" ou "FABRICATION"
    val ingredients: List<IngredientAlimentationDto>,
    val codeAnimal: String? = null,
    val typeAnimalId: Long? = null,
    val fournisseurId: Long? = null,
    val coutTotal: Double? = null,

    /**
     * Référence documentaire utilisée pour définir la ration.
     * Ex : "DGPA/MRAH 2021", "AVIPRO/WISIUM", "ALF ISSEN", "ONG Thamani"
     */
    val sourceReference: String? = null
)