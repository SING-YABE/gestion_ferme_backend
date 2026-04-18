package com.oki.gestion_parc_backend.service

import com.oki.gestion_parc_backend.dto.DeplacementDTO
import com.oki.gestion_parc_backend.dto.HistoriqueDeplacementResponseDTO


interface DeplacementService {
    fun deplacerAnimal(dto: DeplacementDTO): HistoriqueDeplacementResponseDTO
    fun getHistoriqueByAnimal(animalId: Long): List<HistoriqueDeplacementResponseDTO>
    fun getAllHistorique(): List<HistoriqueDeplacementResponseDTO>
}