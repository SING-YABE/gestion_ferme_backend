package com.oki.gestion_parc_backend.repository

import com.oki.gestion_parc_backend.model.TypeTache
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TypeTacheRepository : JpaRepository<TypeTache, Long> {
    fun findByNom(nom: String): TypeTache?
}
