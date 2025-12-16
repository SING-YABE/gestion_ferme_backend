package com.oki.gestion_parc_backend.model


import jakarta.persistence.*

@Entity
@Table(name = "ingredient")
data class Ingredient(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val nom: String,

    @ManyToOne(optional = false)
    @JoinColumn(name = "type_aliment_id")
    val typeAliment: TypeAliment
)