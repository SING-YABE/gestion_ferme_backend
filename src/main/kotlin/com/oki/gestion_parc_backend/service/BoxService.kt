package com.oki.gestion_parc_backend.service

import com.oki.gestion_parc_backend.dto.BoxDTO
import com.oki.gestion_parc_backend.dto.BoxResponseDTO

interface BoxService {
    fun creerBox(dto: BoxDTO): BoxResponseDTO
    fun getAllBoxes(): List<BoxResponseDTO>
    fun getBoxById(id: Long): BoxResponseDTO
    fun getBoxesByBatiment(batimentId: Long): List<BoxResponseDTO>
    fun updateBox(id: Long, dto: BoxDTO): BoxResponseDTO
    fun deleteBox(id: Long)
}