package com.oki.gestion_parc_backend.model


import jakarta.persistence.*
import java.time.LocalDate
@Entity
class Vente(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false)
    var dateVente: LocalDate,

    @Column(nullable = true)
    var dateEnlevement: LocalDate?,


    @Column(nullable = true)
    var dateEnlevementAuPlusTard: LocalDate?,

    @Column(nullable = false)
    var client: String,

    @Column(nullable = false)
    var poidsTotal: Double = 0.0,

    @Column(nullable = false)
    var montantTotal: Double = 0.0,

    @OneToMany(mappedBy = "vente", cascade = [CascadeType.ALL], orphanRemoval = true)
    var animaux: MutableList<VenteAnimal> = mutableListOf()
)







