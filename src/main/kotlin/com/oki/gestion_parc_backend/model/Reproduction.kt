package com.oki.gestion_parc_backend.model

import jakarta.persistence.*
import java.time.LocalDate

@Entity
data class Reproduction(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "truie_id", nullable = false)
    val truie: Animal, // référence à l'animal femelle

    @Column(nullable = false)
    val dateSaillie: LocalDate,

    @ManyToOne
    @JoinColumn(name = "verrat_id", nullable = false)
    val verrat: Animal, // référence à l'animal mâle

    @Column(nullable = false)
    val dateMiseBasPrevue: LocalDate,

    val dateMiseBasReelle: LocalDate? = null,

    val nbNesVivants: Int? = null,
    val nbMortsNes: Int? = null,
    val nbSevres: Int? = null,

    val observations: String? = null
)
