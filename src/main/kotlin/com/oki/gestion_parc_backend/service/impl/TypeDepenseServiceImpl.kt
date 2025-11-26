package com.oki.gestion_parc_backend.service.impl

import com.oki.gestion_parc_backend.dto.TypeDepenseDTO
import com.oki.gestion_parc_backend.dto.TypeDepenseResponseDTO
import com.oki.gestion_parc_backend.mapper.TypeDepenseMapper
import com.oki.gestion_parc_backend.repository.TypeDepenseRepository
import com.oki.gestion_parc_backend.service.TypeDepenseService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TypeDepenseServiceImpl(
    private val repository: TypeDepenseRepository
) : TypeDepenseService {

    @Transactional
    override fun creerTypeDepense(dto: TypeDepenseDTO): TypeDepenseResponseDTO {
        val entity = TypeDepenseMapper.toEntity(dto)
        val saved = repository.save(entity)
        return TypeDepenseMapper.toResponseDTO(saved)
    }

    override fun getAllTypes(): List<TypeDepenseResponseDTO> =
        repository.findAll().map { TypeDepenseMapper.toResponseDTO(it) }

    override fun getTypeById(id: Long): TypeDepenseResponseDTO =
        repository.findById(id).map { TypeDepenseMapper.toResponseDTO(it) }
            .orElseThrow { IllegalArgumentException("TypeDepense avec id $id non trouvé") }

    @Transactional
    override fun updateTypeDepense(id: Long, dto: TypeDepenseDTO): TypeDepenseResponseDTO {
        val typeDepense = repository.findById(id).orElseThrow {
            IllegalArgumentException("Type Depense avec id $id non trouvé")
        }
        val updated = typeDepense.copy(nom = dto.nom)
        return TypeDepenseMapper.toResponseDTO(repository.save(updated))
    }

    @Transactional
    override fun deleteTypeDepense(id: Long) {
        if (!repository.existsById(id)) throw IllegalArgumentException("type depense avec id $id non trouvé")
        repository.deleteById(id)
    }
}
