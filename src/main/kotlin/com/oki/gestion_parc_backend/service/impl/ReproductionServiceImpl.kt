package com.oki.gestion_parc_backend.service.impl

import com.oki.gestion_parc_backend.dto.AlerteMiseBasDTO
import com.oki.gestion_parc_backend.dto.ReproductionDTO
import com.oki.gestion_parc_backend.dto.ReproductionMapper
import com.oki.gestion_parc_backend.dto.ReproductionResponseDTO
import com.oki.gestion_parc_backend.dto.ReproductionStatsDTO
import com.oki.gestion_parc_backend.model.Reproduction
import com.oki.gestion_parc_backend.repository.AnimalRepository
import com.oki.gestion_parc_backend.repository.ReproductionRepository
import com.oki.gestion_parc_backend.service.ReproductionService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class ReproductionServiceImpl(
    private val reproductionRepository: ReproductionRepository,
    private val animalRepository: AnimalRepository
) : ReproductionService {

    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

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

    override fun getAllReproductions(): List<ReproductionResponseDTO> =
        reproductionRepository.findAll().map { ReproductionMapper.toResponseDTO(it) }


    override fun getReproductionById(id: Long): ReproductionResponseDTO =
        reproductionRepository.findById(id)
            .map { ReproductionMapper.toResponseDTO(it) }
            .orElseThrow { IllegalArgumentException("Reproduction avec id $id non trouvée") }

    // MAJ
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

    @Transactional
    override fun deleteReproduction(id: Long) {
        if (!reproductionRepository.existsById(id))
            throw IllegalArgumentException("Reproduction avec id $id non trouvée")
        reproductionRepository.deleteById(id)
    }
    override fun getStatistiquesReproduction(): ReproductionStatsDTO {
        val truiesGestantes = reproductionRepository.countByDateMiseBasReelleIsNull()
        val misesBasMois = reproductionRepository.countMisesBasDuMois()
        val porceletsSevres = reproductionRepository.totalPorceletsSevres()

        val totalSaillies = reproductionRepository.count()
        val total = reproductionRepository.count()
        val totalMisesBas = reproductionRepository.countByDateMiseBasReelleIsNotNull()

        val tauxReussite = if (totalSaillies > 0)
            (totalMisesBas.toDouble() / totalSaillies.toDouble()) * 100
        else 0.0

        return ReproductionStatsDTO(
            truiesGestantes = truiesGestantes,
            misesBasMois = misesBasMois,
            porceletsSevres = porceletsSevres,
            tauxReussite = tauxReussite,
            totalSaillies = totalSaillies
        )
    }

    override fun getAlertesMiseBas(): List<AlerteMiseBasDTO> {
        val today = LocalDate.now()

        return reproductionRepository.findAll()
            .filter { it.dateMiseBasReelle == null } // pas encore mise bas
            .map { reproduction ->
                val joursRestants = java.time.temporal.ChronoUnit.DAYS.between(
                    today,
                    reproduction.dateMiseBasPrevue
                )

                Pair(reproduction, joursRestants)
            }
            .filter { (_, jours) ->
                jours in 0..7  // tous les joursRestants entre 0 et 7 inclus
            }

            .map { (repro, joursRestants) ->
                AlerteMiseBasDTO(
                    truieCode = repro.truie.codeAnimal,
                    dateMiseBasPrevue = repro.dateMiseBasPrevue.format(formatter),
                    joursRestants = joursRestants
                )
            }
    }


}


