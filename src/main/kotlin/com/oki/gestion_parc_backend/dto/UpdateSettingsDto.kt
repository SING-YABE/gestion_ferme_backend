package com.oki.gestion_parc_backend.dto

data class UpdateSettingsDto(
    val farmName: String,
    val contactEmail: String,
    val contactTel: String,
    val slogan: String
)
