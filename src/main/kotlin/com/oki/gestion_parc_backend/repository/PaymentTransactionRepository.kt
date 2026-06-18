package com.oki.gestion_parc_backend.repository

import com.oki.gestion_parc_backend.model.PaymentStatut
import com.oki.gestion_parc_backend.model.PaymentTransaction
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PaymentTransactionRepository : JpaRepository<PaymentTransaction, Long> {

    /** Historique complet des paiements d'une ferme, du plus récent au plus ancien. */
    fun findAllByOrderByCreatedAtDesc(): List<PaymentTransaction>

    /**
     * Vérifie l'idempotence : si un SUCCESS existe déjà pour ce numéro et ce plan,
     * on refuse la deuxième tentative (protection double-clic).
     */
    fun existsByPhoneNumberAndPlanConfigIdAndStatut(
        phoneNumber: String,
        planConfigId: Long,
        statut: PaymentStatut
    ): Boolean
}
