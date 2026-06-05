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

@Component
class JwtAuthenticationFilter(
    private val jwtUtil: JwtUtil,
    private val userDetailsService: CustomUserDetailsService
) : OncePerRequestFilter() {

    private val mapper = ObjectMapper()

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val path = request.servletPath
        return path.startsWith("/login") || request.method.equals("OPTIONS", ignoreCase = true)
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
                val username = jwtUtil.extractUsername(token)

                if (username != null && SecurityContextHolder.getContext().authentication == null) {
                    val userDetails = userDetailsService.loadUserByUsername(username)

                    if (jwtUtil.validateToken(token, userDetails)) {
                        val authToken = UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.authorities
                        )
                        authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                        SecurityContextHolder.getContext().authentication = authToken
                    } else {
                        // Token présent mais invalide (expiré, signature incorrecte…)
                        // → on répond 401 immédiatement, sans passer au filtre suivant
                        sendUnauthorized(response, "Token expiré ou invalide. Veuillez vous reconnecter.")
                        return
                    }
                }
            } catch (e: Exception) {
                // Token malformé ou erreur de décodage
                sendUnauthorized(response, "Token invalide.")
                return
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun sendUnauthorized(response: HttpServletResponse, message: String) {
        response.status = HttpServletResponse.SC_UNAUTHORIZED   // 401
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        mapper.writeValue(response.writer, mapOf("error" to message))
    }
}
