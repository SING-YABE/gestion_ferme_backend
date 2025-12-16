package com.oki.gestion_parc_backend.dto

import com.oki.gestion_parc_backend.model.Batiment
import com.oki.gestion_parc_backend.model.TypeVente

object TypeVenteMapper {
    fun toEntity(dto: TypeVenteDTO) = TypeVente(nom = dto.nom)
    fun toResponseDTO(entity: TypeVente) = TypeVenteResponseDTO(entity.id, entity.nom)
}
