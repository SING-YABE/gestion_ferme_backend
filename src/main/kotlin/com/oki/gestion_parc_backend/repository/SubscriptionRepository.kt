package com.oki.gestion_parc_backend.repository

import com.oki.gestion_parc_backend.model.Subscription
import com.oki.gestion_parc_backend.model.SubscriptionStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface SubscriptionRepository : JpaRepository<Subscription, Long> {

    /**
     * Utilisé par le SubscriptionExpiryJob dans le schéma PUBLIC pour trouver
     * toutes les subscriptions à traiter.
     * Note : en multi-tenant, le job itère sur chaque schéma séparément —
     * cette méthode est appelée avec le bon TenantContext positionné.
     */
    fun findByStatutAndEndDateBefore(statut: SubscriptionStatus, date: LocalDate): List<Subscription>

    fun findByStatutAndGraceEndsAtBefore(statut: SubscriptionStatus, date: LocalDate): List<Subscription>

    fun findByStatutAndTrialEndsAtBefore(statut: SubscriptionStatus, date: LocalDate): List<Subscription>

    /** Fermes dont l'abonnement expire exactement dans N jours (pour SMS d'alerte). */
    fun findByStatutAndEndDateEquals(statut: SubscriptionStatus, date: LocalDate): List<Subscription>
}
