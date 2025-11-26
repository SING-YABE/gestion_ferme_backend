package com.oki.gestion_parc_backend.repository

import com.oki.gestion_parc_backend.model.Alimentation
import com.oki.gestion_parc_backend.model.Animal
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface AlimentationRepository : JpaRepository<Alimentation, Long> {


    // Coût total par mois
    @Query(
        """
        SELECT FUNCTION('YEAR', a.date) as annee, 
               FUNCTION('MONTH', a.date) as mois, 
               SUM(a.quantiteKg * a.prixUnitaire) as coutTotal
        FROM Alimentation a
        GROUP BY FUNCTION('YEAR', a.date), FUNCTION('MONTH', a.date)
        ORDER BY annee, mois
    """
    )
    fun getCoutsMensuels(): List<Array<Any>>

    // Coût par mois pour une période donnée
    @Query(
        """
        SELECT FUNCTION('YEAR', a.date) as annee, 
               FUNCTION('MONTH', a.date) as mois, 
               SUM(a.quantiteKg * a.prixUnitaire) as coutTotal
        FROM Alimentation a
        WHERE a.date BETWEEN :dateDebut AND :dateFin
        GROUP BY FUNCTION('YEAR', a.date), FUNCTION('MONTH', a.date)
        ORDER BY annee, mois
    """
    )
    fun getCoutsMensuelsPeriode(dateDebut: LocalDate, dateFin: LocalDate): List<Array<Any>>

    // Coût mensuel par type d'animal
    @Query(
        """
        SELECT FUNCTION('YEAR', a.date) as annee, 
               FUNCTION('MONTH', a.date) as mois,
               t.nom as typeAnimal,
               SUM(a.quantiteKg * a.prixUnitaire) as coutTotal
        FROM Alimentation a
        JOIN a.typeAnimal t
        WHERE a.typeAnimal IS NOT NULL
        GROUP BY FUNCTION('YEAR', a.date), FUNCTION('MONTH', a.date), t.nom
        ORDER BY annee, mois
    """
    )
    fun getCoutsMensuelsParType(): List<Array<Any>>
}