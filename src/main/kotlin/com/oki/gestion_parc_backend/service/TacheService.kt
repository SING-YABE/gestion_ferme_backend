package com.oki.gestion_parc_backend.service

import com.oki.gestion_parc_backend.dto.*
import com.oki.gestion_parc_backend.model.*
import org.springframework.web.multipart.MultipartFile

interface TacheService {

    // ── Types de tâches ───────────────────────────────────────────────────────
    fun creerTypeTache(dto: TypeTacheCreateDTO): TypeTache
    fun listerTypesTaches(): List<TypeTache>
    fun modifierTypeTache(id: Long, dto: TypeTacheCreateDTO): TypeTache
    fun supprimerTypeTache(id: Long)

    // ── Tâches — CRUD ─────────────────────────────────────────────────────────
    fun creerTache(dto: TacheCreateDTO, createurEmail: String): TacheResponseDTO
    fun modifierTache(id: Long, dto: TacheCreateDTO): TacheResponseDTO
    fun supprimerTache(id: Long)
    fun getTache(id: Long): TacheResponseDTO

    // ── Tâches — Vues ─────────────────────────────────────────────────────────
    /** Toutes les tâches du jour (admin/gérant) */
    fun tachesJourTous(): List<TacheResponseDTO>
    /** Tâches à venir (admin/gérant) */
    fun tachesAVenirTous(): List<TacheResponseDTO>
    /** Tâches passées (admin/gérant) */
    fun tachesPasseesTous(): List<TacheResponseDTO>

    /** Tâches du jour pour l'utilisateur connecté */
    fun tachesJourPourUser(userEmail: String): List<TacheResponseDTO>
    /** Tâches futures pour l'utilisateur connecté */
    fun tachesAVenirPourUser(userEmail: String): List<TacheResponseDTO>
    /** Tâches passées pour l'utilisateur connecté */
    fun tachesPasseesPourUser(userEmail: String): List<TacheResponseDTO>

    /** File de validation (statut EN_ATTENTE_VALIDATION) */
    fun tachesEnAttenteValidation(): List<TacheResponseDTO>

    /** Stats globales */
    fun stats(): TacheStatsDTO

    // ── Actions ouvrier ───────────────────────────────────────────────────────
    fun demarrer(assignationId: Long, userEmail: String): AssignationTacheDTO
    fun soumettre(assignationId: Long, dto: SoumettreDTO, userEmail: String): AssignationTacheDTO
    fun uploadPreuve(assignationId: Long, file: MultipartFile, userEmail: String): PreuveTacheDTO

    // ── Actions gérant ────────────────────────────────────────────────────────
    fun valider(assignationId: Long, dto: ValiderDTO, validateurEmail: String): AssignationTacheDTO
    fun invalider(assignationId: Long, dto: InvaliderDTO, validateurEmail: String): AssignationTacheDTO
}
