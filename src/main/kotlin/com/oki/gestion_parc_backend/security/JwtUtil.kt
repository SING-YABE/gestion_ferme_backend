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

    fun generateToken(userDetails: UserDetails): String {
        // Spring Security's User trie les authorities alphabétiquement,
        // donc on cherche explicitement celle qui commence par ROLE_
        val roleAuthority = userDetails.authorities
            .firstOrNull { it.authority.startsWith("ROLE_") }
            ?.authority ?: "ROLE_OUVRIER"
        // On stocke le nom complet (ex: ROLE_GERANT) pour cohérence avec le frontend
        val claims: Map<String, Any> = mapOf("role" to roleAuthority)
        return createToken(claims, userDetails.username)
    }

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
