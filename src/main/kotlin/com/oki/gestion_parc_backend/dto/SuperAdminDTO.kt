package com.oki.gestion_parc_backend.dto

/**
 * DTOs pour les endpoints Super-Admin de gestion des plans et des abonnements.
 */

// ── Plans ────────────────────────────────────────────────────────────────────

/**
 * Corps de la requête pour créer ou modifier un plan.
 * Tous les champs sont requis sauf description.
 */
data class PlanConfigCreateDTO(
    val nom: String,
    val description: String?,
    val prixFcfa: Int,
    val dureeDays: Int,
    val trialDays: Int,
    val maxAnimaux: Int,
    val maxUtilisateurs: Int,
    val maxBatiments: Int,
    val hasAssistantIA: Boolean,
    val hasAlertesSms: Boolean,
    val hasSyntheseComplete: Boolean,
    val hasExportPdf: Boolean,
    val hasPrevisionPrix: Boolean,
    val actif: Boolean,
    val ordre: Int
)

// ── Abonnements fermes ────────────────────────────────────────────────────────

/**
 * Corps pour l'attribution manuelle d'un plan à une ferme.
 */
data class ManualAssignDTO(
    val planId: Long,
    val dureeDays: Int,
    val notes: String?
)

/**
 * Corps pour la suspension/levée de suspension.
 */
data class SuspendDTO(
    val raison: String?
)

/**
 * Vue d'une ferme dans le tableau de bord Super-Admin (avec abonnement).
 */
data class FermeAdminDTO(
    val id: Long,
    val fermeCode: String,
    val nomFerme: String,
    val active: Boolean,
    // Abonnement
    val subscriptionStatut: String?,
    val planNom: String?,
    val endDate: String?,
    val trialEndsAt: String?,
    // Usage
    val nbUtilisateurs: Long,
    val nbAnimaux: Long
)
