package com.oki.gestion_parc_backend.service.impl

import com.oki.gestion_parc_backend.dto.VenteDTO
import com.oki.gestion_parc_backend.dto.VenteResponseDTO
import com.oki.gestion_parc_backend.dto.VentesEvolutionDTO
import com.oki.gestion_parc_backend.mapper.VenteMapper
import com.oki.gestion_parc_backend.repository.VenteRepository
import com.oki.gestion_parc_backend.service.VenteService
import org.springframework.stereotype.Service
import kotlin.collections.get

@Service
class VenteServiceImpl(
    private val repository: VenteRepository
) : VenteService {

    override fun create(dto: VenteDTO): VenteResponseDTO {
        val entity = VenteMapper.toEntity(dto)
        return VenteMapper.toResponseDTO(repository.save(entity))
    }

    override fun update(id: Long, dto: VenteDTO): VenteResponseDTO {
        val existing = repository.findById(id)
            .orElseThrow { RuntimeException("Vente non trouvée") }

        val updated = existing.copy(
            dateVente = VenteMapper.toEntity(dto).dateVente,
            type = dto.type,
            quantite = dto.quantite,
            poidsTotal = dto.poidsTotal,
            prixUnitaire = dto.prixUnitaire,
            montantTotal = dto.quantite * dto.prixUnitaire,
            client = dto.client
        )

        return VenteMapper.toResponseDTO(repository.save(updated))
    }

    override fun getAll(): List<VenteResponseDTO> =
        repository.findAll().map { VenteMapper.toResponseDTO(it) }

    override fun getById(id: Long): VenteResponseDTO =
        repository.findById(id)
            .map { VenteMapper.toResponseDTO(it) }
            .orElseThrow { RuntimeException("Vente non trouvée") }

    override fun delete(id: Long) {
        repository.deleteById(id)
    }


    override fun evolutionMensuelle(): List<VentesEvolutionDTO> {
        return repository.evolutionMensuelle().map {
            VentesEvolutionDTO(
                annee = it[0] as Int,
                mois = it[1] as Int,
                totalVentes = (it[2] as Number).toDouble()
            )
        }
    }
}
