package com.oki.gestion_parc_backend.mapper
import com.oki.gestion_parc_backend.dto.AnimalMapper
import com.oki.gestion_parc_backend.dto.SoinAnimalResponseDTO
import com.oki.gestion_parc_backend.model.SoinAnimal
import java.time.format.DateTimeFormatter

object SoinAnimalMapper {

    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    fun toResponseDTO(soin: SoinAnimal) = SoinAnimalResponseDTO(
        id = soin.id,
        animal = soin.animal?.let { AnimalMapper.toResponseDTO(it) },
        dateSoin = soin.dateSoin.format(formatter),
        motif = soin.motif,
        traitement = soin.traitement,
        cout = soin.cout,
        veterinaire = soin.veterinaire,
        observations = soin.observations,
        soinCollectif = soin.soinCollectif
    )
}
