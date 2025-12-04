package com.oki.gestion_parc_backend.service

import com.oki.gestion_parc_backend.dto.VenteDTO
import com.oki.gestion_parc_backend.dto.VenteResponseDTO
import com.oki.gestion_parc_backend.dto.VentesEvolutionDTO

interface VenteService {
    fun create(dto: VenteDTO): VenteResponseDTO
    fun update(id: Long, dto: VenteDTO): VenteResponseDTO
    fun getAll(): List<VenteResponseDTO>
    fun getById(id: Long): VenteResponseDTO
    fun delete(id: Long)
    fun evolutionMensuelle(): List<VentesEvolutionDTO>

}
