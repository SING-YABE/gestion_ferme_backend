package com.oki.gestion_parc_backend.dto

data class AnimalVenteDTO(
    val codeAnimal: String,
    val typeVenteId: Long,
    val poidsVente: Double,
    val prixUnitaire: Double
)
