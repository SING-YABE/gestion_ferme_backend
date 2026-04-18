package com.oki.gestion_parc_backend.model


import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
data class HistoriqueDeplacement(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "animal_id", nullable = false)
    val animal: Animal,

    @ManyToOne
    @JoinColumn(name = "ancienne_box_id")
    val ancienneBox: Box?,

    @ManyToOne
    @JoinColumn(name = "nouvelle_box_id", nullable = false)
    val nouvelleBox: Box,

    val dateDeplacement: LocalDateTime = LocalDateTime.now(),

    val motif: String? = null
)