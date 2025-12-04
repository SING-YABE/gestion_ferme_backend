package com.oki.gestion_parc_backend.dto

data class SoinAnimalResponseDTO(
    val id: Long,
    val animal: AnimalResponseDTO?, // null si collectif
    val dateSoin: String,
    val motif: String?,
    val traitement: String,
    val cout: Double,
    val veterinaire: String,
    val observations: String?,
    val soinCollectif: Boolean
)
