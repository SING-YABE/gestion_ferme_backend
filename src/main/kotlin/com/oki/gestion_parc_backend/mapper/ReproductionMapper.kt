package com.oki.gestion_parc_backend.dto

import com.oki.gestion_parc_backend.model.Reproduction
import java.time.format.DateTimeFormatter

object ReproductionMapper {

    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    fun toResponseDTO(entity: Reproduction) = ReproductionResponseDTO(
        id = entity.id,
        truie = AnimalMapper.toResponseDTO(entity.truie),
        dateSaillie = entity.dateSaillie.format(formatter),
        verrat = AnimalMapper.toResponseDTO(entity.verrat),
        dateMiseBasPrevue = entity.dateMiseBasPrevue.format(formatter),
        dateMiseBasReelle = entity.dateMiseBasReelle?.format(formatter),
        nbNesVivants = entity.nbNesVivants,
        nbMortsNes = entity.nbMortsNes,
        nbSevres = entity.nbSevres,
        observations = entity.observations
    )
}
