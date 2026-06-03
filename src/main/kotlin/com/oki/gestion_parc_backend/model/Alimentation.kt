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
    val fournisseur: Fournisseur? = null,

    /**
     * Référence documentaire utilisée pour définir cette ration.
     * Exemples : "DGPA/MRAH 2021", "AVIPRO/WISIUM", "ALF ISSEN", "ONG Thamani"
     *
     * SOURCE: Fiche simplifiée alimentation des porcs — DGPA/MRAH, Burkina Faso — Juin 2021
     * SOURCE: Fiche technique n°3 alimentation porcs — ONG Thamani, Bobo-Dioulasso — secteur 24
     */
    @Column(nullable = true, length = 200)
    val sourceReference: String? = null
) {
    val coutTotal: Double
        get() = ingredients.sumOf { it.sousTotal }
}
