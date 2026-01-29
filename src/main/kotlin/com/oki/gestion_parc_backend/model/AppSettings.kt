package com.oki.gestion_parc_backend.model

import jakarta.annotation.Nullable
import jakarta.persistence.*

@Entity
@Table(name = "app_settings")
class AppSettings(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false)
    var farmName: String = "",

    @Column(nullable = false)
    var contactEmail: String = "",

    @Column(nullable = false)
    var contactTel: String = "",

    @Column(nullable = true)
    var slogan: String = "",

    @Column
    var logoPath: String? = null
)
