package com.oki.gestion_parc_backend.model

import jakarta.persistence.*

/**
 * Surcharge de permission individuelle pour un utilisateur.
 *
 * Permet d'accorder ou de révoquer une permission spécifique
 * indépendamment du rôle de base de l'utilisateur.
 *
 * Exemples d'usage :
 *  - Ouvrier qui exceptionnellement peut voir les stats → accorde=true, permission=ANIMAL_STATS
 *  - Responsable qui ne doit pas accéder aux ventes    → accorde=false, permission=VENTE_READ
 */
@Entity
@Table(
    name = "permission_overrides",
    uniqueConstraints = [UniqueConstraint(columnNames = ["utilisateur_id", "permission"])]
)
data class PermissionOverride(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    /** Utilisateur concerné par cette surcharge */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "utilisateur_id", nullable = false)
    val utilisateur: Utilisateur,

    /**
     * Nom de la permission (valeur de l'enum Permission).
     * Stocké en String pour éviter une dépendance stricte sur l'enum en base.
     */
    @Column(nullable = false, length = 100)
    val permission: String,

    /**
     * true  → permission accordée (même si le rôle ne l'a pas)
     * false → permission révoquée (même si le rôle l'a)
     */
    @Column(nullable = false)
    val accorde: Boolean
)
