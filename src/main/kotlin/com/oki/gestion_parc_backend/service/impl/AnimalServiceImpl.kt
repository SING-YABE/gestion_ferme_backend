package com.oki.gestion_parc_backend.service.impl

import com.oki.gestion_parc_backend.dto.AnimalDTO
import com.oki.gestion_parc_backend.dto.AnimalMapper
import com.oki.gestion_parc_backend.dto.AnimalResponseDTO
import com.oki.gestion_parc_backend.model.Animal
import com.oki.gestion_parc_backend.repository.AnimalRepository
import com.oki.gestion_parc_backend.repository.BatimentRepository
import com.oki.gestion_parc_backend.repository.EtatSanteRepository
import com.oki.gestion_parc_backend.repository.TypeAnimalRepository
import com.oki.gestion_parc_backend.service.AnimalService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class AnimalServiceImpl(
    private val animalRepository: AnimalRepository,
    private val typeAnimalRepository: TypeAnimalRepository,
    private val batimentRepository: BatimentRepository,
    private val etatSanteRepository: EtatSanteRepository
) : AnimalService {

    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    @Transactional

    override fun creerAnimal(dto: AnimalDTO): AnimalResponseDTO {

        val typeAnimal = typeAnimalRepository.findById(dto.typeAnimalId)
            .orElseThrow { IllegalArgumentException("TypeAnimal avec id ${dto.typeAnimalId} non trouvé") }

        val batiment = batimentRepository.findById(dto.batimentId)
            .orElseThrow { IllegalArgumentException("Batiment avec id ${dto.batimentId} non trouvé") }

        val etatSante = etatSanteRepository.findById(dto.etatSanteId)
            .orElseThrow { IllegalArgumentException("EtatSante avec id ${dto.etatSanteId} non trouvé") }

        // 1️ RÉCUPÉRER LE PRÉFIXE
        val prefix = typeAnimal.prefix.uppercase()

        // COMPTER COMBIEN D’ANIMAUX EXISTENT DÉJÀ POUR CE TYPE
        val count = animalRepository.countByTypeAnimal(typeAnimal)

        // 3️GÉNÉRER LE CODE
        val codeAnimal = "$prefix${count + 1}"

        val dateEntree = LocalDate.parse(dto.dateEntree, formatter)

        val animal = Animal(
            codeAnimal = codeAnimal,
            typeAnimal = typeAnimal,
            dateEntree = dateEntree,
            poidsInitial = dto.poidsInitial,
            etatSante = etatSante,
            batiment = batiment,
            observations = dto.observations
        )

        val saved = animalRepository.save(animal)
        return AnimalMapper.toResponseDTO(saved)
    }

    override fun getAllAnimaux(): List<AnimalResponseDTO> =
        animalRepository.findByVenduFalse().map { AnimalMapper.toResponseDTO(it) }

    override fun getAnimalById(id: Long): AnimalResponseDTO =
        animalRepository.findById(id).map { AnimalMapper.toResponseDTO(it) }
            .orElseThrow { IllegalArgumentException("Animal avec id $id non trouvé") }

    @Transactional
    override fun updateAnimal(id: Long, dto: AnimalDTO): AnimalResponseDTO {
        val animal = animalRepository.findById(id).orElseThrow {
            IllegalArgumentException("Animal avec id $id non trouvé")
        }

        val typeAnimal = typeAnimalRepository.findById(dto.typeAnimalId)
            .orElseThrow { IllegalArgumentException("Type animal ${dto.typeAnimalId} non trouvé") }

        val batiment = batimentRepository.findById(dto.batimentId)
            .orElseThrow { IllegalArgumentException("Batiment avec id ${dto.batimentId} non trouvé") }

        val etatSante = etatSanteRepository.findById(dto.etatSanteId)
            .orElseThrow { IllegalArgumentException("EtatSante avec id ${dto.etatSanteId} non trouvé") }

        val updated = animal.copy(
            typeAnimal = typeAnimal,
            batiment = batiment,
            etatSante = etatSante,
            dateEntree = LocalDate.parse(dto.dateEntree, formatter),
            poidsInitial = dto.poidsInitial,
            observations = dto.observations
        )

        return AnimalMapper.toResponseDTO(animalRepository.save(updated))
    }

    @Transactional
    override fun deleteAnimal(id: Long) {
        if (!animalRepository.existsById(id)) throw IllegalArgumentException("Animal avec id $id non trouvé")
        animalRepository.deleteById(id)
    }
    override fun countAllAnimals(): Long {
        return animalRepository.count()
    }

    override fun countAnimalsByType(): List<Map<String, Any>> {
        val types = typeAnimalRepository.findAll()

        return types.map { type ->
            mapOf(
                "id" to type.id,
                "nom" to type.nom,
                "prefix" to type.prefix,
                "total" to animalRepository.countByTypeAnimal(type)
            )
        }
    }

}

