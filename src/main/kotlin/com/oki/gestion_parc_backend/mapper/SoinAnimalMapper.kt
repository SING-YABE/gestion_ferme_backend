package com.oki.gestion_parc_backend.mapper
import com.oki.gestion_parc_backend.dto.SoinAnimalResponseDTO
import com.oki.gestion_parc_backend.model.SoinAnimal
import java.time.format.DateTimeFormatter

object SoinAnimalMapper {
    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    fun toResponseDTO(soin: SoinAnimal): SoinAnimalResponseDTO = SoinAnimalResponseDTO(
        id = soin.id,
        animalCode = soin.animal?.codeAnimal,
        dateSoin = soin.dateSoin.format(formatter),
        motif = soin.motif,
        traitement = soin.traitement,
        traitementApporte = soin.traitementApporte,
        cout = soin.cout,
        coutMedicament = soin.coutMedicament,
        totalPrestation = soin.totalPrestation,
        veterinaire = soin.veterinaire,
        observations = soin.observations,
        soinCollectif = soin.soinCollectif
    )
}
