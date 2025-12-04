package com.oki.gestion_parc_backend.repository

import com.oki.gestion_parc_backend.model.Reproduction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ReproductionRepository : JpaRepository<Reproduction, Long> {

    // 1️Truies gestantes (saillies sans mise bas réelle)
    fun countByDateMiseBasReelleIsNull(): Long

    // MISE BAS MOIS
    @Query("""
        SELECT COUNT(r) FROM Reproduction r
        WHERE MONTH(r.dateMiseBasReelle) = MONTH(CURRENT_DATE)
        AND YEAR(r.dateMiseBasReelle) = YEAR(CURRENT_DATE)
        AND r.dateMiseBasReelle IS NOT NULL
    """)
    fun countMisesBasDuMois(): Long

    // porcelets sevrés
    @Query("SELECT COALESCE(SUM(r.nbSevres), 0) FROM Reproduction r")
    fun totalPorceletsSevres(): Long

    // 4️Total mises bas réelles
    fun countByDateMiseBasReelleIsNotNull(): Long
}
