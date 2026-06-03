package com.oki.gestion_parc_backend.repository

import com.oki.gestion_parc_backend.model.AssignationTache
import com.oki.gestion_parc_backend.model.StatutTache
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AssignationTacheRepository : JpaRepository<AssignationTache, Long> {

    fun findByTacheId(tacheId: Long): List<AssignationTache>

    fun findByAssigneeIdUtilisateur(userId: Long): List<AssignationTache>

    fun findByTacheIdAndAssigneeIdUtilisateur(tacheId: Long, userId: Long): AssignationTache?

    /** Assignations en attente de validation (pour le gérant) */
    fun findByStatut(statut: StatutTache): List<AssignationTache>

    /** Assignations en attente pour un utilisateur donné */
    fun findByAssigneeIdUtilisateurAndStatut(userId: Long, statut: StatutTache): List<AssignationTache>
}
