package com.oki.gestion_parc_backend.config

import com.oki.gestion_parc_backend.model.Role
import com.oki.gestion_parc_backend.model.TypeTache
import com.oki.gestion_parc_backend.model.Utilisateur
import com.oki.gestion_parc_backend.repository.RoleRepository
import com.oki.gestion_parc_backend.repository.TypeTacheRepository
import com.oki.gestion_parc_backend.repository.UtilisateurRepository
import jakarta.annotation.PostConstruct
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class DataInitializer(
    private val roleRepository: RoleRepository,
    private val utilisateurRepository: UtilisateurRepository,
    private val typeTacheRepository: TypeTacheRepository,
    private val passwordEncoder: PasswordEncoder
) {

    @PostConstruct
    fun init() {
        initRolesEtUtilisateurs()
        initTypesTaches()
    }

    private fun initRolesEtUtilisateurs() {
        val roleNames = listOf("ROLE_ADMINISTRATEUR","ROLE_GERANT","ROLE_RESPONSABLE","ROLE_OUVRIER")
        val rolesMap = roleNames.associate { name ->
            val role = roleRepository.findByNom(name) ?: roleRepository.save(Role(nom = name))
            name to role
        }

        listOf(
            Utilisateur(poste="Administrateur système", nom="Kabore", prenom="Admin",
                email="admin@ferme.bf", telephone="0000000000",
                password=passwordEncoder.encode("admin12345"), role=rolesMap["ROLE_ADMINISTRATEUR"]),
            Utilisateur(poste="Gérant de la ferme", nom="Ouedraogo", prenom="Gérant",
                email="gerant@ferme.bf", telephone="1111111111",
                password=passwordEncoder.encode("gerant12345"), role=rolesMap["ROLE_GERANT"]),
            Utilisateur(poste="Responsable de zone", nom="Traore", prenom="Responsable",
                email="responsable@ferme.bf", telephone="2222222222",
                password=passwordEncoder.encode("resp12345"), role=rolesMap["ROLE_RESPONSABLE"]),
            Utilisateur(poste="Ouvrier d'élevage", nom="Diallo", prenom="Ouvrier",
                email="ouvrier@ferme.bf", telephone="3333333333",
                password=passwordEncoder.encode("ouvrier12345"), role=rolesMap["ROLE_OUVRIER"])
        ).forEach { user ->
            if (utilisateurRepository.findByEmail(user.email) == null)
                utilisateurRepository.save(user)
        }
    }

    private fun initTypesTaches() {
        val types = listOf(
            TypeTache(nom="Alimentation",       description="Distribution des rations alimentaires",                     couleur="#16a34a", icone="pi pi-shopping-cart"),
            TypeTache(nom="Soins vétérinaires", description="Vaccination, traitements, contrôle santé",                  couleur="#dc2626", icone="pi pi-heart"),
            TypeTache(nom="Nettoyage",          description="Désinfection des boxes, couloirs et équipements",           couleur="#2563eb", icone="pi pi-refresh"),
            TypeTache(nom="Pesée",              description="Pesée des animaux pour suivi de croissance",                couleur="#d97706", icone="pi pi-chart-bar"),
            TypeTache(nom="Reproduction",       description="Contrôle des chaleurs, saillies, surveillance mise-bas",    couleur="#7c3aed", icone="pi pi-heart-fill"),
            TypeTache(nom="Déplacement",        description="Transfert d'animaux entre boxes ou bâtiments",              couleur="#0891b2", icone="pi pi-arrow-right-arrow-left"),
            TypeTache(nom="Maintenance",        description="Réparations, vérification des équipements",                 couleur="#78716c", icone="pi pi-wrench"),
            TypeTache(nom="Approvisionnement",  description="Réception et inventaire des stocks",                        couleur="#ca8a04", icone="pi pi-box"),
        )
        types.forEach { type ->
            if (typeTacheRepository.findByNom(type.nom) == null)
                typeTacheRepository.save(type)
        }
        println("[DataInitializer] Types de tâches initialisés.")
    }
}
