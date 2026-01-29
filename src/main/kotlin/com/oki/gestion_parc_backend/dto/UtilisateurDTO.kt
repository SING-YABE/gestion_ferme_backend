package com.oki.gestion_parc_backend.dto

data class UtilisateurDTO(
    val poste: String,
    val nom: String,
    val prenom: String,
    val email: String,
    val telephone: String,
    val password: String,
    val roleId: Long? = null
)

