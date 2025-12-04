package com.oki.gestion_parc_backend.repository


import com.oki.gestion_parc_backend.model.SoinAnimal
import com.oki.gestion_parc_backend.model.Animal
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SoinAnimalRepository : JpaRepository<SoinAnimal, Long> {
    fun findByAnimal(animal: Animal): List<SoinAnimal>
}
