package com.oki.gestion_parc_backend.service

import org.springframework.jdbc.datasource.DelegatingDataSource
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.stereotype.Service
import java.sql.Connection
import java.util.Properties
import javax.sql.DataSource

/**
 * Crée un schéma PostgreSQL et initialise toutes les tables JPA dans ce schéma.
 *
 * Utilisé :
 *   - Au démarrage par DataInitializer → "ferme_default"
 *   - À l'inscription d'un nouveau tenant → "ferme_xxx"
 *
 * Mécanisme :
 *   1. CREATE SCHEMA IF NOT EXISTS via JDBC (DDL auto-commité)
 *   2. EMF temporaire avec un DataSource wrapper qui force search_path au schéma cible
 *      + ddl-auto=update → Hibernate crée toutes les tables dans ce schéma
 *   3. L'EMF est détruit après initialisation (sans impact sur l'EMF principal)
 *
 * Idempotent : CREATE SCHEMA IF NOT EXISTS + ddl-auto=update = sans effet si déjà créé.
 *
 * Entrée  : schemaName (ex: "ferme_default", "ferme_bf_001")
 * Sortie  : schéma PostgreSQL + tables JPA créés ; exception en cas d'erreur.
 */
@Service
class SchemaCreationService(private val dataSource: DataSource) {

    fun initializeSchema(schemaName: String) {
        println("[SchemaCreation] ▶ Initialisation du schéma '$schemaName'...")

        // ── Étape 1 : CREATE SCHEMA ───────────────────────────────────────────
        dataSource.connection.use { conn ->
            conn.createStatement().execute(
                """CREATE SCHEMA IF NOT EXISTS "$schemaName""""
            )
        }
        println("[SchemaCreation] ✓ Schéma '$schemaName' créé (ou déjà existant).")

        // ── Étape 2 : Créer les tables via un EMF temporaire ──────────────────
        // Le DataSource wrapper redirige chaque connexion vers schemaName.
        val schemaBoundDataSource = object : DelegatingDataSource(dataSource) {
            override fun getConnection(): Connection =
                super.getConnection().also { applySchema(it) }

            override fun getConnection(username: String, password: String): Connection =
                super.getConnection(username, password).also { applySchema(it) }

            private fun applySchema(conn: Connection) {
                conn.createStatement().execute(
                    """SET search_path TO "$schemaName", public"""
                )
            }
        }

        val emfBean = LocalContainerEntityManagerFactoryBean().apply {
            this.dataSource = schemaBoundDataSource
            setPackagesToScan("com.oki.gestion_parc_backend.model")
            jpaVendorAdapter = HibernateJpaVendorAdapter()
            setJpaProperties(Properties().apply {
                set("hibernate.hbm2ddl.auto",   "update")
                set("hibernate.dialect",         "org.hibernate.dialect.PostgreSQLDialect")
                set("hibernate.default_schema",  schemaName)
                set("hibernate.show_sql",        "false")
                set("hibernate.format_sql",      "false")
                // IMPORTANT : même naming strategy que le main EMF Spring Boot 3.x
                // CamelCaseToUnderscoresNamingStrategy : idRole → id_role
                set("hibernate.physical_naming_strategy",
                    "org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy")
            })
        }

        try {
            emfBean.afterPropertiesSet()
            println("[SchemaCreation] ✓ Tables JPA créées/mises à jour dans '$schemaName'.")
        } finally {
            emfBean.destroy()
        }
    }
}
