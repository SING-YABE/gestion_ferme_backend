package com.oki.gestion_parc_backend.mapper

import com.oki.gestion_parc_backend.dto.TypeAlimentDto
import com.oki.gestion_parc_backend.model.TypeAliment

object TypeAlimentMapper {

    fun toEntity(dto: TypeAlimentDto) =
        TypeAliment(
            id = dto.id ?: 0,
            libelle = dto.libelle
        )

    fun toDto(entity: TypeAliment) =
        TypeAlimentDto(
            id = entity.id,
            libelle = entity.libelle
        )
}
