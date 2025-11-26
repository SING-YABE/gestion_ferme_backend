package com.oki.gestion_parc_backend.mapper

import com.oki.gestion_parc_backend.dto.FournissuerDTO
import com.oki.gestion_parc_backend.model.Fournisseur

object FournisseurMapper {

    fun toEntity(dto: FournissuerDTO) = Fournisseur(
        id = dto.id ?: 0,
        nom = dto.nom,
        contact = dto.contact
    )

    fun toDto(entity: Fournisseur) = FournissuerDTO(
        id = entity.id,
        nom = entity.nom,
        contact = entity.contact
    )
}
