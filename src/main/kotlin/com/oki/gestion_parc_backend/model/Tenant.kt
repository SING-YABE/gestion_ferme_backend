package com.oki.gestion_parc_backend.model

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * Entité globale stockée dans le schéma PUBLIC de PostgreSQL.
 * Chaque enregistrement représente une ferme cliente (tenant SaaS).
 *
 * Entrées :
 *   - fermeCode  : code court utilisé au login mobile (ex: "ferme_bf_001")
 *   - nomFerme   : nom affiché (ex: "Ferme Kaboré")
 *   - schemaName : nom du schéma PostgreSQL dédié (ex: "ferme_bf_001")
 *
 * @Table(schema = "public") garantit que Hibernate qualifie toujours la table
 * avec "public.tenants", quel que soit le search_path courant.
 */
@Entity
@Table(name = "tenants", schema = "public")
class Tenant(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    /** Code court unique, utilisé par l'utilisateur lors du login. */
    @Column(name = "ferme_code", unique = true, nullable = false)
    val fermeCode: String = "",

    /** Nom affiché de la ferme. */
    @Column(name = "nom_ferme", nullable = false)
    val nomFerme: String = "",

    /** Nom du schéma PostgreSQL dédié à ce tenant. */
    @Column(name = "schema_name", unique = true, nullable = false)
    val schemaName: String = "",

    /** Indique si le tenant est actif (soft-disable possible). */
    @Column(nullable = false)
    val active: Boolean = true,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
