package com.oki.gestion_parc_backend.dto
import jakarta.validation.constraints.*

data class SoinAnimalDTO(
    val codeAnimal: String? = null,
    val animalCodes: List<String>? = null,
    val applyToAll: Boolean = false,

    @field:NotBlank
    val dateSoin: String, // dd/MM/yyyy

    @field:NotBlank
    val motif: String,

    @field:NotBlank
    val traitement: String,

    val traitementApporte: String? = null,

    @field:PositiveOrZero
    val cout: Double = 0.0,

    @field:PositiveOrZero
    val coutMedicament: Double = 0.0,

    @field:NotBlank
    val veterinaire: String,

    val observations: String? = null
)
