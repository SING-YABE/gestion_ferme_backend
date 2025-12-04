package com.oki.gestion_parc_backend.dto

data class VenteDTO(
    val dateVente: String,
    val type: String,
    val quantite: Double,
    val poidsTotal: Double,
    val prixUnitaire: Double,
    val client: String
)
