package com.oki.gestion_parc_backend.dto

import com.oki.gestion_parc_backend.model.Batiment

object BatimentMapper {
    fun toEntity(dto: BatimentDTO) = Batiment(nom = dto.nom, localisation = dto.localisation)
    fun toResponseDTO(entity: Batiment) = BatimentResponseDTO(entity.id, entity.nom, entity.localisation)
}
