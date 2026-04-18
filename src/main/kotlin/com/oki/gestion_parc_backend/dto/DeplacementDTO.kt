package com.oki.gestion_parc_backend.dto

data class DeplacementDTO(
    val animalId: Long,
    val nouvelleBoxId: Long,
    val motif: String? = null
)