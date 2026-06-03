package com.oki.gestion_parc_backend.repository

import com.oki.gestion_parc_backend.model.Tache
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface TacheRepository : JpaRepository<Tache, Long> {

    /** Tâches créées par un utilisateur */
    fun findByCreateurIdUtilisateur(createurId: Long): List<Tache>

    /** Tâches dont l'échéance est dans un intervalle */
    fun findByDateEcheanceBetween(debut: LocalDateTime, fin: LocalDateTime): List<Tache>

    /** Tâches du jour (toutes) — pour admin/gérant */
    @Query("""
        SELECT DISTINCT t FROM Tache t
        WHERE t.dateEcheance >= :debut AND t.dateEcheance < :fin
        ORDER BY t.dateEcheance ASC
    """)
    fun findTachesJour(@Param("debut") debut: LocalDateTime, @Param("fin") fin: LocalDateTime): List<Tache>

    /** Tâches à venir (toutes) — pour admin/gérant */
    @Query("SELECT DISTINCT t FROM Tache t WHERE t.dateEcheance > :now ORDER BY t.dateEcheance ASC")
    fun findTachesAVenir(@Param("now") now: LocalDateTime): List<Tache>

    /**
     * Historique admin : tâches dont au moins une assignation est terminée (VALIDEE/INVALIDEE/EXPIREE)
     * OU dont l'échéance est dépassée. Ainsi une tâche validée aujourd'hui apparaît bien ici.
     */
    @Query("""
        SELECT DISTINCT t FROM Tache t
        LEFT JOIN t.assignations a
        WHERE t.dateEcheance < :now
           OR a.statut IN ('VALIDEE', 'INVALIDEE', 'EXPIREE')
        ORDER BY t.dateEcheance DESC
    """)
    fun findTachesPassees(@Param("now") now: LocalDateTime): List<Tache>

    /** Tâches assignées à un utilisateur pour aujourd'hui */
    @Query("""
        SELECT DISTINCT t FROM Tache t
        JOIN t.assignations a
        WHERE a.assignee.idUtilisateur = :userId
          AND t.dateEcheance >= :debut AND t.dateEcheance < :fin
        ORDER BY t.dateEcheance ASC
    """)
    fun findTachesJourPourUser(
        @Param("userId") userId: Long,
        @Param("debut") debut: LocalDateTime,
        @Param("fin") fin: LocalDateTime
    ): List<Tache>

    /** Tâches futures assignées à un utilisateur */
    @Query("""
        SELECT DISTINCT t FROM Tache t
        JOIN t.assignations a
        WHERE a.assignee.idUtilisateur = :userId
          AND t.dateEcheance > :now
        ORDER BY t.dateEcheance ASC
    """)
    fun findTachesAVenirPourUser(@Param("userId") userId: Long, @Param("now") now: LocalDateTime): List<Tache>

    /**
     * Historique ouvrier : tâches assignées à l'utilisateur dont le statut est terminal
     * (VALIDEE / INVALIDEE / EXPIREE) OU dont l'échéance est dépassée.
     * Une tâche validée aujourd'hui à 11h apparaît ici même si l'échéance était ce matin.
     */
    @Query("""
        SELECT DISTINCT t FROM Tache t
        JOIN t.assignations a
        WHERE a.assignee.idUtilisateur = :userId
          AND (t.dateEcheance < :now OR a.statut IN ('VALIDEE', 'INVALIDEE', 'EXPIREE'))
        ORDER BY t.dateEcheance DESC
    """)
    fun findTachesPasseesPourUser(@Param("userId") userId: Long, @Param("now") now: LocalDateTime): List<Tache>
}
