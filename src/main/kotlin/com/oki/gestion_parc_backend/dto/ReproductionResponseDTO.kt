package com.oki.gestion_parc_backend.dto

data class ReproductionResponseDTO(
    val id: Long,
    val truie: AnimalResponseDTO,
    val dateSaillie: String,
    val verrat: AnimalResponseDTO,
    val dateMiseBasPrevue: String,
    val dateMiseBasReelle: String?,
    val nbNesVivants: Int?,
    val nbMortsNes: Int?,
    val nbSevres: Int?,
    val observations: String?
)
