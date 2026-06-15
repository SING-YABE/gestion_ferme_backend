package com.oki.gestion_parc_backend.security

import org.hibernate.context.spi.CurrentTenantIdentifierResolver
import org.springframework.stereotype.Component

/**
 * Hook Hibernate (API 6.x) : résout le schéma PostgreSQL actif pour chaque connexion.
 *
 * - Si TenantContext contient un schéma → on l'utilise (requête normale)
 * - Sinon → "public" (bootstrap, endpoints publics, queries globales)
 *
 * Appelé par MultiTenantConnectionProviderImpl avant chaque getConnection().
 */
@Component
class CurrentTenantIdentifierResolverImpl : CurrentTenantIdentifierResolver<String> {

    override fun resolveCurrentTenantIdentifier(): String {
        return TenantContext.getTenant() ?: "public"
    }

    /**
     * Retourne true pour valider les sessions existantes.
     * Nécessaire pour éviter une exception Hibernate au démarrage.
     */
    override fun validateExistingCurrentSessions(): Boolean = true
}
