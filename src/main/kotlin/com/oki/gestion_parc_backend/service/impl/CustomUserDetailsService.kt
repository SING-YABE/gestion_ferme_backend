package com.oki.gestion_parc_backend.service.impl

import com.oki.gestion_parc_backend.repository.UtilisateurRepository
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import jakarta.annotation.PostConstruct

@Service
class CustomUserDetailsService(
    private val repo: UtilisateurRepository
) : UserDetailsService {

    private val passwordEncoder = BCryptPasswordEncoder()

    override fun loadUserByUsername(username: String): UserDetails {
        val utilisateur = repo.findByEmail(username)
            ?: throw UsernameNotFoundException("Utilisateur avec email $username non trouvé")

        val authorities: List<GrantedAuthority> = listOf(
            SimpleGrantedAuthority(utilisateur.role?.nom ?: "UTILISATEUR")
        )

        return User(
            utilisateur.email,
            utilisateur.password,
            authorities
        )

    }

    @PostConstruct
    fun encodeOldPasswords() {
        val utilisateurs = repo.findAll()
        utilisateurs.forEach { user ->
            if (!user.password.startsWith("\$2a\$")) {
                user.password = passwordEncoder.encode(user.password)
                repo.save(user)
                println("Mot de passe encodé pour : ${user.email}")
            }
        }
    }
}
