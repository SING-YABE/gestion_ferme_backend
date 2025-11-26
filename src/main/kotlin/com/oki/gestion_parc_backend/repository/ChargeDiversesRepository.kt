package com.oki.gestion_parc_backend.repository
import com.oki.gestion_parc_backend.model.ChargeDiverses
import org.springframework.data.jpa.repository.JpaRepository

interface ChargeDiversesRepository : JpaRepository<ChargeDiverses, Long>
