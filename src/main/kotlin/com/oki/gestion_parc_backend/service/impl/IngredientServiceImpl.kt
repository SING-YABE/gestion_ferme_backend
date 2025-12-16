package com.oki.gestion_parc_backend.service.impl

import com.oki.gestion_parc_backend.dto.IngredientDto
import com.oki.gestion_parc_backend.mapper.IngredientMapper
import com.oki.gestion_parc_backend.model.Ingredient
import com.oki.gestion_parc_backend.repository.IngredientRepository
import com.oki.gestion_parc_backend.repository.TypeAlimentRepository
import com.oki.gestion_parc_backend.service.IngredientService
import org.springframework.stereotype.Service

@Service
class IngredientServiceImpl(
    private val repo: IngredientRepository,
    private val typeAlimentRepo: TypeAlimentRepository
) : IngredientService {

    override fun list(): List<IngredientDto> =
        repo.findAll().map { IngredientMapper.toDto(it) }

    override fun listByType(typeAlimentId: Long): List<IngredientDto> =
        repo.findByTypeAlimentId(typeAlimentId).map { IngredientMapper.toDto(it) }

    override fun create(dto: IngredientDto): IngredientDto {
        val typeAliment = typeAlimentRepo.findById(dto.typeAlimentId)
            .orElseThrow { IllegalArgumentException("Type aliment introuvable") }

        val ingredient = Ingredient(
            nom = dto.nom,
            typeAliment = typeAliment
        )

        val saved = repo.save(ingredient)
        return IngredientMapper.toDto(saved)
    }
}