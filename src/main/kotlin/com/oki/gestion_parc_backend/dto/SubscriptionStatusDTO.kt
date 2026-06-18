package com.oki.gestion_parc_backend.dto

/**
 * Réponse retournée par GET /api/subscriptions/me.
 * Consommé par le frontend Angular et le mobile Flutter.
 *
 * Contient l'état complet de l'abonnement :
 *   - plan actuel et ses caractéristiques
 *   - statut (TRIAL, ACTIVE, GRACE, EXPIRED, SUSPENDED, CANCELLED)
 *   - dates clés
 *   - limites actuelles et usage en temps réel
 */
data class SubscriptionStatusDTO(
    /** Nom du plan souscrit (ex : "Éleveur Pro"). */
    val planNom: String,

    /** ID du plan souscrit (null si en TRIAL sans plan choisi). */
    val planId: Long?,

    /** Statut de l'abonnement : TRIAL / ACTIVE / GRACE / EXPIRED / SUSPENDED / CANCELLED */
    val statut: String,

    /** True si l'accès à l'application est autorisé (TRIAL, ACTIVE ou GRACE). */
    val accessAutorise: Boolean,

    /** True si accès complet (pas de restriction lecture seule). */
    val pleinementActif: Boolean,

    // ── Dates ───────────────────────────────────────────────────────────────
    /** Fin de l'essai gratuit (null si pas en TRIAL). */
    val trialEndsAt: String?,

    /** Début du dernier abonnement payé (null si jamais payé). */
    val startDate: String?,

    /** Fin du dernier abonnement payé (null si jamais payé). */
    val endDate: String?,

    /** Fin de la période de grâce (null si pas en GRACE). */
    val graceEndsAt: String?,

    // ── Limites ─────────────────────────────────────────────────────────────
    val limits: LimitsDTO,

    // ── Fonctionnalités ─────────────────────────────────────────────────────
    val features: FeaturesDTO
)

/**
 * Limites quantitatives du plan et usage actuel de la ferme.
 * -1 signifie "illimité" pour les champs maxXxx.
 */
data class LimitsDTO(
    val maxAnimaux: Int,
    val maxUtilisateurs: Int,
    val maxBatiments: Int,
    val currentAnimaux: Long,
    val currentUtilisateurs: Long,
    val currentBatiments: Long,
    /** True si currentAnimaux >= maxAnimaux (et maxAnimaux != -1). */
    val animauxLimitAtteinte: Boolean
)

/**
 * Fonctionnalités booléennes incluses dans le plan actuel.
 * Utilisées par le frontend pour afficher/masquer les sections premium.
 */
data class FeaturesDTO(
    val hasAssistantIA: Boolean,
    val hasAlertesSms: Boolean,
    val hasSyntheseComplete: Boolean,
    val hasExportPdf: Boolean,
    val hasPrevisionPrix: Boolean
)

/**
 * DTO pour la liste publique des plans disponibles
 * (GET /api/plans — accessible sans authentification).
 */
data class PlanPublicDTO(
    val id: Long,
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
    val ordre: Int
)
