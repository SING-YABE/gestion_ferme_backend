package com.oki.gestion_parc_backend.repository

import com.oki.gestion_parc_backend.model.Tenant
import org.springframework.data.jpa.repository.JpaRepository

/**
 * Accès à la table public.tenants (registre global des fermes).
 * Fonctionne quel que soit le TenantContext courant car l'entité Tenant
 * a @Table(schema="public") → SQL toujours qualifié "public.tenants".
 */
interface TenantRepository : JpaRepository<Tenant, Long> {
    fun findByFermeCode(fermeCode: String): Tenant?
    fun existsByFermeCode(fermeCode: String): Boolean
    fun existsBySchemaName(schemaName: String): Boolean
    fun findAllByActiveTrue(): List<Tenant>
}
