package com.oki.gestion_parc_backend.dto

data class VerratPerformanceDTO(
    val truieCode: String,
    val dateSaillie: String,
    val dateMiseBasReelle: String?,
    val nbNesVivants: Int?,
    val nbMortsNes: Int?,
    val nbSevres: Int?
)