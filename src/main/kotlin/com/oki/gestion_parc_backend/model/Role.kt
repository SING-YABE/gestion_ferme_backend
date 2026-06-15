package com.oki.gestion_parc_backend.model

import jakarta.persistence.*
import com.fasterxml.jackson.annotation.JsonIgnore

@Entity
@Table(name = "roles")
data class Role(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idrole")
    val idRole: Long = 0,

    @Column(nullable = false, unique = true)
    var nom: String = "",

    @OneToMany(mappedBy = "role", cascade = [CascadeType.PERSIST, CascadeType.MERGE], fetch = FetchType.LAZY)
    @JsonIgnore
    val utilisateurs: MutableList<Utilisateur> = mutableListOf()
)
