package com.oki.gestion_parc_backend.dto
import jakarta.validation.constraints.*
import java.time.LocalDate

data class TraitementDTO(
    val id: Long? = null,

    @field:NotNull
    val date: String, // format JJ/MM/AAAA

    @field:NotNull
    val animalId: Long,

    @field:NotBlank
    val traitement: String,

    @field:NotBlank
    val motif: String,

    @field:Positive
    val cout: Double,

    @field:NotBlank
    val veterinaire: String,

    val observations: String? = null
)
