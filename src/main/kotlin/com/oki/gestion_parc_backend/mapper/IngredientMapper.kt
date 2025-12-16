package com.oki.gestion_parc_backend.mapper
import com.oki.gestion_parc_backend.dto.IngredientDto
import com.oki.gestion_parc_backend.model.Ingredient

object IngredientMapper {
    fun toDto(entity: Ingredient) = IngredientDto(
        id = entity.id,
        nom = entity.nom,
        typeAlimentId = entity.typeAliment.id,
        typeAlimentLibelle = entity.typeAliment.libelle
    )
}