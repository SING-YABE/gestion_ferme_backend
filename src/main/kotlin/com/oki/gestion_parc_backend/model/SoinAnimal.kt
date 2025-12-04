package com.oki.gestion_parc_backend.model
import jakarta.persistence.*
import java.time.LocalDate

@Entity
data class SoinAnimal(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "animal_id")
    val animal: Animal? = null,

    @Column(nullable = false)
    val dateSoin: LocalDate,

    @Column(nullable = false)
    val motif: String,

    @Column(nullable = false)
    val traitement: String,

    @Column(nullable = false)
    val cout: Double,

    @Column(nullable = false)
    val veterinaire: String,

    val observations: String? = null,

    @Column(nullable = false)
    val soinCollectif: Boolean = false
)
