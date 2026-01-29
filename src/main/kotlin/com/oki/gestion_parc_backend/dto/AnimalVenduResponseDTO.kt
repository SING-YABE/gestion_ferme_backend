package com.oki.gestion_parc_backend.dto

data class AnimalVenduResponseDTO(
    val id: Long,
    val animalCode: String,
    val typeVenteNom: String,
    val poidsVente: Double,
    val prixUnitaire: Double,
    val montantTotal: Double
)
