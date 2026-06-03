package com.oki.gestion_parc_backend.service.impl

import com.oki.gestion_parc_backend.dto.AlimentationDto
import com.oki.gestion_parc_backend.dto.CoutRationDto
import com.oki.gestion_parc_backend.dto.DetailIngredientCout
import com.oki.gestion_parc_backend.dto.IngredientAlimentationDto
import com.oki.gestion_parc_backend.dto.RationReferenceDto
import com.oki.gestion_parc_backend.dto.StatistiqueMensuelleDto
import com.oki.gestion_parc_backend.mapper.DepenseMapper.formatter
import com.oki.gestion_parc_backend.model.Alimentation
import com.oki.gestion_parc_backend.model.IngredientAlimentation
import com.oki.gestion_parc_backend.reference.AlimentationReferenceData
import com.oki.gestion_parc_backend.reference.AlimentationReferenceData.COUT_MAX_KG_ALIMENT_FCFA
import com.oki.gestion_parc_backend.reference.AlimentationReferenceData.QUANTITES_JOURNALIERES
import com.oki.gestion_parc_backend.reference.AlimentationReferenceData.RATIONS_REFERENCE
import com.oki.gestion_parc_backend.reference.AlimentationReferenceData.StadePhysiologique
import com.oki.gestion_parc_backend.repository.AlimentationRepository
import com.oki.gestion_parc_backend.repository.AnimalRepository
import com.oki.gestion_parc_backend.repository.FournisseurRepository
import com.oki.gestion_parc_backend.repository.IngredientRepository
import com.oki.gestion_parc_backend.repository.TypeAnimalRepository
import com.oki.gestion_parc_backend.service.AlimentationService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class AlimentationServiceImpl(
    private val repo: AlimentationRepository,
    private val ingredientRepo: IngredientRepository,
    private val animalRepo: AnimalRepository,
    private val typeAnimalRepo: TypeAnimalRepository,
    private val fournisseurRepo: FournisseurRepository
) : AlimentationService {

    @Transactional
    override fun create(dto: AlimentationDto): AlimentationDto {
        // Validation : animal OU lot (pas les deux)
        if (dto.codeAnimal != null && dto.typeAnimalId != null) {
            throw IllegalArgumentException("Une alimentation ne peut pas cibler un animal et un lot simultanément.")
        }

        // Validation : au moins 1 ingrédient
        if (dto.ingredients.isEmpty()) {
            throw IllegalArgumentException("Au moins un ingrédient est requis.")
        }

        // -----------------------------------------------------------------------
        // Validation MODE FABRICATION — règle assouplie
        //
        // Ancienne règle : exactement 4 ingrédients (1 par type obligatoirement).
        // Nouvelle règle : les 4 types doivent être représentés (au moins 1 ingrédient
        //   de chaque type), mais plusieurs ingrédients par type sont autorisés.
        //
        // Exemple valide : drèche de dolo + drèche de bière (2 Énergétiques) + tourteau
        //   de coton (1 Protéique) + coquillage (1 Minéraux) + prémix (1 Vitamines).
        //
        // SOURCE: Fiche technique n°3 alimentation porcs — ONG Thamani, Bobo-Dioulasso
        //   (formules avec plusieurs ingrédients par catégorie)
        // -----------------------------------------------------------------------
        if (dto.mode == "FABRICATION") {

            // Collecter les types d'aliments présents (par leur id : 1, 2, 3, 4)
            val typesPresents = dto.ingredients.map { ingredientDto ->
                val ingredient = ingredientRepo.findById(ingredientDto.ingredientId)
                    .orElseThrow { IllegalArgumentException("Ingrédient ${ingredientDto.ingredientId} introuvable") }
                ingredient.typeAliment.id
            }.toSet()

            // Vérifier la présence des 4 types (Énergétique=1, Protéique=2, Minéraux=3, Vitamines=4)
            if (!typesPresents.containsAll(listOf(1L, 2L, 3L, 4L))) {
                throw IllegalArgumentException(
                    "Le mode FABRICATION nécessite au minimum 1 ingrédient de chaque type : " +
                    "Énergétique (id=1), Protéique (id=2), Minéraux (id=3), Vitamines (id=4). " +
                    "Plusieurs ingrédients d'un même type sont autorisés."
                )
            }
        }

        // Récupérer les entités associées
        val fournisseur = dto.fournisseurId?.let {
            fournisseurRepo.findById(it).orElseThrow { IllegalArgumentException("Fournisseur introuvable") }
        }

        val animal = dto.codeAnimal?.let {
            animalRepo.findByCodeAnimal(it)
                .orElseThrow { IllegalArgumentException("Animal avec code $it introuvable") }
        }

        val typeAnimal = dto.typeAnimalId?.let {
            typeAnimalRepo.findById(it).orElseThrow { IllegalArgumentException("Type animal introuvable") }
        }

        // Créer l'alimentation avec la référence documentaire
        val alimentation = Alimentation(
            date            = dto.date,
            mode            = dto.mode,
            animal          = animal,
            typeAnimal      = typeAnimal,
            fournisseur     = fournisseur,
            sourceReference = dto.sourceReference
        )

        // Sauvegarder l'alimentation pour générer l'ID
        val savedAlimentation = repo.save(alimentation)

        // Créer et attacher les IngredientAlimentation
        dto.ingredients.forEach { ingredientDto ->
            val ingredient = ingredientRepo.findById(ingredientDto.ingredientId)
                .orElseThrow { IllegalArgumentException("Ingrédient ${ingredientDto.ingredientId} introuvable") }

            val ingredientAlimentation = IngredientAlimentation(
                alimentation = savedAlimentation,
                ingredient   = ingredient,
                quantiteKg   = ingredientDto.quantiteKg,
                prixUnitaire = ingredientDto.prixUnitaire
            )

            savedAlimentation.ingredients.add(ingredientAlimentation)
        }

        val finalSaved = repo.save(savedAlimentation)
        return AlimentationMapper.toDto(finalSaved)
    }

    override fun list(): List<AlimentationDto> =
        repo.findAll().map { AlimentationMapper.toDto(it) }

    override fun delete(id: Long) = repo.deleteById(id)

    override fun getEvolutionCoutsMensuels(): List<StatistiqueMensuelleDto> {
        return repo.getCoutsMensuels().map { row ->
            StatistiqueMensuelleDto(
                annee    = (row[0] as Number).toInt(),
                mois     = (row[1] as Number).toInt(),
                coutTotal = (row[2] as Number).toDouble()
            )
        }
    }

    override fun getEvolutionCoutsMensuelsPeriode(dateDebut: String, dateFin: String): List<StatistiqueMensuelleDto> {
        val debut = LocalDate.parse(dateDebut, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        val fin   = LocalDate.parse(dateFin,   DateTimeFormatter.ofPattern("dd/MM/yyyy"))

        return repo.getCoutsMensuelsPeriode(debut, fin).map { row ->
            StatistiqueMensuelleDto(
                annee     = (row[0] as Number).toInt(),
                mois      = (row[1] as Number).toInt(),
                coutTotal = (row[2] as Number).toDouble()
            )
        }
    }

    // ---------------------------------------------------------------------------
    // MÉTHODE : suggestRation
    //
    // SOURCE: Fiche simplifiée alimentation des porcs — DGPA/MRAH, Burkina Faso — Juin 2021
    //         (Tableau 1 : Exemples de formulation de rations alimentaires)
    // SOURCE: Fiche technique n°3 alimentation porcs — ONG Thamani, Bobo-Dioulasso — secteur 24
    // Disponible dans les documents joints du projet
    // ---------------------------------------------------------------------------

    /**
     * Retourne la ration officielle de référence pour un stade physiologique donné.
     *
     * Les proportions (% MS) sont extraites du Tableau 1 de la DGPA/MRAH (Juin 2021),
     * cohérentes avec les données terrain de l'ONG Thamani (Bobo-Dioulasso, secteur 24).
     *
     * @param stadePhysiologique Nom de l'enum (ex: "TRUIE_GESTANTE", "CROISSANCE")
     * @param poidsKg Poids vif en kg — utilisé pour sélectionner la fourchette de quantité
     */
    override fun suggestRation(stadePhysiologique: String, poidsKg: Double?): RationReferenceDto {
        val stade = try {
            StadePhysiologique.valueOf(stadePhysiologique.uppercase())
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException(
                "Stade physiologique '$stadePhysiologique' invalide. " +
                "Valeurs acceptées : ${StadePhysiologique.values().joinToString()}"
            )
        }

        val ration = RATIONS_REFERENCE[stade]
            ?: throw IllegalStateException("Aucune ration de référence disponible pour le stade $stade")

        val quantite = QUANTITES_JOURNALIERES.find { it.stade == stade }
            ?: throw IllegalStateException("Aucune quantité journalière disponible pour le stade $stade")

        val coutEstime = ration.coutEstimeParKg()
        val conforme   = coutEstime <= COUT_MAX_KG_ALIMENT_FCFA

        return RationReferenceDto(
            stadePhysiologique          = stade.name,
            sonMaïsPct                  = ration.sonMaïsPct,
            drecheBrasseriePct          = ration.drecheBrasseriePct,
            drecheDoloPct               = ration.drecheDoloPct,
            tourteauCotonPct            = ration.tourteauCotonPct,
            farinePoissonPct            = ration.farinePoissonPct,
            coquillagePct               = ration.coquillagePct,
            selPct                      = ration.selPct,
            quantiteJournaliereMinKg    = quantite.quantiteMinKg,
            quantiteJournaliereMaxKg    = quantite.quantiteMaxKg,
            eauJournaliereLitres        = quantite.eauLitres,
            coutEstimeParKgFcfa         = coutEstime,
            coutConformeRegleEconomique = conforme,
            sourceReference             = ration.sourceReference
        )
    }

    // ---------------------------------------------------------------------------
    // MÉTHODE : calculerCoutRation
    //
    // Règle économique :
    //   Coût max d'1 kg d'aliment = 1/6 du prix vente du kg sur pied
    //   À Bobo-Dioulasso : 600 FCFA/kg → seuil = 100 FCFA/kg
    //
    // SOURCE: Fiche technique n°3 alimentation porcs — ONG Thamani, Bobo-Dioulasso — secteur 24
    // Disponible dans les documents joints du projet
    // ---------------------------------------------------------------------------

    /**
     * Calcule le coût total d'une ration et émet une alerte si le seuil économique est dépassé.
     *
     * Entrée : liste d'ingrédients avec quantiteKg et prixUnitaire (FCFA/kg).
     * Sortie : coût total, coût/kg, conformité à la règle Thamani, détail par ingrédient.
     */
    override fun calculerCoutRation(ingredients: List<IngredientAlimentationDto>): CoutRationDto {
        if (ingredients.isEmpty()) {
            throw IllegalArgumentException("La liste d'ingrédients ne peut pas être vide.")
        }

        val details = ingredients.map { dto ->
            val nom = dto.ingredientNom ?: "Ingrédient #${dto.ingredientId}"
            DetailIngredientCout(
                ingredientId      = dto.ingredientId,
                ingredientNom     = nom,
                quantiteKg        = dto.quantiteKg,
                prixUnitaireFcfa  = dto.prixUnitaire,
                sousTotalFcfa     = dto.quantiteKg * dto.prixUnitaire
            )
        }

        val coutTotal      = details.sumOf { it.sousTotalFcfa }
        val quantiteTotale = details.sumOf { it.quantiteKg }
        val coutParKg      = if (quantiteTotale > 0) coutTotal / quantiteTotale else 0.0
        val conforme       = coutParKg <= COUT_MAX_KG_ALIMENT_FCFA

        // SOURCE: ONG Thamani — règle 1/6 du prix vente = 100 FCFA/kg max
        val alerte = if (!conforme) {
            "⚠️ ALERTE ÉCONOMIQUE : Le coût de la ration est de %.2f FCFA/kg, ce qui dépasse le seuil " +
            "recommandé de %.0f FCFA/kg (= 1/6 du prix de vente sur pied de %.0f FCFA/kg à Bobo-Dioulasso — ONG Thamani)."
                .format(coutParKg, COUT_MAX_KG_ALIMENT_FCFA, AlimentationReferenceData.PRIX_VENTE_KG_SUR_PIED_FCFA)
        } else null

        return CoutRationDto(
            coutTotalFcfa              = coutTotal,
            quantiteTotaleKg           = quantiteTotale,
            coutParKgFcfa              = coutParKg,
            conformeRegleEconomique    = conforme,
            seuilCoutMaxFcfa           = COUT_MAX_KG_ALIMENT_FCFA,
            alerte                     = alerte,
            detailIngredients          = details
        )
    }
}
