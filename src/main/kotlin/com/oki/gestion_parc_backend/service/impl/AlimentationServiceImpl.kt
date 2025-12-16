package com.oki.gestion_parc_backend.service.impl

import com.oki.gestion_parc_backend.dto.AlimentationDto
import com.oki.gestion_parc_backend.dto.StatistiqueMensuelleDto
import com.oki.gestion_parc_backend.mapper.DepenseMapper.formatter
import com.oki.gestion_parc_backend.model.Alimentation
import com.oki.gestion_parc_backend.model.IngredientAlimentation
import com.oki.gestion_parc_backend.repository.AlimentationRepository
import com.oki.gestion_parc_backend.repository.AnimalRepository
import com.oki.gestion_parc_backend.repository.FournisseurRepository
import com.oki.gestion_parc_backend.repository.IngredientRepository
import com.oki.gestion_parc_backend.repository.TypeAlimentRepository
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
        // Validation : animal OU lot
        if (dto.codeAnimal != null && dto.typeAnimalId != null) {
            throw IllegalArgumentException("Une alimentation ne peut pas cibler un animal et un lot simultanément.")
        }

        // Validation : au moins 1 ingrédient
        if (dto.ingredients.isEmpty()) {
            throw IllegalArgumentException("Au moins un ingrédient est requis.")
        }

        // Validation MODE FABRICATION : exactement 4 ingrédients, 1 de chaque type
        if (dto.mode == "FABRICATION") {
            if (dto.ingredients.size != 4) {
                throw IllegalArgumentException("Le mode FABRICATION nécessite exactement 4 ingrédients.")
            }

            // Vérifier qu'on a 1 ingrédient de chaque type (1, 2, 3, 4)
            val typesPresents = dto.ingredients.map { ingredientDto ->
                val ingredient = ingredientRepo.findById(ingredientDto.ingredientId)
                    .orElseThrow { IllegalArgumentException("Ingrédient ${ingredientDto.ingredientId} introuvable") }
                ingredient.typeAliment.id
            }.toSet()

            if (typesPresents.size != 4 || !typesPresents.containsAll(listOf(1L, 2L, 3L, 4L))) {
                throw IllegalArgumentException("Le mode FABRICATION nécessite 1 ingrédient de chaque type (Énergétique, Protéine, Minéraux, Vitamines).")
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

        // Créer l'alimentation
        val alimentation = Alimentation(
            date = dto.date,
            mode = dto.mode,
            animal = animal,
            typeAnimal = typeAnimal,
            fournisseur = fournisseur
        )

        // Sauvegarder l'alimentation d'abord pour générer l'ID
        val savedAlimentation = repo.save(alimentation)

        // Créer les IngredientAlimentation et les ajouter directement à l'alimentation
        dto.ingredients.forEach { ingredientDto ->
            val ingredient = ingredientRepo.findById(ingredientDto.ingredientId)
                .orElseThrow { IllegalArgumentException("Ingrédient ${ingredientDto.ingredientId} introuvable") }

            val ingredientAlimentation = IngredientAlimentation(
                alimentation = savedAlimentation,
                ingredient = ingredient,
                quantiteKg = ingredientDto.quantiteKg,
                prixUnitaire = ingredientDto.prixUnitaire
            )

            savedAlimentation.ingredients.add(ingredientAlimentation)
        }

        // Sauvegarder à nouveau l'alimentation (cascade persistera les ingrédients)
        val finalSaved = repo.save(savedAlimentation)

        return AlimentationMapper.toDto(finalSaved)
    }

    override fun list(): List<AlimentationDto> =
        repo.findAll().map { AlimentationMapper.toDto(it) }

    override fun delete(id: Long) = repo.deleteById(id)

    override fun getEvolutionCoutsMensuels(): List<StatistiqueMensuelleDto> {
        return repo.getCoutsMensuels().map { row ->
            StatistiqueMensuelleDto(
                annee = (row[0] as Number).toInt(),
                mois = (row[1] as Number).toInt(),
                coutTotal = (row[2] as Number).toDouble()
            )
        }
    }

    override fun getEvolutionCoutsMensuelsPeriode(dateDebut: String, dateFin: String): List<StatistiqueMensuelleDto> {
        val debut = LocalDate.parse(dateDebut, DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        val fin = LocalDate.parse(dateFin, DateTimeFormatter.ofPattern("dd/MM/yyyy"))

        return repo.getCoutsMensuelsPeriode(debut, fin).map { row ->
            StatistiqueMensuelleDto(
                annee = (row[0] as Number).toInt(),
                mois = (row[1] as Number).toInt(),
                coutTotal = (row[2] as Number).toDouble()
            )
        }
    }
}