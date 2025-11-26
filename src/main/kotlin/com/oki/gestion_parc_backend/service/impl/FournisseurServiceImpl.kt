package com.oki.gestion_parc_backend.service.impl

import com.oki.gestion_parc_backend.dto.FournissuerDTO
import com.oki.gestion_parc_backend.mapper.FournisseurMapper
import com.oki.gestion_parc_backend.repository.FournisseurRepository
import com.oki.gestion_parc_backend.service.FournisseurService
import org.springframework.stereotype.Service

@Service
class FournisseurServiceImpl(
    private val repository: FournisseurRepository
) : FournisseurService {

    override fun create(dto: FournissuerDTO): FournissuerDTO {
        val saved = repository.save(FournisseurMapper.toEntity(dto))
        return FournisseurMapper.toDto(saved)
    }

    override fun list(): List<FournissuerDTO> =
        repository.findAll().map { FournisseurMapper.toDto(it) }

    override fun update(id: Long, dto: FournissuerDTO): FournissuerDTO {
        val existing = repository.findById(id).orElseThrow { RuntimeException("Fournisseur non trouvé") }

        val updated = existing.copy(
            nom = dto.nom,
            contact = dto.contact
        )
        val saved = repository.save(updated)
        return FournisseurMapper.toDto(saved)
    }

    override fun delete(id: Long) {
        repository.deleteById(id)
    }
}
