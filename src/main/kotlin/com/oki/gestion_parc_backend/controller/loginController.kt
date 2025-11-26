package com.oki.gestion_parc_backend.controller

import com.oki.gestion_parc_backend.security.JwtUtil
import com.oki.gestion_parc_backend.service.impl.CustomUserDetailsService
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*

data class LoginRequest(val email: String, val password: String)
data class LoginResponse(val token: String, val role: String?, val username: String)

@RestController
@RequestMapping("/login")
@CrossOrigin(origins = ["http://localhost:4200"])
class LoginController(
    private val authenticationManager: AuthenticationManager,
    private val userDetailsService: CustomUserDetailsService,
    private val jwtUtil: JwtUtil
) {

    @PostMapping
    fun login(@RequestBody request: LoginRequest): ResponseEntity<Any> {
        return try {
            val authToken = UsernamePasswordAuthenticationToken(request.email, request.password)
            authenticationManager.authenticate(authToken)

            val userDetails: UserDetails = userDetailsService.loadUserByUsername(request.email)
            val role = userDetails.authorities.firstOrNull()?.authority
            val jwt = jwtUtil.generateToken(userDetails)

            ResponseEntity.ok(LoginResponse(jwt, role, userDetails.username))
        } catch (e: AuthenticationException) {
            ResponseEntity.status(401).body(mapOf("error" to "Email ou mot de passe incorrect"))
        }
    }
}
