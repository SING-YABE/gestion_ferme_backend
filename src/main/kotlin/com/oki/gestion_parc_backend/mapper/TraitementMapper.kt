package com.oki.gestion_parc_backend.mapper

import com.oki.gestion_parc_backend.dto.BatimentDTO
import com.oki.gestion_parc_backend.dto.BatimentResponseDTO
import com.oki.gestion_parc_backend.dto.TraitementDTO
import com.oki.gestion_parc_backend.dto.TraitementResponseDTO
import com.oki.gestion_parc_backend.model.Animal
import com.oki.gestion_parc_backend.model.Batiment
import com.oki.gestion_parc_backend.model.Traitement
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object TraitementMapper {

    fun toEntity(dto: TraitementDTO) = Traitement(nom = dto.nom, description = dto.description)
    fun toResponseDTO(entity: Traitement) = TraitementResponseDTO(entity.id, entity.nom, entity.description)
}
