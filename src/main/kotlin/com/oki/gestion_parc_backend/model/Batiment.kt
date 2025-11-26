package com.oki.gestion_parc_backend.model

import jakarta.persistence.*

@Entity
data class Batiment(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val nom: String,
    val localisation: String
)
