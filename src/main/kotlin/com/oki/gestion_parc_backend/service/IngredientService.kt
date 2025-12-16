package com.oki.gestion_parc_backend.service

import com.oki.gestion_parc_backend.dto.IngredientDto

interface IngredientService {
    fun list(): List<IngredientDto>
    fun listByType(typeAlimentId: Long): List<IngredientDto>
    fun create(dto: IngredientDto): IngredientDto
}