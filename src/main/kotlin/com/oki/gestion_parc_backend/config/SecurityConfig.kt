package com.oki.gestion_parc_backend.config

import com.oki.gestion_parc_backend.security.JwtAuthenticationFilter
import com.oki.gestion_parc_backend.service.impl.CustomUserDetailsService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

/**
 * Configuration de la sécurité Spring Security.
 *
 * - JWT activé sur toutes les routes sauf /login et /api/utilisateurs (création de compte)
 * - Les contrôles fins sont délégués aux @PreAuthorize sur chaque endpoint
 * - @EnableMethodSecurity active hasAuthority() / hasRole() dans les annotations
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
class SecurityConfig(
    private val userDetailsService: CustomUserDetailsService,
    private val jwtFilter: JwtAuthenticationFilter
) {

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    fun authManager(http: HttpSecurity, passwordEncoder: PasswordEncoder): AuthenticationManager {
        val builder = http.getSharedObject(AuthenticationManagerBuilder::class.java)
        builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder)
        return builder.build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration()
        config.allowedOrigins = listOf("http://localhost:4200")
        config.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        config.allowedHeaders = listOf("*")
        config.allowCredentials = true
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)
        return source
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { it.configurationSource(corsConfigurationSource()) }
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    // Routes publiques
                    .requestMatchers("/login").permitAll()
                    // Fichiers uploadés accessibles sans token (images de preuves, logos…)
                    .requestMatchers("/uploads/**").permitAll()
                    // Appelé en lecture seule par le service Python (FastAPI) sans session utilisateur
                    .requestMatchers("/api/parametres-eleveur").permitAll()
                    // Toutes les autres routes nécessitent un token JWT valide.
                    // Les contrôles fins (quel rôle/permission) sont dans @PreAuthorize.
                    .anyRequest().authenticated()
            }
            // Filtre JWT activé
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}
