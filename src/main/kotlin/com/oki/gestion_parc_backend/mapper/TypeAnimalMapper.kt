package com.oki.gestion_parc_backend.dto

import com.oki.gestion_parc_backend.model.TypeAnimal
object TypeAnimalMapper {
    fun toEntity(dto: TypeAnimalDTO) = TypeAnimal(
        nom = dto.nom,
        prefix = dto.prefix
    )

    fun toResponseDTO(entity: TypeAnimal) = TypeAnimalResponseDTO(
        id = entity.id,
        nom = entity.nom,
        prefix = entity.prefix
    )
}



