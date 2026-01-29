package com.oki.gestion_parc_backend.dto

import com.oki.gestion_parc_backend.model.Animal
import java.time.format.DateTimeFormatter
object AnimalMapper {

    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    fun toResponseDTO(animal: Animal) = AnimalResponseDTO(
        id = animal.id,
        codeAnimal = animal.codeAnimal,
        typeAnimal = TypeAnimalMapper.toResponseDTO(animal.typeAnimal),
        dateEntree = animal.dateEntree.format(formatter),
        poidsInitial = animal.poidsInitial,
        etatSante = EtatSanteMapper.toResponseDTO(animal.etatSante),
        batiment = BatimentMapper.toResponseDTO(animal.batiment),
        observations = animal.observations,
        vendu = animal.vendu
    )
}

