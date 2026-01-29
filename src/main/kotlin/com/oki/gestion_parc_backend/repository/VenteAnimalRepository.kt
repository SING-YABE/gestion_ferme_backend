package com.oki.gestion_parc_backend.repository

import com.oki.gestion_parc_backend.model.VenteAnimal
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface VenteAnimalRepository : JpaRepository<VenteAnimal, Long>