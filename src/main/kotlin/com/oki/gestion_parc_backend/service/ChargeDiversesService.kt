package com.oki.gestion_parc_backend.service
import com.oki.gestion_parc_backend.dto.ChargeDiversesDto

interface ChargeDiversesService {
    fun create(dto: ChargeDiversesDto): ChargeDiversesDto
    fun update(id: Long, dto: ChargeDiversesDto): ChargeDiversesDto
    fun getAll(): List<ChargeDiversesDto>
    fun getById(id: Long): ChargeDiversesDto
    fun delete(id: Long)
}
