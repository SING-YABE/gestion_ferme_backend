package com.oki.gestion_parc_backend.model

import jakarta.persistence.*
import java.time.LocalDate

@Entity
data class Alimentation(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val date: LocalDate = LocalDate.now(),
    @ManyToOne(optional = false)
    val typeAliment: TypeAliment,

    val quantiteKg: Double = 0.0,

    val prixUnitaire: Double = 0.0,

    // alimentation ciblée pour 1 animal
    @ManyToOne(optional = true)
    val animal: Animal? = null,

    // alimentation par LOT
    @ManyToOne(optional = true)
    val typeAnimal: TypeAnimal? = null,

    @ManyToOne(optional = true)
    val fournisseur: Fournisseur? = null
) {
    val coutTotal: Double
        get() = quantiteKg * prixUnitaire
}