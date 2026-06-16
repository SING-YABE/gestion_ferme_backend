package com.oki.gestion_parc_backend.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtUtil {

    private val SECRET = "change_me_with_a_very_long_secret_key_for_prod_please_!_1234567890"
    private val key = Keys.hmacShaKeyFor(SECRET.toByteArray())

    fun extractUsername(token: String): String? = extractClaims(token)?.subject

    private fun extractClaims(token: String): Claims? {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .body
        } catch (e: Exception) {
            null
        }
    }

    private fun extractExpiration(token: String): Date? = extractClaims(token)?.expiration

    private fun isTokenExpired(token: String): Boolean {
        val exp = extractExpiration(token)
        return exp?.before(Date()) ?: true
    }

    /**
     * Génère un token JWT incluant le rôle ET le schéma du tenant.
     * Le schéma est extrait à chaque requête par JwtAuthenticationFilter
     * pour router les queries JPA vers le bon schéma PostgreSQL.
     */
    fun generateToken(userDetails: UserDetails, tenantSchema: String): String {
        val roleAuthority = userDetails.authorities
            .firstOrNull { it.authority.startsWith("ROLE_") }
            ?.authority ?: "ROLE_OUVRIER"
        val claims: Map<String, Any> = mapOf(
            "role"         to roleAuthority,
            "tenantSchema" to tenantSchema
        )
        return createToken(claims, userDetails.username)
    }

    /**
     * Token JWT pour le Super Admin.
     * tenantSchema = "SUPER_ADMIN" — valeur sentinelle reconnue par JwtAuthenticationFilter.
     */
    fun generateSuperAdminToken(email: String): String {
        val claims: Map<String, Any> = mapOf(
            "role"         to "ROLE_SUPER_ADMIN",
            "tenantSchema" to "SUPER_ADMIN"
        )
        return createToken(claims, email)
    }

    /** Extrait le schéma du tenant depuis le token JWT. */
    fun extractTenantSchema(token: String): String? =
        extractClaims(token)?.get("tenantSchema", String::class.java)

    private fun createToken(claims: Map<String, Any>, subject: String): String {
        val now = Date()
        val validity = Date(now.time + 1000L * 60 * 60 * 10) // 10h
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun validateToken(token: String, userDetails: UserDetails): Boolean {
        val username = extractUsername(token) ?: return false
        return username == userDetails.username && !isTokenExpired(token)
    }
}
