package com.oki.gestion_parc_backend.mapper

import com.oki.gestion_parc_backend.model.Role
import com.oki.gestion_parc_backend.model.Utilisateur
import com.oki.gestion_parc_backend.dto.UtilisateurDTO
import com.oki.gestion_parc_backend.dto.UtilisateurResponseDTO
import com.oki.gestion_parc_backend.dto.RoleResponseDTO

object UtilisateurMapper {

    // Convertit un DTO en entité Utilisateur, avec possibilité de passer le Role
    fun toEntity(dto: UtilisateurDTO, role: Role? = null, encodedPassword: String): Utilisateur =
        Utilisateur(
            poste = dto.poste,
            nom = dto.nom,
            prenom = dto.prenom,
            email = dto.email,
            telephone = dto.telephone,
            password = encodedPassword,
            role = role
        )

    // Convertit une entité Utilisateur en DTO de réponse
    fun toResponseDTO(utilisateur: Utilisateur): UtilisateurResponseDTO =
        UtilisateurResponseDTO(
            idUtilisateur = utilisateur.idUtilisateur,
            poste = utilisateur.poste,
            nom = utilisateur.nom,
            prenom = utilisateur.prenom,
            email = utilisateur.email,
            telephone = utilisateur.telephone,
            role = utilisateur.role?.let {
                RoleResponseDTO(
                    idRole = it.idRole,
                    nom = it.nom
                )
            }
        )
}
