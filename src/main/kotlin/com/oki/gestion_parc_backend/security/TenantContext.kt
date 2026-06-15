package com.oki.gestion_parc_backend.security

/**
 * Stocke l'identifiant du schéma PostgreSQL courant dans un ThreadLocal.
 *
 * Cycle de vie par requête :
 *   - SET  : JwtAuthenticationFilter (requêtes authentifiées) ou LoginController (login)
 *   - GET  : CurrentTenantIdentifierResolverImpl → transmis à Hibernate
 *   - CLEAR: JwtAuthenticationFilter (finally) ou LoginController (finally)
 *
 * Jamais de fuite entre requêtes car chaque thread HTTP a son propre ThreadLocal.
 */
object TenantContext {

    private val currentTenant = ThreadLocal<String?>()

    /** Définit le schéma actif pour le thread courant. */
    fun setTenant(tenant: String) {
        currentTenant.set(tenant)
    }

    /** Retourne le schéma actif, ou null si non défini. */
    fun getTenant(): String? = currentTenant.get()

    /** Supprime le schéma du thread courant (obligatoire en fin de requête). */
    fun clear() {
        currentTenant.remove()
    }
}
