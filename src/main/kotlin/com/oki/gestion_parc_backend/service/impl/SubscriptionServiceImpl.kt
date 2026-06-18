package com.oki.gestion_parc_backend.service.impl

import com.oki.gestion_parc_backend.dto.*
import com.oki.gestion_parc_backend.model.Subscription
import com.oki.gestion_parc_backend.model.SubscriptionStatus
import com.oki.gestion_parc_backend.repository.AnimalRepository
import com.oki.gestion_parc_backend.repository.PlanConfigRepository
import com.oki.gestion_parc_backend.repository.SubscriptionRepository
import com.oki.gestion_parc_backend.repository.UtilisateurRepository
import com.oki.gestion_parc_backend.service.SubscriptionService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

/**
 * Implémentation du service d'abonnement.
 *
 * Contexte multi-tenant : toutes les requêtes JPA (Subscription, Animal...)
 * sont automatiquement routées vers le schéma de la ferme connectée
 * grâce au TenantContext positionné par le filtre JWT.
 * PlanConfigRepository pointe vers public.plan_config (schema="public" dans l'entité).
 */
@Service
class SubscriptionServiceImpl(
    private val subscriptionRepository: SubscriptionRepository,
    private val planConfigRepository: PlanConfigRepository,
    private val animalRepository: AnimalRepository,
    private val utilisateurRepository: UtilisateurRepository
) : SubscriptionService {

    // ── Helpers ──────────────────────────────────────────────────────────────

    /**
     * Retourne la subscription de la ferme ou en crée une TRIAL par défaut.
     * Le plan TRIAL par défaut = le premier plan actif avec trialDays > 0.
     * Si aucun plan avec essai n'existe, le premier plan actif est utilisé.
     */
    @Transactional
    override fun getOrCreateSubscription(): Subscription =
        subscriptionRepository.findById(1L).orElseGet {
            val planEssai = planConfigRepository
                .findByActifTrueOrderByOrdreAsc()
                .firstOrNull { it.trialDays > 0 }
                ?: planConfigRepository.findByActifTrueOrderByOrdreAsc().firstOrNull()

            val sub = Subscription(
                planConfigId = planEssai?.id,
                planNom      = planEssai?.nom ?: "STARTER",
                statut       = SubscriptionStatus.TRIAL,
                trialEndsAt  = planEssai?.let { LocalDate.now().plusDays(it.trialDays.toLong()) }
                                ?: LocalDate.now().plusDays(14)
            )
            subscriptionRepository.save(sub)
        }

    // ── Interface SubscriptionService ────────────────────────────────────────

    /**
     * Retourne l'état complet de l'abonnement.
     * Charge le plan depuis public.plan_config si disponible,
     * sinon utilise les valeurs par défaut (STARTER).
     */
    override fun getStatus(): SubscriptionStatusDTO {
        val sub = getOrCreateSubscription()

        // Charger le plan actuel (peut être null si migration depuis l'ancienne version)
        val plan = sub.planConfigId?.let { planConfigRepository.findById(it).orElse(null) }

        // Valeurs par défaut si aucun plan trouvé (safeguard)
        val maxAnimaux      = plan?.maxAnimaux      ?: 10
        val maxUtilisateurs = plan?.maxUtilisateurs ?: 2
        val maxBatiments    = plan?.maxBatiments    ?: 1

        val currentAnimaux      = animalRepository.countByVenduFalse()
        val currentUtilisateurs = utilisateurRepository.count()
        // BatimentRepository sera ajouté quand le module sera créé
        val currentBatiments    = 0L

        val animauxLimitAtteinte = maxAnimaux != -1 && currentAnimaux >= maxAnimaux

        return SubscriptionStatusDTO(
            planNom        = sub.planNom ?: "STARTER",
            planId         = sub.planConfigId,
            statut         = sub.statut.name,
            accessAutorise = sub.isAccessAllowed(),
            pleinementActif = sub.isFullyActive(),
            trialEndsAt    = sub.trialEndsAt?.toString(),
            startDate      = sub.startDate?.toString(),
            endDate        = sub.endDate?.toString(),
            graceEndsAt    = sub.graceEndsAt?.toString(),
            limits = LimitsDTO(
                maxAnimaux           = maxAnimaux,
                maxUtilisateurs      = maxUtilisateurs,
                maxBatiments         = maxBatiments,
                currentAnimaux       = currentAnimaux,
                currentUtilisateurs  = currentUtilisateurs,
                currentBatiments     = currentBatiments,
                animauxLimitAtteinte = animauxLimitAtteinte
            ),
            features = FeaturesDTO(
                hasAssistantIA      = plan?.hasAssistantIA      ?: false,
                hasAlertesSms       = plan?.hasAlertesSms       ?: false,
                hasSyntheseComplete = plan?.hasSyntheseComplete ?: false,
                hasExportPdf        = plan?.hasExportPdf        ?: false,
                hasPrevisionPrix    = plan?.hasPrevisionPrix    ?: false
            )
        )
    }

    /** Liste des plans actifs pour l'écran public de sélection. */
    override fun getPlansPublics(): List<PlanPublicDTO> =
        planConfigRepository.findByActifTrueOrderByOrdreAsc().map { p ->
            PlanPublicDTO(
                id                  = p.id,
                nom                 = p.nom,
                description         = p.description,
                prixFcfa            = p.prixFcfa,
                dureeDays           = p.dureeDays,
                trialDays           = p.trialDays,
                maxAnimaux          = p.maxAnimaux,
                maxUtilisateurs     = p.maxUtilisateurs,
                maxBatiments        = p.maxBatiments,
                hasAssistantIA      = p.hasAssistantIA,
                hasAlertesSms       = p.hasAlertesSms,
                hasSyntheseComplete = p.hasSyntheseComplete,
                hasExportPdf        = p.hasExportPdf,
                hasPrevisionPrix    = p.hasPrevisionPrix,
                ordre               = p.ordre
            )
        }

    /**
     * Vérifie si le quota d'animaux du plan est atteint.
     * Appelé par AnimalServiceImpl avant chaque création.
     * Si l'abonnement est EXPIRED/SUSPENDED : bloque aussi.
     */
    override fun isAnimalLimitAtteinte(): Boolean {
        val sub = getOrCreateSubscription()

        // Accès bloqué si abonnement expiré ou suspendu
        if (!sub.isAccessAllowed()) return true

        val plan = sub.planConfigId?.let { planConfigRepository.findById(it).orElse(null) }
        val maxAnimaux = plan?.maxAnimaux ?: 10
        if (maxAnimaux == -1) return false   // illimité

        val current = animalRepository.countByVenduFalse()
        return current >= maxAnimaux
    }

    /**
     * Vérifie si une fonctionnalité premium est incluse dans le plan actuel.
     * @param feature : "assistantIA" | "alertesSms" | "syntheseComplete" | "exportPdf" | "previsionPrix"
     */
    override fun isFeatureAllowed(feature: String): Boolean {
        val sub = getOrCreateSubscription()
        if (!sub.isAccessAllowed()) return false
        val plan = sub.planConfigId?.let { planConfigRepository.findById(it).orElse(null) }
            ?: return false
        return when (feature) {
            "assistantIA"      -> plan.hasAssistantIA
            "alertesSms"       -> plan.hasAlertesSms
            "syntheseComplete" -> plan.hasSyntheseComplete
            "exportPdf"        -> plan.hasExportPdf
            "previsionPrix"    -> plan.hasPrevisionPrix
            else               -> false
        }
    }
}
