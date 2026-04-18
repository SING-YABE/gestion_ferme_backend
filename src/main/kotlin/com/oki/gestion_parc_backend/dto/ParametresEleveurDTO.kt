package com.oki.gestion_parc_backend.dto

import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin

data class ParametresEleveurDTO(

    @field:NotNull(message = "Le seuil de porcelets nés vivants est obligatoire")
    @field:Positive(message = "Le seuil doit être positif")
    val seuilNesVivants: Int,

    @field:NotNull(message = "Le nombre max de mises bas est obligatoire")
    @field:Positive(message = "Le nombre max doit être positif")
    val nbMisesBasMax: Int,

    @field:NotNull(message = "Le seuil warning occupation box est obligatoire")
    @field:DecimalMin("0.0") @field:DecimalMax("1.0")
    val seuilOccupationBoxWarning: Double,

    @field:NotNull(message = "Le seuil critique occupation box est obligatoire")
    @field:DecimalMin("0.0") @field:DecimalMax("1.0")
    val seuilOccupationBoxCritique: Double

)