package com.oki.gestion_parc_backend.security

/**
 * Registre statique associant chaque rôle à son ensemble de permissions.
 *
 * Hiérarchie :
 *   OUVRIER       → saisies terrain + lecture + animaux + charges + reproduction
 *   RESPONSABLE   → tout OUVRIER + supervision + finance + admin utilisateurs
 *   GERANT        → tout RESPONSABLE + configuration ferme
 *   ADMINISTRATEUR → toutes les permissions
 */
object RolePermissions {

    // ─── OUVRIER ─────────────────────────────────────────────────────────────
    private val OUVRIER_PERMISSIONS = setOf(
        // Référentiels - lecture
        Permission.TYPE_ANIMAL_READ,
        Permission.TYPE_ALIMENT_READ,
        Permission.INGREDIENT_READ,
        Permission.TRAITEMENT_READ,

        // Infrastructure - lecture
        Permission.BATIMENT_READ,
        Permission.BOX_READ,

        // Animaux - lecture + saisie + création/modification
        Permission.ANIMAL_READ,
        Permission.ANIMAL_STATS,
        Permission.ANIMAL_WRITE,
        Permission.ANIMAL_PHOTO,
        Permission.PESEE_READ,
        Permission.PESEE_WRITE,
        Permission.DEPLACEMENT_READ,

        // Reproduction - lecture + alertes
        Permission.REPRODUCTION_READ,
        Permission.REPRODUCTION_ALERTES,

        // Santé - saisie quotidienne
        Permission.ETAT_SANTE_READ,
        Permission.ETAT_SANTE_WRITE,
        Permission.SOIN_READ,
        Permission.SOIN_WRITE,

        // Alimentation - saisie terrain
        Permission.ALIMENTATION_READ,
        Permission.ALIMENTATION_WRITE,
        Permission.ALIMENTATION_RATION,

        // Charges - enregistrement
        Permission.CHARGE_READ,
        Permission.CHARGE_WRITE,

        // Tâches - ouvrier
        Permission.TACHE_READ_OWN,
        Permission.TACHE_COMPLETE,
    )

    // ─── RESPONSABLE ─────────────────────────────────────────────────────────
    // Tout OUVRIER + supervision + finance + gestion utilisateurs
    private val RESPONSABLE_PERMISSIONS = OUVRIER_PERMISSIONS + setOf(
        // Référentiels - écriture
        Permission.TRAITEMENT_WRITE,
        Permission.FOURNISSEUR_READ,
        Permission.FOURNISSEUR_WRITE,
        Permission.TYPE_DEPENSE_READ,
        Permission.TYPE_DEPENSE_WRITE,
        Permission.TYPE_VENTE_READ,
        Permission.TYPE_VENTE_WRITE,
        Permission.TYPE_ANIMAL_WRITE,
        Permission.TYPE_ALIMENT_WRITE,
        Permission.INGREDIENT_WRITE,

        // Infrastructure
        Permission.BOX_WRITE,
        Permission.BATIMENT_WRITE,

        // Animaux - gestion complète
        Permission.ANIMAL_DELETE,
        Permission.DEPLACEMENT_WRITE,

        // Reproduction - gestion complète
        Permission.REPRODUCTION_WRITE,
        Permission.REPRODUCTION_DELETE,
        Permission.REPRODUCTION_STATS,
        Permission.REPRODUCTION_ISSF,

        // Santé - supervision
        Permission.ETAT_SANTE_DELETE,
        Permission.SOIN_STATS,
        Permission.SANTE_BILAN,

        // Finance complète
        Permission.ALIMENTATION_DELETE,
        Permission.ALIMENTATION_STATS,
        Permission.ALIMENTATION_COUT,
        Permission.DEPENSE_READ,
        Permission.DEPENSE_WRITE,
        Permission.DEPENSE_DELETE,
        Permission.CHARGE_WRITE,
        Permission.CHARGE_STATS,
        Permission.CHARGE_READ,
        Permission.VENTE_READ,
        Permission.VENTE_WRITE,
        Permission.VENTE_DELETE,
        Permission.FACTURE_READ,
        Permission.SYNTHESE_READ,

        // Paramètres
        Permission.SETTINGS_READ,
        Permission.SETTINGS_WRITE,
        Permission.SETTINGS_LOGO,
        Permission.PARAMETRES_ELEVEUR_READ,
        Permission.PARAMETRES_ELEVEUR_WRITE,

        // Tâches - superviseur
        Permission.TACHE_READ_ALL,
        Permission.TACHE_CREATE,
        Permission.TACHE_VALIDATE,
        Permission.TYPE_TACHE_MANAGE,

        // Administration utilisateurs (comme l'administrateur)
        Permission.UTILISATEUR_READ,
        Permission.UTILISATEUR_WRITE,
        Permission.ROLE_MANAGE,
        Permission.PERMISSION_MANAGE,
    )

    // ─── GERANT ──────────────────────────────────────────────────────────────
    // Tout RESPONSABLE + configuration complète
    private val GERANT_PERMISSIONS = RESPONSABLE_PERMISSIONS + setOf(
        Permission.FOURNISSEUR_DELETE,
    )

    // ─── ADMINISTRATEUR ───────────────────────────────────────────────────────
    private val ADMINISTRATEUR_PERMISSIONS = Permission.values().toSet()

    // ─── Table de lookup ──────────────────────────────────────────────────────
    private val ROLE_MAP: Map<String, Set<Permission>> = mapOf(
        "ROLE_OUVRIER"         to OUVRIER_PERMISSIONS,
        "ROLE_RESPONSABLE"     to RESPONSABLE_PERMISSIONS,
        "ROLE_GERANT"          to GERANT_PERMISSIONS,
        "ROLE_ADMINISTRATEUR"  to ADMINISTRATEUR_PERMISSIONS,
    )

    fun permissionsFor(roleName: String): Set<Permission> =
        ROLE_MAP[roleName] ?: emptySet()
}
