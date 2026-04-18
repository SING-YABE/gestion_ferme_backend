package com.oki.gestion_parc_backend.repository


import com.oki.gestion_parc_backend.model.HistoriqueDeplacement
import com.oki.gestion_parc_backend.model.Animal
import org.springframework.data.jpa.repository.JpaRepository

interface HistoriqueDeplacementRepository : JpaRepository<HistoriqueDeplacement, Long> {
    fun findByAnimalOrderByDateDeplacementDesc(animal: Animal): List<HistoriqueDeplacement>
}