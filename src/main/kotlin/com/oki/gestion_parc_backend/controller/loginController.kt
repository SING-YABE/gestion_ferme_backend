package com.oki.gestion_parc_backend.controller

import com.oki.gestion_parc_backend.repository.TenantRepository
import com.oki.gestion_parc_backend.repository.UtilisateurRepository
import com.oki.gestion_parc_backend.security.JwtUtil
import com.oki.gestion_parc_backend.security.TenantContext
import com.oki.gestion_parc_backend.service.impl.CustomUserDetailsService
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

/** L'utilisateur n'envoie que son email et son mot de passe. */
data class LoginRequest(val email: String, val password: String)

data class LoginResponse(val token: String, val role: String?, val username: String)

/**
 * Contrôleur de login multi-tenant — Option B : résolution par email.
 *
 * Flux :
 *   1. Parcourir toutes les fermes actives (public.tenants)
 *   2. Pour chacune, vérifier si l'email existe dans son schéma
 *   3. Dès que trouvé → authentifier dans ce schéma
 *   4. Générer le JWT avec tenantSchema en claim
 */
@RestController
@RequestMapping("/login")
@CrossOrigin(origins = ["*"])
class LoginController(
    private val authenticationManager: AuthenticationManager,
    private val userDetailsService: CustomUserDetailsService,
    private val jwtUtil: JwtUtil,
    private val tenantRepository: TenantRepository,
    private val utilisateurRepository: UtilisateurRepository
) {

    @PostMapping
    fun login(@RequestBody request: LoginRequest): ResponseEntity<Any> {

        // ── 1. Trouver la ferme qui contient cet email ────────────────────────
        val tenants = tenantRepository.findAllByActiveTrue()

        var foundSchema: String? = null
        for (tenant in tenants) {
            TenantContext.setTenant(tenant.schemaName)
            try {
                if (utilisateurRepository.findByEmail(request.email) != null) {
                    foundSchema = tenant.schemaName
                    break
                }
            } finally {
                TenantContext.clear()
            }
        }

        if (foundSchema == null) {
            return ResponseEntity.status(401).body(
                mapOf("error" to "Email ou mot de passe incorrect.")
            )
        }

        // ── 2. Authentifier dans le bon schéma ────────────────────────────────
        TenantContext.setTenant(foundSchema)
        return try {
            val authToken = UsernamePasswordAuthenticationToken(request.email, request.password)
            authenticationManager.authenticate(authToken)

            val userDetails: UserDetails = userDetailsService.loadUserByUsername(request.email)
            val role = userDetails.authorities
                .map { it.authority }
                .firstOrNull { it.startsWith("ROLE_") }

            // ── 3. JWT avec tenantSchema ──────────────────────────────────────
            val jwt = jwtUtil.generateToken(userDetails, foundSchema)

            ResponseEntity.ok(LoginResponse(jwt, role, userDetails.username))

        } catch (e: AuthenticationException) {
            ResponseEntity.status(401).body(mapOf("error" to "Email ou mot de passe incorrect."))
        } finally {
            TenantContext.clear()
        }
    }
}
