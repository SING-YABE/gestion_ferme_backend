package com.oki.gestion_parc_backend.mapper
import com.oki.gestion_parc_backend.dto.ChargeDiversesDto
import com.oki.gestion_parc_backend.model.ChargeDiverses
import com.oki.gestion_parc_backend.model.TypeDepense
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object ChargeDiversesMapper {

    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    fun toEntity(dto: ChargeDiversesDto, typeDepense: TypeDepense): ChargeDiverses {
        return ChargeDiverses(
            id = dto.id ?: 0,
            date = java.time.Instant.parse(dto.date)
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate(),

            typeDepense = typeDepense,
            description = dto.description,
            montant = dto.montant,
            modePaiement = dto.modePaiement,
            observations = dto.observations
        )
    }

    fun toDto(entity: ChargeDiverses): ChargeDiversesDto {
        return ChargeDiversesDto(
            id = entity.id,
            date = entity.date.format(formatter),
            typeDepenseId = entity.typeDepense.id,
            description = entity.description,
            montant = entity.montant,
            modePaiement = entity.modePaiement,
            observations = entity.observations
        )
    }
}
