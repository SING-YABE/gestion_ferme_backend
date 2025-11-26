package com.oki.gestion_parc_backend.repository

import com.oki.gestion_parc_backend.model.TypeAliment
import org.springframework.data.jpa.repository.JpaRepository

interface TypeAlimentRepository : JpaRepository<TypeAliment, Long>
