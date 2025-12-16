package com.oki.gestion_parc_backend.repository

import com.oki.gestion_parc_backend.model.Batiment
import com.oki.gestion_parc_backend.model.TypeVente
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TypeVenteRepository : JpaRepository<TypeVente, Long>
