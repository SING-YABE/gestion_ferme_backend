package com.oki.gestion_parc_backend.dto

data class VenteDetailResponseDTO(
    val id: Long,
    val dateVente: String,
    val client: String,
    val montantTotal: Double,
    val poidsTotal: Double,
    val animaux: List<AnimalVenduResponseDTO>,
    val dateEnlevement: String?,
    val dateEnlevementAuPlusTard: String?,
)
