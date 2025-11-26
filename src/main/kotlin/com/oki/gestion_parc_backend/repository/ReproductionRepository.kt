package com.oki.gestion_parc_backend.repository

import com.oki.gestion_parc_backend.model.Reproduction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReproductionRepository : JpaRepository<Reproduction, Long>
