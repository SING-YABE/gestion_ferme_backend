package com.oki.gestion_parc_backend.reference

/**
 * Données de référence pour le module Alimentation — Élevage porcin au Burkina Faso.
 *
 * Ce fichier regroupe toutes les constantes issues des documents officiels et des fournisseurs
 * locaux. Chaque bloc est annoté avec sa source pour assurer la traçabilité scientifique.
 *
 * Règle économique clé (Source Thamani) :
 *   Coût max d'1 kg d'aliment = 1/6 du prix de vente sur pied
 *   À Bobo-Dioulasso : prix vente = 600 FCFA/kg → coût cible ≤ 100 FCFA/kg
 */
object AlimentationReferenceData {

    // =========================================================================
    // SECTION 1 — STADES PHYSIOLOGIQUES
    // =========================================================================

    enum class StadePhysiologique {
        PORCELET_SEVRAGE,   // 7–25 kg
        CROISSANCE,         // 25–60 kg
        FINITION,           // > 60 kg
        TRUIE_GESTANTE,
        TRUIE_ALLAITANTE,
        TRUIE_VIDE,
        VERRAT
    }

    // =========================================================================
    // SECTION 2 — QUANTITÉS JOURNALIÈRES PAR STADE
    // SOURCE: Fiche technique n°3 alimentation porcs — ONG Thamani, Bobo-Dioulasso — secteur 24
    // SOURCE: Fiche simplifiée alimentation des porcs — DGPA/MRAH, Burkina Faso — Juin 2021
    //         (Projet PADEL-B, Banque Mondiale, Crédit IDA P159476)
    // Disponible dans les documents joints du projet
    // =========================================================================

    /**
     * Quantité journalière recommandée en kg de Matière Sèche (MS) par stade.
     * Pour PORCELET_SEVRAGE et CROISSANCE, la valeur est typique (milieu de fourchette).
     */
    data class QuantiteJournaliere(
        val stade: StadePhysiologique,
        val quantiteMinKg: Double,
        val quantiteMaxKg: Double,
        val eauLitres: Double,
        val noteSource: String
    )

    val QUANTITES_JOURNALIERES: List<QuantiteJournaliere> = listOf(
        // SOURCE: ONG Thamani — tableau "Quantité d'aliments pour les porcs"
        QuantiteJournaliere(StadePhysiologique.PORCELET_SEVRAGE,  0.5,  0.7,  2.0,  "Thamani: porcelet 10-20 kg → 0,5 à 0,7 kg MS/j"),
        // SOURCE: DGPA/MRAH Juin 2021 — "le porc en croissance consomme 1 à 2,5 kg/j"
        QuantiteJournaliere(StadePhysiologique.CROISSANCE,        1.0,  2.5,  4.0,  "DGPA/MRAH 2021 + Thamani: 20-60 kg → 1 à 2,5 kg MS/j"),
        // SOURCE: DGPA/MRAH Juin 2021 — "le porc à l'engraissement consomme 2,5 à 3 kg/j"
        // SOURCE: ONG Thamani — porc 60-80 kg → 2,5 à 3 kg MS/j
        QuantiteJournaliere(StadePhysiologique.FINITION,          2.5,  3.0,  6.0,  "DGPA/MRAH 2021 + Thamani: > 60 kg → 2,5 à 3 kg MS/j"),
        // SOURCE: DGPA/MRAH Juin 2021 — "la truie gestante consomme entre 2,5 à 3,5 kg/j"
        QuantiteJournaliere(StadePhysiologique.TRUIE_GESTANTE,    2.5,  3.5,  10.0, "DGPA/MRAH 2021: truie gestante → 2,5 à 3,5 kg/j"),
        // SOURCE: DGPA/MRAH Juin 2021 — "la truie allaitante (10 porcelets) 4,5 à 5 kg/j"
        // SOURCE: ONG Thamani — "5 kg (1 kg + 1/2 kg par porcelet)"
        QuantiteJournaliere(StadePhysiologique.TRUIE_ALLAITANTE,  4.5,  5.0,  25.0, "DGPA/MRAH 2021 + Thamani: truie allaitante → 4,5 à 5 kg/j"),
        // SOURCE: DGPA/MRAH Juin 2021 — "la truie vide consomme 2 à 2,7 kg/j"
        QuantiteJournaliere(StadePhysiologique.TRUIE_VIDE,        2.0,  2.7,  6.0,  "DGPA/MRAH 2021: truie vide → 2 à 2,7 kg/j"),
        // SOURCE: DGPA/MRAH Juin 2021 — "le verrat consomme 2 à 2,7 kg/j"
        // SOURCE: ONG Thamani — verrat → 2 kg MS/j
        QuantiteJournaliere(StadePhysiologique.VERRAT,            2.0,  2.7,  6.0,  "DGPA/MRAH 2021 + Thamani: verrat → 2 à 2,7 kg/j")
    )

