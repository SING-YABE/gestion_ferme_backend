package com.oki.gestion_parc_backend.dto

import java.time.LocalDate
import jakarta.validation.constraints.*

data class AnimalDTO(
    @field:NotNull
    val typeAnimalId: Long,

    @field:NotNull
    val dateEntree: String, //JJ/MM/AAAA

    @field:Positive
    val poidsInitial: Double,

    @field:NotNull
    val etatSanteId: Long,

    @field:NotNull
    val boxId: Long,

    val observations: String? = null
)












