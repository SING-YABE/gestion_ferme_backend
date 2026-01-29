package com.oki.gestion_parc_backend.controller

import com.oki.gestion_parc_backend.model.Utilisateur
import com.oki.gestion_parc_backend.repository.RoleRepository
import com.oki.gestion_parc_backend.service.UtilisateurService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/utilisateurs")
class UtilisateurController(
    private val utilisateurService: UtilisateurService,
    private val roleRepository: RoleRepository
) {

    data class CreateUserRequest(
        val poste: String,
        val nom: String,
        val prenom: String,
        val email: String,
        val telephone: String,
        val rawPassword: String,
        val roleId: Long? = null
    )

    @PostMapping
    fun createUser(@RequestBody req: CreateUserRequest): Utilisateur {

        val role = req.roleId?.let {
            roleRepository.findById(it).orElse(null)
        }

        val user = Utilisateur(
            poste = req.poste,
            nom = req.nom,
            prenom = req.prenom,
            email = req.email,
            telephone = req.telephone,
            role = role
        )

        return utilisateurService.create(user, req.rawPassword)
    }

    @GetMapping
    fun list(): List<Utilisateur> = utilisateurService.list()
}
