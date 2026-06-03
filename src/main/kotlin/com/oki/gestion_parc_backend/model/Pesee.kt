package com.oki.gestion_parc_backend.model

import jakarta.persistence.*
import java.time.LocalDate

/**
 * Enregistrement d'une pesée d'animal.
 *
 * Utilisé pour calculer le GMQ (Gain Moyen Quotidien) :
 *   GMQ (g/jour) = (poids_final - poids_initial) / nb_jours × 1000
 *
 * Le KPI Python calcule le GMQ en prenant la première et dernière pesée
 * de chaque animal sur les 12 derniers mois.
 */
@Entity
@Table(name = "pesee")
data class Pesee(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    /** Animal pesé */
    @ManyToOne(optional = false)
    @JoinColumn(name = "animal_id", nullable = false)
    val animal: Animal,

    /** Poids mesuré en kilogrammes */
    @Column(nullable = false)
    val poids: Double,

    /** Date de la pesée */
    @Column(name = "date_pesee", nullable = false)
    val datePesee: LocalDate,

    /** Observations éventuelles lors de la pesée */
    @Column(nullable = true)
    val observations: String? = null
)
