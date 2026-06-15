package com.oki.gestion_parc_backend.service

import com.oki.gestion_parc_backend.dto.PlanConfigDTO
import com.oki.gestion_parc_backend.dto.SubscriptionStatusDTO
import com.oki.gestion_parc_backend.model.PlanConfig
import com.oki.gestion_parc_backend.model.Subscription

/**
 * Service gérant le plan d'abonnement de la ferme et ses limites.
 *
 * Entrées / sorties de chaque méthode :
 *   getStatus()                  → DTO complet plan + limites + compteurs actuels
 *   getConfig()                  → entité PlanConfig (limites brutes)
 *   updateConfig(dto)            → PlanConfig mis à jour
 *   activatePro(notes)           → Subscription passée en PRO
 *   downgradeToFree()            → Subscription repassée en FREE
 *   isPlanFreeAndLimitAtteinte() → true si FREE et quota animaux atteint (pour bloc création)
 */
interface SubscriptionService {
    fun getStatus(): SubscriptionStatusDTO
    fun getConfig(): PlanConfig
    fun updateConfig(dto: PlanConfigDTO): PlanConfig
    fun activatePro(notes: String?): Subscription
    fun downgradeToFree(): Subscription
    fun isPlanFreeAndLimitAtteinte(): Boolean
}
