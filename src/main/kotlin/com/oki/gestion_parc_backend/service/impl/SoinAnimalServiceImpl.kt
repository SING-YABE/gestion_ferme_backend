package com.oki.gestion_parc_backend.service.impl

import com.oki.gestion_parc_backend.dto.SoinAnimalDTO
import com.oki.gestion_parc_backend.dto.SoinAnimalResponseDTO
import com.oki.gestion_parc_backend.mapper.SoinAnimalMapper
import com.oki.gestion_parc_backend.model.SoinAnimal
import com.oki.gestion_parc_backend.repository.AnimalRepository
import com.oki.gestion_parc_backend.repository.SoinAnimalRepository
import com.oki.gestion_parc_backend.service.SoinAnimalService
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter

import org.springframework.transaction.annotation.Transactional

@Service
class SoinAnimalServiceImpl(
    private val soinRepo: SoinAnimalRepository,
    private val animalRepo: AnimalRepository
) : SoinAnimalService {

    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    @Transactional
    override fun create(dto: SoinAnimalDTO): SoinAnimalResponseDTO {
        val isCollectif = dto.applyToAll || (dto.animalCodes != null && dto.animalCodes.isNotEmpty())

        val date = LocalDate.parse(dto.dateSoin, formatter)
        val total = dto.cout + dto.coutMedicament

        if (!isCollectif) {
            // individuel
            val animal = animalRepo.findByCodeAnimal(dto.codeAnimal!!)
                .orElseThrow { IllegalArgumentException("Animal introuvable : ${dto.codeAnimal}") }

            val soin = SoinAnimal(
                animal = animal,
                dateSoin = date,
                motif = dto.motif,
                traitement = dto.traitement,
                traitementApporte = dto.traitementApporte,
                cout = dto.cout,
                coutMedicament = dto.coutMedicament,
                totalPrestation = total,
                veterinaire = dto.veterinaire,
                observations = dto.observations,
                soinCollectif = false
            )
            return SoinAnimalMapper.toResponseDTO(soinRepo.save(soin))
        } else {
            // collectif
            val animals = if (dto.applyToAll) {
                animalRepo.findAll()
            } else {
                dto.animalCodes!!.map { code ->
                    animalRepo.findByCodeAnimal(code).orElseThrow {
                        IllegalArgumentException("Animal introuvable : $code")
                    }
                }
            }

            val soins = animals.map { animal ->
                SoinAnimal(
                    animal = animal,
                    dateSoin = date,
                    motif = dto.motif,
                    traitement = dto.traitement,
                    traitementApporte = dto.traitementApporte,
                    cout = dto.cout,
                    coutMedicament = dto.coutMedicament,
                    totalPrestation = total,
                    veterinaire = dto.veterinaire,
                    observations = dto.observations,
                    soinCollectif = true
                )
            }

            val saved = soinRepo.saveAll(soins)
            // Retourne le premier pour répondre (ou adapter selon besoin)
            return SoinAnimalMapper.toResponseDTO(saved.first())
        }
    }

    override fun getAll(): List<SoinAnimalResponseDTO> =
        soinRepo.findAll().map { SoinAnimalMapper.toResponseDTO(it) }

    override fun getByAnimal(codeAnimal: String): List<SoinAnimalResponseDTO> {
        val animal = animalRepo.findByCodeAnimal(codeAnimal)
            .orElseThrow { IllegalArgumentException("Animal introuvable : $codeAnimal") }
        return soinRepo.findByAnimal(animal).map { SoinAnimalMapper.toResponseDTO(it) }
    }

    @Transactional
    fun update(id: Long, dto: SoinAnimalDTO): SoinAnimalResponseDTO {
        val existing = soinRepo.findById(id).orElseThrow { IllegalArgumentException("Soin introuvable: $id") }
        val date = LocalDate.parse(dto.dateSoin, formatter)
        val total = dto.cout + dto.coutMedicament

        // For update: if the Soin is collectif we update the record; we don't re-duplicate animals here.
        val animal = if (!dto.codeAnimal.isNullOrBlank())
            animalRepo.findByCodeAnimal(dto.codeAnimal).orElse(null)
        else existing.animal

        val updated = existing.copy(
            animal = animal,
            dateSoin = date,
            motif = dto.motif,
            traitement = dto.traitement,
            traitementApporte = dto.traitementApporte,
            cout = dto.cout,
            coutMedicament = dto.coutMedicament,
            totalPrestation = total,
            veterinaire = dto.veterinaire,
            observations = dto.observations,
            soinCollectif = dto.applyToAll || (dto.animalCodes != null && dto.animalCodes.isNotEmpty())
        )

        return SoinAnimalMapper.toResponseDTO(soinRepo.save(updated))
    }
}