    // =========================================================================
    // SECTION 3 — TABLE DES RATIONS DE RÉFÉRENCE (% MS pour 1 kg d'aliment)
    // SOURCE: Fiche technique n°3 alimentation porcs — ONG Thamani, Bobo-Dioulasso — secteur 24
    // SOURCE: Fiche simplifiée alimentation des porcs — DGPA/MRAH, Burkina Faso — Juin 2021
    //         Tableau 1 : Exemples de formulation de rations alimentaires
    // Disponible dans les documents joints du projet
    // =========================================================================

    /**
     * Composition d'une ration de référence (proportions en % de MS pour préparer 1 kg d'aliment).
     * Ces valeurs sont cohérentes entre les deux sources officielles DGPA/MRAH et ONG Thamani.
     */
    data class RationReference(
        val stade: StadePhysiologique,
        val sonMaïsPct: Double,          // % son de maïs
        val drecheBrasseriePct: Double,  // % drèche de brasserie
        val drecheDoloPct: Double,       // % drèche de dolo
        val tourteauCotonPct: Double,    // % tourteau de coton
        val farinePoissonPct: Double,    // % farine de poisson
        val coquillagePct: Double,       // % coquillage concassé
        val selPct: Double,              // % sel (NaCl)
        val sourceReference: String
    ) {
        /** Coût estimé par kg d'aliment en FCFA (calculé sur la base des prix Thamani Bobo-Dioulasso) */
        fun coutEstimeParKg(): Double {
            return (sonMaïsPct / 100.0)          * PRIX_INGREDIENTS["son_mais"]!!       +
                   (drecheBrasseriePct / 100.0)  * PRIX_INGREDIENTS["dreche_biere"]!!   +
                   (drecheDoloPct / 100.0)       * PRIX_INGREDIENTS["dreche_dolo"]!!    +
                   (tourteauCotonPct / 100.0)    * PRIX_INGREDIENTS["tourteau_coton"]!! +
                   (farinePoissonPct / 100.0)    * PRIX_INGREDIENTS["poisson_seche"]!!  +
                   (coquillagePct / 100.0)       * PRIX_INGREDIENTS["coquillage"]!!
            // Le sel est en très faible quantité (0,3%) et son prix non communiqué → négligé dans estimation
        }
    }

