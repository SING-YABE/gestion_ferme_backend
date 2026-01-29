package com.oki.gestion_parc_backend.dto

data class VenteCreateDTO(
    val dateVente: String, // format dd/MM/yyyy
    val client: String,
    val animaux: List<AnimalVenteDTO>,
    val  dateEnlevement: String,
    val dateEnlevementAuPlusTard: String

)
