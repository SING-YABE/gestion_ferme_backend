package com.oki.gestion_parc_backend.dto

data class RoleResponseDTO(
    val idRole: Long,
    val nom: String,
    val utilisateurs: List<UtilisateurResponseDTO> = emptyList()
)