    // SOURCE: Fiche technique n°3 — ONG Thamani + Tableau 1 DGPA/MRAH Juin 2021
    val RATIONS_REFERENCE: Map<StadePhysiologique, RationReference> = mapOf(
        StadePhysiologique.PORCELET_SEVRAGE to RationReference(
            stade               = StadePhysiologique.PORCELET_SEVRAGE,
            sonMaïsPct          = 35.0,
            drecheBrasseriePct  = 25.0,
            drecheDoloPct       = 20.0,
            tourteauCotonPct    = 10.0,
            farinePoissonPct    = 10.0,
            coquillagePct       = 1.0,
            selPct              = 0.3,
            sourceReference     = "DGPA/MRAH 2021 (Tableau 1) + ONG Thamani — Porcelet 7–25 kg"
        ),
        StadePhysiologique.CROISSANCE to RationReference(
            stade               = StadePhysiologique.CROISSANCE,
            sonMaïsPct          = 35.0,
            drecheBrasseriePct  = 25.0,
            drecheDoloPct       = 20.0,
            tourteauCotonPct    = 10.0,
            farinePoissonPct    = 5.0,
            coquillagePct       = 1.0,
            selPct              = 0.3,
            sourceReference     = "DGPA/MRAH 2021 (Tableau 1) + ONG Thamani — Croissance 25–60 kg"
        ),
        StadePhysiologique.FINITION to RationReference(
            stade               = StadePhysiologique.FINITION,
            sonMaïsPct          = 35.0,
            drecheBrasseriePct  = 35.0,
            drecheDoloPct       = 20.0,
            tourteauCotonPct    = 5.0,
            farinePoissonPct    = 5.0,
            coquillagePct       = 1.0,
            selPct              = 0.3,
            sourceReference     = "DGPA/MRAH 2021 (Tableau 1) + ONG Thamani — Finition > 60 kg"
        ),
        StadePhysiologique.TRUIE_GESTANTE to RationReference(
            stade               = StadePhysiologique.TRUIE_GESTANTE,
            sonMaïsPct          = 40.0,
            drecheBrasseriePct  = 15.0,
            drecheDoloPct       = 35.0,
            tourteauCotonPct    = 0.0,
            farinePoissonPct    = 5.0,
            coquillagePct       = 2.0,
            selPct              = 0.3,
            sourceReference     = "DGPA/MRAH 2021 (Tableau 1) + ONG Thamani — Truie gestante (sans tourteau de coton)"
        ),
        StadePhysiologique.TRUIE_ALLAITANTE to RationReference(
            stade               = StadePhysiologique.TRUIE_ALLAITANTE,
            sonMaïsPct          = 40.0,
            drecheBrasseriePct  = 15.0,
            drecheDoloPct       = 30.0,
            tourteauCotonPct    = 5.0,
            farinePoissonPct    = 10.0,
            coquillagePct       = 0.2,
            selPct              = 0.3,
            sourceReference     = "DGPA/MRAH 2021 (Tableau 1) + ONG Thamani — Truie allaitante (besoin élevé protéines)"
        ),
        // TRUIE_VIDE et VERRAT : utilisation de la ration gestante par défaut (DGPA/MRAH ne précise pas de ration distincte)
        // SOURCE: DGPA/MRAH Juin 2021 — les quantités sont renseignées mais pas la composition spécifique
        StadePhysiologique.TRUIE_VIDE to RationReference(
            stade               = StadePhysiologique.TRUIE_VIDE,
            sonMaïsPct          = 40.0,
            drecheBrasseriePct  = 15.0,
            drecheDoloPct       = 35.0,
            tourteauCotonPct    = 0.0,
            farinePoissonPct    = 5.0,
            coquillagePct       = 2.0,
            selPct              = 0.3,
            sourceReference     = "DGPA/MRAH 2021 — Truie vide (ration similaire truie gestante par défaut)"
        ),
        StadePhysiologique.VERRAT to RationReference(
            stade               = StadePhysiologique.VERRAT,
            sonMaïsPct          = 35.0,
            drecheBrasseriePct  = 25.0,
            drecheDoloPct       = 20.0,
            tourteauCotonPct    = 10.0,
            farinePoissonPct    = 5.0,
            coquillagePct       = 1.0,
            selPct              = 0.3,
            sourceReference     = "DGPA/MRAH 2021 — Verrat (ration similaire croissance par défaut)"
        )
    )

    // =========================================================================
    // SECTION 4 — INGRÉDIENTS LOCAUX ET PRIX (FCFA/kg MS)
    // SOURCE: Fiche technique n°3 alimentation porcs — ONG Thamani, Bobo-Dioulasso — secteur 24
    // Données collectées sur le terrain à Bobo-Dioulasso, secteur 24
    // Disponible dans les documents joints du projet
    // =========================================================================

    /**
     * Prix en FCFA par kg de Matière Sèche des ingrédients disponibles à Bobo-Dioulasso.
     * Clé = identifiant de l'ingrédient (snake_case).
     */
    val PRIX_INGREDIENTS: Map<String, Double> = mapOf(
        // SOURCE: ONG Thamani — 100 FCFA pour 14 litres, densité 113 g MS/litre
        "dreche_dolo"    to 63.0,
        // SOURCE: ONG Thamani — 20 000 FCFA pour une benne (3120 litres), densité 109 g MS/litre
        "dreche_biere"   to 59.0,
        // SOURCE: ONG Thamani — 75 FCFA la boîte de 2 litres (son de maïs non trié), densité 320 g/litre
        "son_mais"       to 117.0,
        // SOURCE: ONG Thamani — 3 000 FCFA le sac de 50 kg (volume)
        "tourteau_coton" to 158.0,
        // SOURCE: ONG Thamani — 12 500 FCFA le sac de 50 kg (poids)
        "poisson_seche"  to 250.0,
        // SOURCE: ONG Thamani — prix terrain Bobo-Dioulasso
        "sang_seche"     to 150.0,
        // SOURCE: ONG Thamani — 4 500 FCFA le sac de 50 kg (poids)
        "coquillage"     to 90.0
        // Sel (NaCl) : utilisé à max 3% — prix non renseigné dans le document Thamani
    )

