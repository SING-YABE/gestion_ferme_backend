package com.oki.gestion_parc_backend.dto
data class ChargeDiversesDto(
    val id: Long? = null,
    val date: String,
    val typeDepenseId: Long,
    val description: String,
    val montant: Double,
    val modePaiement: String,
    val observations: String?
)
