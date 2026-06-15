package com.oki.gestion_parc_backend.repository

import com.oki.gestion_parc_backend.model.PlanConfig
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PlanConfigRepository : JpaRepository<PlanConfig, Long>
