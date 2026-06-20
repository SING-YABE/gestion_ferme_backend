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
        // ── Étape 0a : Supprimer ferme_default si colonnes avec ancien nommage ─
        dropSchemaIfLegacyNaming("ferme_default")

        // ── Étape 0b : Supprimer public.plan_config si schéma incomplet ──────
        // Doit s'exécuter AVANT initializeSchema() pour que l'EMF recrée la table proprement.
        dropPlanConfigIfIncomplete()

        // ── Étape 1 : Créer le schéma + toutes les tables ────────────────────
        // Crée aussi public.plan_config et public.super_admins (car @Table(schema="public"))
        schemaCreationService.initializeSchema("ferme_default")

        // ── Migrations schémas tenant ─────────────────────────────────────────
        migrateAddMustChangePassword()
        migrateSubscriptionAddAutoRenew()
        migrateCreatePaymentTables()

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
        if (planConfigRepository.count() == 0L) {
            val now = LocalDateTime.now()
            planConfigRepository.saveAll(listOf(
                PlanConfig(
                    nom = "STARTER", description = "Démarrage gratuit idéal pour découvrir la plateforme.",
                    prixFcfa = 0, dureeDays = 14, trialDays = 14,
                    maxAnimaux = 10, maxUtilisateurs = 2, maxBatiments = 1,
                    hasAssistantIA = false, hasAlertesSms = false,
                    hasSyntheseComplete = false, hasExportPdf = false, hasPrevisionPrix = false,
                    actif = true, ordre = 0, createdAt = now, updatedAt = now
                ),
                PlanConfig(
                    nom = "ELEVEUR PRO", description = "Pour les éleveurs en croissance  assistant IA inclus.",
                    prixFcfa = 5000, dureeDays = 30, trialDays = 0,
                    maxAnimaux = 50, maxUtilisateurs = 5, maxBatiments = 5,
                    hasAssistantIA = true, hasAlertesSms = false,
                    hasSyntheseComplete = true, hasExportPdf = false, hasPrevisionPrix = false,
                    actif = true, ordre = 1, createdAt = now, updatedAt = now
                ),
                PlanConfig(
                    nom = "FERME PREMIUM", description = "Gestion complète avec alertes SMS et exports PDF.",
                    prixFcfa = 15000, dureeDays = 90, trialDays = 0,
                    maxAnimaux = 200, maxUtilisateurs = 15, maxBatiments = 20,
                    hasAssistantIA = true, hasAlertesSms = true,
                    hasSyntheseComplete = true, hasExportPdf = true, hasPrevisionPrix = false,
                    actif = true, ordre = 2, createdAt = now, updatedAt = now
                ),
                PlanConfig(
                    nom = "ENTREPRISE", description = "Illimité pour les grandes exploitations porcines.",
                    prixFcfa = 45000, dureeDays = 365, trialDays = 0,
                    maxAnimaux = -1, maxUtilisateurs = -1, maxBatiments = -1,
                    hasAssistantIA = true, hasAlertesSms = true,
                    hasSyntheseComplete = true, hasExportPdf = true, hasPrevisionPrix = true,
                    actif = true, ordre = 3, createdAt = now, updatedAt = now
                )
            ))
            println("[DataInitializer] 4 plans PlanConfig créés dans public.plan_config.")
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

    /**
     * Détecte si public.plan_config a un schéma incomplet (colonnes manquantes dues à
     * un ancien déploiement). Si c'est le cas, supprime la table pour qu'elle soit
     * recréée proprement par schemaCreationService.initializeSchema().
     *
     * Détection : vérification de la colonne "duree_days" (absente dans l'ancien schéma).
     * Idempotent : sans effet si la table n'existe pas ou si elle est déjà correcte.
     *
     * ⚠ Doit être appelé AVANT initializeSchema() pour que l'EMF recrée la table.
     */
    private fun dropPlanConfigIfIncomplete() {
        try {
            dataSource.connection.use { conn ->
                // Vérifier si la table existe
                val tableRs = conn.metaData.getTables(null, "public", "plan_config", arrayOf("TABLE"))
                val tableExists = tableRs.next()
                tableRs.close()

                if (!tableExists) {
                    println("[DataInitializer] public.plan_config absente — sera créée par initializeSchema.")
                    return
                }

                // Vérifier si duree_days existe (colonne absente = schéma incomplet)
                val colRs = conn.metaData.getColumns(null, "public", "plan_config", "duree_days")
                val isComplete = colRs.next()
                colRs.close()

                if (!isComplete) {
                    conn.createStatement().execute("DROP TABLE IF EXISTS public.plan_config CASCADE")
                    println("[DataInitializer] ✓ public.plan_config (schéma incomplet) supprimée — sera recréée.")
                } else {
                    println("[DataInitializer] ✓ public.plan_config schéma OK — aucune action.")
                }
            }
        } catch (e: Exception) {
            println("[DataInitializer] ⚠ dropPlanConfigIfIncomplete échoué : ${e.message}")
        }
    }

    /**
     * Migration idempotente : ajoute la colonne must_change_password à la table
     * utilisateurs de TOUS les schémas tenant existants.
     * Utilise ADD COLUMN IF NOT EXISTS → sans effet si la colonne existe déjà.
     */
    private fun migrateAddMustChangePassword() {
        try {
            dataSource.connection.use { conn ->
                // Récupérer tous les schémas tenant (hors public, information_schema, pg_*)
                val schemas = mutableListOf<String>()
                val rs = conn.metaData.getSchemas()
                while (rs.next()) {
                    val schema = rs.getString("TABLE_SCHEM")
                    if (!schema.startsWith("pg_") && schema != "information_schema" && schema != "public") {
                        schemas.add(schema)
                    }
                }
                rs.close()

                schemas.forEach { schema ->
                    conn.createStatement().execute(
                        """ALTER TABLE IF EXISTS "$schema".utilisateurs
                           ADD COLUMN IF NOT EXISTS must_change_password BOOLEAN NOT NULL DEFAULT FALSE"""
                    )
                    println("[DataInitializer] ✓ Migration must_change_password → schéma '$schema'")
                }
            }
        } catch (e: Exception) {
            println("[DataInitializer] ⚠ Migration must_change_password échouée : ${e.message}")
        }
    }

    /**
     * Migration idempotente : ajoute la colonne auto_renew à la table
     * subscription de TOUS les schémas tenant existants.
     * Ajoutée dans Phase 1A mais absente des schémas créés avant ce déploiement.
     */
    private fun migrateSubscriptionAddAutoRenew() {
        try {
            dataSource.connection.use { conn ->
                val schemas = mutableListOf<String>()
                val rs = conn.metaData.getSchemas()
                while (rs.next()) {
                    val schema = rs.getString("TABLE_SCHEM")
                    if (!schema.startsWith("pg_") && schema != "information_schema" && schema != "public") {
                        schemas.add(schema)
                    }
                }
                rs.close()

                schemas.forEach { schema ->
                    conn.createStatement().execute(
                        """ALTER TABLE IF EXISTS "$schema".subscription
                           ADD COLUMN IF NOT EXISTS auto_renew BOOLEAN NOT NULL DEFAULT FALSE"""
                    )
                    println("[DataInitializer] ✓ Migration auto_renew → schéma '$schema'")
                }
            }
        } catch (e: Exception) {
            println("[DataInitializer] ⚠ Migration auto_renew échouée : ${e.message}")
        }
    }

    /**
     * Migration idempotente : crée les tables payment_transactions et sms_logs
     * dans TOUS les schémas tenant existants.
     * Ces tables sont créées à l'inscription des nouvelles fermes par initializeSchema(),
     * mais absentes des schémas créés avant Phase 1B.
     */
    private fun migrateCreatePaymentTables() {
        try {
            dataSource.connection.use { conn ->
                val schemas = mutableListOf<String>()
                val rs = conn.metaData.getSchemas()
                while (rs.next()) {
                    val schema = rs.getString("TABLE_SCHEM")
                    if (!schema.startsWith("pg_") && schema != "information_schema" && schema != "public") {
                        schemas.add(schema)
                    }
                }
                rs.close()

                schemas.forEach { schema ->
                    conn.createStatement().execute("""
                        CREATE TABLE IF NOT EXISTS "$schema".payment_transactions (
                            id              BIGSERIAL PRIMARY KEY,
                            plan_config_id  BIGINT       NOT NULL,
                            plan_nom        VARCHAR(255) NOT NULL,
                            phone_number    VARCHAR(255) NOT NULL,
                            montant_attendu INTEGER      NOT NULL,
                            statut          VARCHAR(50)  NOT NULL,
                            response_code   VARCHAR(255),
                            response_message VARCHAR(500),
                            created_at      TIMESTAMP    NOT NULL
                        )
                    """.trimIndent())

                    conn.createStatement().execute("""
                        CREATE TABLE IF NOT EXISTS "$schema".sms_logs (
                            id           BIGSERIAL PRIMARY KEY,
                            phone_number VARCHAR(255) NOT NULL,
                            message      VARCHAR(500) NOT NULL,
                            event        VARCHAR(50)  NOT NULL,
                            statut       VARCHAR(50)  NOT NULL,
                            error_message VARCHAR(500),
                            created_at   TIMESTAMP    NOT NULL
                        )
                    """.trimIndent())

                    println("[DataInitializer] ✓ Migration payment_transactions + sms_logs → schéma '$schema'")
                }
            }
        } catch (e: Exception) {
            println("[DataInitializer] ⚠ Migration payment tables échouée : ${e.message}")
        }
    }

    /**
     * Initialise la subscription de la ferme en statut TRIAL.
     * Le plan STARTER (trialDays=14) est utilisé par défaut.
     * Idempotent : sans effet si une subscription existe déjà.
     */
    private fun initSubscription() {
        if (!subscriptionRepository.existsById(1L)) {
            // Récupérer le plan STARTER (premier plan avec trialDays > 0)
            val planStarter = planConfigRepository.findByNom("STARTER")
            val sub = Subscription(
                planConfigId = planStarter?.id,
                planNom      = planStarter?.nom ?: "STARTER",
                statut       = com.oki.gestion_parc_backend.model.SubscriptionStatus.TRIAL,
                trialEndsAt  = java.time.LocalDate.now().plusDays(
                    planStarter?.trialDays?.toLong() ?: 14L
                )
            )
            subscriptionRepository.save(sub)
            println("[DataInitializer] Subscription TRIAL initialisée dans ferme_default (${sub.trialEndsAt}).")
        }
    }
}
