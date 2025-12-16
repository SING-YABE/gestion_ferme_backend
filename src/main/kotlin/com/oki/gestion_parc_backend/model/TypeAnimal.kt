package com.oki.gestion_parc_backend.model

import jakarta.persistence.*

@Entity
data class TypeAnimal(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val nom: String,

    @Column(nullable = false, length = 5)
    val prefix: String,

    val description: String? = null
)



