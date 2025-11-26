package com.oki.gestion_parc_backend.repository

import com.oki.gestion_parc_backend.model.Role
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RoleRepository : JpaRepository<Role, Long> {
    fun findByNom(nom: String): Role?   // <-- Kotlin-friendly
}
