package com.oki.gestion_parc_backend.mapper
import com.oki.gestion_parc_backend.dto.RoleResponseDTO

import com.oki.gestion_parc_backend.model.Role

object RoleMapper {

    fun toEntity(dto: com.oki.gestion_parc_backend.dto.RoleDTO): Role =
        Role(nom = dto.nom)

    fun toResponseDTO(role: Role): RoleResponseDTO =
        RoleResponseDTO(
            idRole = role.idRole,
            nom = role.nom,
            utilisateurs = role.utilisateurs.map { UtilisateurMapper.toResponseDTO(it) }
        )

}
