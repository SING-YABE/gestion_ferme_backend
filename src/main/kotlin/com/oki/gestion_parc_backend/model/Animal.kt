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

    @Column(nullable = false)
    val poidsInitial: Double,

    @ManyToOne
    @JoinColumn(name = "etat_sante_id", nullable = false)
    val etatSante: EtatSante,

    @ManyToOne
    @JoinColumn(name = "batiment_id", nullable = false)
    val batiment: Batiment,

    val observations: String? = null
)
