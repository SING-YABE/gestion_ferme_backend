package com.oki.gestion_parc_backend.config

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Component

/**
 * TEMPORAIRE — À SUPPRIMER après le premier déploiement réussi.
 *
 * Supprime l'ancien schéma "ferme_default" (créé avant la migration multi-tenant)
 * dont les colonnes ne respectent pas le nommage snake_case attendu par Hibernate.
 * SchemaCreationService recrée tout proprement au prochain démarrage.
 */
@Component
@Profile("!test")
class DatabaseCleanupRunner(private val jdbcTemplate: JdbcTemplate) : CommandLineRunner {

    private val logger = LoggerFactory.getLogger(DatabaseCleanupRunner::class.java)

    override fun run(vararg args: String?) {
        try {
            val schemaExists = jdbcTemplate.queryForObject(
                "SELECT EXISTS(SELECT 1 FROM information_schema.schemata WHERE schema_name = 'ferme_default')",
                Boolean::class.java
            ) ?: false

            if (schemaExists) {
                logger.info("🧹 Suppression de l'ancien schéma ferme_default...")
                jdbcTemplate.execute("DROP SCHEMA IF EXISTS ferme_default CASCADE")
                logger.info("✅ Schéma ferme_default supprimé.")
            } else {
                logger.info("ℹ️ Schéma ferme_default absent — rien à nettoyer.")
            }

            val tenantsExists = jdbcTemplate.queryForObject(
                "SELECT EXISTS(SELECT 1 FROM information_schema.tables WHERE table_schema = 'public' AND table_name = 'tenants')",
                Boolean::class.java
            ) ?: false

            if (tenantsExists) {
                logger.info("🧹 Suppression de la table public.tenants...")
                jdbcTemplate.execute("DROP TABLE IF EXISTS public.tenants CASCADE")
                logger.info("✅ Table public.tenants supprimée.")
            }

        } catch (e: Exception) {
            logger.error("❌ Erreur lors du nettoyage DB : ${e.message}", e)
        }
    }
}
