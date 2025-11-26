package com.oki.gestion_parc_backend.dto
import jakarta.validation.constraints.*
import java.time.LocalDate

data class DepenseDTO(
    val id: Long? = null,

    @field:NotNull
    val date: String, // format JJ/MM/AAAA

    @field:NotNull
    val typeDepenseId: Long,

    @field:NotBlank
    val description: String,

    @field:Positive
    val montant: Double,

    @field:NotBlank
    val modePaiement: String,

    val observations: String? = null
)
