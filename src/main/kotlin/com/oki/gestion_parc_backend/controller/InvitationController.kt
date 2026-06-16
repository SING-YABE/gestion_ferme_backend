package com.oki.gestion_parc_backend.controller

import com.oki.gestion_parc_backend.model.Utilisateur
import com.oki.gestion_parc_backend.repository.RoleRepository
import com.oki.gestion_parc_backend.repository.UtilisateurRepository
import com.oki.gestion_parc_backend.security.TenantContext
import com.oki.gestion_parc_backend.service.InvitationService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*

/**
 * Endpoints liés aux invitations utilisateur.
 *
 * POST /api/invitations              → Admin crée une invitation email (protégé, UTILISATEUR_WRITE)
 * POST /api/invitations/validate     → Public, valide le token + retourne JWT
 * POST /api/invitations/create-direct → Admin crée un utilisateur avec mot de passe temporaire
 */
@RestController
@RequestMapping("/api/invitations")
@CrossOrigin(origins = ["*"])
class InvitationController(
    private val invitationService: InvitationService,
    private val utilisateurRepository: UtilisateurRepository,
    private val roleRepository: RoleRepository,
    private val passwordEncoder: PasswordEncoder
) {

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

    /**
     * Mode 1 — Invitation par email.
     * Crée un compte en attente et envoie un lien d'activation.
     */
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
        } catch (e: IllegalStateException) {
            ResponseEntity.internalServerError().body(mapOf("error" to (e.message ?: "Erreur serveur")))
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

    // ── Mode 2 : Création directe avec mot de passe temporaire ───────────────

    data class CreateDirectRequest(
        val prenom: String,
        val nom: String,
        val email: String,
        val poste: String = "",
        val telephone: String = "",
        val roleId: Long,
        val temporaryPassword: String? = null   // null = auto-généré
    )

    data class CreateDirectResponse(
        val message: String,
        val temporaryPassword: String   // retourné pour que l'admin puisse le communiquer
    )

    /**
     * Mode 2 — Création directe avec mot de passe temporaire.
     *
     * - Crée un compte immédiatement actif (pas d'email)
     * - mustChangePassword = true → le front forcera le changement à la 1ère connexion
     * - Retourne le mot de passe temporaire en clair pour que l'admin le communique
     *   via WhatsApp / téléphone
     */
    @PostMapping("/create-direct")
    @PreAuthorize("hasAuthority('UTILISATEUR_WRITE')")
    fun createDirect(@RequestBody req: CreateDirectRequest): ResponseEntity<Any> {
        return try {
            // Vérifier que l'email n'existe pas déjà
            if (utilisateurRepository.findByEmail(req.email) != null) {
                return ResponseEntity.badRequest()
                    .body(mapOf("error" to "Un utilisateur avec cet email existe déjà."))
            }

            val role = roleRepository.findById(req.roleId).orElseThrow {
                IllegalArgumentException("Rôle introuvable.")
            }

            // Générer un mot de passe temporaire si non fourni
            val tempPassword = req.temporaryPassword?.takeIf { it.isNotBlank() }
                ?: generateTemporaryPassword()

            val user = Utilisateur(
                prenom             = req.prenom,
                nom                = req.nom,
                email              = req.email,
                poste              = req.poste,
                telephone          = req.telephone,
                password           = passwordEncoder.encode(tempPassword),
                role               = role,
                mustChangePassword = true
            )
            utilisateurRepository.save(user)

            ResponseEntity.ok(
                CreateDirectResponse(
                    message            = "Utilisateur ${req.prenom} ${req.nom} créé avec succès.",
                    temporaryPassword  = tempPassword
                )
            )
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to (e.message ?: "Erreur")))
        }
    }

    /**
     * Génère un mot de passe temporaire de la forme Ferme@XXXX
     * (facile à dicter par téléphone, respecte les règles de sécurité de base).
     */
    private fun generateTemporaryPassword(): String {
        val digits = (1000..9999).random()
        return "Ferme@$digits"
    }
}
