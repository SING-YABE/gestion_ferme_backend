package com.oki.gestion_parc_backend.service.impl

import com.oki.gestion_parc_backend.dto.TraitementDTO
import com.oki.gestion_parc_backend.mapper.TraitementMapper
import com.oki.gestion_parc_backend.model.Animal
import com.oki.gestion_parc_backend.repository.AnimalRepository
import com.oki.gestion_parc_backend.repository.TraitementRepository
import com.oki.gestion_parc_backend.service.TraitementService
import org.springframework.stereotype.Service

@Service
class TraitementServiceImpl(
    private val repo: TraitementRepository,
    private val animalRepo: AnimalRepository
) : TraitementService {

    override fun create(dto: TraitementDTO): TraitementDTO {
        val animal: Animal = animalRepo.findById(dto.animalId)
            .orElseThrow { IllegalArgumentException("Animal avec id ${dto.animalId} introuvable") }
        val entity = TraitementMapper.toEntity(dto, animal)
        return TraitementMapper.toDTO(repo.save(entity))
    }

    override fun list(): List<TraitementDTO> =
        repo.findAll().map { TraitementMapper.toDTO(it) }

    override fun update(id: Long, dto: TraitementDTO): TraitementDTO {
        val existing = repo.findById(id)
            .orElseThrow { IllegalArgumentException("Traitement avec id $id introuvable") }

        val animal: Animal = animalRepo.findById(dto.animalId)
            .orElseThrow { IllegalArgumentException("Animal avec id ${dto.animalId} introuvable") }

        val updated = existing.copy(
            date = TraitementMapper.parseDate(dto.date),
            animal = animal,
            traitement = dto.traitement,
            motif = dto.motif,
            cout = dto.cout,
            veterinaire = dto.veterinaire,
            observations = dto.observations
        )

        return TraitementMapper.toDTO(repo.save(updated))
    }

    override fun delete(id: Long) = repo.deleteById(id)
}
