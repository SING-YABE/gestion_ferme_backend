package com.oki.gestion_parc_backend.service

import com.oki.gestion_parc_backend.dto.PeseeCreateDto
import com.oki.gestion_parc_backend.dto.PeseeResponseDto
import com.oki.gestion_parc_backend.model.Pesee
import com.oki.gestion_parc_backend.repository.AnimalRepository
import com.oki.gestion_parc_backend.repository.PeseeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

/**
 * Service de gestion des pesées animales.
 *
 * Le GMQ (Gain Moyen Quotidien) est calculé automatiquement à chaque
 * nouvelle pesée par rapport à la dernière pesée enregistrée pour l'animal.
 *
 * Formule GMQ : (poids_actuel - poids_précédent) / nb_jours × 1000  [g/jour]
 *
 * Objectif Burkina Faso (conditions locales) : 400–600 g/jour
 * SOURCE: Fiche simplifiée alimentation — DGPA/MRAH, Burkina Faso — Juin 2021
 */
@Service
class PeseeService(
    private val peseeRepository: PeseeRepository,
    private val animalRepository: AnimalRepository
) {

    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    /**
     * Enregistre une nouvelle pesée et calcule le GMQ par rapport
     * à la pesée précédente du même animal.
     */
    @Transactional
    fun enregistrer(dto: PeseeCreateDto): PeseeResponseDto {
        val animal = animalRepository.findById(dto.animalId)
            .orElseThrow { IllegalArgumentException("Animal id=${dto.animalId} introuvable") }

        val datePesee = LocalDate.parse(dto.datePesee, formatter)

        // Récupérer la dernière pesée de cet animal (pour calcul GMQ)
        val historique = peseeRepository.findByAnimalOrderByDatePeseeAsc(animal)
        val derniere   = historique.lastOrNull()

        val pesee = Pesee(
            animal       = animal,
            poids        = dto.poids,
            datePesee    = datePesee,
            observations = dto.observations
        )
        val saved = peseeRepository.save(pesee)

        // GMQ depuis la pesée précédente
        val gmq = if (derniere != null) {
            val jours = ChronoUnit.DAYS.between(derniere.datePesee, datePesee).toDouble()
            if (jours > 0) ((dto.poids - derniere.poids) / jours * 1000).let {
                if (it > 0) it else null
            } else null
        } else null

        return toDto(saved, gmq)
    }

    /** Toutes les pesées d'un animal, ordre chronologique */
    fun listeParAnimal(animalId: Long): List<PeseeResponseDto> {
        val animal = animalRepository.findById(animalId)
            .orElseThrow { IllegalArgumentException("Animal id=$animalId introuvable") }

        val pesees = peseeRepository.findByAnimalOrderByDatePeseeAsc(animal)

        return pesees.mapIndexed { idx, p ->
            val gmq = if (idx > 0) {
                val prev  = pesees[idx - 1]
                val jours = ChronoUnit.DAYS.between(prev.datePesee, p.datePesee).toDouble()
                if (jours > 0) ((p.poids - prev.poids) / jours * 1000).let { if (it > 0) it else null }
                else null
            } else null
            toDto(p, gmq)
        }
    }

    /** Toutes les pesées (admin) */
    fun listerTout(): List<PeseeResponseDto> =
        peseeRepository.findAll().map { toDto(it, null) }

    /** Supprimer une pesée par id */
    @Transactional
    fun supprimer(id: Long) {
        if (!peseeRepository.existsById(id))
            throw IllegalArgumentException("Pesée id=$id introuvable")
        peseeRepository.deleteById(id)
    }

    // ── Mapping ──────────────────────────────────────────────

    private fun toDto(p: Pesee, gmq: Double?) = PeseeResponseDto(
        id           = p.id,
        animalId     = p.animal.id,
        codeAnimal   = p.animal.codeAnimal,
        poids        = p.poids,
        datePesee    = p.datePesee.format(formatter),
        observations = p.observations,
        gmqDepuisPrecedente = gmq?.let { Math.round(it * 10) / 10.0 }
    )
}
