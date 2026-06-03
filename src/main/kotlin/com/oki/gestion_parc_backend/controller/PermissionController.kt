package com.oki.gestion_parc_backend.controller

import com.oki.gestion_parc_backend.model.PermissionOverride
import com.oki.gestion_parc_backend.repository.PermissionOverrideRepository
import com.oki.gestion_parc_backend.repository.UtilisateurRepository
import com.oki.gestion_parc_backend.security.Permission
import com.oki.gestion_parc_backend.security.RolePermissions
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*

// ─── DTOs locaux ────────────────────────────────────────────────────────────

/** Corps de la requête pour accorder ou révoquer une permission */
data class OverrideRequest(
    val permission: String,
    val accorde: Boolean        // true = accorder, false = révoquer
)

/** Réponse listant les permissions effectives d'un utilisateur */
data class UtilisateurPermissionsDTO(
    val utilisateurId: Long,
    val email: String,
    val role: String?,
    val permissionsDeBase: List<String>,
    val overrides: List<OverrideResponseDTO>,
    val permissionsEffectives: List<String>
)

data class OverrideResponseDTO(
    val id: Long,
    val permission: String,
    val accorde: Boolean
)

// ─── Contrôleur ─────────────────────────────────────────────────────────────

/**
 * Gestion dynamique des permissions par utilisateur.
 * Accessible uniquement à l'ADMINISTRATEUR.
 *
 * Endpoints :
 *   GET    /api/permissions/{userId}           → permissions effectives d'un utilisateur
 *   POST   /api/permissions/{userId}           → ajouter/modifier un override
 *   DELETE /api/permissions/{userId}/{perm}    → supprimer un override (retour aux droits du rôle)
 *   GET    /api/permissions/disponibles        → liste de toutes les permissions disponibles
 */
@RestController
@RequestMapping("/api/permissions")
@PreAuthorize("hasAuthority('PERMISSION_MANAGE')")
class PermissionController(
    private val overrideRepo: PermissionOverrideRepository,
    private val utilisateurRepo: UtilisateurRepository
) {

    /**
     * Retourne les permissions effectives d'un utilisateur :
     * permissions de base du rôle + overrides appliqués.
     */
    @GetMapping("/{userId}")
    fun getPermissions(@PathVariable userId: Long): ResponseEntity<UtilisateurPermissionsDTO> {
        val user = utilisateurRepo.findById(userId)
            .orElseThrow { IllegalArgumentException("Utilisateur $userId introuvable") }

        val roleName = user.role?.nom ?: "ROLE_OUVRIER"
        val basePerms = RolePermissions.permissionsFor(roleName).map { it.name }
        val overrides = overrideRepo.findByUtilisateurIdUtilisateur(userId)

        // Calcul des permissions effectives
        val effective = basePerms.toMutableSet()
        overrides.forEach { ov ->
            if (ov.accorde) effective.add(ov.permission) else effective.remove(ov.permission)
        }

        return ResponseEntity.ok(
            UtilisateurPermissionsDTO(
                utilisateurId        = user.idUtilisateur,
                email                = user.email,
                role                 = roleName,
                permissionsDeBase    = basePerms.sorted(),
                overrides            = overrides.map { OverrideResponseDTO(it.id, it.permission, it.accorde) },
                permissionsEffectives = effective.sorted()
            )
        )
    }

    /**
     * Accorde ou révoque une permission pour un utilisateur.
     * Si un override existe déjà pour cette permission, il est mis à jour.
     */
    @PostMapping("/{userId}")
    @Transactional
    fun setOverride(
        @PathVariable userId: Long,
        @RequestBody request: OverrideRequest
    ): ResponseEntity<OverrideResponseDTO> {
        // Validation : la permission doit exister dans l'enum
        val permName = request.permission.uppercase()
        runCatching { Permission.valueOf(permName) }
            .getOrElse { throw IllegalArgumentException("Permission '$permName' inconnue") }

        val user = utilisateurRepo.findById(userId)
            .orElseThrow { IllegalArgumentException("Utilisateur $userId introuvable") }

        // Supprimer l'override existant s'il y en a un, puis recréer
        overrideRepo.deleteByUtilisateurIdUtilisateurAndPermission(userId, permName)

        val saved = overrideRepo.save(
            PermissionOverride(
                utilisateur = user,
                permission  = permName,
                accorde     = request.accorde
            )
        )

        return ResponseEntity.ok(OverrideResponseDTO(saved.id, saved.permission, saved.accorde))
    }

    /**
     * Supprime un override : l'utilisateur retrouve les droits par défaut de son rôle.
     */
    @DeleteMapping("/{userId}/{permission}")
    @Transactional
    fun removeOverride(
        @PathVariable userId: Long,
        @PathVariable permission: String
    ): ResponseEntity<Void> {
        overrideRepo.deleteByUtilisateurIdUtilisateurAndPermission(userId, permission.uppercase())
        return ResponseEntity.noContent().build()
    }

    /**
     * Liste toutes les permissions disponibles dans le système.
     * Utile pour le frontend qui construit le formulaire d'attribution.
     */
    @GetMapping("/disponibles")
    fun listPermissionsDisponibles(): ResponseEntity<List<String>> {
        return ResponseEntity.ok(Permission.values().map { it.name }.sorted())
    }

    /**
     * Retourne les permissions de base associées à chaque rôle.
     * Pratique pour afficher ce qu'un rôle apporte avant d'assigner un utilisateur.
     */
    @GetMapping("/par-role")
    fun permissionsParRole(): ResponseEntity<Map<String, List<String>>> {
        val result = mapOf(
            "ROLE_OUVRIER"        to RolePermissions.permissionsFor("ROLE_OUVRIER").map { it.name }.sorted(),
            "ROLE_RESPONSABLE"    to RolePermissions.permissionsFor("ROLE_RESPONSABLE").map { it.name }.sorted(),
            "ROLE_GERANT"         to RolePermissions.permissionsFor("ROLE_GERANT").map { it.name }.sorted(),
            "ROLE_ADMINISTRATEUR" to RolePermissions.permissionsFor("ROLE_ADMINISTRATEUR").map { it.name }.sorted(),
        )
        return ResponseEntity.ok(result)
    }
}