    /**
     * Détail des ingrédients locaux (densité et prix).
     */
    data class IngredientLocal(
        val cle: String,
        val nom: String,
        val poidsParLitreG: Double?,     // g de produit frais par litre
        val msPourUnLitreG: Double?,     // g de MS par litre
        val prixParKgMs: Double,         // FCFA/kg MS
        val typeAliment: String          // Énergétique / Protéique / Minéraux / Vitamines
    )

    // SOURCE: Fiche technique n°3 alimentation porcs — ONG Thamani, Bobo-Dioulasso — secteur 24
    val INGREDIENTS_LOCAUX: List<IngredientLocal> = listOf(
        IngredientLocal("dreche_dolo",    "Drèche de Dolo",          520.0,  113.0,  63.0,  "Énergétique"),
        IngredientLocal("dreche_biere",   "Drèche de Bière",         443.0,  109.0,  59.0,  "Énergétique"),
        IngredientLocal("son_mais",       "Son de Maïs",             320.0,  320.0,  117.0, "Énergétique"),
        IngredientLocal("tourteau_coton", "Tourteau de Coton",       380.0,  380.0,  158.0, "Protéique"),
        IngredientLocal("poisson_seche",  "Poisson Séché",           600.0,  600.0,  250.0, "Protéique"),
        IngredientLocal("sang_seche",     "Sang Séché",              null,   null,   150.0, "Protéique"),
        IngredientLocal("coquillage",     "Coquillage Concassé",     1300.0, 1300.0, 90.0,  "Minéraux"),
        IngredientLocal("sel",            "Sel (NaCl)",              null,   null,   0.0,   "Minéraux")
    )

    // =========================================================================
    // SECTION 5 — FORMULES COMMERCIALES FOURNISSEURS
    // =========================================================================

    /**
     * Spécifications nutritionnelles d'une formule commerciale (valeurs analytiques garanties).
     */
    data class FormuleCommerciale(
        val fournisseur: String,
        val nomFormule: String,
        val stades: List<StadePhysiologique>,
        val inclusions: Map<String, Double>,     // ingrédient → kg pour 1000 kg aliment complet
        val proteinesPct: ClosedRange<Double>,
        val lysinegPerKg: ClosedRange<Double>,
        val calciumgPerKg: ClosedRange<Double>,
        val phosphoregPerKg: ClosedRange<Double>,
        val contact: String,
        val sourceReference: String
    )

    // SOURCE: Formule Prémix 3% Porcelet 1er âge — AVIPRO SARL / WISIUM
    //         17 BP : 264 Ouaga Pissy, Secteur (Nouveau) 27, Ouagadougou — Tél : 58 00 24 24
    // Disponible dans les documents joints du projet
    val FORMULE_WISIUM_PORCELET_1ER_AGE = FormuleCommerciale(
        fournisseur = "AVIPRO SARL / WISIUM",
        nomFormule  = "Prémix 3% Porcelet 1er âge — Post-sevrage (28 à 70 jours)",
        stades      = listOf(StadePhysiologique.PORCELET_SEVRAGE),
        inclusions  = mapOf(
            // Option 1 (référence principale)
            "PMX Démarrage 3% (30 kg)" to 30.0,
            "LACTOPIG"                 to 50.0,
            "Maïs 7.8"                 to 620.0,
            "Son de Blé"               to 60.0,
            "Soja Torréfié 37"         to 50.0,
            "Tourteau de Soja 46.5"    to 180.0,
            "Calcaire"                 to 10.0
        ),
        proteinesPct    = 17.8..18.1,
        lysinegPerKg    = 12.4..12.6,
        calciumgPerKg   = 8.3..9.1,
        phosphoregPerKg = 5.8..6.6,
        contact         = "AVIPRO SARL — Ouaga Pissy, Secteur 27 — Tél : 58 00 24 24 / 58 00 25 25",
        sourceReference = "Formule Prémix 3% Porcelet 1er âge — AVIPRO SARL / WISIUM, Ouagadougou Pissy"
    )

