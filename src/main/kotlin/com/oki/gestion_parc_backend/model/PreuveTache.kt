package com.oki.gestion_parc_backend.model

import com.fasterxml.jackson.annotation.JsonIgnore
import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * Photo uploadée par l'assigné comme preuve de réalisation d'une tâche.
 */
@Entity
@Table(name = "preuves_taches")
data class PreuveTache(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assignation_id", nullable = false)
    @JsonIgnore
    var assignation: AssignationTache? = null,

    /** URL relative accessible par le frontend (ex: /uploads/taches/img.jpg) */
    @Column(nullable = false)
    var urlFichier: String = "",

    @Column(nullable = false)
    var dateUpload: LocalDateTime = LocalDateTime.now()
)
