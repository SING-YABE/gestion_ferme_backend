package com.oki.gestion_parc_backend.model

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * Configuration des limites du plan FREE — singleton (1 seule ligne, id = 1).
 * Modifiable par l'administrateur via PUT /subscription/config.
 *
 * Champs :
 *   - maxAnimauxFreePlan : nombre maximum d'animaux actifs autorisés en plan FREE
 *                          (défaut : 5 — configurable par l'admin sans redéploiement)
 *   - updatedAt          : horodatage de la dernière modification
 */
/**
 * IMPORTANT : stockée dans le schéma PUBLIC (pas dans le schéma tenant).
 * Le Super Admin de la plateforme est le seul à modifier ces limites.
 * Toutes les fermes lisent cette config commune via public.plan_config.
 */
@Entity
@Table(name = "plan_config", schema = "public")
class PlanConfig(

    @Id
    val id: Long = 1L,

    /** Limite animaux actifs pour le plan FREE. */
    @Column(nullable = false)
    var maxAnimauxFreePlan: Int = 5,

    /** Limite animaux actifs pour le plan PREMIUM (-1 = illimité). */
    @Column(nullable = false)
    var maxAnimauxPremiumPlan: Int = -1,

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