    // SOURCE: Formule Prémix 3% Truie Allaitante — AVIPRO SARL / WISIUM
    //         17 BP : 264 Ouaga Pissy, Secteur (Nouveau) 27, Ouagadougou — Tél : 58 00 24 24
    // Disponible dans les documents joints du projet
    val FORMULE_WISIUM_TRUIE_ALLAITANTE = FormuleCommerciale(
        fournisseur = "AVIPRO SARL / WISIUM",
        nomFormule  = "Prémix 3% Truie Allaitante",
        stades      = listOf(StadePhysiologique.TRUIE_ALLAITANTE),
        inclusions  = mapOf(
            // Option 1 (référence principale)
            "PMX Truie Allaitante 3% (30 kg)" to 30.0,
            "Maïs 7.8"                        to 620.0,
            "Son de Blé"                      to 140.0,
            "Soja Torréfié 37"                to 100.0,
            "Tourteau de Soja 46.5"           to 100.0,
            "Calcaire"                        to 10.0
        ),
        proteinesPct    = 16.1..17.0,
        lysinegPerKg    = 11.0..11.4,
        calciumgPerKg   = 8.8..9.5,
        phosphoregPerKg = 5.2..6.3,
        contact         = "AVIPRO SARL — Ouaga Pissy, Secteur 27 — Tél : 58 00 24 24 / 58 00 25 25",
        sourceReference = "Formule Prémix 3% Truie Allaitante — AVIPRO SARL / WISIUM, Ouagadougou Pissy"
    )

    // SOURCE: Formule Prémix 2.7% Croissance (Porcelet 2ème âge) — AVIPRO SARL / WISIUM
    //         17 BP : 264 Ouaga Pissy, Secteur (Nouveau) 27, Ouagadougou — Tél : 58 00 24 24
    // Disponible dans les documents joints du projet
    val FORMULE_WISIUM_CROISSANCE = FormuleCommerciale(
        fournisseur = "AVIPRO SARL / WISIUM",
        nomFormule  = "Prémix 2.7% Croissance — Porcelet 2ème âge",
        stades      = listOf(StadePhysiologique.CROISSANCE),
        inclusions  = mapOf(
            // Option 1 (référence principale)
            "PMX Croissance 2.7 (27 kg)" to 27.0,
            "Maïs 7.8"                   to 660.0,
            "Son de Blé"                 to 110.0,
            "Tourteau de Soja 46.5"      to 200.0,
            "Calcaire"                   to 3.0
        ),
        proteinesPct    = 16.5..17.5,
        lysinegPerKg    = 10.2..10.3,
        calciumgPerKg   = 8.7..8.9,
        phosphoregPerKg = 4.4..4.8,
        contact         = "AVIPRO SARL — Ouaga Pissy, Secteur 27 — Tél : 58 00 24 24 / 58 00 25 25",
        sourceReference = "Formule Prémix 2.7% Croissance — AVIPRO SARL / WISIUM, Ouagadougou Pissy"
    )

    // SOURCE: Formule Prémix 2.5% Truie Gestante (et Reproducteur) — AVIPRO SARL / WISIUM
    //         17 BP : 264 Ouaga Pissy, Secteur (Nouveau) 27, Ouagadougou — Tél : 58 00 24 24
    // Disponible dans les documents joints du projet
    val FORMULE_WISIUM_TRUIE_GESTANTE = FormuleCommerciale(
        fournisseur = "AVIPRO SARL / WISIUM",
        nomFormule  = "Prémix 2.5% Truie Gestante (et Reproducteur)",
        stades      = listOf(StadePhysiologique.TRUIE_GESTANTE, StadePhysiologique.VERRAT),
        inclusions  = mapOf(
            // Option 1 (référence principale)
            "PMX Truie Gestante 2.5 (25 kg)" to 25.0,
            "Maïs 7.8"                       to 620.0,
            "Son de Blé"                     to 200.0,
            "Soja Torréfié 37"               to 25.0,
            "Tourteau de Soja 46.5"          to 125.0,
            "Calcaire"                       to 5.0
        ),
        proteinesPct    = 14.5..15.3,
        lysinegPerKg    = 6.6..7.0,
        calciumgPerKg   = 8.0..8.8,
        phosphoregPerKg = 5.2..5.9,
        contact         = "AVIPRO SARL — Ouaga Pissy, Secteur 27 — Tél : 58 00 24 24 / 58 00 25 25",
        sourceReference = "Formule Prémix 2.5% Truie Gestante — AVIPRO SARL / WISIUM, Ouagadougou Pissy"
    )

