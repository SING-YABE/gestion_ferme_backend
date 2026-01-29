package com.oki.gestion_parc_backend.repository

import com.oki.gestion_parc_backend.model.Vente
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate


@Repository
interface VenteRepository : JpaRepository<Vente, Long> {

    // ✅ Nouvelle requête : somme directe du montantTotal
    @Query("SELECT COALESCE(SUM(v.montantTotal), 0) FROM Vente v")
    fun sumTotalVentes(): Double

    // ✅ Nouvelle requête : somme entre deux dates
    @Query("SELECT COALESCE(SUM(v.montantTotal), 0) FROM Vente v WHERE v.dateVente BETWEEN :start AND :end")
    fun sumTotalVentesBetween(@Param("start") start: LocalDate, @Param("end") end: LocalDate): Double

    // ✅ Évolution mensuelle basée sur montantTotal
    @Query("""
        SELECT YEAR(v.dateVente) AS annee,
               MONTH(v.dateVente) AS mois,
               SUM(v.montantTotal) AS total
        FROM Vente v
        GROUP BY YEAR(v.dateVente), MONTH(v.dateVente)
        ORDER BY YEAR(v.dateVente), MONTH(v.dateVente)
    """)
    fun evolutionMensuelle(): List<Array<Any>>


    fun findByDateEnlevementBetween(
        startDate: LocalDate,
        endDate: LocalDate
    ): List<Vente>
}