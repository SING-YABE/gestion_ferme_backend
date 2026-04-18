package com.oki.gestion_parc_backend.dto

data class BoxResponseDTO(
    val id: Long,
    val numero: Int,
    val code: String,
    val capaciteMax: Int,
    val batimentId: Long,
    val batimentNom: String,
    val occupationActuelle: Int
)