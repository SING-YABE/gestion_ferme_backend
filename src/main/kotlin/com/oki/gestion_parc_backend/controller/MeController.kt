package com.oki.gestion_parc_backend.controller

import com.oki.gestion_parc_backend.repository.PermissionOverrideRepository
import com.oki.gestion_parc_backend.repository.UtilisateurRepository
import com.oki.gestion_parc_backend.security.RolePermissions
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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
    val permissions: List<String>    // permissions effectives (rôle + overrides)
)

@RestController
@RequestMapping("/api/me")
class MeController(
    private val utilisateurRepo: UtilisateurRepository,
    private val overrideRepo: PermissionOverrideRepository
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
                id          = utilisateur.idUtilisateur,
                nom         = utilisateur.nom,
                prenom      = utilisateur.prenom,
                email       = utilisateur.email,
                poste       = utilisateur.poste,
                role        = roleName,
                permissions = effectivePermissions.sorted()
            )
        )
    }
}
