package com.oki.gestion_parc_backend.repository

import com.oki.gestion_parc_backend.model.Batiment
import com.oki.gestion_parc_backend.model.Box
import org.springframework.data.jpa.repository.JpaRepository


interface BoxRepository : JpaRepository<Box, Long> {
    fun existsByNumeroAndBatiment(numero: Int, batiment: Batiment): Boolean
    fun findByBatiment(batiment: Batiment): List<Box>
}