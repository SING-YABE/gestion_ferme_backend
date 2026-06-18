package com.oki.gestion_parc_backend.service

import com.oki.gestion_parc_backend.dto.PlanPublicDTO
import com.oki.gestion_parc_backend.dto.SubscriptionStatusDTO
import com.oki.gestion_parc_backend.model.Subscription

/**
 * Service principal de gestion de l'abonnement de la ferme.
 *
 * Méthodes :
 *   getStatus()               → État complet abonnement + limites + features
 *   getPlansPublics()         → Liste des plans actifs (pour l'écran de choix)
 *   isAnimalLimitAtteinte()   → true si quota animaux atteint (bloc création)
 *   isFeatureAllowed(feature) → true si la fonctionnalité est incluse dans le plan actuel
 *   getOrCreateSubscription() → Subscription existante ou créée en TRIAL
 */
interface SubscriptionService {

    /** Retourne l'état complet de l'abonnement pour le frontend/mobile. */
    fun getStatus(): SubscriptionStatusDTO

    /** Liste des plans actifs pour l'écran public de sélection. */
    fun getPlansPublics(): List<PlanPublicDTO>

    /**
     * Vérifie si le quota d'animaux du plan actuel est atteint.
     * Utilisé dans AnimalServiceImpl avant chaque création d'animal.
     * Retourne true → création bloquée (lever HTTP 402).
     */
    fun isAnimalLimitAtteinte(): Boolean

    /**
     * Vérifie si une fonctionnalité est incluse dans le plan actuel.
     * @param feature une des constantes : "assistantIA", "exportPdf", "previsionPrix"...
     */
    fun isFeatureAllowed(feature: String): Boolean

    /** Retourne la subscription existante ou en crée une TRIAL par défaut. */
    fun getOrCreateSubscription(): Subscription
}
