package com.oki.gestion_parc_backend.service.impl

import com.oki.gestion_parc_backend.dto.BatimentDTO
import com.oki.gestion_parc_backend.dto.BatimentMapper
import com.oki.gestion_parc_backend.dto.BatimentResponseDTO
import com.oki.gestion_parc_backend.repository.BatimentRepository
import com.oki.gestion_parc_backend.service.BatimentService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BatimentServiceImpl(private val repository: BatimentRepository) : BatimentService {

    @Transactional
    override fun creerBatiment(dto: BatimentDTO): BatimentResponseDTO {
        val saved = repository.save(BatimentMapper.toEntity(dto))
        return BatimentMapper.toResponseDTO(saved)
    }

    override fun getAllBatiments(): List<BatimentResponseDTO> =
        repository.findAll().map { BatimentMapper.toResponseDTO(it) }

    override fun getBatimentById(id: Long): BatimentResponseDTO =
        repository.findById(id).map { BatimentMapper.toResponseDTO(it) }
            .orElseThrow { IllegalArgumentException("Batiment avec id $id non trouvé") }

    @Transactional
    override fun updateBatiment(id: Long, dto: BatimentDTO): BatimentResponseDTO {
        val batiment = repository.findById(id).orElseThrow {
            IllegalArgumentException("Batiment avec id $id non trouvé")
        }
        val updated = batiment.copy(nom = dto.nom, localisation = dto.localisation)
        return BatimentMapper.toResponseDTO(repository.save(updated))
    }

    @Transactional
    override fun deleteBatiment(id: Long) {
        if (!repository.existsById(id)) throw IllegalArgumentException("Batiment avec id $id non trouvé")
        repository.deleteById(id)
    }
}