    // SOURCE: Formule Aliment Porc avec Prémix ALF ISSEN — ALF ISSEN, Burkina Faso
    //         Production d'aliments composés pour bétail et volailles
    // Disponible dans les documents joints du projet
    val FORMULE_ALF_ISSEN_PORCELET = FormuleCommerciale(
        fournisseur = "ALF ISSEN",
        nomFormule  = "Aliment Porcelet ≤ 12 semaines — ALF ISSEN",
        stades      = listOf(StadePhysiologique.PORCELET_SEVRAGE),
        inclusions  = mapOf(
            "Maïs"                           to 688.0,
            "Tourteau de Soja"               to 155.0,
            "Son de Blé"                     to 10.0,
            "Tourteau de Palmiste"           to 10.0,
            "Farine de Poisson"              to 90.0,
            "Son de Maïs"                    to 5.0,
            "Coquillages"                    to 1.5,
            "Sel"                            to 0.5,
            "Prémix Porcelets 4% ALF ISSEN"  to 40.0
        ),
        proteinesPct    = 0.0..0.0,   // non spécifié sur la fiche image
        lysinegPerKg    = 0.0..0.0,
        calciumgPerKg   = 0.0..0.0,
        phosphoregPerKg = 0.0..0.0,
        contact         = "ALF ISSEN — Production d'aliments composés pour bétail et volailles, Burkina Faso",
        sourceReference = "Formule Aliment Porc avec Prémix ALF ISSEN — ALF ISSEN, Burkina Faso"
    )

    // SOURCE: Formule Aliment Porc avec Prémix ALF ISSEN — ALF ISSEN, Burkina Faso
    val FORMULE_ALF_ISSEN_ENGRAISSEMENT = FormuleCommerciale(
        fournisseur = "ALF ISSEN",
        nomFormule  = "Aliment Porc Engraissement — ALF ISSEN",
        stades      = listOf(StadePhysiologique.CROISSANCE, StadePhysiologique.FINITION),
        inclusions  = mapOf(
            "Maïs"                               to 698.0,
            "Tourteau de Soja"                   to 163.0,
            "Son de Blé"                         to 26.0,
            "Farine de Poisson"                  to 90.0,
            "Coquillages"                        to 2.0,
            "Sel"                                to 1.0,
            "Prémix Porc Engraissement 2% ALF ISSEN" to 20.0
        ),
        proteinesPct    = 0.0..0.0,
        lysinegPerKg    = 0.0..0.0,
        calciumgPerKg   = 0.0..0.0,
        phosphoregPerKg = 0.0..0.0,
        contact         = "ALF ISSEN — Production d'aliments composés pour bétail et volailles, Burkina Faso",
        sourceReference = "Formule Aliment Porc avec Prémix ALF ISSEN — ALF ISSEN, Burkina Faso"
    )

    // SOURCE: Formule Aliment Porc avec Prémix ALF ISSEN — ALF ISSEN, Burkina Faso
    val FORMULE_ALF_ISSEN_TRUIES = FormuleCommerciale(
        fournisseur = "ALF ISSEN",
        nomFormule  = "Aliment Truies — ALF ISSEN",
        stades      = listOf(StadePhysiologique.TRUIE_GESTANTE, StadePhysiologique.TRUIE_ALLAITANTE, StadePhysiologique.TRUIE_VIDE),
        inclusions  = mapOf(
            "Maïs"                      to 699.0,
            "Tourteau de Soja"          to 73.0,
            "Tourteau de Palmiste"      to 132.0,
            "Farine de Poisson"         to 65.0,
            "Coquillages"               to 9.0,
            "Sel"                       to 2.0,
            "Prémix Truies 2% ALF ISSEN" to 20.0
        ),
        proteinesPct    = 0.0..0.0,
        lysinegPerKg    = 0.0..0.0,
        calciumgPerKg   = 0.0..0.0,
        phosphoregPerKg = 0.0..0.0,
        contact         = "ALF ISSEN — Production d'aliments composés pour bétail et volailles, Burkina Faso",
        sourceReference = "Formule Aliment Porc avec Prémix ALF ISSEN — ALF ISSEN, Burkina Faso"
    )

