package com.oki.gestion_parc_backend.config

import com.oki.gestion_parc_backend.model.Role
import com.oki.gestion_parc_backend.model.Utilisateur
import com.oki.gestion_parc_backend.repository.RoleRepository
import com.oki.gestion_parc_backend.repository.UtilisateurRepository
import jakarta.annotation.PostConstruct
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class DataInitializer(
    private val roleRepository: RoleRepository,
    private val utilisateurRepository: UtilisateurRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @PostConstruct
    fun init() {
        // Création des rôles si non existants
        val rolesMap = mutableMapOf<String, Role>()
        listOf("ADMIN", "GESTIONNAIRE", "DEMANDEUR", "APPROBATEUR").forEach { roleName ->
            val role = roleRepository.findByNom(roleName) ?: Role(nom = roleName).also { roleRepository.save(it) }
            rolesMap[roleName] = role
        }

        // Création des utilisateurs
        val utilisateurs = listOf(
            Utilisateur(
                poste = "Administrateur",
                nom = "Kabore",
                prenom = "developeur fullst",
                email = "amira@example.com",
                telephone = "0000000000",
                password = passwordEncoder.encode("admin12345"),
                role = rolesMap["ADMIN"]
            ),
            Utilisateur(
                poste = "developer front",
                nom = "Ouedraogo",
                prenom = "Idriss",
                email = "idriss@example.com",
                telephone = "1111111111",
                password = passwordEncoder.encode("gest12345"),
                role = rolesMap["GESTIONNAIRE"]
            ),
            Utilisateur(
                poste = "Disigner",
                nom = "Konkisre",
                prenom = "flavien",
                email = "flavien@example.com",
                telephone = "2222222222",
                password = passwordEncoder.encode("dem12345"),
                role = rolesMap["DEMANDEUR"]
            ),
            Utilisateur(
                poste = "dev",
                nom = "Kologo",
                prenom = "Erneste",
                email = "Erneste@example.com",
                telephone = "3333333333",
                password = passwordEncoder.encode("app12345"),
                role = rolesMap["APPROBATEUR"]
            ),
            Utilisateur(
                poste = "dev",
                nom = "Diallo",
                prenom = "Adriens",
                email = "Adriens@example.com",
                telephone = "4444444444",
                password = passwordEncoder.encode("user12345"),
                role = null
            )
        )

        utilisateurs.forEach { user ->
            if (utilisateurRepository.findByEmail(user.email) == null) {
                utilisateurRepository.save(user)
            }
        }

        println("Initialisation terminée : 5 utilisateurs créés avec rôles.")
    }
}
