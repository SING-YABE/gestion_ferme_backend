package com.oki.gestion_parc_backend.mapper

import com.oki.gestion_parc_backend.dto.BoxDTO
import com.oki.gestion_parc_backend.dto.BoxResponseDTO
import com.oki.gestion_parc_backend.model.Batiment
import com.oki.gestion_parc_backend.model.Box


object BoxMapper {

    fun toEntity(dto: BoxDTO, batiment: Batiment, code: String) = Box(
        numero = dto.numero,
        code = code,
        capaciteMax = dto.capaciteMax,
        batiment = batiment
    )

    fun toResponseDTO(entity: Box, occupationActuelle: Int) = BoxResponseDTO(
        id = entity.id,
        numero = entity.numero,
        code = entity.code,
        capaciteMax = entity.capaciteMax,
        batimentId = entity.batiment.id,
        batimentNom = entity.batiment.nom,
        occupationActuelle = occupationActuelle
    )
}