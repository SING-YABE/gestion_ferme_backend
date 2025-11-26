package com.oki.gestion_parc_backend.dto

import com.oki.gestion_parc_backend.model.EtatSante

object EtatSanteMapper {
    fun toEntity(dto: EtatSanteDTO) = EtatSante(description = dto.description)
    fun toResponseDTO(entity: EtatSante) = EtatSanteResponseDTO(entity.id, entity.description)
}
