package com.oki.gestion_parc_backend.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table


@Entity
@Table(name = "parametres_eleveur")
data class ParametresEleveur(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "seuil_nes_vivants", nullable = false)
    val seuilNesVivants: Int,

    @Column(name = "nb_mises_bas_max", nullable = false)
    val nbMisesBasMax: Int,

    @Column(name = "seuil_occupation_box_warning", nullable = false)
    val seuilOccupationBoxWarning: Double,


    @Column(name = "seuil_occupation_box_critique", nullable = false)
    val seuilOccupationBoxCritique: Double
)