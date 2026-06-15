package com.oki.gestion_parc_backend.security

import com.fasterxml.jackson.databind.ObjectMapper
import com.oki.gestion_parc_backend.service.impl.CustomUserDetailsService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 * Filtre JWT étendu au multi-tenant.
 *
 * Pour chaque requête authentifiée :
 *   1. Extrait username + tenantSchema du JWT
 *   2. Définit TenantContext.setTenant(tenantSchema) → Hibernate route vers le bon schéma
 *   3. Charge l'utilisateur depuis le bon schéma
 *   4. Valide le token et pose l'authentification Spring Security
 *   5. Nettoie TenantContext dans le bloc finally (obligatoire, évite les fuites inter-requêtes)
 *
 * Compatibilité : si le JWT est ancien (sans tenantSchema), on défaut sur "ferme_default".
 */
@Component
class JwtAuthenticationFilter(
    private val jwtUtil: JwtUtil,
    private val userDetailsService: CustomUserDetailsService
) : OncePerRequestFilter() {

    private val mapper = ObjectMapper()

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.servletPath
        return path.startsWith("/login")
            || path.startsWith("/api/register-ferme")
            || request.method.equals("OPTIONS", ignoreCase = true)
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            val token = authHeader.substring(7)

            try {
                val username     = jwtUtil.extractUsername(token)
                // Compatibilité avec les anciens JWT (sans claim tenantSchema)
                val tenantSchema = jwtUtil.extractTenantSchema(token) ?: "ferme_default"

                if (username != null && SecurityContextHolder.getContext().authentication == null) {
                    // ── Définir le tenant AVANT de charger l'utilisateur ──────
                    TenantContext.setTenant(tenantSchema)

                    val userDetails = userDetailsService.loadUserByUsername(username)

                    if (jwtUtil.validateToken(token, userDetails)) {
                        val authToken = UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.authorities
                        )
                        authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                        SecurityContextHolder.getContext().authentication = authToken
                    } else {
                        TenantContext.clear()
                        sendUnauthorized(response, "Token expiré ou invalide. Veuillez vous reconnecter.")
                        return
                    }
                }
            } catch (e: Exception) {
                TenantContext.clear()
                sendUnauthorized(response, "Token invalide.")
                return
            }
        }

        try {
            filterChain.doFilter(request, response)
        } finally {
            // ── Nettoyage obligatoire après chaque requête ────────────────────
            TenantContext.clear()
        }
    }

    private fun sendUnauthorized(response: HttpServletResponse, message: String) {
        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        mapper.writeValue(response.writer, mapOf("error" to message))
    }
}
