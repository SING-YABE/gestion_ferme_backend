package com.oki.gestion_parc_backend.dto

import java.time.LocalDate
import jakarta.validation.constraints.*

data class AnimalDTO(
    @field:NotNull
    val typeAnimalId: Long,

    @field:NotNull
    val dateEntree: String,      // format dd/MM/yyyy

    /**
     * Date de naissance de l'animal (optionnel).
     * Format dd/MM/yyyy. Utilisée pour calculer l'âge à la vente (KPI SAD).
     */
    val dateNaissance: String? = null,   // format dd/MM/yyyy

    @field:Positive
    val poidsInitial: Double,

    @field:NotNull
    val etatSanteId: Long,

    @field:NotNull
    val boxId: Long,

    val observations: String? = null
)












