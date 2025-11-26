package com.oki.gestion_parc_backend.model

import jakarta.persistence.*

@Entity
data class TypeDepense(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val nom: String
)
