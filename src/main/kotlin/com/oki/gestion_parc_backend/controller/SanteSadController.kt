package com.oki.gestion_parc_backend.controller

import com.oki.gestion_parc_backend.dto.*
import com.oki.gestion_parc_backend.repository.AnimalRepository
import com.oki.gestion_parc_backend.repository.ReproductionRepository
import com.oki.gestion_parc_backend.repository.SoinAnimalRepository
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import org.springframework.security.access.prepost.PreAuthorize

/**
 * Controller SAD — Endpoints santé et reproduction pour le tableau de bord IA.
 *
 * Ces endpoints sont en lecture seule et n'utilisent aucun nouveau modèle.
 * Ils agrègent les données existantes (soin_animal, reproduction, animal).
 *
 * Endpoints :
 *   GET /api/soins/top-consommateurs?limit=10&mois=12
 *       → Top animaux par coût de soins sur les N derniers mois
 *
 *   GET /api/animaux/{id}/bilan-sante?mois=12
 *       → Bilan sanitaire complet d'un animal (visites, coûts, motifs)
 *
 *   GET /api/reproductions/issf
 *       → Intervalle Sevrage–Saillie Fécondante moyen (objectif ≤ 7 jours)
 */
@RestController
class SanteSadController(
    private val soinRepo:   SoinAnimalRepository,
    private val animalRepo: AnimalRepository,
    private val reproRepo:  ReproductionRepository
) {

    private val fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    // =========================================================================
    // TOP CONSOMMATEURS DE SOINS
    // GET /api/soins/top-consommateurs?limit=10&mois=12
    // =========================================================================

    @GetMapping("/api/soins/top-consommateurs")
    @PreAuthorize("hasAuthority('SOIN_STATS')")
    fun topConsommateurs(
        @RequestParam(defaultValue = "10") limit: Int,
        @RequestParam(defaultValue = "12") mois: Int
    ): List<TopConsommateurDto> {

        val dateDebut = LocalDate.now().minusMonths(mois.toLong())

        // Récupérer tous les soins sur la période
        val tousLesSoins = soinRepo.findAll().filter {
            it.animal != null && !it.dateSoin.isBefore(dateDebut)
        }

        // Grouper par animal
        return tousLesSoins
            .groupBy { it.animal!! }
            .map { (animal, soins) ->
                val sortedSoins = soins.sortedByDescending { it.dateSoin }
                TopConsommateurDto(
                    animalId      = animal.id,
                    codeAnimal    = animal.codeAnimal,
                    typeAnimal    = animal.typeAnimal.nom,
                    nbVisites     = soins.size,
                    coutTotal     = soins.sumOf { it.totalPrestation },
                    coutMedicaments = soins.sumOf { it.coutMedicament },
                    dernierMotif  = sortedSoins.firstOrNull()?.motif,
                    derniereSoin  = sortedSoins.firstOrNull()?.dateSoin?.format(fmt)
                )
            }
            .sortedByDescending { it.coutTotal }
            .take(limit)
    }

    // =========================================================================
    // BILAN SANTÉ D'UN ANIMAL
    // GET /api/animaux/{id}/bilan-sante?mois=12
    // =========================================================================

    @GetMapping("/api/animaux/{id}/bilan-sante")
    @PreAuthorize("hasAuthority('SANTE_BILAN')")
    fun bilanSante(
        @PathVariable id: Long,
        @RequestParam(defaultValue = "12") mois: Int
    ): BilanSanteAnimalDto {

        val animal = animalRepo.findById(id)
            .orElseThrow { IllegalArgumentException("Animal id=$id introuvable") }

        val dateDebut = LocalDate.now().minusMonths(mois.toLong())

        val soins = soinRepo.findByAnimal(animal)
            .filter { !it.dateSoin.isBefore(dateDebut) }
            .sortedByDescending { it.dateSoin }

        val motifsPrincipaux = soins
            .groupBy { it.motif }
            .entries
            .sortedByDescending { it.value.size }
            .take(3)
            .map { it.key }

        return BilanSanteAnimalDto(
            animalId              = animal.id,
            codeAnimal            = animal.codeAnimal,
            typeAnimal            = animal.typeAnimal.nom,
            nbVisitesSoins        = soins.size,
            coutTotalSoins        = soins.sumOf { it.cout },
            coutTotalMedicaments  = soins.sumOf { it.coutMedicament },
            coutTotalPrestations  = soins.sumOf { it.totalPrestation },
            derniereVisite        = soins.firstOrNull()?.dateSoin?.format(fmt),
            motifsPrincipaux      = motifsPrincipaux,
            soins = soins.map { s ->
                SoinResume(
                    dateSoin    = s.dateSoin.format(fmt),
                    motif       = s.motif,
                    traitement  = s.traitement,
                    coutTotal   = s.totalPrestation,
                    veterinaire = s.veterinaire
                )
            }
        )
    }

    // =========================================================================
    // ISSF — INTERVALLE SEVRAGE–SAILLIE FÉCONDANTE
    // GET /api/reproductions/issf
    //
    // Calcul : pour chaque truie, on prend la date estimée de sevrage
    // (date_mise_bas_reelle + 28 jours standard) et la date de la prochaine
    // saillie. L'ISSF = nb jours entre les deux.
    // Objectif élevage porcin BF : ≤ 7 jours
    // SOURCE: DGPA/MRAH Burkina Faso — Juin 2021
    // =========================================================================

    @GetMapping("/api/reproductions/issf")
    @PreAuthorize("hasAuthority('REPRODUCTION_ISSF')")
    fun getIssf(): IssfDto {

        val toutesReproductions = reproRepo.findAll()
            .filter { it.dateMiseBasReelle != null && it.nbSevres != null && it.nbSevres!! > 0 }
            .sortedBy { it.dateMiseBasReelle }

        // Grouper par truie
        val parTruie = toutesReproductions.groupBy { it.truie.id }

        val details = mutableListOf<IssfDetailDto>()

        parTruie.forEach { (_, repros) ->
            val sortees = repros.sortedBy { it.dateMiseBasReelle }

            for (i in 0 until sortees.size - 1) {
                val current = sortees[i]
                val next    = sortees[i + 1]

                // Date estimée de sevrage = date_mise_bas_reelle + 28 jours
                val dateSevrageEstime = current.dateMiseBasReelle!!.plusDays(28)
                val dateSaillieSuivante = next.dateSaillie

                // ISSF = jours entre sevrage et saillie suivante
                val issf = ChronoUnit.DAYS.between(dateSevrageEstime, dateSaillieSuivante).toInt()

                // Ignorer les valeurs négatives ou aberrantes (> 180 jours)
                if (issf in 0..180) {
                    details.add(IssfDetailDto(
                        truieId              = current.truie.id,
                        codeAnimal           = current.truie.codeAnimal,
                        dateSevrageEstime    = dateSevrageEstime.format(fmt),
                        dateSailliesSuivante = dateSaillieSuivante.format(fmt),
                        issfJours            = issf
                    ))
                }
            }
        }

        val issfMoyen = if (details.isNotEmpty())
            details.mapNotNull { it.issfJours }.average().let {
                Math.round(it * 10) / 10.0
            }
        else null

        return IssfDto(
            issfMoyenJours    = issfMoyen,
            nbTruiesCalculees = parTruie.size,
            objectifJours     = 7,
            conforme          = issfMoyen != null && issfMoyen <= 7.0,
            detail            = details
        )
    }
}
