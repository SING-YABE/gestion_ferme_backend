package com.oki.gestion_parc_backend.service

import com.oki.gestion_parc_backend.dto.RegisterFermeDTO
import com.oki.gestion_parc_backend.model.Tenant

/**
 * Contrat du service de gestion des tenants.
 *
 * registerFerme : crée le schéma PostgreSQL, les tables, l'utilisateur admin,
 *                 les rôles, les types de tâches et l'abonnement FREE par défaut.
 * findAll       : liste tous les tenants (usage admin global).
 */
interface TenantService {
    fun registerFerme(dto: RegisterFermeDTO): Tenant
    fun findAll(): List<Tenant>
}
