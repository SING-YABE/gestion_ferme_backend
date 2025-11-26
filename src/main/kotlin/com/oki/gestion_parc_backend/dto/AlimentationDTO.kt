package com.oki.gestion_parc_backend.dto

import java.time.LocalDate

data class AlimentationDto(
    val id: Long? = null,
    val date: LocalDate,
    val typeAlimentId: Long,
    val quantiteKg: Double,
    val prixUnitaire: Double,
    val codeAnimal: String? = null,
    val typeAnimalId: Long? = null,
    val fournisseurId: Long? = null,
    val coutTotal: Double? = null,
    val typeAlimentLibelle: String? = null


)