package com.oki.gestion_parc_backend.repository

import com.oki.gestion_parc_backend.model.Animal
import com.oki.gestion_parc_backend.model.Pesee
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface PeseeRepository : JpaRepository<Pesee, Long> {

    /** Toutes les pesées d'un animal, triées par date croissante */
    fun findByAnimalOrderByDatePeseeAsc(animal: Animal): List<Pesee>

    /** Toutes les pesées d'un animal */
    fun findByAnimalId(animalId: Long): List<Pesee>

    /**
     * GMQ moyen de l'élevage sur les N derniers jours.
     * Calcul : (poids_max - poids_min) / nb_jours × 1000 par animal,
     * puis moyenne sur tous les animaux ayant ≥ 2 pesées.
     */
    @Query("""
        SELECT AVG(sub.gmq)
        FROM (
            SELECT
                p.animal.id AS animalId,
                (MAX(p.poids) - MIN(p.poids)) /
                NULLIF(DATEDIFF(DAY, MIN(p.datePesee), MAX(p.datePesee)), 0) * 1000 AS gmq
            FROM Pesee p
            WHERE p.datePesee >= :dateDebut
            GROUP BY p.animal.id
            HAVING COUNT(p.id) >= 2
        ) sub
    """)
    fun findGmqMoyen(@Param("dateDebut") dateDebut: java.time.LocalDate): Double?
}
