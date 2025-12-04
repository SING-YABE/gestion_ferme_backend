package com.oki.gestion_parc_backend.dto

data class AlerteMiseBasDTO(
    val truieCode: String,
    val dateMiseBasPrevue: String,
    val joursRestants: Long
)