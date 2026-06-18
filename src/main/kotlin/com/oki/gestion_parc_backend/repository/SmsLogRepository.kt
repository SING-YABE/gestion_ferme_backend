package com.oki.gestion_parc_backend.repository

import com.oki.gestion_parc_backend.model.SmsEvent
import com.oki.gestion_parc_backend.model.SmsLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SmsLogRepository : JpaRepository<SmsLog, Long> {

    /**
     * Vérifie si un SMS de type [event] a déjà été envoyé avec succès pour cet abonnement.
     * Utilisé par le job d'expiration pour ne pas envoyer deux fois le même SMS.
     * phoneNumber = numéro de l'admin ferme.
     */
    fun existsByPhoneNumberAndEvent(phoneNumber: String, event: SmsEvent): Boolean

    /** Historique SMS d'une ferme, du plus récent au plus ancien. */
    fun findAllByOrderByCreatedAtDesc(): List<SmsLog>
}
