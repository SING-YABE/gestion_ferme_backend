package com.oki.gestion_parc_backend.service.impl

import com.oki.gestion_parc_backend.dto.EtatSanteDTO
import com.oki.gestion_parc_backend.dto.EtatSanteMapper
import com.oki.gestion_parc_backend.dto.EtatSanteResponseDTO
import com.oki.gestion_parc_backend.model.EtatSante
import com.oki.gestion_parc_backend.repository.EtatSanteRepository
import com.oki.gestion_parc_backend.repository.TypeAnimalRepository
import com.oki.gestion_parc_backend.service.EtatSanteService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class EtatSanteServiceImpl(
    private val repository: EtatSanteRepository,
    private val typeAnimalRepository: TypeAnimalRepository
) : EtatSanteService {


    @Transactional
    override fun creerEtat(dto: EtatSanteDTO): EtatSanteResponseDTO {
        val typeAnimal = typeAnimalRepository.findById(dto.typeAnimalId)
            .orElseThrow { IllegalArgumentException("TypeAnimal non trouvé") }

        val entity = EtatSante(
            description = dto.description,
            typeAnimal = typeAnimal  // ← AJOUT
        )
        val saved = repository.save(entity)
        return EtatSanteMapper.toResponseDTO(saved)
    }

    override fun getAllEtats(): List<EtatSanteResponseDTO> =
        repository.findAll().map { EtatSanteMapper.toResponseDTO(it) }

    override fun getEtatById(id: Long): EtatSanteResponseDTO =
        repository.findById(id).map { EtatSanteMapper.toResponseDTO(it) }
            .orElseThrow { IllegalArgumentException("EtatSante avec id $id non trouvé") }

    @Transactional
    override fun updateEtat(id: Long, dto: EtatSanteDTO): EtatSanteResponseDTO {
        val etat = repository.findById(id).orElseThrow {
            IllegalArgumentException("EtatSante avec id $id non trouvé")
        }
        val updated = etat.copy(description = dto.description)
        return EtatSanteMapper.toResponseDTO(repository.save(updated))
    }

    @Transactional
    override fun deleteEtat(id: Long) {
        if (!repository.existsById(id)) throw IllegalArgumentException("EtatSante avec id $id non trouvé")
        repository.deleteById(id)
    }

    override fun getEtatsByTypeAnimal(typeAnimalId: Long): List<EtatSanteResponseDTO> =
        repository.findByTypeAnimalId(typeAnimalId)
            .map { EtatSanteMapper.toResponseDTO(it) }
}
