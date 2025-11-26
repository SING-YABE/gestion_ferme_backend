package com.oki.gestion_parc_backend.controller

import com.oki.gestion_parc_backend.model.Role
import com.oki.gestion_parc_backend.model.Utilisateur
import com.oki.gestion_parc_backend.service.RoleService
import com.oki.gestion_parc_backend.service.UtilisateurService
import org.springframework.web.bind.annotation.*

data class SimpleUserDTO(val nom: String, val email: String, val password: String, val roleId: Long?)
data class AssignRoleRequest(val roleId: Long?)
data class SimpleRoleDTO(val nom: String)

@RestController
@RequestMapping("/api/admin")
class AuthRoleController(
    private val userService: UtilisateurService,
    private val roleService: RoleService
) {

    // ---------------------- Utilisateur ----------------------
    @PostMapping("/utilisateurs")
    fun createUser(@RequestBody dto: SimpleUserDTO): Utilisateur {
        val role = dto.roleId?.let { roleService.get(it) }
        val user = Utilisateur(nom = dto.nom, email = dto.email, password = "")
        return userService.create(user, dto.password)
    }

    @GetMapping("/utilisateurs/{id}")
    fun getUser(@PathVariable id: Long): Utilisateur = userService.get(id)

    @GetMapping("/utilisateurs")
    fun listUsers(): List<Utilisateur> = userService.list()

    @PutMapping("/utilisateurs/{id}")
    fun updateUser(@PathVariable id: Long, @RequestBody dto: SimpleUserDTO): Utilisateur {
        val user = Utilisateur(idUtilisateur = id, nom = dto.nom, email = dto.email, password = "")
        return userService.update(user, dto.password)
    }

    @DeleteMapping("/utilisateurs/{id}")
    fun deleteUser(@PathVariable id: Long) = userService.delete(id)

    @PutMapping("/utilisateurs/{id}/role")
    fun assignRole(@PathVariable id: Long, @RequestBody dto: AssignRoleRequest): Utilisateur {
        val role: Role? = dto.roleId?.let { roleService.get(it) }
        return userService.assignRole(id, role)
    }

    // ---------------------- Rôle ----------------------
    @PostMapping("/roles")
    fun createRole(@RequestBody dto: SimpleRoleDTO): Role = roleService.create(Role(nom = dto.nom))

    @GetMapping("/roles/{id}")
    fun getRole(@PathVariable id: Long): Role = roleService.get(id)

    @GetMapping("/roles")
    fun listRoles(): List<Role> = roleService.list()

    @PutMapping("/roles/{id}")
    fun updateRole(@PathVariable id: Long, @RequestBody dto: SimpleRoleDTO): Role =
        roleService.update(Role(idRole = id, nom = dto.nom))

    @DeleteMapping("/roles/{id}")
    fun deleteRole(@PathVariable id: Long) = roleService.delete(id)
}
