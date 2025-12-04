package com.oki.gestion_parc_backend.service.impl

import com.oki.gestion_parc_backend.dto.SoinAnimalDTO
import com.oki.gestion_parc_backend.dto.SoinAnimalResponseDTO
import com.oki.gestion_parc_backend.mapper.SoinAnimalMapper
import com.oki.gestion_parc_backend.model.SoinAnimal
import com.oki.gestion_parc_backend.repository.AnimalRepository
import com.oki.gestion_parc_backend.repository.SoinAnimalRepository
import com.oki.gestion_parc_backend.service.SoinAnimalService
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class SoinAnimalServiceImpl(
    private val soinRepo: SoinAnimalRepository,
    private val animalRepo: AnimalRepository
) : SoinAnimalService {

    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    override fun create(dto: SoinAnimalDTO): SoinAnimalResponseDTO {

        val isCollectif = dto.codeAnimal.isNullOrBlank()

        val animal = if (!isCollectif) {
            animalRepo.findByCodeAnimal(dto.codeAnimal!!)
                .orElseThrow { IllegalArgumentException("Animal introuvable : ${dto.codeAnimal}") }
        } else null

        val soin = SoinAnimal(
            animal = animal,
            dateSoin = LocalDate.parse(dto.dateSoin, formatter),
            motif = dto.motif,
            traitement = dto.traitement,
            cout = dto.cout,
            veterinaire = dto.veterinaire,
            observations = dto.observations,
            soinCollectif = isCollectif
        )

        return SoinAnimalMapper.toResponseDTO(soinRepo.save(soin))
    }

    override fun getAll(): List<SoinAnimalResponseDTO> =
        soinRepo.findAll().map { SoinAnimalMapper.toResponseDTO(it) }

    override fun getByAnimal(codeAnimal: String): List<SoinAnimalResponseDTO> {
        val animal = animalRepo.findByCodeAnimal(codeAnimal)
            .orElseThrow { IllegalArgumentException("Animal introuvable : $codeAnimal") }

        return soinRepo.findByAnimal(animal).map { SoinAnimalMapper.toResponseDTO(it) }
    }
}
