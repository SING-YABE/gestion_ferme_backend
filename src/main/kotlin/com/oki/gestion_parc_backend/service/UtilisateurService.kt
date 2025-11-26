package com.oki.gestion_parc_backend.service

import com.oki.gestion_parc_backend.model.Role
import com.oki.gestion_parc_backend.model.Utilisateur

interface UtilisateurService {
    fun create(utilisateur: Utilisateur, rawPassword: String): Utilisateur
    fun get(id: Long): Utilisateur
    fun list(): List<Utilisateur>
    fun update(utilisateur: Utilisateur, rawPassword: String? = null): Utilisateur
    fun delete(id: Long)

    // Gestion des rôles
    fun assignRole(userId: Long, role: Role?): Utilisateur
    fun listByRole(role: Role): List<Utilisateur>

    // Méthodes métier (stubs)
    fun faireDemande(user: Utilisateur)
    fun validerDemande(user: Utilisateur)
    fun rejeterDemande(user: Utilisateur)
    fun creerAssignation(user: Utilisateur)
    fun traiterAssignation(user: Utilisateur)
}
