package com.oki.gestion_parc_backend.service.impl

import com.oki.gestion_parc_backend.dto.RegisterFermeDTO
import com.oki.gestion_parc_backend.model.*
import com.oki.gestion_parc_backend.repository.*
import com.oki.gestion_parc_backend.security.TenantContext
import com.oki.gestion_parc_backend.service.SchemaCreationService
import com.oki.gestion_parc_backend.service.TenantService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

/**
 * Implémentation du service de gestion des tenants.
 *
 * registerFerme() — séquence :
 *   1. Validation du fermeCode (format + unicité)
 *   2. Création du schéma PostgreSQL + tables JPA (SchemaCreationService)
 *   3. Enregistrement du tenant dans public.tenants
 *   4. Initialisation des données du tenant dans son schéma :
 *      - Rôles (ROLE_ADMINISTRATEUR, GERANT, RESPONSABLE, OUVRIER)
 *      - Utilisateur administrateur
 *      - Types de tâches par défaut
 *      - Abonnement FREE + PlanConfig (limite 5 animaux)
 *
 * Note : chaque appel à TenantContext.setTenant() est suivi d'un finally { clear() }
 * pour éviter toute fuite entre threads.
 */
@Service
class TenantServiceImpl(
    private val tenantRepository: TenantRepository,
    private val schemaCreationService: SchemaCreationService,
    private val roleRepository: RoleRepository,
    private val utilisateurRepository: UtilisateurRepository,
    private val typeTacheRepository: TypeTacheRepository,
    private val subscriptionRepository: SubscriptionRepository,
    private val planConfigRepository: PlanConfigRepository,
    private val passwordEncoder: PasswordEncoder
) : TenantService {

    override fun registerFerme(dto: RegisterFermeDTO): Tenant {
        // ── Validation ────────────────────────────────────────────────────────
        val codeRegex = Regex("^[a-zA-Z0-9_]{3,50}$")
        if (!dto.fermeCode.matches(codeRegex)) {
            throw IllegalArgumentException(
                "fermeCode invalide. Utilisez uniquement lettres, chiffres et underscores (3-50 chars)."
            )
        }
        if (tenantRepository.existsByFermeCode(dto.fermeCode)) {
            throw IllegalArgumentException("Le code ferme '${dto.fermeCode}' est déjà utilisé.")
        }
        if (dto.adminEmail.isBlank() || dto.adminPassword.length < 6) {
            throw IllegalArgumentException("Email admin invalide ou mot de passe trop court (min 6 chars).")
        }

        val schemaName = dto.fermeCode.lowercase()

        // ── Étape 1 : Schéma + tables ─────────────────────────────────────────
        schemaCreationService.initializeSchema(schemaName)

        // ── Étape 2 : Enregistrement global (public.tenants) ──────────────────
        // TenantContext est null ici → resolves to "public" → INSERT INTO public.tenants
        val tenant = tenantRepository.save(
            Tenant(
                fermeCode  = dto.fermeCode,
                nomFerme   = dto.nomFerme,
                schemaName = schemaName
            )
        )
        println("[TenantService] Tenant '${dto.fermeCode}' enregistré dans public.tenants.")

        // ── Étape 3 : Données initiales dans le schéma du tenant ─────────────
        TenantContext.setTenant(schemaName)
        try {
            val rolesMap = initRoles()
            initAdminUser(dto, rolesMap)
            initTypesTaches()
            initSubscription()
        } finally {
            TenantContext.clear()
        }

        return tenant
    }

    override fun findAll(): List<Tenant> = tenantRepository.findAll()

    // ── Helpers privés ────────────────────────────────────────────────────────

    private fun initRoles(): Map<String, Role> {
        val roleNames = listOf("ROLE_ADMINISTRATEUR", "ROLE_GERANT", "ROLE_RESPONSABLE", "ROLE_OUVRIER")
        return roleNames.associate { name ->
            val role = roleRepository.findByNom(name) ?: roleRepository.save(Role(nom = name))
            name to role
        }
    }

    private fun initAdminUser(dto: RegisterFermeDTO, rolesMap: Map<String, Role>) {
        if (utilisateurRepository.findByEmail(dto.adminEmail) == null) {
            utilisateurRepository.save(
                Utilisateur(
                    nom       = dto.adminNom,
                    prenom    = dto.adminPrenom,
                    email     = dto.adminEmail,
                    password  = passwordEncoder.encode(dto.adminPassword),
                    telephone = "",
                    poste     = "Administrateur",
                    role      = rolesMap["ROLE_ADMINISTRATEUR"]
                )
            )
        }
    }

    private fun initTypesTaches() {
        val types = listOf(
            TypeTache(nom="Alimentation",       description="Distribution des rations alimentaires",          couleur="#16a34a", icone="pi pi-shopping-cart"),
            TypeTache(nom="Soins vétérinaires", description="Vaccination, traitements, contrôle santé",      couleur="#dc2626", icone="pi pi-heart"),
            TypeTache(nom="Nettoyage",          description="Désinfection des boxes et équipements",         couleur="#2563eb", icone="pi pi-refresh"),
            TypeTache(nom="Pesée",              description="Pesée des animaux pour suivi de croissance",    couleur="#d97706", icone="pi pi-chart-bar"),
            TypeTache(nom="Reproduction",       description="Contrôle des chaleurs, saillies, surveillance", couleur="#7c3aed", icone="pi pi-heart-fill"),
            TypeTache(nom="Maintenance",        description="Réparations, vérification des équipements",     couleur="#78716c", icone="pi pi-wrench"),
        )
        types.forEach { type ->
            if (typeTacheRepository.findByNom(type.nom) == null)
                typeTacheRepository.save(type)
        }
    }

    private fun initSubscription() {
        if (!subscriptionRepository.existsById(1L))
            subscriptionRepository.save(Subscription())
        if (!planConfigRepository.existsById(1L))
            planConfigRepository.save(PlanConfig(maxAnimauxFreePlan = 5))
    }
}
