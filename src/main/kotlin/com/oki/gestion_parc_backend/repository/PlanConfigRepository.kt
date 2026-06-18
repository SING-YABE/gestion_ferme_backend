package com.oki.gestion_parc_backend.repository

import com.oki.gestion_parc_backend.model.PlanConfig
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PlanConfigRepository : JpaRepository<PlanConfig, Long> {

    /** Plans actifs triés par ordre d'affichage — utilisé pour la liste publique. */
    fun findByActifTrueOrderByOrdreAsc(): List<PlanConfig>

    /** Vérifie l'unicité du nom avant création/modification. */
    fun findByNom(nom: String): PlanConfig?

    /** Vérifie s'il existe au moins un plan actif avec trialDays > 0. */
    fun existsByActifTrueAndTrialDaysGreaterThan(trialDays: Int): Boolean
}
