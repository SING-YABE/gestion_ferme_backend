package com.oki.gestion_parc_backend.dto

/**
 * DTO — Suggestion de ration officielle par stade physiologique.
 *
 * Retourné par AlimentationService.suggestRation().
 * Les proportions sont exprimées en % de Matière Sèche pour préparer 1 kg d'aliment.
 *
 * SOURCE: Fiche simplifiée alimentation des porcs — DGPA/MRAH, Burkina Faso — Juin 2021
 *         (Tableau 1 : Exemples de formulation de rations alimentaires)
 * SOURCE: Fiche technique n°3 alimentation porcs — ONG Thamani, Bobo-Dioulasso — secteur 24
 * Disponible dans les documents joints du projet
 */
data class RationReferenceDto(
    val stadePhysiologique: String,

    // Proportions de la ration (% MS pour 1 kg d'aliment)
    val sonMaïsPct: Double,
    val drecheBrasseriePct: Double,
    val drecheDoloPct: Double,
    val tourteauCotonPct: Double,
    val farinePoissonPct: Double,
    val coquillagePct: Double,
    val selPct: Double,

    // Quantités journalières recommandées
    val quantiteJournaliereMinKg: Double,
    val quantiteJournaliereMaxKg: Double,
    val eauJournaliereLitres: Double,

    // Coût estimé sur la base des prix Bobo-Dioulasso (FCFA/kg MS)
    // SOURCE: ONG Thamani — données terrain secteur 24, Bobo-Dioulasso
    val coutEstimeParKgFcfa: Double,
    val coutConformeRegleEconomique: Boolean, // true si <= 100 FCFA/kg

    val sourceReference: String,
    val noteEconomique: String = "Seuil : 100 FCFA/kg (= 1/6 du prix vente 600 FCFA/kg sur pied à Bobo-Dioulasso — ONG Thamani)"
)
