package com.oki.gestion_parc_backend.dto

data class UtilisateurResponseDTO(
    val idUtilisateur: Long,
    val poste: String,
    val nom: String,
    val prenom: String,
    val email: String,
    val telephone: String,
    val role: RoleResponseDTO? = null // nullable si l'utilisateur n'a pas de rôle
)
