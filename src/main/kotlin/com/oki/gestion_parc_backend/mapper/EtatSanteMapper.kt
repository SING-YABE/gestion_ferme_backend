package com.oki.gestion_parc_backend.dto

import com.oki.gestion_parc_backend.model.EtatSante
import com.oki.gestion_parc_backend.model.TypeAnimal

object EtatSanteMapper {
    fun toEntity(dto: EtatSanteDTO, typeAnimal: TypeAnimal) = EtatSante(
        description = dto.description,
        typeAnimal = typeAnimal
    )
    fun toResponseDTO(entity: EtatSante) = EtatSanteResponseDTO(
        entity.id, entity.description,
        typeAnimal = TypeAnimalMapper.toResponseDTO(entity.typeAnimal)
    )
}


