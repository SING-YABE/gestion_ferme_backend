package com.oki.gestion_parc_backend.mapper


import com.oki.gestion_parc_backend.dto.DepenseDTO
import com.oki.gestion_parc_backend.model.Depense
import com.oki.gestion_parc_backend.model.TypeDepense
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DepenseMapper {

    val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    fun toEntity(dto: DepenseDTO, typeDepense: TypeDepense): Depense {
        return Depense(
            id = dto.id ?: 0,
            date = LocalDate.parse(dto.date, formatter),
            typeDepense = typeDepense,
            description = dto.description,
            montant = dto.montant,
            modePaiement = dto.modePaiement,
            observations = dto.observations
        )
    }

    fun toDTO(entity: Depense): DepenseDTO {
        return DepenseDTO(
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
