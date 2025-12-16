package com.oki.gestion_parc_backend.model

import jakarta.persistence.*
import java.time.LocalDate

@Entity
data class Alimentation(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val date: LocalDate = LocalDate.now(),

    @Column(nullable = false)
    val mode: String, // "ACHAT" ou "FABRICATION"

    @OneToMany(mappedBy = "alimentation", cascade = [CascadeType.ALL], orphanRemoval = true)
    var ingredients: MutableList<IngredientAlimentation> = mutableListOf(),

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
        get() = ingredients.sumOf { it.sousTotal }
}
