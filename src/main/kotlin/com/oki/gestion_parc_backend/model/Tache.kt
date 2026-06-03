package com.oki.gestion_parc_backend.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * Tâche à réaliser sur la ferme.
 * Peut être assignée à une ou plusieurs personnes (AssignationTache).
 */
@Entity
@Table(name = "taches")
data class Tache(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    var titre: String = "",

    @Column(columnDefinition = "TEXT")
    var description: String = "",

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "type_tache_id")
    var typeTache: TypeTache? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var priorite: PrioriteTache = PrioriteTache.NORMALE,

    /** Date et heure limite d'exécution */
    @Column(nullable = false)
    var dateEcheance: LocalDateTime = LocalDateTime.now(),

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var recurrence: TypeRecurrence = TypeRecurrence.UNIQUE,

    /**
     * Pour récurrence hebdomadaire : jours séparés par virgule (1=Lun, 7=Dim).
     * Ex: "1,3,5" = lundi, mercredi, vendredi
     */
    @Column
    var joursRecurrence: String? = null,

    /** Bâtiment concerné (optionnel) */
    @Column
    var batiment: String? = null,

    /** Box concerné (optionnel) */
    @Column
    var box: String? = null,

    /** Notes supplémentaires */
    @Column(columnDefinition = "TEXT")
    var notes: String? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "createur_id", nullable = false)
    var createur: Utilisateur? = null,

    @Column(nullable = false)
    var dateCreation: LocalDateTime = LocalDateTime.now(),

    /** Tâche parente si générée par récurrence */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tache_parente_id")
    @JsonIgnore
    var tacheParente: Tache? = null,

    @OneToMany(mappedBy = "tache", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JsonIgnore
    val assignations: MutableList<AssignationTache> = mutableListOf()
)
