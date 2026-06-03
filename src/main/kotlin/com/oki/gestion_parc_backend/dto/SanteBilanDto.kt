package com.oki.gestion_parc_backend.dto

/**
 * DTO — Bilan sanitaire d'un animal individuel.
 * GET /api/animaux/{id}/bilan-sante
 */
data class BilanSanteAnimalDto(
    val animalId: Long,
    val codeAnimal: String,
    val typeAnimal: String,
    val nbVisitesSoins: Int,
    val coutTotalSoins: Double,
    val coutTotalMedicaments: Double,
    val coutTotalPrestations: Double,
    val derniereVisite: String?,
    val motifsPrincipaux: List<String>,
    val soins: List<SoinResume>
)

data class SoinResume(
    val dateSoin: String,
    val motif: String,
    val traitement: String,
    val coutTotal: Double,
    val veterinaire: String
)

/**
 * DTO — Un animal classé parmi les top consommateurs de soins.
 * GET /api/soins/top-consommateurs
 */
data class TopConsommateurDto(
    val animalId: Long,
    val codeAnimal: String,
    val typeAnimal: String,
    val nbVisites: Int,
    val coutTotal: Double,
    val coutMedicaments: Double,
    /** Dernier motif de soin enregistré */
    val dernierMotif: String?,
    val derniereSoin: String?
)

/**
 * DTO — ISSF (Intervalle Sevrage–Saillie Fécondante) d'une truie.
 * GET /api/reproductions/issf
 */
data class IssfDto(
    /** ISSF moyen de l'élevage en jours (objectif : ≤ 7 jours) */
    val issfMoyenJours: Double?,
    /** Nombre de truies pour lesquelles l'ISSF a pu être calculé */
    val nbTruiesCalculees: Int,
    val objectifJours: Int = 7,
    val conforme: Boolean,
    /** Détail par truie */
    val detail: List<IssfDetailDto>
)

data class IssfDetailDto(
    val truieId: Long,
    val codeAnimal: String,
    val dateSevrageEstime: String?,
    val dateSailliesSuivante: String?,
    val issfJours: Int?
)
