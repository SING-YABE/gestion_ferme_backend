package com.oki.gestion_parc_backend.model

import jakarta.persistence.*
import java.time.LocalDate

@Entity
data class Animal(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val codeAnimal: String,

    @ManyToOne
    @JoinColumn(name = "type_animal_id", nullable = false)
    val typeAnimal: TypeAnimal,

    @Column(nullable = false)
    val dateEntree: LocalDate,

    /**
     * Date de naissance de l'animal.
     * Utilisée pour calculer l'âge à la vente (KPI SAD).
     * Nullable pour rétrocompatibilité avec les animaux existants.
     */
    @Column(nullable = true)
    val dateNaissance: LocalDate? = null,

    @Column(nullable = false)
    val poidsInitial: Double,

    @ManyToOne
    @JoinColumn(name = "etat_sante_id", nullable = false)
    val etatSante: EtatSante,

    @ManyToOne
    @JoinColumn(name = "box_id", nullable = false)
    val box: Box,

    val observations: String? = null,

    @ManyToOne
    @JoinColumn(name = "reproduction_id")
    val reproduction: Reproduction? = null,

    @Column(nullable = false)
    val vendu : Boolean = false,

    @Column
    val photoUrl: String? = null
)
