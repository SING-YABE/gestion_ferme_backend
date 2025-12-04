package com.oki.gestion_parc_backend.dto

data class VenteResponseDTO(
    val id: Long,
    val dateVente: String,
    val type: String,
    val quantite: Double,
    val poidsTotal: Double,
    val prixUnitaire: Double,
    val montantTotal: Double,
    val client: String
)
