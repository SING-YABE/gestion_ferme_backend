package com.oki.gestion_parc_backend.service.impl

import com.oki.gestion_parc_backend.dto.AnimalDTO
import com.oki.gestion_parc_backend.dto.AnimalMapper
import com.oki.gestion_parc_backend.dto.AnimalResponseDTO
import com.oki.gestion_parc_backend.model.Animal
import com.oki.gestion_parc_backend.repository.AnimalRepository
import com.oki.gestion_parc_backend.repository.BoxRepository
import com.oki.gestion_parc_backend.repository.EtatSanteRepository
import com.oki.gestion_parc_backend.repository.TypeAnimalRepository
import com.oki.gestion_parc_backend.service.AnimalService
import com.oki.gestion_parc_backend.service.SubscriptionService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Service
class AnimalServiceImpl(
    private val animalRepository: AnimalRepository,
    private val typeAnimalRepository: TypeAnimalRepository,
    private val boxRepository: BoxRepository,
    private val etatSanteRepository: EtatSanteRepository,
    private val subscriptionService: SubscriptionService
) : AnimalService {

    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    @Transactional
    override fun creerAnimal(dto: AnimalDTO): AnimalResponseDTO {

        // ── Vérification limite SaaS ────────────────────────────────────────────
        if (subscriptionService.isAnimalLimitAtteinte()) {
            throw IllegalStateException(
                "ANIMAL_LIMIT_REACHED: Limite d'animaux atteinte. Passez à un plan supérieur."
            )
        }

        val typeAnimal = typeAnimalRepository.findById(dto.typeAnimalId)
            .orElseThrow { IllegalArgumentException("TypeAnimal avec id ${dto.typeAnimalId} non trouvé") }

        val box = boxRepository.findById(dto.boxId)
            .orElseThrow { IllegalArgumentException("Box avec id ${dto.boxId} non trouvée") }

        // Vérifier capacité box à la création aussi
        val occupation = animalRepository.countByBoxAndVenduFalse(box)
        if (occupation >= box.capaciteMax) {
            throw IllegalStateException(
                "La box ${box.code} est pleine (${occupation}/${box.capaciteMax})"
            )
        }

        val etatSante = etatSanteRepository.findById(dto.etatSanteId)
            .orElseThrow { IllegalArgumentException("EtatSante avec id ${dto.etatSanteId} non trouvé") }

        val prefix = typeAnimal.prefix.uppercase()
        val count = animalRepository.countByTypeAnimal(typeAnimal)
        val codeAnimal = "$prefix${count + 1}"

        val animal = Animal(
            codeAnimal   = codeAnimal,
            typeAnimal   = typeAnimal,
            dateEntree   = LocalDate.parse(dto.dateEntree, formatter),
            dateNaissance = dto.dateNaissance?.let { LocalDate.parse(it, formatter) },
            poidsInitial = dto.poidsInitial,
            etatSante    = etatSante,
            box          = box,
            observations = dto.observations
        )

        return AnimalMapper.toResponseDTO(animalRepository.save(animal))
    }

    override fun getAllAnimaux(): List<AnimalResponseDTO> =
        animalRepository.findByVenduFalse().map { AnimalMapper.toResponseDTO(it) }

    override fun getAnimalById(id: Long): AnimalResponseDTO =
        animalRepository.findById(id).map { AnimalMapper.toResponseDTO(it) }
            .orElseThrow { IllegalArgumentException("Animal avec id $id non trouvé") }

    @Transactional
    override fun updateAnimal(id: Long, dto: AnimalDTO): AnimalResponseDTO {
        val animal = animalRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Animal avec id $id non trouvé") }

        val typeAnimal = typeAnimalRepository.findById(dto.typeAnimalId)
            .orElseThrow { IllegalArgumentException("TypeAnimal ${dto.typeAnimalId} non trouvé") }

        val box = boxRepository.findById(dto.boxId)
            .orElseThrow { IllegalArgumentException("Box avec id ${dto.boxId} non trouvée") }

        val etatSante = etatSanteRepository.findById(dto.etatSanteId)
            .orElseThrow { IllegalArgumentException("EtatSante avec id ${dto.etatSanteId} non trouvé") }

        val updated = animal.copy(
            typeAnimal    = typeAnimal,
            box           = box,
            etatSante     = etatSante,
            dateEntree    = LocalDate.parse(dto.dateEntree, formatter),
            dateNaissance = dto.dateNaissance?.let { LocalDate.parse(it, formatter) },
            poidsInitial  = dto.poidsInitial,
            observations  = dto.observations
        )

        return AnimalMapper.toResponseDTO(animalRepository.save(updated))
    }

    @Transactional
    override fun deleteAnimal(id: Long) {
        if (!animalRepository.existsById(id)) throw IllegalArgumentException("Animal avec id $id non trouvé")
        animalRepository.deleteById(id)
    }

    override fun countAllAnimals(): Long = animalRepository.count()

    override fun countAnimalsByType(): List<Map<String, Any>> {
        return typeAnimalRepository.findAll().map { type ->
            mapOf(
                "id" to type.id,
                "nom" to type.nom,
                "prefix" to type.prefix,
                "total" to animalRepository.countByTypeAnimal(type)
            )
        }
    }
}

