package com.oki.gestion_parc_backend.model
import jakarta.persistence.*
import java.time.LocalDate

@Entity
data class Depense(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val date: LocalDate,

    @ManyToOne
    @JoinColumn(name = "type_depense_id", nullable = false)
    val typeDepense: TypeDepense,

    @Column(nullable = false)
    val description: String,

    @Column(nullable = false)
    val montant: Double,

    @Column(nullable = false)
    val modePaiement: String,

    val observations: String? = null
)
