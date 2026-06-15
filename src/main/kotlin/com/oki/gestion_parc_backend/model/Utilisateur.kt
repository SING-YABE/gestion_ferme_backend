package com.oki.gestion_parc_backend.model

import jakarta.persistence.*

@Entity
@Table(name = "utilisateurs")
open class Utilisateur(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idutilisateur")
    open var idUtilisateur: Long = 0L,

    @Column(nullable = false)
    open var poste: String = "",

    @Column(nullable = false)
    open var nom: String = "",

    @Column(nullable = false)
    open var prenom: String = "",

    @Column(nullable = false, unique = true)
    open var email: String = "",

    @Column(nullable = false)
    open var telephone: String = "",

    @Column(nullable = false)
    open var password: String = "",

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    open var role: Role? = null,

)



