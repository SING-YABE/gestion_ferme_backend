package com.oki.gestion_parc_backend.model

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * Super administrateur de la plateforme SaaS.
 * Stocké dans le schéma PUBLIC — n'appartient à aucune ferme.
 *
 * Le Super Admin peut :
 *   - Voir toutes les fermes inscrites
 *   - Activer / désactiver une ferme
 *   - Configurer les limites des plans FREE / PREMIUM
 *   - Consulter les statistiques globales de la plateforme
 */
@Entity
@Table(name = "super_admins", schema = "public")
class SuperAdmin(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(unique = true, nullable = false)
    val email: String = "",

    @Column(nullable = false)
    var password: String = "",

    @Column(nullable = false)
    val nom: String = "",

    @Column(nullable = false)
    val prenom: String = "",

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
