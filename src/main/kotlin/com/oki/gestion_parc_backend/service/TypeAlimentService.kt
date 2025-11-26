package com.oki.gestion_parc_backend.service

import com.oki.gestion_parc_backend.dto.TypeAlimentDto
import com.oki.gestion_parc_backend.model.TypeAliment

interface TypeAlimentService {
    fun create(dto: TypeAlimentDto): TypeAliment
    fun list(): List<TypeAliment>
    fun update(id: Long, dto: TypeAlimentDto): TypeAliment
    fun delete(id: Long)
}
