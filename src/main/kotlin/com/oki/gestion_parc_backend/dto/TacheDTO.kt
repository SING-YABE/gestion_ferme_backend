package com.oki.gestion_parc_backend.dto

import com.oki.gestion_parc_backend.model.PrioriteTache
import com.oki.gestion_parc_backend.model.StatutTache
import com.oki.gestion_parc_backend.model.TypeRecurrence
import java.time.LocalDateTime

// ─── Création / Modification ──────────────────────────────────────────────────

data class TacheCreateDTO(
    val titre: String,
    val description: String = "",
    val typeTacheId: Long?,
    val priorite: PrioriteTache = PrioriteTache.NORMALE,
    val dateEcheance: LocalDateTime,
    val recurrence: TypeRecurrence = TypeRecurrence.UNIQUE,
    val joursRecurrence: String? = null,
    val batiment: String? = null,
    val box: String? = null,
    val notes: String? = null,
    val assigneeIds: List<Long> = emptyList()   // IDs des utilisateurs assignés
)

// ─── Réponses ─────────────────────────────────────────────────────────────────

data class TypeTacheDTO(
    val id: Long,
    val nom: String,
    val description: String,
    val couleur: String,
    val icone: String
)

data class UtilisateurLightDTO(
    val id: Long,
    val nom: String,
    val prenom: String,
    val email: String,
    val poste: String
)

data class PreuveTacheDTO(
    val id: Long,
    val urlFichier: String,
    val dateUpload: LocalDateTime
)

data class AssignationTacheDTO(
    val id: Long,
    val assignee: UtilisateurLightDTO,
    val statut: StatutTache,
    val dateDebut: LocalDateTime?,
    val dateSoumission: LocalDateTime?,
    val commentaireOuvrier: String?,
    val dateValidation: LocalDateTime?,
    val validateur: UtilisateurLightDTO?,
    val commentaireValidateur: String?,
    val preuves: List<PreuveTacheDTO>
)

data class TacheResponseDTO(
    val id: Long,
    val titre: String,
    val description: String,
    val typeTache: TypeTacheDTO?,
    val priorite: PrioriteTache,
    val dateEcheance: LocalDateTime,
    val recurrence: TypeRecurrence,
    val joursRecurrence: String?,
    val batiment: String?,
    val box: String?,
    val notes: String?,
    /** URL audio d'instruction en langue locale (optionnel) */
    val audioInstructionUrl: String?,
    val createur: UtilisateurLightDTO?,
    val dateCreation: LocalDateTime,
    val assignations: List<AssignationTacheDTO>,
    /** Statut global = statut le plus "avancé" parmi les assignations */
    val statutGlobal: StatutTache
)

// ─── Actions ──────────────────────────────────────────────────────────────────

data class SoumettreDTO(
    val commentaireOuvrier: String? = null
)

data class ValiderDTO(
    val commentaire: String? = null   // optionnel si validé
)

data class InvaliderDTO(
    val commentaire: String           // obligatoire si invalidé
)

data class TypeTacheCreateDTO(
    val nom: String,
    val description: String = "",
    val couleur: String = "#2d8a4e",
    val icone: String = "pi pi-check"
)

// ─── Stats ────────────────────────────────────────────────────────────────────

data class TacheStatsDTO(
    val aFaire: Int,
    val enCours: Int,
    val enAttenteValidation: Int,
    val validees: Int,
    val invalidees: Int,
    val expirees: Int
)
