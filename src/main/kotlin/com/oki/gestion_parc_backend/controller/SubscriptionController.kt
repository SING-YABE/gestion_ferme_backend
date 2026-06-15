package com.oki.gestion_parc_backend.controller

import com.oki.gestion_parc_backend.dto.PlanConfigDTO
import com.oki.gestion_parc_backend.dto.SubscriptionStatusDTO
import com.oki.gestion_parc_backend.service.SubscriptionService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

/**
 * Controller gérant l'abonnement SaaS de la ferme.
 *
 * Routes exposées :
 *   GET  /subscription/status        → plan actif + limites + compteurs (tous les utilisateurs)
 *   GET  /subscription/config        → config des limites FREE (admin)
 *   PUT  /subscription/config        → modifier la limite d'animaux FREE (admin)
 *   PUT  /subscription/activate-pro  → activer PRO manuellement (admin, en attente paiement)
 *   PUT  /subscription/downgrade     → rétrograder vers FREE (admin)
 */
@RestController
@RequestMapping("/subscription")
class SubscriptionController(
    private val subscriptionService: SubscriptionService
) {

    /**
     * Retourne le statut de l'abonnement avec les limites en vigueur.
     * Appelé par le mobile Flutter juste après le login.
     * Accessible à tout utilisateur authentifié (pas de restriction de rôle).
     */
    @GetMapping("/status")
    fun getStatus(): ResponseEntity<SubscriptionStatusDTO> =
        ResponseEntity.ok(subscriptionService.getStatus())

    /**
     * Retourne la configuration brute des limites du plan FREE.
     * Accessible uniquement à l'administrateur.
     */
    @GetMapping("/config")
    @PreAuthorize("hasAuthority('SUBSCRIPTION_MANAGE')")
    fun getConfig(): ResponseEntity<PlanConfigDTO> {
        val config = subscriptionService.getConfig()
        return ResponseEntity.ok(PlanConfigDTO(maxAnimauxFreePlan = config.maxAnimauxFreePlan))
    }

    /**
     * Modifie la limite d'animaux du plan FREE.
     * Entrée : { "maxAnimauxFreePlan": 10 }
     * Accessible uniquement à l'administrateur.
     */
    @PutMapping("/config")
    @PreAuthorize("hasAuthority('SUBSCRIPTION_MANAGE')")
    fun updateConfig(@RequestBody dto: PlanConfigDTO): ResponseEntity<PlanConfigDTO> {
        val updated = subscriptionService.updateConfig(dto)
        return ResponseEntity.ok(PlanConfigDTO(maxAnimauxFreePlan = updated.maxAnimauxFreePlan))
    }

    /**
     * Active le plan PRO manuellement (utilisé en attendant l'intégration paiement).
     * Paramètre optionnel "notes" pour documenter la raison (ex: "Paiement Orange Money reçu").
     * Accessible uniquement à l'administrateur.
     */
    @PutMapping("/activate-pro")
    @PreAuthorize("hasAuthority('SUBSCRIPTION_MANAGE')")
    fun activatePro(
        @RequestParam(required = false) notes: String?
    ): ResponseEntity<Map<String, Any?>> {
        val sub = subscriptionService.activatePro(notes)
        return ResponseEntity.ok(mapOf(
            "message"    to "Plan PRO activé avec succès",
            "plan"       to sub.plan.name,
            "dateDebut"  to sub.dateDebut.toString(),
            "notes"      to sub.notes
        ))
    }

    /**
     * Rétrograde la ferme vers le plan FREE.
     * Accessible uniquement à l'administrateur.
     */
    @PutMapping("/downgrade")
    @PreAuthorize("hasAuthority('SUBSCRIPTION_MANAGE')")
    fun downgrade(): ResponseEntity<Map<String, Any>> {
        val sub = subscriptionService.downgradeToFree()
        return ResponseEntity.ok(mapOf(
            "message" to "Ferme rétrogradée vers le plan FREE",
            "plan"    to sub.plan.name
        ))
    }
}
