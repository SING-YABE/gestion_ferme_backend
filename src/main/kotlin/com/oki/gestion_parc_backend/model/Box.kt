package com.oki.gestion_parc_backend.model

import jakarta.persistence.*

@Entity
@Table(
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["numero", "batiment_id"]),
        UniqueConstraint(columnNames = ["code"])
    ]
)
data class Box(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val numero: Int,

    @Column(nullable = false, unique = true)
    val code: String,

    val capaciteMax: Int,

    @ManyToOne
    @JoinColumn(name = "batiment_id", nullable = false)
    val batiment: Batiment
)
