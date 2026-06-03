package com.oki.gestion_parc_backend.repository

import com.oki.gestion_parc_backend.model.PermissionOverride
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PermissionOverrideRepository : JpaRepository<PermissionOverride, Long> {

    /** Tous les overrides d'un utilisateur donné */
    fun findByUtilisateurIdUtilisateur(utilisateurId: Long): List<PermissionOverride>

    /** Vérifier si un override précis existe déjà */
    fun findByUtilisateurIdUtilisateurAndPermission(
        utilisateurId: Long,
        permission: String
    ): PermissionOverride?

    /** Supprimer l'override d'une permission pour un utilisateur */
    fun deleteByUtilisateurIdUtilisateurAndPermission(
        utilisateurId: Long,
        permission: String
    )
}
