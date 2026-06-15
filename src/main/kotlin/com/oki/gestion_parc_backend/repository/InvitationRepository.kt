package com.oki.gestion_parc_backend.repository

import com.oki.gestion_parc_backend.model.Invitation
import org.springframework.data.jpa.repository.JpaRepository

/**
 * Accès aux invitations stockées dans le schéma courant du tenant.
 * Le TenantContext doit être positionné avant tout appel.
 */
interface InvitationRepository : JpaRepository<Invitation, Long> {
    fun findByToken(token: String): Invitation?
}
