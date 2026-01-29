//package com.oki.gestion_parc_backend.service.impl
//
//import com.oki.gestion_parc_backend.dto.AlerteMiseBasDTO
//import com.oki.gestion_parc_backend.dto.ReproductionDTO
//import com.oki.gestion_parc_backend.dto.ReproductionMapper
//import com.oki.gestion_parc_backend.dto.ReproductionResponseDTO
//import com.oki.gestion_parc_backend.dto.ReproductionStatsDTO
//import com.oki.gestion_parc_backend.model.Reproduction
//import com.oki.gestion_parc_backend.repository.AnimalRepository
//import com.oki.gestion_parc_backend.repository.ReproductionRepository
//import com.oki.gestion_parc_backend.service.ReproductionService
//import org.springframework.stereotype.Service
//import org.springframework.transaction.annotation.Transactional
//import java.time.LocalDate
//import java.time.format.DateTimeFormatter
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
//        val truie = animalRepository.findByCodeAnimal(dto.truieCode)
//            .orElseThrow { IllegalArgumentException("Truie avec code ${dto.truieCode} non trouvée") }
//
//        val verrat = animalRepository.findByCodeAnimal(dto.verratCode)
//            .orElseThrow { IllegalArgumentException("Verrat avec code ${dto.verratCode} non trouvé") }
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
//        return ReproductionMapper.toResponseDTO(reproductionRepository.save(reproduction))
//    }
//
//    override fun getAllReproductions(): List<ReproductionResponseDTO> =
//        reproductionRepository.findAll().map { ReproductionMapper.toResponseDTO(it) }
//
//
//    override fun getReproductionById(id: Long): ReproductionResponseDTO =
//        reproductionRepository.findById(id)
//            .map { ReproductionMapper.toResponseDTO(it) }
//            .orElseThrow { IllegalArgumentException("Reproduction avec id $id non trouvée") }
//
//    // MAJ
//    @Transactional
//    override fun updateReproduction(id: Long, dto: ReproductionDTO): ReproductionResponseDTO {
//        val existing = reproductionRepository.findById(id)
//            .orElseThrow { IllegalArgumentException("Reproduction avec id $id non trouvée") }
//
//        val truie = animalRepository.findByCodeAnimal(dto.truieCode)
//            .orElseThrow { IllegalArgumentException("Truie avec code ${dto.truieCode} non trouvée") }
//
//        val verrat = animalRepository.findByCodeAnimal(dto.verratCode)
//            .orElseThrow { IllegalArgumentException("Verrat avec code ${dto.verratCode} non trouvé") }
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
//    override fun getStatistiquesReproduction(): ReproductionStatsDTO {
//        val truiesGestantes = reproductionRepository.countByDateMiseBasReelleIsNull()
//        val misesBasMois = reproductionRepository.countMisesBasDuMois()
//        val porceletsSevres = reproductionRepository.totalPorceletsSevres()
//
//        val totalSaillies = reproductionRepository.count()
//        val total = reproductionRepository.count()
//        val totalMisesBas = reproductionRepository.countByDateMiseBasReelleIsNotNull()
//
//        val tauxReussite = if (totalSaillies > 0)
//            (totalMisesBas.toDouble() / totalSaillies.toDouble()) * 100
//        else 0.0
//
//        return ReproductionStatsDTO(
//            truiesGestantes = truiesGestantes,
//            misesBasMois = misesBasMois,
//            porceletsSevres = porceletsSevres,
//            tauxReussite = tauxReussite,
//            totalSaillies = totalSaillies
//        )
//    }
//
//    override fun getAlertesMiseBas(): List<AlerteMiseBasDTO> {
//        val today = LocalDate.now()
//
//        return reproductionRepository.findAll()
//            .filter { it.dateMiseBasReelle == null } // pas encore mise bas
//            .map { reproduction ->
//                val joursRestants = java.time.temporal.ChronoUnit.DAYS.between(
//                    today,
//                    reproduction.dateMiseBasPrevue
//                )
//
//                Pair(reproduction, joursRestants)
//            }
//            .filter { (_, jours) ->
//                jours in 0..7  // tous les joursRestants entre 0 et 7 inclus
//            }
//
//            .map { (repro, joursRestants) ->
//                AlerteMiseBasDTO(
//                    truieCode = repro.truie.codeAnimal,
//                    dateMiseBasPrevue = repro.dateMiseBasPrevue.format(formatter),
//                    joursRestants = joursRestants
//                )
//            }
//    }
//
//
//}
//
//
//
//
//
//////



