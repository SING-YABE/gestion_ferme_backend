package com.oki.gestion_parc_backend.dto

import com.oki.gestion_parc_backend.model.TypeAnimal
object TypeAnimalMapper {
    fun toEntity(dto: TypeAnimalDTO): TypeAnimal {
        val generatedPrefix = dto.nom.take(1).uppercase() // prend la première lettre du nom
        return TypeAnimal(
            nom = dto.nom,
            prefix = generatedPrefix,
            description = dto.description
        )
    }

    fun toResponseDTO(entity: TypeAnimal) = TypeAnimalResponseDTO(
        id = entity.id,
        nom = entity.nom,
        prefix = entity.prefix,
        description = entity.description
    )
}



