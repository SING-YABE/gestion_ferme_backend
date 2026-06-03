package com.oki.gestion_parc_backend.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * Représente l'attribution d'une tâche à un utilisateur.
 * Une tâche peut avoir plusieurs assignations (une par personne assignée).
 * Chaque assignation suit son propre cycle de statut.
 */
@Entity
@Table(name = "assignations_taches")
data class AssignationTache(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tache_id", nullable = false)
    @JsonIgnore
    var tache: Tache? = null,

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    var assignee: Utilisateur? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var statut: StatutTache = StatutTache.A_FAIRE,

    /** Quand l'assigné a démarré la tâche */
    @Column
    var dateDebut: LocalDateTime? = null,

    /** Quand l'assigné a soumis ses preuves */
    @Column
    var dateSoumission: LocalDateTime? = null,

    /** Note de l'assigné lors de la soumission */
    @Column(columnDefinition = "TEXT")
    var commentaireOuvrier: String? = null,

    /** Date de validation/invalidation */
    @Column
    var dateValidation: LocalDateTime? = null,

    /** Qui a validé/invalidé */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "validateur_id")
    var validateur: Utilisateur? = null,

    /** Commentaire du gérant (obligatoire si invalidée) */
    @Column(columnDefinition = "TEXT")
    var commentaireValidateur: String? = null,

    /** Photos soumises comme preuve */
    @OneToMany(mappedBy = "assignation", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val preuves: MutableList<PreuveTache> = mutableListOf()
)
