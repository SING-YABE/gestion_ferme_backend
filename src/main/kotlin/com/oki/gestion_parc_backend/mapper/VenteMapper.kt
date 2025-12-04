package com.oki.gestion_parc_backend.mapper

import com.oki.gestion_parc_backend.dto.VenteDTO
import com.oki.gestion_parc_backend.dto.VenteResponseDTO
import com.oki.gestion_parc_backend.model.Vente
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object VenteMapper {

    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    fun toEntity(dto: VenteDTO): Vente {
        val date = LocalDate.parse(dto.dateVente, formatter)

        val montantTotal = dto.quantite * dto.prixUnitaire

        return Vente(
            dateVente = date,
            type = dto.type,
            quantite = dto.quantite,
            poidsTotal = dto.poidsTotal,
            prixUnitaire = dto.prixUnitaire,
            montantTotal = montantTotal,
            client = dto.client
        )
    }

    fun toResponseDTO(v: Vente) = VenteResponseDTO(
        id = v.id,
        dateVente = v.dateVente.format(formatter),
        type = v.type,
        quantite = v.quantite,
        poidsTotal = v.poidsTotal,
        prixUnitaire = v.prixUnitaire,
        montantTotal = v.montantTotal,
        client = v.client
    )
}
