package com.oki.gestion_parc_backend.dto

data class TruieCarriereDTO(
    val rang: Int,
    val verratCode: String,
    val dateSaillie: String,
    val dateMiseBasReelle: String?,
    val nbNesVivants: Int?,
    val nbMortsNes: Int?,
    val nbSevres: Int?
)