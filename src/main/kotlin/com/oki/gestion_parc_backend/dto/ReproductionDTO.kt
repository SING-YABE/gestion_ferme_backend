package com.oki.gestion_parc_backend.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero

data class ReproductionDTO(

    @field:NotBlank
    val truieCode: String,

    @field:NotBlank
    val verratCode: String,

    @field:NotBlank
    val dateSaillie: String, // format JJ/MM/AAAA

//    @field:NotBlank
//    val dateMiseBasPrevue: String, // format JJ/MM/AAAA

    val dateMiseBasReelle: String? = null,

    @field:PositiveOrZero
    val nbNesVivants: Int? = null,

    @field:PositiveOrZero
    val nbMortsNes: Int? = null,

    @field:PositiveOrZero
    val nbSevres: Int? = null,

    val observations: String? = null
)







