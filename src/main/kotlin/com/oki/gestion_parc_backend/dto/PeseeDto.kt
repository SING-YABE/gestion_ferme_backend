package com.oki.gestion_parc_backend.dto

/**
 * DTO — Création d'une pesée.
 * La date est au format "dd/MM/yyyy".
 */
data class PeseeCreateDto(
    val animalId: Long,
    val poids: Double,
    val datePesee: String,        // format dd/MM/yyyy
    val observations: String? = null
)

/** DTO — Réponse renvoyée après création ou lecture d'une pesée. */
data class PeseeResponseDto(
    val id: Long,
    val animalId: Long,
    val codeAnimal: String,
    val poids: Double,
    val datePesee: String,
    val observations: String?,

    /**
     * GMQ calculé par rapport à la pesée précédente de ce même animal.
     * Null si c'est la première pesée.
     * Unité : g/jour
     */
    val gmqDepuisPrecedente: Double? = null
)
