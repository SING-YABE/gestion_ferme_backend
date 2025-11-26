package com.oki.gestion_parc_backend.service

import com.oki.gestion_parc_backend.dto.BatimentDTO
import com.oki.gestion_parc_backend.dto.BatimentResponseDTO

interface BatimentService {
    fun creerBatiment(dto: BatimentDTO): BatimentResponseDTO
    fun getAllBatiments(): List<BatimentResponseDTO>
    fun getBatimentById(id: Long): BatimentResponseDTO
    fun updateBatiment(id: Long, dto: BatimentDTO): BatimentResponseDTO
    fun deleteBatiment(id: Long)
}
