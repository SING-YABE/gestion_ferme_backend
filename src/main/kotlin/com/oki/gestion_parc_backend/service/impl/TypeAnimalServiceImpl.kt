package com.oki.gestion_parc_backend.service.impl

import com.oki.gestion_parc_backend.dto.TypeAnimalDTO
import com.oki.gestion_parc_backend.dto.TypeAnimalMapper
import com.oki.gestion_parc_backend.dto.TypeAnimalResponseDTO
import com.oki.gestion_parc_backend.repository.TypeAnimalRepository
import com.oki.gestion_parc_backend.service.TypeAnimalService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TypeAnimalServiceImpl(
    private val repository: TypeAnimalRepository
) : TypeAnimalService {

    @Transactional
    override fun creerTypeAnimal(dto: TypeAnimalDTO): TypeAnimalResponseDTO {
        val entity = TypeAnimalMapper.toEntity(dto)
        val saved = repository.save(entity)
        return TypeAnimalMapper.toResponseDTO(saved)
    }



    override fun getAllTypes(): List<TypeAnimalResponseDTO> =
        repository.findAll().map { TypeAnimalMapper.toResponseDTO(it) }

    override fun getTypeById(id: Long): TypeAnimalResponseDTO =
        repository.findById(id).map { TypeAnimalMapper.toResponseDTO(it) }
            .orElseThrow { IllegalArgumentException("TypeAnimal avec id $id non trouvé") }

    @Transactional
    override fun updateTypeAnimal(id: Long, dto: TypeAnimalDTO): TypeAnimalResponseDTO {
        val typeAnimal = repository.findById(id).orElseThrow {
            IllegalArgumentException("TypeAnimal avec id $id non trouvé")
        }
        val updated = typeAnimal.copy(nom = dto.nom)
        return TypeAnimalMapper.toResponseDTO(repository.save(updated))
    }

    @Transactional
    override fun deleteTypeAnimal(id: Long) {
        if (!repository.existsById(id)) throw IllegalArgumentException("TypeAnimal avec id $id non trouvé")
        repository.deleteById(id)
    }
}
