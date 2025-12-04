package com.oki.gestion_parc_backend.dto

import jakarta.validation.constraints.*

data class SoinAnimalDTO(

    val codeAnimal: String? = null, // null si soin collectif

    @field:NotBlank
    val dateSoin: String, // format dd/MM/yyyy

    @field:NotBlank
    val motif: String,

    @field:NotBlank
    val traitement: String,

    @field:Positive
    val cout: Double,

    @field:NotBlank
    val veterinaire: String,

    val observations: String? = null
)
