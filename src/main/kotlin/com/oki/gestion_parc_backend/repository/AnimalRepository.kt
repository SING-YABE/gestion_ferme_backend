package com.oki.gestion_parc_backend.repository

import com.oki.gestion_parc_backend.model.Animal
import com.oki.gestion_parc_backend.model.Box
import com.oki.gestion_parc_backend.model.Reproduction
import com.oki.gestion_parc_backend.model.TypeAnimal
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository


interface AnimalRepository : JpaRepository<Animal, Long> {
    fun countByTypeAnimal(typeAnimal: TypeAnimal): Long

    fun findByCodeAnimal(code: String): Optional<Animal>

    fun countByReproduction(reproduction: Reproduction): Long

    fun findByVenduFalse(): List<Animal>
    fun countByBoxAndVenduFalse(box: Box): Long

    /** Compte tous les animaux actifs (non vendus) — utilisé pour vérifier la limite FREE */
    fun countByVenduFalse(): Long


}





