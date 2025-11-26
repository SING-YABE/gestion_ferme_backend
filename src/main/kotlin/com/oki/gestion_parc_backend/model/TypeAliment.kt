package com.oki.gestion_parc_backend.model

import jakarta.persistence.*

@Entity
data class TypeAliment(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true)
    val libelle: String = ""
)
