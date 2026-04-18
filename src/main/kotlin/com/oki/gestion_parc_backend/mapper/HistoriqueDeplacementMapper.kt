package com.oki.gestion_parc_backend.mapper

import com.oki.gestion_parc_backend.dto.HistoriqueDeplacementResponseDTO
import com.oki.gestion_parc_backend.model.HistoriqueDeplacement

object HistoriqueDeplacementMapper {

    fun toResponseDTO(entity: HistoriqueDeplacement) = HistoriqueDeplacementResponseDTO(
        id = entity.id,
        animalId = entity.animal.id,
        codeAnimal = entity.animal.codeAnimal,
        ancienneBoxCode = entity.ancienneBox?.code,
        nouvelleBoxCode = entity.nouvelleBox.code,
        dateDeplacement = entity.dateDeplacement,
        motif = entity.motif
        
    )
}