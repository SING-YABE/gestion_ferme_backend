package com.oki.gestion_parc_backend.model

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * Configuration d'un plan d'abonnement — stockée dans le schéma PUBLIC.
 *
 * La table peut contenir plusieurs lignes (un plan = une ligne).
 * Le Super-Admin gère ces plans via /api/super-admin/plans.
 *
 * Entrées/Sorties :
 *   - Entrée  : CRUD via SuperAdminPlanController
 *   - Sortie  : lu par SubscriptionService pour vérifier les limites de la ferme
 *
 * Champs de limites :
 *   -1 signifie "illimité" pour les champs maxXxx.
 */
@Entity
@Table(name = "plan_config", schema = "public")
class PlanConfig(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    // ── Identité du plan ──────────────────────────────────────────────────────

    /** Nom affiché aux éleveurs (ex : "Éleveur Pro"). Doit être unique. */
    @Column(nullable = false, unique = true)
    var nom: String = "",

    /** Courte description affichée sur l'écran de choix du plan. */
    @Column(nullable = true, length = 500)
    var description: String? = null,

    // ── Tarification ─────────────────────────────────────────────────────────

    /** Prix en FCFA. 0 = gratuit. */
    @Column(nullable = false)
    var prixFcfa: Int = 0,

    /** Durée de l'abonnement en jours une fois payé (30, 90, 365...). */
    @Column(nullable = false)
    var dureeDays: Int = 30,

    /**
     * Jours d'essai gratuit offerts lors de la première inscription.
     * 0 = pas d'essai pour ce plan.
     */
    @Column(nullable = false)
    var trialDays: Int = 0,

    // ── Limites quantitatives (-1 = illimité) ────────────────────────────────

    @Column(nullable = false)
    var maxAnimaux: Int = 10,

    @Column(nullable = false)
    var maxUtilisateurs: Int = 2,

    @Column(nullable = false)
    var maxBatiments: Int = 1,

    // ── Fonctionnalités incluses ─────────────────────────────────────────────

    /** Accès à l'assistant LLM (conseil alimentaire, santé, recommandations). */
    @Column(nullable = false)
    var hasAssistantIA: Boolean = false,

    /** Alertes SMS automatiques (expiration, santé animale, reproduction). */
    @Column(nullable = false)
    var hasAlertesSms: Boolean = false,

    /** Synthèse financière complète avec graphiques et comparatifs. */
    @Column(nullable = false)
    var hasSyntheseComplete: Boolean = false,

    /** Export PDF des rapports (animaux, finances, santé). */
    @Column(nullable = false)
    var hasExportPdf: Boolean = false,

    /** Module de prévision des prix porcins (Machine Learning). */
    @Column(nullable = false)
    var hasPrevisionPrix: Boolean = false,

    // ── Gestion ──────────────────────────────────────────────────────────────

    /**
     * Si false : le plan n'est plus proposé aux nouvelles fermes.
     * Les abonnements en cours ne sont PAS affectés.
     */
    @Column(nullable = false)
    var actif: Boolean = true,

    /** Ordre d'affichage sur l'écran de choix (0 = premier). */
    @Column(nullable = false)
    var ordre: Int = 0,

    @Column(nullable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
