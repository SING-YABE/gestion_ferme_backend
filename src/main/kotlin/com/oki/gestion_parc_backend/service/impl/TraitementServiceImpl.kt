package com.oki.gestion_parc_backend.service.impl

import com.oki.gestion_parc_backend.dto.BatimentDTO
import com.oki.gestion_parc_backend.dto.BatimentMapper
import com.oki.gestion_parc_backend.dto.BatimentResponseDTO
import com.oki.gestion_parc_backend.dto.TraitementDTO
import com.oki.gestion_parc_backend.dto.TraitementResponseDTO
import com.oki.gestion_parc_backend.mapper.TraitementMapper
import com.oki.gestion_parc_backend.model.Animal
import com.oki.gestion_parc_backend.repository.AnimalRepository
import com.oki.gestion_parc_backend.repository.TraitementRepository
import com.oki.gestion_parc_backend.service.TraitementService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TraitementServiceImpl(
    private val repo: TraitementRepository,
    private val animalRepo: AnimalRepository
) : TraitementService {

    override fun create(dto: TraitementDTO): TraitementResponseDTO {
        val saved = repo.save(TraitementMapper.toEntity(dto))
        return TraitementMapper.toResponseDTO(saved)
    }

    override fun list(): List<TraitementResponseDTO> =
        repo.findAll().map { TraitementMapper.toResponseDTO(it) }

    override fun getTraitementById(id: Long): TraitementResponseDTO =
        repo.findById(id).map { TraitementMapper.toResponseDTO(it) }
            .orElseThrow { IllegalArgumentException("TTT avec id $id non trouvé") }

    @Transactional
    override fun update(id: Long, dto: TraitementDTO): TraitementResponseDTO {
        val traitement = repo.findById(id).orElseThrow {
            IllegalArgumentException("TTT avec id $id non trouvé")
        }
        val updated = traitement.copy(nom = dto.nom, description = dto.description)
        return TraitementMapper.toResponseDTO(repo.save(updated))
    }

    @Transactional
    override fun delete(id: Long) {
        if (!repo.existsById(id)) throw IllegalArgumentException("TTT avec id $id non trouvé")
        repo.deleteById(id)
    }



}
