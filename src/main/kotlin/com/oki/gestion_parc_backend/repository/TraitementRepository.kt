package com.oki.gestion_parc_backend.repository

import com.oki.gestion_parc_backend.model.Traitement
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface TraitementRepository : JpaRepository<Traitement, Long>
