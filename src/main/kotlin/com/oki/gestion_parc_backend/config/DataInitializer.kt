package com.oki.gestion_parc_backend.config

import com.oki.gestion_parc_backend.model.*
import com.oki.gestion_parc_backend.repository.*
import com.oki.gestion_parc_backend.security.TenantContext
import com.oki.gestion_parc_backend.service.SchemaCreationService
import jakarta.annotation.PostConstruct
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import javax.sql.DataSource

/**
 * Initialisation des données au démarrage — version multi-tenant.
 *
 * Séquence (idempotente) :
 *   1. SchemaCreationService.initializeSchema("ferme_default")
 *      → CREATE SCHEMA IF NOT EXISTS ferme_default
 *      → Crée toutes les tables JPA dans ferme_default (ddl-auto=update via EMF temporaire)
 *      → Crée aussi public.tenants (car Tenant a @Table(schema="public"))
 *
 *   2. Création du tenant par défaut dans public.tenants (si absent)
 *
 *   3. TenantContext.setTenant("ferme_default")
 *      → Toutes les queries JPA suivantes vont dans ferme_default
 *
 *   4. Création des rôles, utilisateurs, types de tâches, abonnement dans ferme_default
 *
 *   5. TenantContext.clear()
 */
@Component
class DataInitializer(
    private val roleRepository: RoleRepository,
    private val utilisateurRepository: UtilisateurRepository,
    private val typeTacheRepository: TypeTacheRepository,
    private val passwordEncoder: PasswordEncoder,
    private val subscriptionRepository: SubscriptionRepository,
    private val planConfigRepository: PlanConfigRepository,
    private val tenantRepository: TenantRepository,
    private val superAdminRepository: SuperAdminRepository,
    private val schemaCreationService: SchemaCreationService,
    private val dataSource: DataSource
) {

    @PostConstruct
    fun init() {
        // ── Étape 0 : Supprimer ferme_default si colonnes avec ancien nommage ─
        dropSchemaIfLegacyNaming("ferme_default")

        // ── Étape 1 : Créer le schéma + toutes les tables ────────────────────
        // Crée aussi public.plan_config et public.super_admins (car @Table(schema="public"))
        schemaCreationService.initializeSchema("ferme_default")

        // ── Étape 2 : Données schéma PUBLIC (sans TenantContext) ─────────────
        initPublicData()

        // ── Étape 3 : Tenant par défaut dans public.tenants ──────────────────
        initDefaultTenant()

        // ── Étapes 4-5 : Données dans ferme_default ──────────────────────────
        TenantContext.setTenant("ferme_default")
        try {
            initRolesEtUtilisateurs()
            initTypesTaches()
            initSubscription()
        } finally {
            TenantContext.clear()
        }
    }

    /**
     * Initialise les données dans le schéma PUBLIC :
     *   - PlanConfig (limites des plans — commune à toutes les fermes)
     *   - SuperAdmin (compte propriétaire de la plateforme)
     *
     * Pas de TenantContext nécessaire car @Table(schema="public") est explicite.
     */
    private fun initPublicData() {
        if (!planConfigRepository.existsById(1L)) {
            planConfigRepository.save(PlanConfig(maxAnimauxFreePlan = 5, maxAnimauxPremiumPlan = -1))
            println("[DataInitializer] PlanConfig initialisée dans public.plan_config (FREE=5, PREMIUM=illimité).")
        }

        if (superAdminRepository.findByEmail("superadmin@ferme.bf") == null) {
            superAdminRepository.save(
                SuperAdmin(
                    email     = "superadmin@ferme.bf",
                    password  = passwordEncoder.encode("SuperAdmin@2026!"),
                    nom       = "Admin",
                    prenom    = "Super",
                    createdAt = LocalDateTime.now()
                )
            )
            println("[DataInitializer] SuperAdmin créé : superadmin@ferme.bf / SuperAdmin@2026!")
        }
    }

    private fun initDefaultTenant() {
        if (!tenantRepository.existsByFermeCode("ferme_default")) {
            tenantRepository.save(
                Tenant(
                    fermeCode  = "ferme_default",
                    nomFerme   = "Ferme par défaut",
                    schemaName = "ferme_default"
                )
            )
            println("[DataInitializer] Tenant 'ferme_default' créé dans public.tenants.")
        }
    }

    private fun initRolesEtUtilisateurs() {
        val roleNames = listOf("ROLE_ADMINISTRATEUR", "ROLE_GERANT", "ROLE_RESPONSABLE", "ROLE_OUVRIER")
        val rolesMap = roleNames.associate { name ->
            val role = roleRepository.findByNom(name) ?: roleRepository.save(Role(nom = name))
            name to role
        }

        listOf(
            Utilisateur(poste="Administrateur système", nom="Kabore",    prenom="Admin",
                email="admin@ferme.bf",        telephone="0000000000",
                password=passwordEncoder.encode("admin12345"),    role=rolesMap["ROLE_ADMINISTRATEUR"]),
            Utilisateur(poste="Gérant de la ferme",    nom="Ouedraogo", prenom="Gérant",
                email="gerant@ferme.bf",       telephone="1111111111",
                password=passwordEncoder.encode("gerant12345"),   role=rolesMap["ROLE_GERANT"]),
            Utilisateur(poste="Responsable de zone",   nom="Traore",    prenom="Responsable",
                email="responsable@ferme.bf",  telephone="2222222222",
                password=passwordEncoder.encode("resp12345"),     role=rolesMap["ROLE_RESPONSABLE"]),
            Utilisateur(poste="Ouvrier d'élevage",     nom="Diallo",    prenom="Ouvrier",
                email="ouvrier@ferme.bf",      telephone="3333333333",
                password=passwordEncoder.encode("ouvrier12345"),  role=rolesMap["ROLE_OUVRIER"])
        ).forEach { user ->
            if (utilisateurRepository.findByEmail(user.email) == null)
                utilisateurRepository.save(user)
        }
        println("[DataInitializer] Rôles et utilisateurs initialisés dans ferme_default.")
    }

    private fun initTypesTaches() {
        val types = listOf(
            TypeTache(nom="Alimentation",       description="Distribution des rations alimentaires",                  couleur="#16a34a", icone="pi pi-shopping-cart"),
            TypeTache(nom="Soins vétérinaires", description="Vaccination, traitements, contrôle santé",               couleur="#dc2626", icone="pi pi-heart"),
            TypeTache(nom="Nettoyage",          description="Désinfection des boxes, couloirs et équipements",        couleur="#2563eb", icone="pi pi-refresh"),
            TypeTache(nom="Pesée",              description="Pesée des animaux pour suivi de croissance",             couleur="#d97706", icone="pi pi-chart-bar"),
            TypeTache(nom="Reproduction",       description="Contrôle des chaleurs, saillies, surveillance mise-bas", couleur="#7c3aed", icone="pi pi-heart-fill"),
            TypeTache(nom="Déplacement",        description="Transfert d'animaux entre boxes ou bâtiments",           couleur="#0891b2", icone="pi pi-arrow-right-arrow-left"),
            TypeTache(nom="Maintenance",        description="Réparations, vérification des équipements",              couleur="#78716c", icone="pi pi-wrench"),
            TypeTache(nom="Approvisionnement",  description="Réception et inventaire des stocks",                     couleur="#ca8a04", icone="pi pi-box"),
        )
        types.forEach { type ->
            if (typeTacheRepository.findByNom(type.nom) == null)
                typeTacheRepository.save(type)
        }
        println("[DataInitializer] Types de tâches initialisés dans ferme_default.")
    }

    /**
     * Supprime le schéma s'il a été créé avec l'ancien nommage (sans underscore).
     * Détection : colonne "datedebut" dans subscription → ancien nommage.
     * Sans underscore = SchemaCreationService sans CamelCaseToUnderscoresNamingStrategy.
     * Idempotent : sans effet si schéma absent ou déjà correct.
     */
    private fun dropSchemaIfLegacyNaming(schemaName: String) {
        try {
            dataSource.connection.use { conn ->
                val rs = conn.metaData.getColumns(null, schemaName, "subscription", "datedebut")
                val isLegacy = rs.next()
                rs.close()
                if (isLegacy) {
                    conn.createStatement().execute("""DROP SCHEMA IF EXISTS "$schemaName" CASCADE""")
                    println("[DataInitializer] ✓ Schéma '$schemaName' (nommage legacy) supprimé.")
                }
            }
        } catch (e: Exception) {
            println("[DataInitializer] ⚠ Vérification nommage échouée : ${e.message}")
        }
    }

    private fun initSubscription() {
        if (!subscriptionRepository.existsById(1L)) {
            subscriptionRepository.save(Subscription())
            println("[DataInitializer] Subscription FREE initialisée dans ferme_default.")
        }
        // PlanConfig est dans public.plan_config — initialisée dans initPublicData()
    }
}
