package com.oki.gestion_parc_backend.dto

/**
 * DTO — Résultat du calcul du coût d'une ration à partir de ses ingrédients.
 *
 * Retourné par AlimentationService.calculerCoutRation().
 *
 * Règle économique appliquée :
 *   Coût max d'1 kg d'aliment = 1/6 du prix de vente du porc sur pied
 *   À Bobo-Dioulasso : prix vente = 600 FCFA/kg → seuil = 100 FCFA/kg
 *
 * SOURCE: Fiche technique n°3 alimentation porcs — ONG Thamani, Bobo-Dioulasso — secteur 24
 * Disponible dans les documents joints du projet
 */
data class CoutRationDto(
    /** Coût total de la ration en FCFA (somme des sous-totaux de chaque ingrédient) */
    val coutTotalFcfa: Double,

    /** Quantité totale en kg de la ration */
    val quantiteTotaleKg: Double,

    /** Coût par kg d'aliment en FCFA */
    val coutParKgFcfa: Double,

    /**
     * true si le coût/kg est conforme à la règle économique Thamani (≤ 100 FCFA/kg)
     * SOURCE: ONG Thamani — "Prix max d'1 kg d'aliment = 1/6 du prix vente (600 FCFA/kg) = 100 FCFA/kg"
     */
    val conformeRegleEconomique: Boolean,

    /** Seuil de coût max utilisé (100 FCFA/kg par défaut — Bobo-Dioulasso) */
    val seuilCoutMaxFcfa: Double = 100.0,

    /** Message d'alerte si le coût dépasse le seuil */
    val alerte: String? = null,

    /** Détail par ingrédient */
    val detailIngredients: List<DetailIngredientCout>
)

/**
 * Détail du coût pour un ingrédient de la ration.
 */
data class DetailIngredientCout(
    val ingredientId: Long,
    val ingredientNom: String,
    val quantiteKg: Double,
    val prixUnitaireFcfa: Double,
    val sousTotalFcfa: Double
)
