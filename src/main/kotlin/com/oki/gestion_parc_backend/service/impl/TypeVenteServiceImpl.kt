package com.oki.gestion_parc_backend.service.impl

import com.oki.gestion_parc_backend.dto.BatimentDTO
import com.oki.gestion_parc_backend.dto.BatimentMapper
import com.oki.gestion_parc_backend.dto.BatimentResponseDTO
import com.oki.gestion_parc_backend.dto.TypeVenteDTO
import com.oki.gestion_parc_backend.dto.TypeVenteMapper
import com.oki.gestion_parc_backend.dto.TypeVenteResponseDTO
import com.oki.gestion_parc_backend.repository.BatimentRepository
import com.oki.gestion_parc_backend.repository.TypeVenteRepository
import com.oki.gestion_parc_backend.service.BatimentService
import com.oki.gestion_parc_backend.service.TypeVenteService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TypeVenteServiceImpl(private val repository: TypeVenteRepository) : TypeVenteService {

    @Transactional
    override fun creerTypeVente(dto: TypeVenteDTO): TypeVenteResponseDTO {
        val saved = repository.save(TypeVenteMapper.toEntity(dto))
        return TypeVenteMapper.toResponseDTO(saved)
    }

    override fun getAllTypeVente(): List<TypeVenteResponseDTO> =
        repository.findAll().map { TypeVenteMapper.toResponseDTO(it) }

    override fun getTypeVenteById(id: Long): TypeVenteResponseDTO =
        repository.findById(id).map { TypeVenteMapper.toResponseDTO(it) }
            .orElseThrow { IllegalArgumentException("Type vente avec id $id non trouvé") }

    @Transactional
    override fun updateTypeVente(id: Long, dto: TypeVenteDTO): TypeVenteResponseDTO {
        val typeVente = repository.findById(id).orElseThrow {
            IllegalArgumentException("Batiment avec id $id non trouvé")
        }
        val updated = typeVente.copy(nom = dto.nom)
        return TypeVenteMapper.toResponseDTO(repository.save(updated))
    }

    @Transactional
    override fun deleteTypeVente(id: Long) {
        if (!repository.existsById(id)) throw IllegalArgumentException("Type vente avec id $id non trouvé")
        repository.deleteById(id)
    }
}
