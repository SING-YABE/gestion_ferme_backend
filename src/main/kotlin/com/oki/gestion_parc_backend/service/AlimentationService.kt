package com.oki.gestion_parc_backend.service

import com.oki.gestion_parc_backend.dto.AlimentationDto
import com.oki.gestion_parc_backend.dto.CoutRationDto
import com.oki.gestion_parc_backend.dto.IngredientAlimentationDto
import com.oki.gestion_parc_backend.dto.RationReferenceDto
import com.oki.gestion_parc_backend.dto.StatistiqueMensuelleDto

interface AlimentationService {
    fun create(dto: AlimentationDto): AlimentationDto
    fun list(): List<AlimentationDto>
    fun delete(id: Long)
    fun getEvolutionCoutsMensuels(): List<StatistiqueMensuelleDto>
    fun getEvolutionCoutsMensuelsPeriode(dateDebut: String, dateFin: String): List<StatistiqueMensuelleDto>

    /**
     * Suggère la ration officielle de référence pour un stade physiologique et un poids vif donnés.
     *
     * SOURCE: Fiche simplifiée alimentation des porcs — DGPA/MRAH, Burkina Faso — Juin 2021
     * SOURCE: Fiche technique n°3 alimentation porcs — ONG Thamani, Bobo-Dioulasso — secteur 24
     *
     * @param stadePhysiologique Valeur de l'enum StadePhysiologique (ex: "TRUIE_GESTANTE")
     * @param poidsKg Poids vif de l'animal en kg (utilisé pour préciser la fourchette de quantité)
     * @return RationReferenceDto avec les proportions officielles et la quantité journalière estimée
     */
    fun suggestRation(stadePhysiologique: String, poidsKg: Double?): RationReferenceDto

    /**
     * Calcule le coût total d'une ration en FCFA et vérifie la conformité à la règle économique.
     *
     * Règle : coût/kg ≤ 100 FCFA (= 1/6 du prix vente 600 FCFA/kg sur pied à Bobo-Dioulasso)
     * SOURCE: Fiche technique n°3 alimentation porcs — ONG Thamani, Bobo-Dioulasso — secteur 24
     *
     * @param ingredients Liste des ingrédients avec quantités et prix unitaires
     * @return CoutRationDto avec coût total, coût/kg, conformité et détail par ingrédient
     */
    fun calculerCoutRation(ingredients: List<IngredientAlimentationDto>): CoutRationDto
}