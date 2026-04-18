package com.oki.gestion_parc_backend.service.impl



import com.oki.gestion_parc_backend.dto.DeplacementDTO
import com.oki.gestion_parc_backend.dto.HistoriqueDeplacementResponseDTO
import com.oki.gestion_parc_backend.mapper.HistoriqueDeplacementMapper
import com.oki.gestion_parc_backend.model.HistoriqueDeplacement
import com.oki.gestion_parc_backend.repository.AnimalRepository
import com.oki.gestion_parc_backend.repository.BoxRepository
import com.oki.gestion_parc_backend.repository.HistoriqueDeplacementRepository
import com.oki.gestion_parc_backend.service.DeplacementService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class DeplacementServiceImpl(
    private val animalRepository: AnimalRepository,
    private val boxRepository: BoxRepository,
    private val historiqueRepository: HistoriqueDeplacementRepository
) : DeplacementService {

    @Transactional
    override fun deplacerAnimal(dto: DeplacementDTO): HistoriqueDeplacementResponseDTO {

        // 1. Récupérer l'animal
        val animal = animalRepository.findById(dto.animalId)
            .orElseThrow { IllegalArgumentException("Animal avec id ${dto.animalId} non trouvé") }

        // 2. Récupérer la nouvelle box
        val nouvelleBox = boxRepository.findById(dto.nouvelleBoxId)
            .orElseThrow { IllegalArgumentException("Box avec id ${dto.nouvelleBoxId} non trouvée") }

        // 3. Vérifier que ce n'est pas la même box
        if (animal.box.id == nouvelleBox.id) {
            throw IllegalArgumentException("L'animal est déjà dans la box ${nouvelleBox.code}")
        }

        // 4. Vérifier capacité
        val occupationActuelle = animalRepository.countByBoxAndVenduFalse(nouvelleBox)
        if (occupationActuelle >= nouvelleBox.capaciteMax) {
            throw IllegalStateException(
                "La box ${nouvelleBox.code} est pleine (${occupationActuelle}/${nouvelleBox.capaciteMax})"
            )
        }

        // 5. Sauvegarder l'historique
        val historique = HistoriqueDeplacement(
            animal = animal,
            ancienneBox = animal.box,
            nouvelleBox = nouvelleBox,
            motif = dto.motif
        )
        val savedHistorique = historiqueRepository.save(historique)

        // 6. Mettre à jour la box de l'animal
        val animalMisAJour = animal.copy(box = nouvelleBox)
        animalRepository.save(animalMisAJour)

        return HistoriqueDeplacementMapper.toResponseDTO(savedHistorique)
    }

    override fun getHistoriqueByAnimal(animalId: Long): List<HistoriqueDeplacementResponseDTO> {
        val animal = animalRepository.findById(animalId)
            .orElseThrow { IllegalArgumentException("Animal avec id $animalId non trouvé") }
        return historiqueRepository.findByAnimalOrderByDateDeplacementDesc(animal)
            .map { HistoriqueDeplacementMapper.toResponseDTO(it) }
    }

    override fun getAllHistorique(): List<HistoriqueDeplacementResponseDTO> =
        historiqueRepository.findAll()
            .sortedByDescending { it.dateDeplacement }
            .map { HistoriqueDeplacementMapper.toResponseDTO(it) }
}