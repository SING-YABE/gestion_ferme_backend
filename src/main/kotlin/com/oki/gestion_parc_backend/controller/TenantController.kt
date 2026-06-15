package com.oki.gestion_parc_backend.controller

import com.oki.gestion_parc_backend.dto.RegisterFermeDTO
import com.oki.gestion_parc_backend.service.TenantService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

/**
 * Contrôleur de gestion des tenants (fermes clientes).
 *
 * POST /api/register-ferme — public — inscription d'une nouvelle ferme
 *   Crée le schéma PostgreSQL, les tables, un admin et les données par défaut.
 *   Retourne : { message, fermeCode, schemaName }
 *
 * GET /api/tenants — admin (SUBSCRIPTION_MANAGE) — liste tous les tenants
 */
@RestController
@CrossOrigin(origins = ["*"])
class TenantController(
    private val tenantService: TenantService
) {

    @PostMapping("/api/register-ferme")
    fun registerFerme(@RequestBody dto: RegisterFermeDTO): ResponseEntity<Any> {
        return try {
            val tenant = tenantService.registerFerme(dto)
            ResponseEntity.ok(mapOf(
                "message"    to "Ferme '${tenant.nomFerme}' créée avec succès.",
                "fermeCode"  to tenant.fermeCode,
                "schemaName" to tenant.schemaName
            ))
        } catch (e: IllegalArgumentException) {
            ResponseEntity.badRequest().body(mapOf("error" to e.message))
        } catch (e: Exception) {
            ResponseEntity.internalServerError().body(
                mapOf("error" to "Erreur lors de la création : ${e.message}")
            )
        }
    }

    @GetMapping("/api/tenants")
    @PreAuthorize("hasAuthority('SUBSCRIPTION_MANAGE')")
    fun listTenants(): ResponseEntity<Any> {
        return ResponseEntity.ok(tenantService.findAll())
    }
}
