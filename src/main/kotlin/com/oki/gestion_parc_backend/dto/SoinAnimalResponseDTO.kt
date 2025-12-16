package com.oki.gestion_parc_backend.dto
data class SoinAnimalResponseDTO(
    val id: Long,
    val animalCode: String?,
    val dateSoin: String,
    val motif: String,
    val traitement: String,
    val traitementApporte: String?,
    val cout: Double,
    val coutMedicament: Double,
    val totalPrestation: Double,
    val veterinaire: String,
    val observations: String?,
    val soinCollectif: Boolean
)
