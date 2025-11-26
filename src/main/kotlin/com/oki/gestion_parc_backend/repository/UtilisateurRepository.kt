package com.oki.gestion_parc_backend.repository

import com.oki.gestion_parc_backend.model.Utilisateur
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

@Repository
interface UtilisateurRepository : JpaRepository<Utilisateur, Long> {

    // Vérifier si un utilisateur existe par email
    fun existsByEmail(email: String): Boolean


    // Option 2 : Requête explicite pour récupérer les utilisateurs par id de rôle
    @Query("SELECT u FROM Utilisateur u WHERE u.role.idRole = :roleId")
    fun findAllByRoleId(@Param("roleId") roleId: Long): List<Utilisateur>

    // Récupérer un utilisateur par email (optionnelle si nécessaire)
    fun findByEmail(email: String): Utilisateur?
}

