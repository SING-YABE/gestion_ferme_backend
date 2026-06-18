package com.oki.gestion_parc_backend.controller

import com.oki.gestion_parc_backend.dto.*
import com.oki.gestion_parc_backend.model.*
import com.oki.gestion_parc_backend.repository.*
import com.oki.gestion_parc_backend.security.TenantContext
import com.oki.gestion_parc_backend.service.C0de4hopeService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Controller réservé au Super-Admin de la plateforme.
 * Toutes les routes sont protégées par ROLE_SUPER_ADMIN.
 *
 * Endpoints :
 *   GET  /api/super-admin/me                         — profil du super admin connecté
 *   GET  /api/super-admin/stats                      — statistiques globales
 *   GET  /api/super-admin/fermes                     — liste fermes + abonnements
 *   PUT  /api/super-admin/fermes/{code}/toggle       — activer / suspendre une ferme
 *
 *   GET  /api/super-admin/plans                      — liste de tous les plans
 *   POST /api/super-admin/plans                      — créer un plan
 *   PUT  /api/super-admin/plans/{id}                 — modifier un plan
 *   DELETE /api/super-admin/plans/{id}               — supprimer un plan (si inutilisé)
 *   PUT  /api/super-admin/plans/{id}/toggle-actif    — activer / désactiver un plan
 *
 *   GET  /api/super-admin/subscriptions              — liste abonnements toutes fermes
 *   PUT  /api/super-admin/subscriptions/{code}/assign — attribuer un plan manuellement
 *   PUT  /api/super-admin/subscriptions/{code}/suspend — suspendre / lever suspension
 *   GET  /api/super-admin/transactions               — historique paiements toutes fermes
 */
