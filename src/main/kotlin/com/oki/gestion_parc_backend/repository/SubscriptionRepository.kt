package com.oki.gestion_parc_backend.repository

import com.oki.gestion_parc_backend.model.Subscription
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SubscriptionRepository : JpaRepository<Subscription, Long>
