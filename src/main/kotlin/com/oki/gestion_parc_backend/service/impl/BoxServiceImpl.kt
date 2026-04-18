package com.oki.gestion_parc_backend.service.impl
import com.oki.gestion_parc_backend.dto.BoxDTO
import com.oki.gestion_parc_backend.dto.BoxResponseDTO
import com.oki.gestion_parc_backend.mapper.BoxMapper
import com.oki.gestion_parc_backend.repository.AnimalRepository
import com.oki.gestion_parc_backend.repository.BatimentRepository
import com.oki.gestion_parc_backend.repository.BoxRepository
import com.oki.gestion_parc_backend.service.BoxService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class BoxServiceImpl(
    private val boxRepository: BoxRepository,
    private val batimentRepository: BatimentRepository,
    private val animalRepository: AnimalRepository
) : BoxService {

    private fun genererCode(batimentNom: String, numero: Int): String {
        val numeroFormate = numero.toString().padStart(2, '0')
        val prefixBat = batimentNom.uppercase().replace(" ", "-").take(6)
        return "$prefixBat-$numeroFormate"
    }

    @Transactional
    override fun creerBox(dto: BoxDTO): BoxResponseDTO {
        val batiment = batimentRepository.findById(dto.batimentId)
            .orElseThrow { IllegalArgumentException("Batiment avec id ${dto.batimentId} non trouvé") }

        if (boxRepository.existsByNumeroAndBatiment(dto.numero, batiment)) {
            throw IllegalArgumentException("La box numéro ${dto.numero} existe déjà dans le bâtiment ${batiment.nom}")
        }

        val code = genererCode(batiment.nom, dto.numero)
        val box = BoxMapper.toEntity(dto, batiment, code)
        val saved = boxRepository.save(box)
        val occupation = animalRepository.countByBoxAndVenduFalse(saved).toInt()
        return BoxMapper.toResponseDTO(saved, occupation)
    }

    override fun getAllBoxes(): List<BoxResponseDTO> =
        boxRepository.findAll().map { box ->
            val occupation = animalRepository.countByBoxAndVenduFalse(box).toInt()
            BoxMapper.toResponseDTO(box, occupation)
        }

    override fun getBoxById(id: Long): BoxResponseDTO {
        val box = boxRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Box avec id $id non trouvée") }
        val occupation = animalRepository.countByBoxAndVenduFalse(box).toInt()
        return BoxMapper.toResponseDTO(box, occupation)
    }

    override fun getBoxesByBatiment(batimentId: Long): List<BoxResponseDTO> {
        val batiment = batimentRepository.findById(batimentId)
            .orElseThrow { IllegalArgumentException("Batiment avec id $batimentId non trouvé") }
        return boxRepository.findByBatiment(batiment).map { box ->
            val occupation = animalRepository.countByBoxAndVenduFalse(box).toInt()
            BoxMapper.toResponseDTO(box, occupation)
        }
    }

    @Transactional
    override fun updateBox(id: Long, dto: BoxDTO): BoxResponseDTO {
        val box = boxRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Box avec id $id non trouvée") }

        val batiment = batimentRepository.findById(dto.batimentId)
            .orElseThrow { IllegalArgumentException("Batiment avec id ${dto.batimentId} non trouvé") }

        // Vérifier unicité numero/batiment seulement si changement
        if (box.numero != dto.numero || box.batiment.id != dto.batimentId) {
            if (boxRepository.existsByNumeroAndBatiment(dto.numero, batiment)) {
                throw IllegalArgumentException("La box numéro ${dto.numero} existe déjà dans le bâtiment ${batiment.nom}")
            }
        }

        val code = genererCode(batiment.nom, dto.numero)
        val updated = box.copy(numero = dto.numero, capaciteMax = dto.capaciteMax, batiment = batiment, code = code)
        val saved = boxRepository.save(updated)
        val occupation = animalRepository.countByBoxAndVenduFalse(saved).toInt()
        return BoxMapper.toResponseDTO(saved, occupation)
    }

    @Transactional
    override fun deleteBox(id: Long) {
        if (!boxRepository.existsById(id)) throw IllegalArgumentException("Box avec id $id non trouvée")
        boxRepository.deleteById(id)
    }
}