package com.oki.gestion_parc_backend.service.impl

import com.oki.gestion_parc_backend.dto.EtatSanteDTO
import com.oki.gestion_parc_backend.dto.EtatSanteMapper
import com.oki.gestion_parc_backend.dto.EtatSanteResponseDTO
import com.oki.gestion_parc_backend.repository.EtatSanteRepository
import com.oki.gestion_parc_backend.service.EtatSanteService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class EtatSanteServiceImpl(private val repository: EtatSanteRepository) : EtatSanteService {

    @Transactional
    override fun creerEtat(dto: EtatSanteDTO): EtatSanteResponseDTO {
        val saved = repository.save(EtatSanteMapper.toEntity(dto))
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
}
