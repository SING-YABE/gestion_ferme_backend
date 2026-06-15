package com.oki.gestion_parc_backend.security

import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider
import org.springframework.stereotype.Component
import java.sql.Connection
import javax.sql.DataSource

/**
 * Hook Hibernate (API 6.x) : route les connexions JDBC vers le schéma du tenant courant.
 *
 * Mécanisme PostgreSQL : SET search_path TO "schema", public
 *   - Les tables sans qualificateur de schéma sont résolues dans "schema"
 *   - Les tables avec @Table(schema="public") sont toujours dans public (ex: tenants)
 *
 * getAnyConnection()               → connexion en search_path=public  (bootstrap Hibernate)
 * getConnection(schema)            → connexion en search_path=schema,public
 * releaseConnection(schema, conn)  → reset à public avant retour au pool HikariCP
 *
 * supportsAggressiveRelease = false : conserve la connexion pendant toute la transaction,
 * évitant un changement de search_path intempestif entre deux opérations liées.
 */
@Component
class MultiTenantConnectionProviderImpl(
    private val dataSource: DataSource
) : MultiTenantConnectionProvider<String> {

    private fun setSchema(conn: Connection, schema: String) {
        conn.createStatement().use { stmt ->
            stmt.execute("""SET search_path TO "$schema", public""")
        }
    }

    private fun resetToPublic(conn: Connection) {
        conn.createStatement().use { stmt ->
            stmt.execute("SET search_path TO public")
        }
    }

    override fun getAnyConnection(): Connection =
        dataSource.connection.also { resetToPublic(it) }

    override fun releaseAnyConnection(connection: Connection) {
        resetToPublic(connection)
        connection.close()
    }

    override fun getConnection(tenantIdentifier: String): Connection =
        dataSource.connection.also { setSchema(it, tenantIdentifier) }

    override fun releaseConnection(tenantIdentifier: String, connection: Connection) {
        resetToPublic(connection)
        connection.close()
    }

    /** false = Hibernate conserve la connexion pendant toute la transaction. */
    override fun supportsAggressiveRelease(): Boolean = false

    override fun isUnwrappableAs(unwrapType: Class<*>): Boolean = false

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> unwrap(unwrapType: Class<T>): T =
        throw UnsupportedOperationException("MultiTenantConnectionProviderImpl ne supporte pas unwrap")
}
