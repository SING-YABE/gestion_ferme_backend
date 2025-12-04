package com.oki.gestion_parc_backend.model


import jakarta.persistence.*
import java.time.LocalDate

@Entity
data class Vente(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val dateVente: LocalDate,

    @Column(nullable = false)
    val type: String,

    @Column(nullable = false)
    val quantite: Double,

    @Column(nullable = false)
    val poidsTotal: Double,

    @Column(nullable = false)
    val prixUnitaire: Double,

    @Column(nullable = false)
    val montantTotal: Double,

    @Column(nullable = false)
    val client: String
)
