package com.oki.gestion_parc_backend.service.impl
import com.oki.gestion_parc_backend.dto.VentesEvolutionDTO
import com.oki.gestion_parc_backend.repository.VenteRepository
import com.oki.gestion_parc_backend.service.VenteService
import org.springframework.stereotype.Service
import kotlin.collections.get

import com.oki.gestion_parc_backend.dto.VenteCreateDTO
import com.oki.gestion_parc_backend.dto.VenteDetailResponseDTO
import com.oki.gestion_parc_backend.mapper.VenteDetailsMapper
import com.oki.gestion_parc_backend.model.Vente
import com.oki.gestion_parc_backend.model.VenteAnimal
import com.oki.gestion_parc_backend.repository.AnimalRepository
import com.oki.gestion_parc_backend.repository.TypeVenteRepository

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
        // 1. Valider que tous les animaux existent et ne sont pas déjà vendus
        val animaux = dto.animaux.map { animalDto ->
            val animal = animalRepository.findByCodeAnimal(animalDto.codeAnimal)
                .orElseThrow { IllegalArgumentException("Animal avec code ${animalDto.codeAnimal} non trouvé") }

            if (animal.vendu) {
                throw IllegalArgumentException("L'animal ${animal.codeAnimal} est déjà vendu")
            }

            animal
        }

        // 2. Créer la vente principale
        val dateVente = LocalDate.parse(dto.dateVente, formatter)
        val dateEnlevement = LocalDate.parse(dto.dateEnlevement, formatter)
        val dateEnlevementAuPlusTard =
            LocalDate.parse(dto.dateEnlevementAuPlusTard, formatter)


        var vente = Vente(
            dateVente = dateVente,
            dateEnlevement = dateEnlevement,
            dateEnlevementAuPlusTard = dateEnlevementAuPlusTard,
            client = dto.client,
            poidsTotal = 0.0,    // ✅ Initialisé
            montantTotal = 0.0
        )

        vente = venteRepository.save(vente)

        // 3. Créer les VenteAnimal et calculer les totaux
        var montantTotal = 0.0
        var poidsTotal = 0.0  // ✅ Ajouter cette ligne

        val venteAnimaux = dto.animaux.mapIndexed { index, animalDto ->
            val animal = animaux[index]
            val typeVente = typeVenteRepository.findById(animalDto.typeVenteId)
                .orElseThrow { IllegalArgumentException("TypeVente avec id ${animalDto.typeVenteId} non trouvé") }

            val montant = animalDto.poidsVente * animalDto.prixUnitaire
            montantTotal += montant
            poidsTotal += animalDto.poidsVente  // ✅ Cumuler le poids

            VenteAnimal(
                vente = vente,
                animal = animal,
                typeVente = typeVente,
                poidsVente = animalDto.poidsVente,
                prixUnitaire = animalDto.prixUnitaire,
                montantTotal = montant
            )
        }
        vente.montantTotal = montantTotal
        vente.poidsTotal = poidsTotal
        vente.animaux.clear()
        vente.animaux.addAll(venteAnimaux)

        venteRepository.save(vente)

        vente = venteRepository.save(vente)

        // 5. Marquer les animaux comme vendus
        animaux.forEach { animal ->
            animalRepository.save(animal.copy(vendu = true))
        }

        return VenteDetailsMapper.toResponseDTO(vente)
    }
    override fun getAllVentes(): List<VenteDetailResponseDTO> {
        return venteRepository.findAll().map { VenteDetailsMapper.toResponseDTO(it) }
    }

    override fun getVenteById(id: Long): VenteDetailResponseDTO {
        val vente = venteRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Vente avec id $id non trouvée") }
        return VenteDetailsMapper.toResponseDTO(vente)
    }

    @Transactional
    override fun deleteVente(id: Long) {
        val vente = venteRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Vente avec id $id non trouvée") }

        // Remettre les animaux comme non vendus
        vente.animaux.forEach { venteAnimal ->
            val animal = venteAnimal.animal
            animalRepository.save(animal.copy(vendu = false))
        }

        venteRepository.deleteById(id)
    }

    override fun getVenteEntityById(id: Long): Vente {
        return venteRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Vente avec id $id non trouvée") }
    }
}
