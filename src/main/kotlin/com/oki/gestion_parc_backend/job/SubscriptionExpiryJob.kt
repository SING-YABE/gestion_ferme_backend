package com.oki.gestion_parc_backend.job

import com.oki.gestion_parc_backend.model.SmsEvent
import com.oki.gestion_parc_backend.model.SubscriptionStatus
import com.oki.gestion_parc_backend.repository.*
import com.oki.gestion_parc_backend.security.TenantContext
import com.oki.gestion_parc_backend.service.C0de4hopeService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.LocalDate

/**
 * Job planifié qui tourne chaque nuit à 00h00 (UTC+0 = heure Ouagadougou).
 *
 * Responsabilités :
 *   1. Pour chaque ferme (tenant) :
 *      a. ACTIVE  dont endDate < aujourd'hui → passer en GRACE (graceEndsAt = endDate + 3j)
 *      b. GRACE   dont graceEndsAt < aujourd'hui → passer en EXPIRED
 *      c. TRIAL   dont trialEndsAt < aujourd'hui → passer en EXPIRED
 *   2. Envoyer les SMS d'avertissement :
 *      - J-7 : SMS EXPIRY_7D (si pas déjà envoyé)
 *      - J-3 : SMS EXPIRY_3D (si pas déjà envoyé)
 *      - J=0 : SMS EXPIRY_0D (si pas déjà envoyé)
 *
 * Architecture multi-tenant :
 *   Le job itère sur tous les tenants de public.tenants,
 *   positionne le TenantContext pour chaque ferme,
 *   et exécute les requêtes JPA dans le bon schéma.
 *
 * @Scheduled cron : "0 0 0 * * *" = tous les jours à minuit UTC
 */
@Component
class SubscriptionExpiryJob(
    private val tenantRepository: TenantRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val smsLogRepository: SmsLogRepository,
    private val utilisateurRepository: UtilisateurRepository,
    private val roleRepository: RoleRepository,
    private val c0de4hopeService: C0de4hopeService
) {

    /**
     * Point d'entrée principal — lancé chaque nuit à 00h00 UTC.
     * Itère sur tous les tenants actifs et traite leurs abonnements.
     */
    @Scheduled(cron = "0 0 0 * * *", zone = "UTC")
    fun runNightly() {
        println("[ExpiryJob] ====== Début du job nuit ${LocalDate.now()} ======")
        val tenants = tenantRepository.findAll()
        tenants.forEach { tenant ->
            try {
                TenantContext.setTenant(tenant.schemaName)
                processTenant(tenant.schemaName, tenant.nomFerme)
            } catch (e: Exception) {
                println("[ExpiryJob] ERREUR schéma ${tenant.schemaName} : ${e.message}")
            } finally {
                TenantContext.clear()
            }
        }
        println("[ExpiryJob] ====== Fin du job nuit ======")
    }

    // ── Traitement d'un tenant ───────────────────────────────────────────────

    private fun processTenant(schema: String, nomFerme: String) {
        val today = LocalDate.now()
        val sub = subscriptionRepository.findById(1L).orElse(null) ?: return

        // Récupérer le numéro de l'admin pour les SMS
        val adminPhone = getAdminPhone()
        val planNom = sub.planNom ?: "votre plan"

        // ── Transitions d'état ───────────────────────────────────────────────

        when (sub.statut) {

            SubscriptionStatus.ACTIVE -> {
                val endDate = sub.endDate
                if (endDate != null) {
                    // ACTIVE → GRACE si expiré
                    if (today.isAfter(endDate)) {
                        sub.statut      = SubscriptionStatus.GRACE
                        sub.graceEndsAt = endDate.plusDays(3)
                        subscriptionRepository.save(sub)
                        println("[ExpiryJob] $schema : ACTIVE → GRACE (graceEndsAt=${sub.graceEndsAt})")

                        // SMS J=0
                        if (adminPhone != null) sendSmsIfNotSent(adminPhone, SmsEvent.EXPIRY_0D,
                            "Votre abonnement $planNom a expire. Vous avez 3 jours de grace " +
                            "(acces lecture seule). Renouvelez sur l'application.")
                    } else {
                        // SMS d'alerte J-7 et J-3
                        if (adminPhone != null) {
                            sendExpiryAlerts(adminPhone, endDate, planNom)
                        }
                    }
                }
            }

            SubscriptionStatus.GRACE -> {
                val graceEnd = sub.graceEndsAt
                if (graceEnd != null && today.isAfter(graceEnd)) {
                    sub.statut = SubscriptionStatus.EXPIRED
                    subscriptionRepository.save(sub)
                    println("[ExpiryJob] $schema : GRACE → EXPIRED")
                }
            }

            SubscriptionStatus.TRIAL -> {
                val trialEnd = sub.trialEndsAt
                if (trialEnd != null && today.isAfter(trialEnd)) {
                    sub.statut = SubscriptionStatus.EXPIRED
                    subscriptionRepository.save(sub)
                    println("[ExpiryJob] $schema : TRIAL → EXPIRED")

                    if (adminPhone != null) sendSmsIfNotSent(adminPhone, SmsEvent.EXPIRY_0D,
                        "Votre periode d'essai a expire. Choisissez un plan sur l'application " +
                        "pour continuer a utiliser Gestion Ferme.")
                }
            }

            else -> { /* EXPIRED / SUSPENDED / CANCELLED : rien à faire */ }
        }
    }

    // ── Envoi des SMS d'alerte ───────────────────────────────────────────────

    /**
     * Envoie les SMS J-7 et J-3 si pas encore envoyés pour cet abonnement.
     */
    private fun sendExpiryAlerts(adminPhone: String, endDate: LocalDate, planNom: String) {
        val today = LocalDate.now()
        val daysLeft = endDate.toEpochDay() - today.toEpochDay()

        when (daysLeft.toInt()) {
            7 -> sendSmsIfNotSent(adminPhone, SmsEvent.EXPIRY_7D,
                "Votre abonnement $planNom expire le $endDate. " +
                "Renouvelez sur l'application pour continuer sans interruption.")

            3 -> sendSmsIfNotSent(adminPhone, SmsEvent.EXPIRY_3D,
                "URGENT : votre abonnement $planNom expire dans 3 jours ($endDate). " +
                "Envoyez le montant exact au 56239334 (Orange Money) puis validez sur l'app.")
        }
    }

    /**
     * Envoie un SMS uniquement si aucun SMS du même type n'a été envoyé avec succès.
     * Garantit qu'on n'envoie jamais deux fois le même SMS d'alerte.
     */
    private fun sendSmsIfNotSent(phoneNumber: String, event: SmsEvent, message: String) {
        val dejaEnvoye = smsLogRepository.existsByPhoneNumberAndEvent(phoneNumber, event)
        if (!dejaEnvoye) {
            c0de4hopeService.sendSms(phoneNumber, message, event)
        }
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    /**
     * Récupère le numéro de téléphone de l'administrateur de la ferme.
     * L'administrateur est le premier utilisateur ayant le rôle ROLE_ADMINISTRATEUR.
     * Retourne null si aucun admin n'est trouvé.
     */
    private fun getAdminPhone(): String? {
        return try {
            val roleAdmin = roleRepository.findByNom("ROLE_ADMINISTRATEUR") ?: return null
            val admins = utilisateurRepository.findAllByRoleId(roleAdmin.idRole)
            admins.firstOrNull()?.telephone?.takeIf { it.isNotBlank() }
        } catch (e: Exception) {
            null
        }
    }
}
