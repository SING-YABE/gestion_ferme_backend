package com.oki.gestion_parc_backend.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import javax.sql.DataSource

@RestController
class HealthController(private val dataSource: DataSource) {

    @GetMapping("/health")
    fun health() = mapOf("status" to "UP")

    /**
     * Endpoint de diagnostic — NE PAS LAISSER EN PRODUCTION.
     * Inspecte l'état réel de la BD PostgreSQL :
     *   - Tous les schémas existants
     *   - Tables dans ferme_default (ou autre schéma)
     *   - Nombre de lignes dans utilisateurs si elle existe
     */
    @GetMapping("/health/debug")
    fun debug(): Map<String, Any> {
        val result = mutableMapOf<String, Any>()

        dataSource.connection.use { conn ->

            // ── 1. Schémas existants ──────────────────────────────────────────
            val schemas = mutableListOf<String>()
            val rsSchemas = conn.metaData.getSchemas()
            while (rsSchemas.next()) schemas.add(rsSchemas.getString("TABLE_SCHEM"))
            rsSchemas.close()
            result["schemas"] = schemas

            // ── 2. Tables dans ferme_default ──────────────────────────────────
            val tables = mutableListOf<String>()
            val rsTables = conn.metaData.getTables(null, "ferme_default", "%", arrayOf("TABLE"))
            while (rsTables.next()) tables.add(rsTables.getString("TABLE_NAME"))
            rsTables.close()
            result["ferme_default_tables"] = tables

            // ── 3. Inspecter les utilisateurs si la table existe ─────────────
            if (tables.contains("utilisateurs")) {
                conn.createStatement().execute("""SET search_path TO "ferme_default", public""")
                conn.createStatement().use { stmt ->
                    val rsCount = stmt.executeQuery("SELECT COUNT(*) FROM utilisateurs")
                    if (rsCount.next()) result["utilisateurs_count"] = rsCount.getLong(1)
                    rsCount.close()
                }
                // Liste emails + début du hash pour vérifier l'encodage
                val users = mutableListOf<String>()
                conn.createStatement().use { stmt ->
                    val rsUsers = stmt.executeQuery(
                        "SELECT email, LEFT(password, 7) as pwd_prefix FROM utilisateurs"
                    )
                    while (rsUsers.next())
                        users.add("${rsUsers.getString(1)} | pwd_prefix=${rsUsers.getString(2)}")
                    rsUsers.close()
                }
                result["utilisateurs"] = users
            } else {
                result["utilisateurs_count"] = "TABLE DOES NOT EXIST"
            }

            // ── 4. Tenants dans public ────────────────────────────────────────
            val tenants = mutableListOf<String>()
            try {
                val rsTenants = conn.createStatement().executeQuery(
                    "SELECT schema_name, ferme_code, active FROM public.tenants"
                )
                while (rsTenants.next())
                    tenants.add("${rsTenants.getString(1)} | ${rsTenants.getString(2)} | active=${rsTenants.getBoolean(3)}")
                rsTenants.close()
            } catch (e: Exception) {
                tenants.add("ERROR: ${e.message}")
            }
            result["tenants"] = tenants
        }

        return result
    }
}
