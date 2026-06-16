package com.oki.gestion_parc_backend.repository

import com.oki.gestion_parc_backend.model.SuperAdmin
import org.springframework.data.jpa.repository.JpaRepository

/**
 * Repository pour l'entité SuperAdmin (schéma public).
 * Pas de TenantContext nécessaire — l'annotation @Table(schema="public") route directement.
 */
interface SuperAdminRepository : JpaRepository<SuperAdmin, Long> {
    fun findByEmail(email: String): SuperAdmin?
}
