package com.oki.gestion_parc_backend.service.impl
import com.oki.gestion_parc_backend.dto.DepenseDTO
import com.oki.gestion_parc_backend.mapper.DepenseMapper
import com.oki.gestion_parc_backend.model.TypeDepense
import com.oki.gestion_parc_backend.repository.DepenseRepository
import com.oki.gestion_parc_backend.repository.TypeDepenseRepository
import com.oki.gestion_parc_backend.service.DepenseService
import org.springframework.stereotype.Service

@Service
class DepenseServiceImpl(
    private val depenseRepo: DepenseRepository,
    private val typeDepenseRepo: TypeDepenseRepository
) : DepenseService {

    override fun create(dto: DepenseDTO): DepenseDTO {
        val typeDepense: TypeDepense = typeDepenseRepo.findById(dto.typeDepenseId)
            .orElseThrow { IllegalArgumentException("TypeDepense avec id ${dto.typeDepenseId} introuvable") }

        val entity = DepenseMapper.toEntity(dto, typeDepense)
        return DepenseMapper.toDTO(depenseRepo.save(entity))
    }

    override fun list(): List<DepenseDTO> =
        depenseRepo.findAll().map { DepenseMapper.toDTO(it) }

    override fun update(id: Long, dto: DepenseDTO): DepenseDTO {
        val existing = depenseRepo.findById(id)
            .orElseThrow { IllegalArgumentException("Depense avec id $id introuvable") }

        val typeDepense: TypeDepense = typeDepenseRepo.findById(dto.typeDepenseId)
            .orElseThrow { IllegalArgumentException("TypeDepense avec id ${dto.typeDepenseId} introuvable") }

        val updated = existing.copy(
            date = java.time.LocalDate.parse(dto.date, DepenseMapper.formatter),
            typeDepense = typeDepense,
            description = dto.description,
            montant = dto.montant,
            modePaiement = dto.modePaiement,
            observations = dto.observations
        )

        return DepenseMapper.toDTO(depenseRepo.save(updated))
    }

    override fun delete(id: Long) = depenseRepo.deleteById(id)
}
