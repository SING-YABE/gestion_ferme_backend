package com.oki.gestion_parc_backend.service.impl

import com.oki.gestion_parc_backend.model.Role
import com.oki.gestion_parc_backend.model.Utilisateur
import com.oki.gestion_parc_backend.repository.RoleRepository
import com.oki.gestion_parc_backend.service.RoleService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RoleServiceImpl(
    private val roleRepo: RoleRepository
) : RoleService {

    override fun create(role: Role): Role {
        val existingRole = roleRepo.findByNom(role.nom)
        if (existingRole != null) {
            throw IllegalArgumentException("Le rôle '${role.nom}' existe déjà")
        }
        return roleRepo.save(role)
    }

    @Transactional(readOnly = true)
    override fun get(id: Long): Role =
        roleRepo.findById(id).orElseThrow { IllegalArgumentException("Rôle avec id $id non trouvé") }

    @Transactional(readOnly = true)
    override fun list(): List<Role> = roleRepo.findAll()

    override fun update(role: Role): Role {
        val existing = get(role.idRole)
        existing.nom = role.nom
        return roleRepo.save(existing)
    }

    override fun delete(id: Long) {
        val role = get(id)
        // Retirer le rôle de tous les utilisateurs avant suppression
        role.utilisateurs.forEach { it.role = null }
        roleRepo.deleteById(id)
    }

    @Transactional(readOnly = true)
    override fun listUsersByRole(id: Long): List<Utilisateur> {
        val role = get(id)
        return role.utilisateurs.toList()
    }

    @Transactional(readOnly = true)
    override fun findByNom(nom: String): Role? = roleRepo.findByNom(nom)
}
