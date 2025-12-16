package com.oki.gestion_parc_backend.model

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "soin_animal")
data class SoinAnimal(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "animal_id")
    val animal: Animal? = null,

    @Column(name = "date_soin", nullable = false)
    val dateSoin: LocalDate,

    @Column(nullable = false)
    val motif: String,

    @Column(nullable = false)
    val traitement: String,

    @Column(name = "traitement_apporte")
    val traitementApporte: String? = null,

    @Column(nullable = false)
    val cout: Double = 0.0,

    @Column(name = "cout_medicament", nullable = false)
    val coutMedicament: Double = 0.0,

    @Column(name = "total_prestation", nullable = false)
    val totalPrestation: Double = 0.0,

    @Column(nullable = false)
    val veterinaire: String,

    val observations: String? = null,

    @Column(name = "soin_collectif", nullable = false)
    val soinCollectif: Boolean = false
)
