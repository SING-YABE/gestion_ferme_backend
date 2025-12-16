package com.oki.gestion_parc_backend.service
import com.oki.gestion_parc_backend.dto.BatimentDTO
import com.oki.gestion_parc_backend.dto.BatimentResponseDTO
import com.oki.gestion_parc_backend.dto.TraitementDTO
import com.oki.gestion_parc_backend.dto.TraitementResponseDTO

interface TraitementService {
    fun create(dto: TraitementDTO): TraitementResponseDTO
    fun list(): List<TraitementResponseDTO>
    fun update(id: Long, dto: TraitementDTO): TraitementResponseDTO
    fun delete(id: Long)
    fun getTraitementById(id: Long): TraitementResponseDTO

}

