package com.oki.gestion_parc_backend.controller

import com.oki.gestion_parc_backend.model.PlanConfig
import com.oki.gestion_parc_backend.repository.AnimalRepository
import com.oki.gestion_parc_backend.repository.PlanConfigRepository
import com.oki.gestion_parc_backend.repository.SuperAdminRepository
import com.oki.gestion_parc_backend.repository.TenantRepository
import com.oki.gestion_parc_backend.repository.UtilisateurRepository
import com.oki.gestion_parc_backend.security.TenantContext
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

/**
 * Controller réservé au Super Admin de la plateforme.
 * Toutes les routes sont protégées par @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')").
 *
 * Endpoints :
 *   GET  /api/super-admin/stats          — statistiques globales de la plateforme
 *   GET  /api/super-admin/fermes         — liste de toutes les fermes (tenants)
 *   PUT  /api/super-admin/fermes/{code}/toggle  — activer / suspendre une ferme
 *   GET  /api/super-admin/plan-config    — configuration actuelle des plans
 *   PUT  /api/super-admin/plan-config    — modifier les limites des plans
 */
@RestController
@RequestMapping("/api/super-admin")
@CrossOrigin(origins = ["*"])
@PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
class SuperAdminController(
    private val tenantRepository: TenantRepository,
    private val planConfigRepository: PlanConfigRepository,
    private val utilisateurRepository: UtilisateurRepository,
    private val animalRepository: AnimalRepository,
    private val superAdminRepository: SuperAdminRepository
) {

    // ── Profil Super Admin (appelé par Angular après login) ───────────────────

    /**
     * Retourne le profil du Super Admin connecté.
     * Même format que /api/me pour que AuthService Angular fonctionne sans modification.
     */
    @GetMapping("/me")
    fun getMe(): ResponseEntity<Any> {
        val email = SecurityContextHolder.getContext().authentication.name
        val sa = superAdminRepository.findByEmail(email)
            ?: return ResponseEntity.status(404).body(mapOf("error" to "Super admin introuvable."))
        return ResponseEntity.ok(
            mapOf(
                "id"          to sa.id,
                "nom"         to sa.nom,
                "prenom"      to sa.prenom,
                "email"       to sa.email,
                "poste"       to "Propriétaire de la plateforme",
                "role"        to "ROLE_SUPER_ADMIN",
                "permissions" to emptyList<String>()
            )
        )
    }

    // ── Statistiques globales ─────────────────────────────────────────────────

    /**
     * Retourne les KPIs de la plateforme :
     *   - Nombre total de fermes inscrites
     *   - Fermes actives
     *   - Fermes suspendues
     *   - Nombre total d'utilisateurs (agrégé sur tous les schémas)
     *   - Nombre total d'animaux (agrégé sur tous les schémas)
     */
    @GetMapping("/stats")
    fun getStats(): ResponseEntity<Map<String, Any>> {
        val allTenants  = tenantRepository.findAll()
        val actifs      = allTenants.count { it.active }
        val suspendus   = allTenants.count { !it.active }

        var totalUsers   = 0L
        var totalAnimaux = 0L

        for (tenant in allTenants) {
            TenantContext.setTenant(tenant.schemaName)
            try {
                totalUsers   += utilisateurRepository.count()
                totalAnimaux += animalRepository.count()
            } catch (e: Exception) {
                println("[SuperAdmin] ⚠ Erreur stats pour '${tenant.schemaName}': ${e.message}")
            } finally {
                TenantContext.clear()
            }
        }

        return ResponseEntity.ok(
            mapOf(
                "totalFermes"   to allTenants.size,
                "fermesActives" to actifs,
                "fermesSuspendues" to suspendus,
                "totalUtilisateurs" to totalUsers,
                "totalAnimaux"  to totalAnimaux,
                "generatedAt"   to LocalDateTime.now().toString()
            )
        )
    }

    // ── Gestion des fermes ────────────────────────────────────────────────────

    /**
     * Liste toutes les fermes avec leur statut et informations clés.
     */
    @GetMapping("/fermes")
    fun getAllFermes(): ResponseEntity<List<Map<String, Any?>>> {
        val tenants = tenantRepository.findAll()
        val result = tenants.map { t ->
            var nbUsers   = 0L
            var nbAnimaux = 0L
            TenantContext.setTenant(t.schemaName)
            try {
                nbUsers   = utilisateurRepository.count()
                nbAnimaux = animalRepository.count()
            } catch (e: Exception) {
                println("[SuperAdmin] ⚠ Erreur données pour '${t.schemaName}': ${e.message}")
            } finally {
                TenantContext.clear()
            }
            mapOf(
                "id"           to t.id,
                "fermeCode"    to t.fermeCode,
                "nomFerme"     to t.nomFerme,
                "schemaName"   to t.schemaName,
                "active"       to t.active,
                "nbUtilisateurs" to nbUsers,
                "nbAnimaux"    to nbAnimaux
            )
        }
        return ResponseEntity.ok(result)
    }

    /**
     * Bascule le statut actif/suspendu d'une ferme.
     * Quand suspendue, le login de ses utilisateurs échoue (tenant inactif).
     */
    @PutMapping("/fermes/{fermeCode}/toggle")
    fun toggleFerme(@PathVariable fermeCode: String): ResponseEntity<Any> {
        val tenant = tenantRepository.findAll().find { it.fermeCode == fermeCode }
            ?: return ResponseEntity.notFound().build()

        tenant.active = !tenant.active
        tenantRepository.save(tenant)

        val status = if (tenant.active) "activée" else "suspendue"
        println("[SuperAdmin] Ferme '$fermeCode' $status.")

        return ResponseEntity.ok(
            mapOf(
                "fermeCode" to fermeCode,
                "active"    to tenant.active,
                "message"   to "Ferme $status avec succès."
            )
        )
    }

    // ── Configuration des plans ───────────────────────────────────────────────

    /**
     * Retourne la configuration actuelle des plans (public.plan_config, id=1).
     */
    @GetMapping("/plan-config")
    fun getPlanConfig(): ResponseEntity<Any> {
        val config = planConfigRepository.findById(1L).orElse(null)
            ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(
            mapOf(
                "maxAnimauxFreePlan"    to config.maxAnimauxFreePlan,
                "maxAnimauxPremiumPlan" to config.maxAnimauxPremiumPlan,
                "updatedAt"             to config.updatedAt.toString()
            )
        )
    }

    /**
     * Met à jour les limites des plans.
     * Body : { "maxAnimauxFreePlan": 10, "maxAnimauxPremiumPlan": -1 }
     * -1 = illimité pour PREMIUM.
     */
    @PutMapping("/plan-config")
    fun updatePlanConfig(@RequestBody dto: PlanConfigUpdateDto): ResponseEntity<Any> {
        val config = planConfigRepository.findById(1L).orElse(PlanConfig())
        config.maxAnimauxFreePlan    = dto.maxAnimauxFreePlan
        config.maxAnimauxPremiumPlan = dto.maxAnimauxPremiumPlan
        config.updatedAt             = LocalDateTime.now()
        planConfigRepository.save(config)

        println("[SuperAdmin] PlanConfig mise à jour : FREE=${dto.maxAnimauxFreePlan}, PREMIUM=${dto.maxAnimauxPremiumPlan}")
        return ResponseEntity.ok(
            mapOf(
                "message"               to "Configuration mise à jour avec succès.",
                "maxAnimauxFreePlan"    to config.maxAnimauxFreePlan,
                "maxAnimauxPremiumPlan" to config.maxAnimauxPremiumPlan,
                "updatedAt"             to config.updatedAt.toString()
            )
        )
    }
}

data class PlanConfigUpdateDto(
    val maxAnimauxFreePlan: Int,
    val maxAnimauxPremiumPlan: Int
)
