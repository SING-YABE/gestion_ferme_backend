package com.oki.gestion_parc_backend.dto

data class FournissuerDTO(
    val id: Long,
    val nom: String,
    val contact: String? =null
)

