package com.oki.gestion_parc_backend.service.impl

import com.oki.gestion_parc_backend.repository.PermissionOverrideRepository
import com.oki.gestion_parc_backend.repository.UtilisateurRepository
import com.oki.gestion_parc_backend.security.RolePermissions
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service

/**
 * Chargement des détails utilisateur pour Spring Security.
 *
 * Construit les authorities en 3 étapes :
 *  1. Rôle de base (ex: ROLE_GERANT)
 *  2. Permissions du rôle via RolePermissions (registre statique)
 *  3. Application des overrides individuels (PermissionOverride)
 *     → true  = permission accordée en plus
 *     → false = permission retirée du rôle
 */
@Service
class CustomUserDetailsService(
    private val utilisateurRepo: UtilisateurRepository,
    private val overrideRepo: PermissionOverrideRepository
) : UserDetailsService {

    private val passwordEncoder = BCryptPasswordEncoder()

    override fun loadUserByUsername(username: String): UserDetails {
        val utilisateur = utilisateurRepo.findByEmail(username)
            ?: throw UsernameNotFoundException("Utilisateur '$username' introuvable")

        val roleName = utilisateur.role?.nom ?: "ROLE_OUVRIER"

        // 1. Permissions de base liées au rôle
        val effectivePermissions: MutableSet<String> = RolePermissions
            .permissionsFor(roleName)
            .map { it.name }
            .toMutableSet()

        // 2. Application des overrides individuels
        overrideRepo
            .findByUtilisateurIdUtilisateur(utilisateur.idUtilisateur)
            .forEach { override ->
                if (override.accorde) {
                    effectivePermissions.add(override.permission)
                } else {
                    effectivePermissions.remove(override.permission)
                }
            }

        // 3. Authorities = rôle + toutes les permissions effectives
        val authorities: List<GrantedAuthority> = buildList {
            add(SimpleGrantedAuthority(roleName))
            effectivePermissions.forEach { perm ->
                add(SimpleGrantedAuthority(perm))
            }
        }

        return User(utilisateur.email, utilisateur.password, authorities)
    }

}
