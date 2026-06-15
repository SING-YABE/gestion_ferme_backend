package com.oki.gestion_parc_backend.dto

/**
 * DTO d'inscription d'une nouvelle ferme cliente (tenant SaaS).
 *
 * Endpoint : POST /api/register-ferme (public, sans authentification)
 *
 * Contraintes :
 *   - fermeCode : unique, alphanumérique + underscores, 3-50 chars (ex: "ferme_bf_001")
 *   - fermeCode devient le nom du schéma PostgreSQL (converti en minuscules)
 */
data class RegisterFermeDTO(
    val fermeCode: String,       // ex: "ferme_bf_001" — identifiant unique de la ferme
    val nomFerme: String,        // ex: "Ferme Kaboré" — nom affiché
    val adminEmail: String,      // Email du premier administrateur
    val adminPassword: String,   // Mot de passe (hashé côté backend)
    val adminNom: String,
    val adminPrenom: String
)
