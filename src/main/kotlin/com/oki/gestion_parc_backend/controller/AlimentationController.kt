package com.oki.gestion_parc_backend.controller


import com.oki.gestion_parc_backend.dto.AlimentationDto
import com.oki.gestion_parc_backend.dto.IngredientAlimentationDto
import com.oki.gestion_parc_backend.service.AlimentationService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.security.access.prepost.PreAuthorize

@RestController
@RequestMapping("/api/alimentations")
class AlimentationController(
    private val service: AlimentationService
) {

    @PostMapping
    @PreAuthorize("hasAuthority('ALIMENTATION_WRITE')")
    fun create(@RequestBody dto: AlimentationDto) = service.create(dto)

    @GetMapping
    @PreAuthorize("hasAuthority('ALIMENTATION_READ')")
    fun list() = service.list()

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ALIMENTATION_DELETE')")
    fun delete(@PathVariable id: Long) = service.delete(id)

    @GetMapping("/statistiques/couts-mensuels")
    @PreAuthorize("hasAuthority('ALIMENTATION_STATS')")
    fun getEvolutionCoutsMensuels() = service.getEvolutionCoutsMensuels()

    @GetMapping("/statistiques/couts-mensuels-periode")
    @PreAuthorize("hasAuthority('ALIMENTATION_STATS')")
    fun getEvolutionCoutsMensuelsPeriode(
        @RequestParam dateDebut: String,
        @RequestParam dateFin: String
    ) = service.getEvolutionCoutsMensuelsPeriode(dateDebut, dateFin)

    /**
     * Suggère la ration officielle de référence pour un stade physiologique donné.
     *
     * GET /api/alimentations/ration-reference?stade=TRUIE_GESTANTE&poids=80
     *
     * Stades valides : PORCELET_SEVRAGE, CROISSANCE, FINITION,
     *                  TRUIE_GESTANTE, TRUIE_ALLAITANTE, TRUIE_VIDE, VERRAT
     *
     * SOURCE: Fiche simplifiée alimentation des porcs — DGPA/MRAH, Burkina Faso — Juin 2021
     * SOURCE: Fiche technique n°3 alimentation porcs — ONG Thamani, Bobo-Dioulasso — secteur 24
     */
    @GetMapping("/ration-reference")
    @PreAuthorize("hasAuthority('ALIMENTATION_RATION')")
    fun suggestRation(
        @RequestParam stade: String,
        @RequestParam(required = false) poids: Double?
    ) = service.suggestRation(stade, poids)

    /**
     * Calcule le coût d'une ration et vérifie la conformité à la règle économique.
     *
     * POST /api/alimentations/calculer-cout
     * Body : liste de IngredientAlimentationDto (ingredientId, quantiteKg, prixUnitaire)
     *
     * Règle : coût/kg ≤ 100 FCFA (= 1/6 × 600 FCFA/kg prix sur pied — Bobo-Dioulasso)
     * SOURCE: Fiche technique n°3 alimentation porcs — ONG Thamani, Bobo-Dioulasso — secteur 24
     */
    @PostMapping("/calculer-cout")
    @PreAuthorize("hasAuthority('ALIMENTATION_COUT')")
    fun calculerCoutRation(@RequestBody ingredients: List<IngredientAlimentationDto>) =
        service.calculerCoutRation(ingredients)
}