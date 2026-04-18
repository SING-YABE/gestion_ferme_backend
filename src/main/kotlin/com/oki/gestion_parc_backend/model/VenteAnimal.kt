package com.oki.gestion_parc_backend.model

import jakarta.persistence.*

@Entity
data class VenteAnimal(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne
    @JoinColumn(name = "vente_id", nullable = false)
    val vente: Vente,

    @ManyToOne
    @JoinColumn(name = "animal_id", nullable = false)
    val animal: Animal,

    @ManyToOne
    @JoinColumn(name = "type_vente_id", nullable = false)
    val typeVente: TypeVente,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val modeVente: ModeVente,

    // Rempli uniquement si AU_POIDS
    val poidsVente: Double? = null,
    val prixUnitaire: Double? = null,

    // Rempli uniquement si SANS_PESEE
    val prixNegocie: Double? = null,

    @Column(nullable = false)
    val montantTotal: Double
)