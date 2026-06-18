package com.oki.gestion_parc_backend.controller

import com.oki.gestion_parc_backend.dto.PlanPublicDTO
import com.oki.gestion_parc_backend.dto.SubscriptionStatusDTO
import com.oki.gestion_parc_backend.service.SubscriptionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Controller gérant la consultation de l'abonnement et des plans disponibles.
 *
 * Routes exposées :
 *   GET /api/plans             → liste des plans actifs (public, sans authentification)
 *   GET /api/subscriptions/me  → statut complet abonnement de la ferme connectée
 *
 * Note : les endpoints de paiement sont dans SubscriptionPaymentController.
 * Note : le CRUD plans Super-Admin est dans SuperAdminPlanController.
 */
@RestController
class SubscriptionController(
    private val subscriptionService: SubscriptionService
) {

    /**
     * Liste des plans actifs disponibles à la souscription.
     * Public : accessible sans authentification (pour l'écran de choix de plan).
     */
    @GetMapping("/api/plans")
    fun getPlansPublics(): ResponseEntity<List<PlanPublicDTO>> =
        ResponseEntity.ok(subscriptionService.getPlansPublics())

    /**
     * Statut complet de l'abonnement de la ferme connectée.
     * Inclut le plan actuel, le statut, les dates et les limites avec usage en temps réel.
     * Accessible à tout utilisateur authentifié de la ferme.
     */
    @GetMapping("/api/subscriptions/me")
    fun getMySubscription(): ResponseEntity<SubscriptionStatusDTO> =
        ResponseEntity.ok(subscriptionService.getStatus())
}
