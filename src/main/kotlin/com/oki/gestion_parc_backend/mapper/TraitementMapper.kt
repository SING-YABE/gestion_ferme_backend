package com.oki.gestion_parc_backend.mapper

import com.oki.gestion_parc_backend.dto.TraitementDTO
import com.oki.gestion_parc_backend.model.Animal
import com.oki.gestion_parc_backend.model.Traitement
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object TraitementMapper {

    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    fun parseDate(date: String): LocalDate = LocalDate.parse(date, formatter)
    fun formatDate(date: LocalDate): String = date.format(formatter)

    fun toEntity(dto: TraitementDTO, animal: Animal): Traitement {
        return Traitement(
            id = dto.id ?: 0,
            date = parseDate(dto.date),
            animal = animal,
            traitement = dto.traitement,
            motif = dto.motif,
            cout = dto.cout,
            veterinaire = dto.veterinaire,
            observations = dto.observations
        )
    }

    fun toDTO(entity: Traitement): TraitementDTO {
        return TraitementDTO(
            id = entity.id,
            date = formatDate(entity.date),
            animalId = entity.animal.id,
            traitement = entity.traitement,
            motif = entity.motif,
            cout = entity.cout,
            veterinaire = entity.veterinaire,
            observations = entity.observations
        )
    }
}
