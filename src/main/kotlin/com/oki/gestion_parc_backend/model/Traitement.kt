package com.oki.gestion_parc_backend.model
import jakarta.persistence.*
import java.time.LocalDate

@Entity
data class Traitement(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val date: LocalDate,

    @ManyToOne
    @JoinColumn(name = "animal_id", nullable = false)
    val animal: Animal,

    @Column(nullable = false)
    val traitement: String,

    @Column(nullable = false)
    val motif: String,

    @Column(nullable = false)
    val cout: Double,

    @Column(nullable = false)
    val veterinaire: String,

    val observations: String? = null
)
