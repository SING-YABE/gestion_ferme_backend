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
@Entity
@Table(name = "plan_config")
class PlanConfig(

    @Id
    val id: Long = 1L,

    /**
     * Limite d'animaux actifs (non vendus) pour le plan FREE.
     * Valeur -1 = illimité (ne pas utiliser en prod pour FREE).
     */
    @Column(nullable = false)
    var maxAnimauxFreePlan: Int = 5,

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
