package com.oki.gestion_parc_backend.dto
import jakarta.validation.constraints.*
import java.time.LocalDate

data class TraitementDTO(
    val id: Long? = null,
    val description : String,
    val nom: String
)
