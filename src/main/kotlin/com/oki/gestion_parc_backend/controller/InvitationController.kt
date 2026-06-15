package com.oki.gestion_parc_backend.controller

import com.oki.gestion_parc_backend.service.InvitationService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

/**
 * Endpoints liés aux invitations utilisateur.
 *
 * POST /api/invitations          → Admin crée une invitation (protégé, UTILISATEUR_WRITE)
 * POST /api/invitations/validate → Public, valide le token + retourne JWT
 */
@RestController
@RequestMapping("/api/invitations")
@CrossOrigin(origins = ["*"])
class InvitationController(private val invitationService: InvitationService) {

    data class CreateInvitationRequest(
        val prenom: String,
        val nom: String,
        val email: String,
        val poste: String = "",
        val telephone: String = "",
        val roleId: Long
    )

    data class ValidateRequest(
        val token: String,
        val newPassword: String
    )

    /** Crée une invitation et envoie l'email. Réservé aux admins/gérants. */
    @PostMapping
    @PreAuthorize("hasAuthority('UTILISATEUR_WRITE')")
    fun createInvitation(@RequestBody req: CreateInvitationRequest): ResponseEntity<Any> {
        return try {
            val result = invitationService.createInvitation(
                InvitationService.CreateInvitationRequest(
                    prenom    = req.prenom,
                    nom       = req.nom,
                    email     = req.email,
                    poste     = req.poste,
                    telephone = req.telephone,
                    roleId    = req.roleId
                )
            )
            ResponseEntity.ok(mapOf("message" to result.message))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to (e.message ?: "Erreur")))
        }
    }

    /**
     * Valide le token d'invitation + définit le mot de passe.
     * Retourne un JWT → l'utilisateur est connecté directement.
     * Route publique (pas de JWT requis).
     */
    @PostMapping("/validate")
    fun validateInvitation(@RequestBody req: ValidateRequest): ResponseEntity<Any> {
        return try {
            val result = invitationService.validateInvitation(req.token, req.newPassword)
            ResponseEntity.ok(mapOf(
                "token"    to result.token,
                "role"     to result.role,
                "username" to result.username,
                "nomFerme" to result.nomFerme
            ))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to (e.message ?: "Token invalide")))
        } catch (e: IllegalStateException) {
            ResponseEntity.internalServerError().body(mapOf("error" to (e.message ?: "Erreur serveur")))
        }
    }
}
