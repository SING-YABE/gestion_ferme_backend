package com.oki.gestion_parc_backend.service.impl

import com.oki.gestion_parc_backend.dto.TypeAlimentDto
import com.oki.gestion_parc_backend.model.TypeAliment
import com.oki.gestion_parc_backend.repository.TypeAlimentRepository
import com.oki.gestion_parc_backend.service.TypeAlimentService
import org.springframework.stereotype.Service

@Service
class TypeAlimentServiceImpl(
    private val repo: TypeAlimentRepository
) : TypeAlimentService {

    override fun create(dto: TypeAlimentDto): TypeAliment {
        val entity = TypeAliment(
            libelle = dto.libelle
        )
        return repo.save(entity)
    }

    override fun list(): List<TypeAliment> = repo.findAll()

    override fun update(id: Long, dto: TypeAlimentDto): TypeAliment {
        val entity = repo.findById(id).orElseThrow {
            IllegalArgumentException("TypeAliment avec id $id introuvable")
        }

        val updated = entity.copy(libelle = dto.libelle)
        return repo.save(updated)
    }

    override fun delete(id: Long) {
        repo.deleteById(id)
    }
}
