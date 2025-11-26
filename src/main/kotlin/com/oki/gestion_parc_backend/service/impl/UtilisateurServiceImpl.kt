package com.oki.gestion_parc_backend.service.impl

import com.oki.gestion_parc_backend.model.Role
import com.oki.gestion_parc_backend.model.Utilisateur
import com.oki.gestion_parc_backend.repository.UtilisateurRepository
import com.oki.gestion_parc_backend.service.UtilisateurService
import org.springframework.stereotype.Service
import org.springframework.security.crypto.password.PasswordEncoder

@Service
class UtilisateurServiceImpl(
    private val repo: UtilisateurRepository,
    private val passwordEncoder: PasswordEncoder
) : UtilisateurService {

    // ---------- CRUD ----------
    override fun create(utilisateur: Utilisateur, rawPassword: String): Utilisateur {
        utilisateur.password = passwordEncoder.encode(rawPassword)
        return repo.save(utilisateur)
    }

    override fun get(id: Long): Utilisateur =
        repo.findById(id).orElseThrow { IllegalArgumentException("Utilisateur avec id $id non trouvé") }

    override fun list(): List<Utilisateur> = repo.findAll()

    override fun update(utilisateur: Utilisateur, rawPassword: String?): Utilisateur {
        val exist = get(utilisateur.idUtilisateur)
        exist.nom = utilisateur.nom
        exist.prenom = utilisateur.prenom
        exist.poste = utilisateur.poste
        exist.email = utilisateur.email
        exist.telephone = utilisateur.telephone
        exist.role = utilisateur.role
        // Encodage du mot de passe si fourni
        if (!rawPassword.isNullOrBlank()) {
            exist.password = passwordEncoder.encode(rawPassword)
        }
        return repo.save(exist)
    }

    override fun delete(id: Long) = repo.deleteById(id)

    // ---------- Gestion des rôles ----------
    override fun assignRole(userId: Long, role: Role?): Utilisateur {
        val user = get(userId)
        user.role = role // peut-être null pour retirer le rôle
        return repo.save(user)
    }

    override fun listByRole(role: Role): List<Utilisateur> =
        repo.findAll().filter { it.role?.idRole == role.idRole }

    // ---------- Méthodes métier (stubs) ----------
    override fun faireDemande(user: Utilisateur) {
        if (user.role?.nom != "DEMANDEUR") throw IllegalAccessException("Seul un DEMANDEUR peut créer une demande")
    }

    override fun validerDemande(user: Utilisateur) {
        if (user.role?.nom != "APPROBATEUR") throw IllegalAccessException("Seul un APPROBATEUR peut valider une demande")
    }

    override fun rejeterDemande(user: Utilisateur) {
        if (user.role?.nom != "APPROBATEUR") throw IllegalAccessException("Seul un APPROBATEUR peut rejeter une demande")
    }

    override fun creerAssignation(user: Utilisateur) {
        val isDernierApprobateur = true // TODO: logique réelle
        if (!isDernierApprobateur) throw IllegalAccessException("Seul le dernier approbateur peut créer une assignation")
    }

    override fun traiterAssignation(user: Utilisateur) {
        if (user.role?.nom != "GESTIONNAIRE") throw IllegalAccessException("Seul un GESTIONNAIRE peut traiter une assignation")
    }
}
