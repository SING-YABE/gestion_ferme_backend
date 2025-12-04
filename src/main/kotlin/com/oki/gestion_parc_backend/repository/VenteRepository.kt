package com.oki.gestion_parc_backend.repository

import com.oki.gestion_parc_backend.model.Vente
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface VenteRepository : JpaRepository<Vente, Long>{

    @Query("select coalesce(sum(v.quantite * v.prixUnitaire), 0) from Vente v")
    fun sumTotalVentes(): Double

    @Query("select coalesce(sum(v.quantite * v.prixUnitaire), 0) from Vente v where v.dateVente between :start and :end")
    fun sumTotalVentesBetween(@Param("start") start: LocalDate, @Param("end") end: LocalDate): Double


    @Query("""
        SELECT YEAR(v.dateVente) AS annee,
               MONTH(v.dateVente) AS mois,
               SUM(v.montantTotal) AS total
        FROM Vente v
        GROUP BY YEAR(v.dateVente), MONTH(v.dateVente)
        ORDER BY YEAR(v.dateVente), MONTH(v.dateVente)
    """)
    fun evolutionMensuelle(): List<Array<Any>>
}



