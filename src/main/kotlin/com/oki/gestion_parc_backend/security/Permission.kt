package com.oki.gestion_parc_backend.security

/**
 * Toutes les permissions du système de gestion de ferme.
 * Chaque permission correspond à une action précise sur une ressource.
 * Utilisé avec @PreAuthorize("hasAuthority('NOM_PERMISSION')")
 */
enum class Permission {

    // ─── Référentiels : Types ───────────────────────────────────────────────
    TYPE_ANIMAL_READ,
    TYPE_ANIMAL_WRITE,

    TYPE_ALIMENT_READ,
    TYPE_ALIMENT_WRITE,

    TYPE_DEPENSE_READ,
    TYPE_DEPENSE_WRITE,

    TYPE_VENTE_READ,
    TYPE_VENTE_WRITE,

    // ─── Référentiels : Ingrédients & Traitements ──────────────────────────
    INGREDIENT_READ,
    INGREDIENT_WRITE,

    TRAITEMENT_READ,
    TRAITEMENT_WRITE,

    // ─── Référentiels : Fournisseurs ───────────────────────────────────────
    FOURNISSEUR_READ,
    FOURNISSEUR_WRITE,   // create + update
    FOURNISSEUR_DELETE,

    // ─── Paramètres & Settings ─────────────────────────────────────────────
    SETTINGS_READ,
    SETTINGS_WRITE,
    SETTINGS_LOGO,       // upload logo uniquement

    PARAMETRES_ELEVEUR_READ,
    PARAMETRES_ELEVEUR_WRITE,

    // ─── Infrastructure : Bâtiments & Boxes ───────────────────────────────
    BATIMENT_READ,
    BATIMENT_WRITE,      // create + update + delete

    BOX_READ,
    BOX_WRITE,           // create + update + delete

    // ─── Animaux ───────────────────────────────────────────────────────────
    ANIMAL_READ,         // list, getById, count
    ANIMAL_STATS,        // count-by-type (agrégats)
    ANIMAL_WRITE,        // create + update
    ANIMAL_DELETE,
    ANIMAL_PHOTO,

    DEPLACEMENT_READ,
    DEPLACEMENT_WRITE,

    PESEE_READ,
    PESEE_WRITE,         // create + delete

    REPRODUCTION_READ,
    REPRODUCTION_WRITE,  // create + update
    REPRODUCTION_DELETE,
    REPRODUCTION_STATS,
    REPRODUCTION_ALERTES,// accessible même à l'ouvrier
    REPRODUCTION_ISSF,   // indicateur synthétique

    // ─── Santé ─────────────────────────────────────────────────────────────
    ETAT_SANTE_READ,
    ETAT_SANTE_WRITE,    // create + update
    ETAT_SANTE_DELETE,

    SOIN_READ,
    SOIN_WRITE,          // create + update
    SOIN_STATS,          // top-consommateurs

    SANTE_BILAN,         // bilan-sante par animal

    // ─── Finance : Alimentation ────────────────────────────────────────────
    ALIMENTATION_READ,
    ALIMENTATION_WRITE,
    ALIMENTATION_DELETE,
    ALIMENTATION_STATS,  // statistiques coûts mensuels
    ALIMENTATION_RATION, // ration de référence (lecture)
    ALIMENTATION_COUT,   // calculer coût ration

    // ─── Finance : Dépenses & Charges ─────────────────────────────────────
    DEPENSE_READ,
    DEPENSE_WRITE,
    DEPENSE_DELETE,

    CHARGE_READ,
    CHARGE_WRITE,        // create + update + delete
    CHARGE_STATS,        // pourcentage-par-type

    // ─── Finance : Ventes ─────────────────────────────────────────────────
    VENTE_READ,
    VENTE_WRITE,
    VENTE_DELETE,
    FACTURE_READ,        // génération facture PDF

    SYNTHESE_READ,       // synthèse financière globale

    // ─── Administration ────────────────────────────────────────────────────
    UTILISATEUR_READ,
    UTILISATEUR_WRITE,
    ROLE_MANAGE,
    PERMISSION_MANAGE,   // gérer les overrides de permissions

    // ─── Tâches ────────────────────────────────────────────────────────────────
    TACHE_READ_OWN,      // voir ses propres tâches (ouvrier)
    TACHE_READ_ALL,      // voir toutes les tâches (gérant/admin)
    TACHE_CREATE,        // créer et assigner des tâches
    TACHE_COMPLETE,      // démarrer, soumettre + upload preuves (ouvrier)
    TACHE_VALIDATE,      // valider / invalider (gérant/admin)
    TYPE_TACHE_MANAGE,   // CRUD des types de tâches (admin/gérant)

    // ─── Abonnement SaaS ───────────────────────────────────────────────────────
    SUBSCRIPTION_MANAGE, // voir et modifier plan + limites (administrateur uniquement)
}
