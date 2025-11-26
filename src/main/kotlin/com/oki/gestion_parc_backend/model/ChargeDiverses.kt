package com.oki.gestion_parc_backend.model
import jakarta.persistence.*
import java.time.LocalDate

@Entity
data class ChargeDiverses(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val date: LocalDate,

    @ManyToOne
    @JoinColumn(name = "type_depense_id")
    val typeDepense: TypeDepense,

    val description: String,

    val montant: Double,

    val modePaiement: String,

    val observations: String? = null
)
