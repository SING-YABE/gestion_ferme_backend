package com.oki.gestion_parc_backend.model

enum class StatutTache {
    A_FAIRE,              // créée, assignée, pas encore commencée
    EN_COURS,             // l'assigné a démarré
    EN_ATTENTE_VALIDATION,// l'assigné a soumis ses preuves, en attente du gérant
    VALIDEE,              // gérant a approuvé
    INVALIDEE,            // gérant a refusé (commentaire obligatoire) → repasse A_FAIRE
    EXPIREE               // date dépassée sans soumission
}
