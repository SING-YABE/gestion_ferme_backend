package com.oki.gestion_parc_backend.service.impl

import com.oki.gestion_parc_backend.dto.LimitsDTO
import com.oki.gestion_parc_backend.dto.PlanConfigDTO
import com.oki.gestion_parc_backend.dto.SubscriptionStatusDTO
import com.oki.gestion_parc_backend.model.PlanConfig
import com.oki.gestion_parc_backend.model.Subscription
import com.oki.gestion_parc_backend.model.SubscriptionPlan
import com.oki.gestion_parc_backend.repository.AnimalRepository
import com.oki.gestion_parc_backend.repository.PlanConfigRepository
import com.oki.gestion_parc_backend.repository.SubscriptionRepository
import com.oki.gestion_parc_backend.service.SubscriptionService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

@Service
class SubscriptionServiceImpl(
    private val subscriptionRepository: SubscriptionRepository,
    private val planConfigRepository: PlanConfigRepository,
    private val animalRepository: AnimalRepository
) : SubscriptionService {

    // ── Helpers internes ────────────────────────────────────────────────────────

    /**
     * Retourne la subscription existante ou en crée une FREE par défaut.
     * Garantit qu'il y a toujours 1 ligne en base (singleton id=1).
     */
    private fun getOrCreateSubscription(): Subscription =
        subscriptionRepository.findById(1L).orElseGet {
            subscriptionRepository.save(Subscription())
        }

    /**
     * Retourne la config des limites existante ou en crée une par défaut (max=5).
     * Garantit qu'il y a toujours 1 ligne en base (singleton id=1).
     */
    private fun getOrCreateConfig(): PlanConfig =
        planConfigRepository.findById(1L).orElseGet {
            planConfigRepository.save(PlanConfig())
        }

    // ── Implémentation ──────────────────────────────────────────────────────────

    /**
     * Retourne le statut complet de l'abonnement pour le mobile.
     * Inclut le plan, les dates, et les compteurs animaux en temps réel.
     */
    override fun getStatus(): SubscriptionStatusDTO {
        val sub     = getOrCreateSubscription()
        val config  = getOrCreateConfig()
        val currentAnimaux = animalRepository.countByVenduFalse()

        // PRO = illimité (-1), FREE = limite configurée
        val maxAnimaux = if (sub.plan == SubscriptionPlan.PRO) -1 else config.maxAnimauxFreePlan
        val limitAtteinte = maxAnimaux != -1 && currentAnimaux >= maxAnimaux

        return SubscriptionStatusDTO(
            plan       = sub.plan.name,
            isPro      = sub.plan == SubscriptionPlan.PRO,
            dateDebut  = sub.dateDebut.toString(),
            dateFin    = sub.dateFin?.toString(),
            active     = sub.active,
            limits     = LimitsDTO(
                maxAnimaux     = maxAnimaux,
                currentAnimaux = currentAnimaux,
                limitAtteinte  = limitAtteinte
            )
        )
    }

    /** Retourne la configuration brute des limites (pour l'écran admin). */
    override fun getConfig(): PlanConfig = getOrCreateConfig()

    /**
     * Met à jour la limite d'animaux pour le plan FREE.
     * Accessible uniquement à l'administrateur (permission SUBSCRIPTION_MANAGE).
     */
    @Transactional
    override fun updateConfig(dto: PlanConfigDTO): PlanConfig {
        val config = getOrCreateConfig()
        config.maxAnimauxFreePlan = dto.maxAnimauxFreePlan
        config.updatedAt = LocalDateTime.now()
        return planConfigRepository.save(config)
    }

    /**
     * Active le plan PRO manuellement (en attente d'intégration paiement).
     * L'admin peut ajouter une note (ex: "Paiement reçu le 15/06/2026").
     */
    @Transactional
    override fun activatePro(notes: String?): Subscription {
        val sub = getOrCreateSubscription()
        sub.plan      = SubscriptionPlan.PRO
        sub.dateDebut = LocalDate.now()
        sub.dateFin   = null   // pas d'expiration pour activation manuelle
        sub.active    = true
        sub.notes     = notes
        return subscriptionRepository.save(sub)
    }

    /**
     * Rétrograde la ferme vers FREE (ex: non-renouvellement).
     */
    @Transactional
    override fun downgradeToFree(): Subscription {
        val sub = getOrCreateSubscription()
        sub.plan    = SubscriptionPlan.FREE
        sub.dateFin = LocalDate.now()
        sub.notes   = "Rétrogradé vers FREE le ${LocalDate.now()}"
        return subscriptionRepository.save(sub)
    }

    /**
     * Vérifie si le plan est FREE et si le quota d'animaux est atteint.
     * Utilisé dans AnimalServiceImpl avant chaque création d'animal.
     *
     * Retourne true  → la création doit être bloquée
     * Retourne false → la création est autorisée
     */
    override fun isPlanFreeAndLimitAtteinte(): Boolean {
        val sub = getOrCreateSubscription()
        if (sub.plan == SubscriptionPlan.PRO) return false
        val config = getOrCreateConfig()
        val currentAnimaux = animalRepository.countByVenduFalse()
        return currentAnimaux >= config.maxAnimauxFreePlan
    }
}
