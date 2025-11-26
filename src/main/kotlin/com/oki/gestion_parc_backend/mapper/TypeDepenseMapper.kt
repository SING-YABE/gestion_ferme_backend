package com.oki.gestion_parc_backend.mapper

import com.oki.gestion_parc_backend.dto.TypeAnimalDTO
import com.oki.gestion_parc_backend.dto.TypeAnimalResponseDTO
import com.oki.gestion_parc_backend.dto.TypeDepenseDTO
import com.oki.gestion_parc_backend.dto.TypeDepenseResponseDTO
import com.oki.gestion_parc_backend.model.TypeDepense

object TypeDepenseMapper {
    fun toEntity(dto: TypeDepenseDTO) = TypeDepense(nom = dto.nom)
    fun toResponseDTO(entity: TypeDepense) = TypeDepenseResponseDTO(entity.id, entity.nom)
}
