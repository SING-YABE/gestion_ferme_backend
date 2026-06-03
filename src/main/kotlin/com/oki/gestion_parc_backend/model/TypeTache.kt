package com.oki.gestion_parc_backend.model

import jakarta.persistence.*

/**
 * Catégorie de tâche (Alimentation, Soins, Nettoyage, etc.)
 * Configurable par l'administrateur.
 */
@Entity
@Table(name = "types_taches")
data class TypeTache(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    var nom: String = "",

    @Column
    var description: String = "",

    /** Couleur hexadécimale pour l'UI (ex: #2d8a4e) */
    @Column(length = 10)
    var couleur: String = "#2d8a4e",

    /** Nom de l'icône (ex: "pi pi-heart", "local_hospital") */
    @Column(length = 60)
    var icone: String = "pi pi-check"
)
