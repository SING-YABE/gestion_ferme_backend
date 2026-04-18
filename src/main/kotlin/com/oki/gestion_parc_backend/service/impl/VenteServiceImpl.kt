package com.oki.gestion_parc_backend.service.impl

import com.oki.gestion_parc_backend.dto.VenteCreateDTO
import com.oki.gestion_parc_backend.dto.VenteDetailResponseDTO
import com.oki.gestion_parc_backend.mapper.VenteDetailsMapper
import com.oki.gestion_parc_backend.model.ModeVente
import com.oki.gestion_parc_backend.model.Vente
import com.oki.gestion_parc_backend.model.VenteAnimal
import com.oki.gestion_parc_backend.repository.AnimalRepository
import com.oki.gestion_parc_backend.repository.TypeVenteRepository
import com.oki.gestion_parc_backend.repository.VenteRepository
import com.oki.gestion_parc_backend.service.VenteService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class VenteServiceImpl(
    private val venteRepository: VenteRepository,
    private val animalRepository: AnimalRepository,
    private val typeVenteRepository: TypeVenteRepository
) : VenteService {

    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    @Transactional
    override fun creerVente(dto: VenteCreateDTO): VenteDetailResponseDTO {

        // 1. Valider animaux
        val animaux = dto.animaux.map { animalDto ->
            val animal = animalRepository.findByCodeAnimal(animalDto.codeAnimal)
                .orElseThrow { IllegalArgumentException("Animal ${animalDto.codeAnimal} non trouvé") }
            if (animal.vendu) throw IllegalArgumentException("L'animal ${animal.codeAnimal} est déjà vendu")
            animal
        }

        // 2. Valider les champs selon le mode de vente
        dto.animaux.forEach { animalDto ->
            when (animalDto.modeVente) {
                ModeVente.AU_POIDS -> {
                    requireNotNull(animalDto.poidsVente) {
                        "poidsVente requis pour une vente AU_POIDS (animal: ${animalDto.codeAnimal})"
                    }
                    requireNotNull(animalDto.prixUnitaire) {
                        "prixUnitaire requis pour une vente AU_POIDS (animal: ${animalDto.codeAnimal})"
                    }
                }
                ModeVente.SANS_PESEE -> {
                    requireNotNull(animalDto.prixNegocie) {
                        "prixNegocie requis pour une vente SANS_PESEE (animal: ${animalDto.codeAnimal})"
                    }
                }
            }
        }

        // 3. Créer la vente principale
        var vente = Vente(
            dateVente = LocalDate.parse(dto.dateVente, formatter),
            dateEnlevement = LocalDate.parse(dto.dateEnlevement, formatter),
            dateEnlevementAuPlusTard = LocalDate.parse(dto.dateEnlevementAuPlusTard, formatter),
            client = dto.client,
            poidsTotal = 0.0,
            montantTotal = 0.0
        )
        vente = venteRepository.save(vente)

        // 4. Créer VenteAnimal et calculer totaux
        var montantTotal = 0.0
        var poidsTotal = 0.0

        val venteAnimaux = dto.animaux.mapIndexed { index, animalDto ->
            val animal = animaux[index]
            val typeVente = typeVenteRepository.findById(animalDto.typeVenteId)
                .orElseThrow { IllegalArgumentException("TypeVente ${animalDto.typeVenteId} non trouvé") }

            val montant: Double
            val poids: Double

            when (animalDto.modeVente) {
                ModeVente.AU_POIDS -> {
                    poids = animalDto.poidsVente!!
                    montant = poids * animalDto.prixUnitaire!!
                    poidsTotal += poids
                }
                ModeVente.SANS_PESEE -> {
                    poids = 0.0 // Pas de poids
                    montant = animalDto.prixNegocie!!
                }
            }

            montantTotal += montant

            VenteAnimal(
                vente = vente,
                animal = animal,
                typeVente = typeVente,
                modeVente = animalDto.modeVente,
                poidsVente = animalDto.poidsVente,
                prixUnitaire = animalDto.prixUnitaire,
                prixNegocie = animalDto.prixNegocie,
                montantTotal = montant
            )
        }

        vente.montantTotal = montantTotal
        vente.poidsTotal = poidsTotal
        vente.animaux.clear()
        vente.animaux.addAll(venteAnimaux)
        vente = venteRepository.save(vente)

        // 5. Marquer les animaux comme vendus
        animaux.forEach { animalRepository.save(it.copy(vendu = true)) }

        return VenteDetailsMapper.toResponseDTO(vente)
    }

    override fun getAllVentes(): List<VenteDetailResponseDTO> =
        venteRepository.findAll().map { VenteDetailsMapper.toResponseDTO(it) }

    override fun getVenteById(id: Long): VenteDetailResponseDTO =
        venteRepository.findById(id)
            .map { VenteDetailsMapper.toResponseDTO(it) }
            .orElseThrow { IllegalArgumentException("Vente avec id $id non trouvée") }

    @Transactional
    override fun deleteVente(id: Long) {
        val vente = venteRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Vente avec id $id non trouvée") }
        vente.animaux.forEach { animalRepository.save(it.animal.copy(vendu = false)) }
        venteRepository.deleteById(id)
    }

    override fun getVenteEntityById(id: Long): Vente =
        venteRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Vente avec id $id non trouvée") }
}