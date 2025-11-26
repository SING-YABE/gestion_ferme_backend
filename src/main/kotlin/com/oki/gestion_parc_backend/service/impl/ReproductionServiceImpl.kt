package com.oki.gestion_parc_backend.service.impl

import com.oki.gestion_parc_backend.dto.ReproductionDTO
import com.oki.gestion_parc_backend.dto.ReproductionMapper
import com.oki.gestion_parc_backend.dto.ReproductionResponseDTO
import com.oki.gestion_parc_backend.model.Reproduction
import com.oki.gestion_parc_backend.repository.AnimalRepository
import com.oki.gestion_parc_backend.repository.ReproductionRepository
import com.oki.gestion_parc_backend.service.ReproductionService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.format.DateTimeFormatter
//
//@Service
//class ReproductionServiceImpl(
//    private val reproductionRepository: ReproductionRepository,
//    private val animalRepository: AnimalRepository
//) : ReproductionService {
//
//    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
//
//    @Transactional
//    override fun creerReproduction(dto: ReproductionDTO): ReproductionResponseDTO {
//        val truie = animalRepository.findById(dto.truieId)
//            .orElseThrow { IllegalArgumentException("Truie avec id ${dto.truieId} non trouvée") }
//
//        val verrat = animalRepository.findById(dto.verratId)
//            .orElseThrow { IllegalArgumentException("Verrat avec id ${dto.verratId} non trouvé") }
//
//        val reproduction = Reproduction(
//            truie = truie,
//            verrat = verrat,
//            dateSaillie = LocalDate.parse(dto.dateSaillie, formatter),
//            dateMiseBasPrevue = LocalDate.parse(dto.dateMiseBasPrevue, formatter),
//            dateMiseBasReelle = dto.dateMiseBasReelle?.let { LocalDate.parse(it, formatter) },
//            nbNesVivants = dto.nbNesVivants,
//            nbMortsNes = dto.nbMortsNes,
//            nbSevres = dto.nbSevres,
//            observations = dto.observations
//        )
//
//        val saved = reproductionRepository.save(reproduction)
//        return ReproductionMapper.toResponseDTO(saved)
//    }
//
//    override fun getAllReproductions(): List<ReproductionResponseDTO> =
//        reproductionRepository.findAll().map { ReproductionMapper.toResponseDTO(it) }
//
//    override fun getReproductionById(id: Long): ReproductionResponseDTO =
//        reproductionRepository.findById(id).map { ReproductionMapper.toResponseDTO(it) }
//            .orElseThrow { IllegalArgumentException("Reproduction avec id $id non trouvée") }
//
//    @Transactional
//    override fun updateReproduction(id: Long, dto: ReproductionDTO): ReproductionResponseDTO {
//        val existing = reproductionRepository.findById(id)
//            .orElseThrow { IllegalArgumentException("Reproduction avec id $id non trouvée") }
//
//        val truie = animalRepository.findById(dto.truieId)
//            .orElseThrow { IllegalArgumentException("Truie avec id ${dto.truieId} non trouvée") }
//
//        val verrat = animalRepository.findById(dto.verratId)
//            .orElseThrow { IllegalArgumentException("Verrat avec id ${dto.verratId} non trouvé") }
//
//        val updated = existing.copy(
//            truie = truie,
//            verrat = verrat,
//            dateSaillie = LocalDate.parse(dto.dateSaillie, formatter),
//            dateMiseBasPrevue = LocalDate.parse(dto.dateMiseBasPrevue, formatter),
//            dateMiseBasReelle = dto.dateMiseBasReelle?.let { LocalDate.parse(it, formatter) },
//            nbNesVivants = dto.nbNesVivants,
//            nbMortsNes = dto.nbMortsNes,
//            nbSevres = dto.nbSevres,
//            observations = dto.observations
//        )
//
//        return ReproductionMapper.toResponseDTO(reproductionRepository.save(updated))
//    }
//
//    @Transactional
//    override fun deleteReproduction(id: Long) {
//        if (!reproductionRepository.existsById(id))
//            throw IllegalArgumentException("Reproduction avec id $id non trouvée")
//        reproductionRepository.deleteById(id)
//    }
//}

@Service
class ReproductionServiceImpl(
    private val reproductionRepository: ReproductionRepository,
    private val animalRepository: AnimalRepository
) : ReproductionService {

    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    // --- Création ---
    @Transactional
    override fun creerReproduction(dto: ReproductionDTO): ReproductionResponseDTO {
        val truie = animalRepository.findByCodeAnimal(dto.truieCode)
            .orElseThrow { IllegalArgumentException("Truie avec code ${dto.truieCode} non trouvée") }

        val verrat = animalRepository.findByCodeAnimal(dto.verratCode)
            .orElseThrow { IllegalArgumentException("Verrat avec code ${dto.verratCode} non trouvé") }

        val reproduction = Reproduction(
            truie = truie,
            verrat = verrat,
            dateSaillie = LocalDate.parse(dto.dateSaillie, formatter),
            dateMiseBasPrevue = LocalDate.parse(dto.dateMiseBasPrevue, formatter),
            dateMiseBasReelle = dto.dateMiseBasReelle?.let { LocalDate.parse(it, formatter) },
            nbNesVivants = dto.nbNesVivants,
            nbMortsNes = dto.nbMortsNes,
            nbSevres = dto.nbSevres,
            observations = dto.observations
        )

        return ReproductionMapper.toResponseDTO(reproductionRepository.save(reproduction))
    }

    // --- Lecture de toutes les reproductions ---
    override fun getAllReproductions(): List<ReproductionResponseDTO> =
        reproductionRepository.findAll().map { ReproductionMapper.toResponseDTO(it) }

    // --- Lecture d'une reproduction par ID ---
    override fun getReproductionById(id: Long): ReproductionResponseDTO =
        reproductionRepository.findById(id)
            .map { ReproductionMapper.toResponseDTO(it) }
            .orElseThrow { IllegalArgumentException("Reproduction avec id $id non trouvée") }

    // --- Mise à jour ---
    @Transactional
    override fun updateReproduction(id: Long, dto: ReproductionDTO): ReproductionResponseDTO {
        val existing = reproductionRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Reproduction avec id $id non trouvée") }

        val truie = animalRepository.findByCodeAnimal(dto.truieCode)
            .orElseThrow { IllegalArgumentException("Truie avec code ${dto.truieCode} non trouvée") }

        val verrat = animalRepository.findByCodeAnimal(dto.verratCode)
            .orElseThrow { IllegalArgumentException("Verrat avec code ${dto.verratCode} non trouvé") }

        val updated = existing.copy(
            truie = truie,
            verrat = verrat,
            dateSaillie = LocalDate.parse(dto.dateSaillie, formatter),
            dateMiseBasPrevue = LocalDate.parse(dto.dateMiseBasPrevue, formatter),
            dateMiseBasReelle = dto.dateMiseBasReelle?.let { LocalDate.parse(it, formatter) },
            nbNesVivants = dto.nbNesVivants,
            nbMortsNes = dto.nbMortsNes,
            nbSevres = dto.nbSevres,
            observations = dto.observations
        )

        return ReproductionMapper.toResponseDTO(reproductionRepository.save(updated))
    }

    // --- Suppression ---
    @Transactional
    override fun deleteReproduction(id: Long) {
        if (!reproductionRepository.existsById(id))
            throw IllegalArgumentException("Reproduction avec id $id non trouvée")
        reproductionRepository.deleteById(id)
    }
}
