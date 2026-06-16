package com.oki.gestion_parc_backend.controller

import com.oki.gestion_parc_backend.repository.PermissionOverrideRepository
import com.oki.gestion_parc_backend.repository.UtilisateurRepository
import com.oki.gestion_parc_backend.security.RolePermissions
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*

/**
 * Profil de l'utilisateur connecté avec ses permissions effectives.
 * Utilisé par le frontend au démarrage pour adapter l'interface.
 */
data class MeResponse(
    val id: Long,
    val nom: String,
    val prenom: String,
    val email: String,
    val poste: String,
    val role: String?,
    val permissions: List<String>,   // permissions effectives (rôle + overrides)
    val mustChangePassword: Boolean = false
)

@RestController
@RequestMapping("/api/me")
class MeController(
    private val utilisateurRepo: UtilisateurRepository,
    private val overrideRepo: PermissionOverrideRepository,
    private val passwordEncoder: PasswordEncoder
) {

    /**
     * GET /api/me
     * Retourne le profil complet + permissions effectives de l'utilisateur authentifié.
     * Appelé par le frontend après login pour initialiser l'état de l'application.
     */
    @GetMapping
    fun getMe(@AuthenticationPrincipal userDetails: UserDetails): ResponseEntity<MeResponse> {
        val utilisateur = utilisateurRepo.findByEmail(userDetails.username)
            ?: return ResponseEntity.notFound().build()

        val roleName = utilisateur.role?.nom ?: "ROLE_OUVRIER"

        // Permissions de base du rôle
        val effectivePermissions = RolePermissions
            .permissionsFor(roleName)
            .map { it.name }
            .toMutableSet()

        // Application des overrides
        overrideRepo
            .findByUtilisateurIdUtilisateur(utilisateur.idUtilisateur)
            .forEach { override ->
                if (override.accorde) effectivePermissions.add(override.permission)
                else effectivePermissions.remove(override.permission)
            }

        return ResponseEntity.ok(
            MeResponse(
                id                 = utilisateur.idUtilisateur,
                nom                = utilisateur.nom,
                prenom             = utilisateur.prenom,
                email              = utilisateur.email,
                poste              = utilisateur.poste,
                role               = roleName,
                permissions        = effectivePermissions.sorted(),
                mustChangePassword = utilisateur.mustChangePassword
            )
        )
    }

    // ── Changement de mot de passe (première connexion ou volontaire) ─────────

    data class ChangePasswordRequest(
        val currentPassword: String,
        val newPassword: String
    )

    /**
     * POST /api/me/change-password
     * Vérifie l'ancien mot de passe, encode le nouveau, remet mustChangePassword à false.
     */
    @PostMapping("/change-password")
    fun changePassword(
        @AuthenticationPrincipal userDetails: UserDetails,
        @RequestBody req: ChangePasswordRequest
    ): ResponseEntity<Any> {
        val utilisateur = utilisateurRepo.findByEmail(userDetails.username)
            ?: return ResponseEntity.notFound().build()

        if (!passwordEncoder.matches(req.currentPassword, utilisateur.password)) {
            return ResponseEntity.badRequest()
                .body(mapOf("error" to "Mot de passe actuel incorrect."))
        }

        if (req.newPassword.length < 6) {
            return ResponseEntity.badRequest()
                .body(mapOf("error" to "Le nouveau mot de passe doit contenir au moins 6 caractères."))
        }

        utilisateur.password           = passwordEncoder.encode(req.newPassword)
        utilisateur.mustChangePassword = false
        utilisateurRepo.save(utilisateur)

        return ResponseEntity.ok(mapOf("message" to "Mot de passe mis à jour avec succès."))
    }
}
