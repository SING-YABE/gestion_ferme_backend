package com.oki.gestion_parc_backend.model

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * Invitation d'un utilisateur dans une ferme.
 *
 * Stockée dans le schéma du tenant (ferme qui invite).
 * Le token est un UUID unique généré à la création.
 * Durée de vie : 72h. Une fois utilisé, used=true.
 *
 * Flux :
 *   1. Admin crée une invitation → token généré + email envoyé
 *   2. Utilisateur clique le lien → POST /api/invitations/validate?token=xxx
 *   3. Backend active le compte + retourne JWT → connecté directement
 */
@Entity
@Table(name = "invitations")
open class Invitation(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long = 0L,

    /** Token UUID unique envoyé dans le lien email */
    @Column(nullable = false, unique = true, length = 64)
    open var token: String = "",

    /** Email de la personne invitée */
    @Column(nullable = false)
    open var email: String = "",

    /** Prénom de la personne invitée (pour personnaliser l'email) */
    @Column(nullable = false)
    open var prenom: String = "",

    /** Nom de la personne invitée */
    @Column(nullable = false)
    open var nom: String = "",

    /** Schéma du tenant (ferme) — pour router la connexion au validate */
    @Column(name = "schema_name", nullable = false)
    open var schemaName: String = "",

    /** Nom de la ferme (pour l'email d'invitation) */
    @Column(name = "nom_ferme", nullable = false)
    open var nomFerme: String = "",

    /** ID du rôle à assigner lors de l'activation */
    @Column(name = "role_id", nullable = false)
    open var roleId: Long = 0L,

    /** Expiration — 72h après création */
    @Column(name = "expires_at", nullable = false)
    open var expiresAt: LocalDateTime = LocalDateTime.now().plusHours(72),

    /** true dès que l'utilisateur a cliqué le lien */
    @Column(nullable = false)
    open var used: Boolean = false,

    @Column(name = "created_at", nullable = false)
    open var createdAt: LocalDateTime = LocalDateTime.now()
)
