package com.oki.gestion_parc_backend.service

import com.oki.gestion_parc_backend.model.Role
import com.oki.gestion_parc_backend.model.Utilisateur

interface RoleService {
    fun create(role: Role): Role
    fun get(id: Long): Role
    fun list(): List<Role>
    fun update(role: Role): Role
    fun delete(id: Long)
    fun listUsersByRole(id: Long): List<Utilisateur>
    fun findByNom(nom: String): Role?
}
