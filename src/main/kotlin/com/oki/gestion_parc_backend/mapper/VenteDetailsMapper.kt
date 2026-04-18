package com.oki.gestion_parc_backend.mapper

import com.oki.gestion_parc_backend.dto.AnimalVenduResponseDTO
import com.oki.gestion_parc_backend.dto.VenteDetailResponseDTO
import com.oki.gestion_parc_backend.model.Vente
import com.oki.gestion_parc_backend.model.VenteAnimal
import java.time.format.DateTimeFormatter

object VenteDetailsMapper {
    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    fun toResponseDTO(vente: Vente): VenteDetailResponseDTO {
        return VenteDetailResponseDTO(
            id = vente.id,
            dateVente = vente.dateVente.format(formatter),
            dateEnlevement = vente.dateEnlevement?.format(formatter),
            dateEnlevementAuPlusTard = vente.dateEnlevementAuPlusTard?.format(formatter),
            client = vente.client,
            montantTotal = vente.montantTotal,
            poidsTotal = vente.poidsTotal,
            animaux = vente.animaux.map { toAnimalVenduResponseDTO(it) }
        )
    }

    private fun toAnimalVenduResponseDTO(venteAnimal: VenteAnimal): AnimalVenduResponseDTO {
        return AnimalVenduResponseDTO(
            id = venteAnimal.id,
            animalCode = venteAnimal.animal.codeAnimal,
            typeVenteNom = venteAnimal.typeVente.nom,
            modeVente = venteAnimal.modeVente,
            poidsVente = venteAnimal.poidsVente,
            prixUnitaire = venteAnimal.prixUnitaire,
            prixNegocie = venteAnimal.prixNegocie,
            montantTotal = venteAnimal.montantTotal
        )
    }
}