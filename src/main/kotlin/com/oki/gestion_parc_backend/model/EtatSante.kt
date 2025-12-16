package com.oki.gestion_parc_backend.model

import jakarta.persistence.*

@Entity
data class EtatSante(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val description: String,

    @ManyToOne
    @JoinColumn(name = "type_animal_id", nullable = false)
    val typeAnimal: TypeAnimal
)