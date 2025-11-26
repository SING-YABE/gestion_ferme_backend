package com.oki.gestion_parc_backend.service.impl

import com.oki.gestion_parc_backend.dto.AlimentationDto
import com.oki.gestion_parc_backend.dto.StatistiqueMensuelleDto
import com.oki.gestion_parc_backend.mapper.DepenseMapper.formatter
import com.oki.gestion_parc_backend.model.Alimentation
import com.oki.gestion_parc_backend.repository.AlimentationRepository
import com.oki.gestion_parc_backend.repository.AnimalRepository
import com.oki.gestion_parc_backend.repository.FournisseurRepository
import com.oki.gestion_parc_backend.repository.TypeAlimentRepository
import com.oki.gestion_parc_backend.repository.TypeAnimalRepository
import com.oki.gestion_parc_backend.service.AlimentationService
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter


@Service
class AlimentationServiceImpl(
    private val repo: AlimentationRepository,
    private val typeAlimentRepo: TypeAlimentRepository,
    private val animalRepo: AnimalRepository,
    private val typeAnimalRepo: TypeAnimalRepository,
    private val fournisseurRepo: FournisseurRepository
) : AlimentationService {

    override fun create(dto: AlimentationDto): AlimentationDto {

        // Validation : animal OU lot
        if (dto.codeAnimal != null && dto.typeAnimalId != null) {
            throw IllegalArgumentException("Une alimentation ne peut pas cibler un animal et un lot simultanément.")
        }

        val typeAliment = typeAlimentRepo.findById(dto.typeAlimentId)
            .orElseThrow { IllegalArgumentException("Type aliment introuvable") }

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

        val entity = Alimentation(
            date = dto.date,
            typeAliment = typeAliment,
            quantiteKg = dto.quantiteKg,
            prixUnitaire = dto.prixUnitaire,
            animal = animal,
            typeAnimal = typeAnimal,
            fournisseur = fournisseur
        )

        val saved = repo.save(entity)
        return AlimentationMapper.toDto(saved)
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