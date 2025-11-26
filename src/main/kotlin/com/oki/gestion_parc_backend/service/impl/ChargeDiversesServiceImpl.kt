package com.oki.gestion_parc_backend.service.impl

import com.oki.gestion_parc_backend.dto.ChargeDiversesDto
import com.oki.gestion_parc_backend.mapper.ChargeDiversesMapper
import com.oki.gestion_parc_backend.repository.ChargeDiversesRepository
import com.oki.gestion_parc_backend.repository.TypeDepenseRepository
import com.oki.gestion_parc_backend.service.ChargeDiversesService
import org.springframework.stereotype.Service

@Service
class ChargeDiversesServiceImpl(
    val repo: ChargeDiversesRepository,
    val typeDepenseRepo: TypeDepenseRepository
) : ChargeDiversesService {

    override fun create(dto: ChargeDiversesDto): ChargeDiversesDto {
        val type = typeDepenseRepo.findById(dto.typeDepenseId).orElseThrow()
        val entity = ChargeDiversesMapper.toEntity(dto, type)
        return ChargeDiversesMapper.toDto(repo.save(entity))
    }

    override fun update(id: Long, dto: ChargeDiversesDto): ChargeDiversesDto {
        val existing = repo.findById(id).orElseThrow()
        val type = typeDepenseRepo.findById(dto.typeDepenseId).orElseThrow()

        val updated = existing.copy(
            date = existing.date,
            description = dto.description,
            montant = dto.montant,
            modePaiement = dto.modePaiement,
            observations = dto.observations,
            typeDepense = type
        )

        return ChargeDiversesMapper.toDto(repo.save(updated))
    }

    override fun getAll() = repo.findAll().map { ChargeDiversesMapper.toDto(it) }

    override fun getById(id: Long) =
        ChargeDiversesMapper.toDto(repo.findById(id).orElseThrow())

    override fun delete(id: Long) = repo.deleteById(id)
}
