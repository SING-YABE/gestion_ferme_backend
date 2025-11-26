package com.oki.gestion_parc_backend.dto

data class AnimalResponseDTO(
    val id: Long,
    val codeAnimal: String,
    val typeAnimal: TypeAnimalResponseDTO,
    val dateEntree: String,
    val poidsInitial: Double,
    val etatSante: EtatSanteResponseDTO,
    val batiment: BatimentResponseDTO,
    val observations: String?
)

