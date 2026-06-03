package com.oki.gestion_parc_backend.controller

import com.oki.gestion_parc_backend.model.Role
import com.oki.gestion_parc_backend.model.Utilisateur
import com.oki.gestion_parc_backend.service.RoleService
import com.oki.gestion_parc_backend.service.UtilisateurService
import org.springframework.web.bind.annotation.*
import org.springframework.security.access.prepost.PreAuthorize

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
    @PreAuthorize("hasAuthority('UTILISATEUR_WRITE')")
    fun createUser(@RequestBody dto: SimpleUserDTO): Utilisateur {
        val role = dto.roleId?.let { roleService.get(it) }
        val user = Utilisateur(nom = dto.nom, email = dto.email, password = "")
        return userService.create(user, dto.password)
    }

    @GetMapping("/utilisateurs/{id}")
    @PreAuthorize("hasAuthority('UTILISATEUR_READ')")
    fun getUser(@PathVariable id: Long): Utilisateur = userService.get(id)

    @GetMapping("/utilisateurs")
    @PreAuthorize("hasAuthority('UTILISATEUR_READ')")
    fun listUsers(): List<Utilisateur> = userService.list()

    @PutMapping("/utilisateurs/{id}")
    @PreAuthorize("hasAuthority('UTILISATEUR_WRITE')")
    fun updateUser(@PathVariable id: Long, @RequestBody dto: SimpleUserDTO): Utilisateur {
        val user = Utilisateur(idUtilisateur = id, nom = dto.nom, email = dto.email, password = "")
        return userService.update(user, dto.password)
    }

    @DeleteMapping("/utilisateurs/{id}")
    @PreAuthorize("hasAuthority('UTILISATEUR_WRITE')")
    fun deleteUser(@PathVariable id: Long) = userService.delete(id)

    @PutMapping("/utilisateurs/{id}/role")
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    fun assignRole(@PathVariable id: Long, @RequestBody dto: AssignRoleRequest): Utilisateur {
        val role: Role? = dto.roleId?.let { roleService.get(it) }
        return userService.assignRole(id, role)
    }

    // ---------------------- Rôle ----------------------
    @PostMapping("/roles")
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    fun createRole(@RequestBody dto: SimpleRoleDTO): Role = roleService.create(Role(nom = dto.nom))

    @GetMapping("/roles/{id}")
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    fun getRole(@PathVariable id: Long): Role = roleService.get(id)

    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    fun listRoles(): List<Role> = roleService.list()

    @PutMapping("/roles/{id}")
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    fun updateRole(@PathVariable id: Long, @RequestBody dto: SimpleRoleDTO): Role =
        roleService.update(Role(idRole = id, nom = dto.nom))

    @DeleteMapping("/roles/{id}")
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    fun deleteRole(@PathVariable id: Long) = roleService.delete(id)
}
