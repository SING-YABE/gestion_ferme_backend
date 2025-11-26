package com.oki.gestion_parc_backend.service
import com.oki.gestion_parc_backend.dto.TraitementDTO

interface TraitementService {
    fun create(dto: TraitementDTO): TraitementDTO
    fun list(): List<TraitementDTO>
    fun update(id: Long, dto: TraitementDTO): TraitementDTO
    fun delete(id: Long)
}
