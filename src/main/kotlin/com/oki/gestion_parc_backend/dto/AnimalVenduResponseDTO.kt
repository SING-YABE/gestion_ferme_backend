package com.oki.gestion_parc_backend.dto

import com.oki.gestion_parc_backend.model.ModeVente


data class AnimalVenduResponseDTO(
    val id: Long,
    val animalCode: String,
    val typeVenteNom: String,
    val modeVente: ModeVente,

    // AU_POIDS
    val poidsVente: Double?,
    val prixUnitaire: Double?,

    // SANS_PESEE
    val prixNegocie: Double?,

    val montantTotal: Double
)