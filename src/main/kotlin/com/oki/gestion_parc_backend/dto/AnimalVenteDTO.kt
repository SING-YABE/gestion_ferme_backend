package com.oki.gestion_parc_backend.dto

import com.oki.gestion_parc_backend.model.ModeVente


data class AnimalVenteDTO(
    val codeAnimal: String,
    val typeVenteId: Long,

    val modeVente: ModeVente, // AU_POIDS ou SANS_PESEE

    // AU_POIDS seulement
    val poidsVente: Double? = null,
    val prixUnitaire: Double? = null,

    // SANS_PESEE seulement
    val prixNegocie: Double? = null
)