@RestController
@RequestMapping("/api/super-admin")
@CrossOrigin(origins = ["*"])
@PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
class SuperAdminController(
    private val tenantRepository: TenantRepository,
    private val planConfigRepository: PlanConfigRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val paymentTransactionRepository: PaymentTransactionRepository,
    private val utilisateurRepository: UtilisateurRepository,
    private val animalRepository: AnimalRepository,
    private val superAdminRepository: SuperAdminRepository,
    private val c0de4hopeService: C0de4hopeService
) {

    // ── Profil ───────────────────────────────────────────────────────────────

    @GetMapping("/me")
    fun getMe(): ResponseEntity<Any> {
        val email = SecurityContextHolder.getContext().authentication.name
        val sa = superAdminRepository.findByEmail(email)
            ?: return ResponseEntity.status(404).body(mapOf("error" to "Super admin introuvable."))
        return ResponseEntity.ok(mapOf(
            "id" to sa.id, "nom" to sa.nom, "prenom" to sa.prenom,
            "email" to sa.email, "poste" to "Proprietaire de la plateforme",
            "role" to "ROLE_SUPER_ADMIN", "permissions" to emptyList<String>()
        ))
    }

    // ── Statistiques globales ─────────────────────────────────────────────────

    @GetMapping("/stats")
    fun getStats(): ResponseEntity<Map<String, Any>> {
        val allTenants = tenantRepository.findAll()
        val actifs     = allTenants.count { it.active }
        var totalUsers = 0L
        var totalAnimaux = 0L
        var totalActifs = 0L; var totalEssai = 0L; var totalExpires = 0L

        for (tenant in allTenants) {
            TenantContext.setTenant(tenant.schemaName)
            try {
                totalUsers   += utilisateurRepository.count()
                totalAnimaux += animalRepository.count()
                val sub = subscriptionRepository.findById(1L).orElse(null)
                when (sub?.statut) {
                    SubscriptionStatus.ACTIVE              -> totalActifs++
                    SubscriptionStatus.TRIAL               -> totalEssai++
                    SubscriptionStatus.EXPIRED,
                    SubscriptionStatus.SUSPENDED,
                    SubscriptionStatus.CANCELLED           -> totalExpires++
                    else                                   -> {}
                }
            } catch (e: Exception) {
                println("[SuperAdmin] Erreur stats '${tenant.schemaName}': ${e.message}")
            } finally {
                TenantContext.clear()
            }
        }

        return ResponseEntity.ok(mapOf(
            "totalFermes"        to allTenants.size,
            "fermesActives"      to actifs,
            "abonnementsActifs"  to totalActifs,
            "abonnementsEssai"   to totalEssai,
            "abonnementsExpires" to totalExpires,
            "totalUtilisateurs"  to totalUsers,
            "totalAnimaux"       to totalAnimaux,
            "generatedAt"        to LocalDateTime.now().toString()
        ))
    }

    // ── Fermes ───────────────────────────────────────────────────────────────

    @GetMapping("/fermes")
    fun getAllFermes(): ResponseEntity<List<FermeAdminDTO>> {
        val tenants = tenantRepository.findAll()
        val result = tenants.map { t ->
            var nbUsers = 0L; var nbAnimaux = 0L
            var sub: Subscription? = null
            TenantContext.setTenant(t.schemaName)
            try {
                nbUsers   = utilisateurRepository.count()
                nbAnimaux = animalRepository.count()
                sub = subscriptionRepository.findById(1L).orElse(null)
            } catch (e: Exception) {
                println("[SuperAdmin] Erreur ferme '${t.schemaName}': ${e.message}")
            } finally {
                TenantContext.clear()
            }
            FermeAdminDTO(
                id                  = t.id,
                fermeCode           = t.fermeCode,
                nomFerme            = t.nomFerme,
                active              = t.active,
                subscriptionStatut  = sub?.statut?.name,
                planNom             = sub?.planNom,
                endDate             = sub?.endDate?.toString(),
                trialEndsAt         = sub?.trialEndsAt?.toString(),
                nbUtilisateurs      = nbUsers,
                nbAnimaux           = nbAnimaux
            )
        }
        return ResponseEntity.ok(result)
    }

    @PutMapping("/fermes/{fermeCode}/toggle")
    fun toggleFerme(@PathVariable fermeCode: String): ResponseEntity<Any> {
        val tenant = tenantRepository.findAll().find { it.fermeCode == fermeCode }
            ?: return ResponseEntity.notFound().build()
        tenant.active = !tenant.active
        tenantRepository.save(tenant)
        return ResponseEntity.ok(mapOf(
            "fermeCode" to fermeCode,
            "active"    to tenant.active,
            "message"   to "Ferme ${if (tenant.active) "activee" else "suspendue"} avec succes."
        ))
    }

    // ── Plans — CRUD ─────────────────────────────────────────────────────────

    @GetMapping("/plans")
    @Transactional(readOnly = true)
    fun getAllPlans(): ResponseEntity<Any> {
        return try {
            val plans = planConfigRepository.findAll().sortedBy { it.ordre }
            println("[SuperAdmin] getAllPlans() → ${plans.size} plans trouvés")
            ResponseEntity.ok(plans)
        } catch (e: Exception) {
            println("[SuperAdmin] ❌ getAllPlans() exception : ${e.javaClass.name} — ${e.message}")
            e.printStackTrace()
            ResponseEntity.status(500).body(
                mapOf(
                    "error"  to "Erreur lors du chargement des plans",
                    "detail" to e.message,
                    "cause"  to e.cause?.message,
                    "type"   to e.javaClass.name
                )
            )
        }
    }

    @PostMapping("/plans")
    fun createPlan(@RequestBody dto: PlanConfigCreateDTO): ResponseEntity<Any> {
        if (planConfigRepository.findByNom(dto.nom) != null)
            return ResponseEntity.badRequest().body(mapOf("error" to "Un plan avec ce nom existe deja."))

        val now = LocalDateTime.now()
        val plan = planConfigRepository.save(PlanConfig(
            nom                 = dto.nom,
            description         = dto.description,
            prixFcfa            = dto.prixFcfa,
            dureeDays           = dto.dureeDays,
            trialDays           = dto.trialDays,
            maxAnimaux          = dto.maxAnimaux,
            maxUtilisateurs     = dto.maxUtilisateurs,
            maxBatiments        = dto.maxBatiments,
            hasAssistantIA      = dto.hasAssistantIA,
            hasAlertesSms       = dto.hasAlertesSms,
            hasSyntheseComplete = dto.hasSyntheseComplete,
            hasExportPdf        = dto.hasExportPdf,
            hasPrevisionPrix    = dto.hasPrevisionPrix,
            actif               = dto.actif,
            ordre               = dto.ordre,
            createdAt           = now,
            updatedAt           = now
        ))
        return ResponseEntity.ok(plan)
    }

    @PutMapping("/plans/{id}")
    fun updatePlan(@PathVariable id: Long, @RequestBody dto: PlanConfigCreateDTO): ResponseEntity<Any> {
        val plan = planConfigRepository.findById(id).orElse(null)
            ?: return ResponseEntity.notFound().build()

        // Vérifier unicité du nom (si changé)
        val existing = planConfigRepository.findByNom(dto.nom)
        if (existing != null && existing.id != id)
            return ResponseEntity.badRequest().body(mapOf("error" to "Un autre plan porte deja ce nom."))

        plan.nom                 = dto.nom
        plan.description         = dto.description
        plan.prixFcfa            = dto.prixFcfa
        plan.dureeDays           = dto.dureeDays
        plan.trialDays           = dto.trialDays
        plan.maxAnimaux          = dto.maxAnimaux
        plan.maxUtilisateurs     = dto.maxUtilisateurs
        plan.maxBatiments        = dto.maxBatiments
        plan.hasAssistantIA      = dto.hasAssistantIA
        plan.hasAlertesSms       = dto.hasAlertesSms
        plan.hasSyntheseComplete = dto.hasSyntheseComplete
        plan.hasExportPdf        = dto.hasExportPdf
        plan.hasPrevisionPrix    = dto.hasPrevisionPrix
        plan.actif               = dto.actif
        plan.ordre               = dto.ordre
        plan.updatedAt           = LocalDateTime.now()
        planConfigRepository.save(plan)

        return ResponseEntity.ok(plan)
    }

    @DeleteMapping("/plans/{id}")
    fun deletePlan(@PathVariable id: Long): ResponseEntity<Any> {
        planConfigRepository.findById(id).orElse(null)
            ?: return ResponseEntity.notFound().build()
        // Vérifier qu'aucune ferme n'utilise ce plan
        val inUse = tenantRepository.findAll().any { t ->
            var used = false
            TenantContext.setTenant(t.schemaName)
            try {
                val sub = subscriptionRepository.findById(1L).orElse(null)
                used = sub?.planConfigId == id
            } finally { TenantContext.clear() }
            used
        }
        if (inUse) return ResponseEntity.badRequest()
            .body(mapOf("error" to "Ce plan est utilise par au moins une ferme active."))

        planConfigRepository.deleteById(id)
        return ResponseEntity.ok(mapOf("message" to "Plan supprime avec succes."))
    }

    @PutMapping("/plans/{id}/toggle-actif")
    fun togglePlanActif(@PathVariable id: Long): ResponseEntity<Any> {
        val plan = planConfigRepository.findById(id).orElse(null)
            ?: return ResponseEntity.notFound().build()
        plan.actif     = !plan.actif
        plan.updatedAt = LocalDateTime.now()
        planConfigRepository.save(plan)
        return ResponseEntity.ok(mapOf(
            "id"      to plan.id,
            "actif"   to plan.actif,
            "message" to "Plan ${if (plan.actif) "active" else "desactive"}."
        ))
    }

    // ── Abonnements — gestion par ferme ──────────────────────────────────────

    /**
     * Attribution manuelle d'un plan à une ferme (sans paiement).
     * Utilisé pour les partenariats, phases de test, compensations.
     */
    @PutMapping("/subscriptions/{fermeCode}/assign")
    fun assignPlan(
        @PathVariable fermeCode: String,
        @RequestBody dto: ManualAssignDTO
    ): ResponseEntity<Any> {
        val tenant = tenantRepository.findAll().find { it.fermeCode == fermeCode }
            ?: return ResponseEntity.notFound().build()

        val plan = planConfigRepository.findById(dto.planId).orElse(null)
            ?: return ResponseEntity.badRequest().body(mapOf("error" to "Plan introuvable."))

        val superAdminEmail = SecurityContextHolder.getContext().authentication.name

        TenantContext.setTenant(tenant.schemaName)
        try {
            val sub = subscriptionRepository.findById(1L).orElseGet { Subscription() }
            val today   = LocalDate.now()
            val endDate = today.plusDays(dto.dureeDays.toLong())

            sub.planConfigId   = plan.id
            sub.planNom        = plan.nom
            sub.statut         = SubscriptionStatus.ACTIVE
            sub.startDate      = today
            sub.endDate        = endDate
            sub.graceEndsAt    = null
            sub.lastPaymentRef = "MANUAL-$superAdminEmail"
            sub.notes          = dto.notes ?: "Attribution manuelle — $superAdminEmail — $today"
            sub.updatedAt      = LocalDateTime.now()
            subscriptionRepository.save(sub)

            // SMS de notification à l'admin ferme
            val adminPhone = getAdminPhoneForTenant()
            if (adminPhone != null) {
                c0de4hopeService.sendSms(
                    phoneNumber = adminPhone,
                    message     = "Votre abonnement ${plan.nom} a ete active par l'administrateur jusqu'au $endDate.",
                    event       = SmsEvent.MANUAL
                )
            }

            return ResponseEntity.ok(mapOf(
                "message" to "Plan ${plan.nom} attribue a $fermeCode jusqu'au $endDate.",
                "endDate" to endDate.toString()
            ))
        } finally {
            TenantContext.clear()
        }
    }

    /**
     * Suspend ou lève la suspension d'une ferme.
     * Suspend : statut → SUSPENDED (accès bloqué)
     * Lève : statut → ACTIVE si endDate non dépassée, sinon EXPIRED
     */
    @PutMapping("/subscriptions/{fermeCode}/suspend")
    fun toggleSuspend(
        @PathVariable fermeCode: String,
        @RequestBody dto: SuspendDTO
    ): ResponseEntity<Any> {
        val tenant = tenantRepository.findAll().find { it.fermeCode == fermeCode }
            ?: return ResponseEntity.notFound().build()

        TenantContext.setTenant(tenant.schemaName)
        try {
            val sub = subscriptionRepository.findById(1L).orElse(null)
                ?: return ResponseEntity.badRequest().body(mapOf("error" to "Aucun abonnement pour cette ferme."))

            if (sub.statut == SubscriptionStatus.SUSPENDED) {
                // Lever la suspension
                val today = LocalDate.now()
                sub.statut = if (sub.endDate != null && sub.endDate!!.isAfter(today))
                    SubscriptionStatus.ACTIVE else SubscriptionStatus.EXPIRED
                sub.notes  = "Suspension levee le $today"
            } else {
                // Suspendre
                sub.statut = SubscriptionStatus.SUSPENDED
                sub.notes  = dto.raison ?: "Suspendu par le Super-Admin le ${LocalDate.now()}"
            }
            sub.updatedAt = LocalDateTime.now()
            subscriptionRepository.save(sub)

            return ResponseEntity.ok(mapOf(
                "statut"  to sub.statut.name,
                "message" to "Abonnement de $fermeCode : ${sub.statut.name}."
            ))
        } finally {
            TenantContext.clear()
        }
    }

    // ── Historique paiements ─────────────────────────────────────────────────

    /**
     * Retourne l'historique de toutes les transactions de toutes les fermes.
     * Tri : du plus récent au plus ancien.
     */
    @GetMapping("/transactions")
    fun getAllTransactions(): ResponseEntity<List<Map<String, Any?>>> {
        val all = mutableListOf<Map<String, Any?>>()
        val tenants = tenantRepository.findAll()

        for (tenant in tenants) {
            TenantContext.setTenant(tenant.schemaName)
            try {
                val txs = paymentTransactionRepository.findAllByOrderByCreatedAtDesc()
                txs.forEach { tx ->
                    all.add(mapOf(
                        "fermeCode"       to tenant.fermeCode,
                        "nomFerme"        to tenant.nomFerme,
                        "planNom"         to tx.planNom,
                        "phoneNumber"     to maskPhone(tx.phoneNumber),
                        "montantAttendu"  to tx.montantAttendu,
                        "statut"          to tx.statut.name,
                        "responseCode"    to tx.responseCode,
                        "createdAt"       to tx.createdAt.toString()
                    ))
                }
            } catch (e: Exception) {
                println("[SuperAdmin] Erreur transactions '${tenant.schemaName}': ${e.message}")
            } finally {
                TenantContext.clear()
            }
        }

        return ResponseEntity.ok(all.sortedByDescending { it["createdAt"].toString() })
    }

    // ── Helpers privés ───────────────────────────────────────────────────────

    /**
     * Masque le numéro de téléphone pour les logs (ex: +22670XXXXXX98).
     * TenantContext doit être positionné avant l'appel.
     */
    private fun maskPhone(phone: String): String {
        if (phone.length < 6) return "****"
        return phone.take(6) + "X".repeat(phone.length - 8) + phone.takeLast(2)
    }

    /** Récupère le numéro de l'admin ferme du schéma courant. */
    private fun getAdminPhoneForTenant(): String? = try {
        utilisateurRepository.findAll()
            .firstOrNull { it.role?.nom == "ROLE_ADMINISTRATEUR" }
            ?.telephone?.takeIf { it.isNotBlank() }
    } catch (e: Exception) { null }
}
