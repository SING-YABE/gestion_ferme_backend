package com.oki.gestion_parc_backend.mapper

import com.oki.gestion_parc_backend.dto.IngredientAlimentationDto
import com.oki.gestion_parc_backend.model.IngredientAlimentation

object IngredientAlimentationMapper {
    fun toDto(entity: IngredientAlimentation) = IngredientAlimentationDto(
        ingredientId = entity.ingredient.id,
        ingredientNom = entity.ingredient.nom,
        typeAlimentId = entity.ingredient.typeAliment.id,
        typeAlimentLibelle = entity.ingredient.typeAliment.libelle,
        quantiteKg = entity.quantiteKg,
        prixUnitaire = entity.prixUnitaire,
        sousTotal = entity.sousTotal
    )
}