    // SOURCE: Formule VITALAC — CCTRE / VITALAC, Burkina Faso (fiche terrain)
    // Disponible dans les documents joints du projet
    val FORMULE_VITALAC_TRUIE = FormuleCommerciale(
        fournisseur = "VITALAC / CCTRE",
        nomFormule  = "Aliment Truie Gestante / Allaitante — VITALAC",
        stades      = listOf(StadePhysiologique.TRUIE_GESTANTE, StadePhysiologique.TRUIE_ALLAITANTE),
        inclusions  = mapOf(
            "Maïs"              to 650.0,
            "Son de Blé"        to 220.0,
            "Tourteau de Soja"  to 100.0,
            "AMV Truie"         to 17.5,   // moyenne 15–20 kg
            "Coquillage"        to 15.0
        ),
        proteinesPct    = 0.0..0.0,
        lysinegPerKg    = 0.0..0.0,
        calciumgPerKg   = 0.0..0.0,
        phosphoregPerKg = 0.0..0.0,
        contact         = "VITALAC / CCTRE — fiche terrain Burkina Faso",
        sourceReference = "Formule VITALAC — CCTRE / VITALAC, Burkina Faso"
    )

    // SOURCE: Formule VITALAC — CCTRE / VITALAC, Burkina Faso (fiche terrain)
    val FORMULE_VITALAC_CROISSANCE = FormuleCommerciale(
        fournisseur = "VITALAC / CCTRE",
        nomFormule  = "Aliment Croissance / Engraissement Porcelet — VITALAC",
        stades      = listOf(StadePhysiologique.CROISSANCE, StadePhysiologique.FINITION),
        inclusions  = mapOf(
            "Maïs"              to 600.0,
            "Son de Blé"        to 190.0,
            "Tourteau de Soja"  to 170.0,
            "AMV Porc"          to 20.0,
            "Coquillage"        to 20.0
        ),
        proteinesPct    = 0.0..0.0,
        lysinegPerKg    = 0.0..0.0,
        calciumgPerKg   = 0.0..0.0,
        phosphoregPerKg = 0.0..0.0,
        contact         = "VITALAC / CCTRE — fiche terrain Burkina Faso",
        sourceReference = "Formule VITALAC — CCTRE / VITALAC, Burkina Faso"
    )

    // =========================================================================
    // SECTION 6 — RÈGLE ÉCONOMIQUE
    // SOURCE: Fiche technique n°3 alimentation porcs — ONG Thamani, Bobo-Dioulasso — secteur 24
    // "Le prix de revient d'1 kg d'aliment ne doit pas dépasser 1/6 du prix de vente du kg sur pied"
    // À Bobo-Dioulasso : prix vente = 600 FCFA/kg → coût aliment cible ≤ 100 FCFA/kg
    // Disponible dans les documents joints du projet
    // =========================================================================

    /** Prix de vente moyen du porc sur pied à Bobo-Dioulasso (FCFA/kg) */
    const val PRIX_VENTE_KG_SUR_PIED_FCFA = 600.0

    /** Seuil économique maximal pour le coût d'1 kg d'aliment (= 1/6 du prix sur pied) */
    const val COUT_MAX_KG_ALIMENT_FCFA = PRIX_VENTE_KG_SUR_PIED_FCFA / 6.0  // = 100.0 FCFA/kg

    // =========================================================================
    // SECTION 7 — BESOINS NUTRITIONNELS MINIMAUX
    // SOURCE: Fiche technique n°3 alimentation porcs — ONG Thamani, Bobo-Dioulasso — secteur 24
    // Disponible dans les documents joints du projet
    // =========================================================================

    /** Énergie : entre 2 400 et 2 600 kcal/kg d'aliment (toutes catégories) */
    const val ENERGIE_MIN_KCAL_PER_KG = 2400.0
    const val ENERGIE_MAX_KCAL_PER_KG = 2600.0

    /** Protéines : 15 à 20% de la ration */
    const val PROTEINES_MIN_PCT = 15.0
    const val PROTEINES_MAX_PCT = 20.0

    /** Calcium : 0,9% */
    const val CALCIUM_PCT = 0.9

    /** Phosphore : 0,3% */
    const val PHOSPHORE_PCT = 0.3

    /** Sel maximum : 3% de la ration */
    const val SEL_MAX_PCT = 3.0
}
