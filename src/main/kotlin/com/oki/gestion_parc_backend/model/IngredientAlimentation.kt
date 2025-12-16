package com.oki.gestion_parc_backend.model

import jakarta.persistence.*

@Entity
@Table(name = "ingredient_alimentation")
data class IngredientAlimentation(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(optional = false)
    @JoinColumn(name = "alimentation_id")
    val alimentation: Alimentation,

    @ManyToOne(optional = false)
    @JoinColumn(name = "ingredient_id")
    val ingredient: Ingredient,

    @Column(nullable = false)
    val quantiteKg: Double,

    @Column(nullable = false)
    val prixUnitaire: Double
) {
    val sousTotal: Double
        get() = quantiteKg * prixUnitaire
}