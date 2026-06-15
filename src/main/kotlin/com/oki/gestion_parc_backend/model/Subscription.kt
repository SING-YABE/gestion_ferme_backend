package com.oki.gestion_parc_backend.model

import jakarta.persistence.*
import java.time.LocalDate

/**
 * Entité singleton (1 seule ligne, id = 1) représentant le plan actif de la ferme.
 *
 * Entrées/sorties :
 *   - plan        : FREE ou PRO
 *   - dateDebut   : date d'activation du plan courant
 *   - dateFin     : date d'expiration (null = pas d'expiration)
 *   - active      : true si l'abonnement est actif
 *   - notes       : note interne (ex: "Activé manuellement en attente paiement")
 */
@Entity
@Table(name = "subscription")
class Subscription(

    @Id
    val id: Long = 1L,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var plan: SubscriptionPlan = SubscriptionPlan.FREE,

    @Column(nullable = false)
    var dateDebut: LocalDate = LocalDate.now(),

    @Column(nullable = true)
    var dateFin: LocalDate? = null,

    @Column(nullable = false)
    var active: Boolean = true,

    @Column(nullable = true)
    var notes: String? = null
)
