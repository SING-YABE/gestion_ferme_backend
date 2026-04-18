package com.oki.gestion_parc_backend.dto
import java.time.LocalDateTime

data class HistoriqueDeplacementResponseDTO(
    val id: Long,
    val animalId: Long,
    val codeAnimal: String,
    val ancienneBoxCode: String?,
    val nouvelleBoxCode: String,
    val dateDeplacement: LocalDateTime,
    val motif: String?
)
