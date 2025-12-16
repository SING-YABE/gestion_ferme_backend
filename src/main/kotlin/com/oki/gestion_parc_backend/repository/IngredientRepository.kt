package com.oki.gestion_parc_backend.repository

import com.oki.gestion_parc_backend.model.Ingredient
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface IngredientRepository : JpaRepository<Ingredient, Long> {
    fun findByTypeAlimentId(typeAlimentId: Long): List<Ingredient>
}