package com.oki.gestion_parc_backend.service.impl

import com.oki.gestion_parc_backend.dto.AlerteMiseBasDTO
import com.oki.gestion_parc_backend.dto.ReproductionDTO
import com.oki.gestion_parc_backend.dto.ReproductionMapper
import com.oki.gestion_parc_backend.dto.ReproductionResponseDTO
import com.oki.gestion_parc_backend.dto.ReproductionStatsDTO
import com.oki.gestion_parc_backend.model.Animal
import com.oki.gestion_parc_backend.model.Reproduction
import com.oki.gestion_parc_backend.repository.AnimalRepository
import com.oki.gestion_parc_backend.repository.ReproductionRepository
import com.oki.gestion_parc_backend.repository.TypeAnimalRepository
import com.oki.gestion_parc_backend.repository.EtatSanteRepository
import com.oki.gestion_parc_backend.repository.BatimentRepository
import com.oki.gestion_parc_backend.service.ReproductionService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class ReproductionServiceImpl(
    private val reproductionRepository: ReproductionRepository,
    private val animalRepository: AnimalRepository,
    private val typeAnimalRepository: TypeAnimalRepository,
    private val etatSanteRepository: EtatSanteRepository,
    private val batimentRepository: BatimentRepository
) : ReproductionService {

    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    /**
     * Calcule la date de mise bas prévue
     * Formule : date saillie + 3 mois + 3 semaines + 3 jours
     */
    private fun calculerDateMiseBasPrevue(dateSaillie: LocalDate): LocalDate {
        return dateSaillie
            .plusMonths(3)
            .plusWeeks(3)
            .plusDays(3)
    }

    @Transactional
    override fun creerReproduction(dto: ReproductionDTO): ReproductionResponseDTO {
        val truie = animalRepository.findByCodeAnimal(dto.truieCode)
            .orElseThrow { IllegalArgumentException("Truie avec code ${dto.truieCode} non trouvée") }

        val verrat = animalRepository.findByCodeAnimal(dto.verratCode)
            .orElseThrow { IllegalArgumentException("Verrat avec code ${dto.verratCode} non trouvé") }

        val dateSaillie = LocalDate.parse(dto.dateSaillie, formatter)
        val dateMiseBasPrevue = calculerDateMiseBasPrevue(dateSaillie)

        val reproduction = Reproduction(
            truie = truie,
            verrat = verrat,
            dateSaillie = dateSaillie,
            dateMiseBasPrevue = dateMiseBasPrevue, // Calculée automatiquement
            dateMiseBasReelle = null, // Sera ajoutée lors de la mise à jour
            nbNesVivants = null,
            nbMortsNes = null,
            nbSevres = null,
            observations = dto.observations
        )

        val saved = reproductionRepository.save(reproduction)
        return ReproductionMapper.toResponseDTO(saved)
    }

    @Transactional
    override fun updateReproduction(id: Long, dto: ReproductionDTO): ReproductionResponseDTO {
        val existing = reproductionRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Reproduction avec id $id non trouvée") }

        val truie = animalRepository.findByCodeAnimal(dto.truieCode)
            .orElseThrow { IllegalArgumentException("Truie avec code ${dto.truieCode} non trouvée") }

        val verrat = animalRepository.findByCodeAnimal(dto.verratCode)
            .orElseThrow { IllegalArgumentException("Verrat avec code ${dto.verratCode} non trouvé") }

        val dateSaillie = LocalDate.parse(dto.dateSaillie, formatter)
        val dateMiseBasPrevue = calculerDateMiseBasPrevue(dateSaillie)

        val dateMiseBasReelle = dto.dateMiseBasReelle?.let { LocalDate.parse(it, formatter) }

        val updated = existing.copy(
            truie = truie,
            verrat = verrat,
            dateSaillie = dateSaillie,
            dateMiseBasPrevue = dateMiseBasPrevue,
            dateMiseBasReelle = dateMiseBasReelle,
            nbNesVivants = dto.nbNesVivants,
            nbMortsNes = dto.nbMortsNes,
            nbSevres = dto.nbSevres,
            observations = dto.observations
        )

        val saved = reproductionRepository.save(updated)

        // CRÉATION AUTOMATIQUE DES PORCELETS
        // Conditions : date mise bas réelle renseignée + nb nés vivants > 0 + pas encore de porcelets créés
        if (dateMiseBasReelle != null &&
            dto.nbNesVivants != null &&
            dto.nbNesVivants > 0 &&
            animalRepository.countByReproduction(saved) == 0L) {
            creerPorceletsDeReproduction(saved, dto.nbNesVivants)
        }

        return ReproductionMapper.toResponseDTO(saved)
    }

    /**
     * Crée automatiquement les porcelets avec codes séquentiels
     * Format : P_[TRUIE]_[VERRAT]_[NUMERO]
     */
    @Transactional
    fun creerPorceletsDeReproduction(reproduction: Reproduction, nombre: Int) {

        val reproId = reproduction.id
            ?: throw IllegalStateException("Reproduction non persistée")

        val typePorcelet = typeAnimalRepository.findAll()
            .firstOrNull { it.nom.equals("Porcelet", ignoreCase = true) }
            ?: throw IllegalArgumentException("Type 'Porcelet' non trouvé")

        val etatSanteDefaut = etatSanteRepository.findById(1L)
            .orElseThrow { IllegalArgumentException("État de santé par défaut non trouvé") }

        val batimentDefaut = reproduction.truie.batiment
        val dateMiseBas = reproduction.dateMiseBasReelle ?: LocalDate.now()

        val codeTruie = reproduction.truie.codeAnimal
        val codeVerrat = reproduction.verrat.codeAnimal

        for (i in 1..nombre) {

            val numero = String.format("%03d", i)
            val codePorcelet = "P_R${reproId}_${codeTruie}_${codeVerrat}_$numero"

            val porcelet = Animal(
                codeAnimal = codePorcelet,
                typeAnimal = typePorcelet,
                dateEntree = dateMiseBas,
                poidsInitial = 1.5,
                etatSante = etatSanteDefaut,
                batiment = batimentDefaut,
                observations = "Né le ${dateMiseBas.format(formatter)} de $codeTruie × $codeVerrat",
                reproduction = reproduction
            )

            animalRepository.save(porcelet)
        }
    }

    override fun getAllReproductions(): List<ReproductionResponseDTO> =
        reproductionRepository.findAll().map { ReproductionMapper.toResponseDTO(it) }

    override fun getReproductionById(id: Long): ReproductionResponseDTO =
        reproductionRepository.findById(id)
            .map { ReproductionMapper.toResponseDTO(it) }
            .orElseThrow { IllegalArgumentException("reproduction $id non trouvée") }

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
            .filter { it.dateMiseBasReelle == null }
            .map { reproduction ->
                val joursRestants = java.time.temporal.ChronoUnit.DAYS.between(
                    today,
                    reproduction.dateMiseBasPrevue
                )
                Pair(reproduction, joursRestants)
            }
            .filter { (_, jours) -> jours in 0..7 }
            .map { (repro, joursRestants) ->
                AlerteMiseBasDTO(
                    truieCode = repro.truie.codeAnimal,
                    dateMiseBasPrevue = repro.dateMiseBasPrevue.format(formatter),
                    joursRestants = joursRestants
                )
            }
    }
}

