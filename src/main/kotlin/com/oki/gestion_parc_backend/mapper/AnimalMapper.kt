package com.oki.gestion_parc_backend.dto

import com.oki.gestion_parc_backend.model.Animal
import java.time.format.DateTimeFormatter

object AnimalMapper {

    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    fun toResponseDTO(entity: Animal) = AnimalResponseDTO(
        id = entity.id,
        codeAnimal = entity.codeAnimal,
        typeAnimalId = entity.typeAnimal.id,
        typeAnimalNom = entity.typeAnimal.nom,
        dateEntree = entity.dateEntree.toString(),
        poidsInitial = entity.poidsInitial,
        etatSanteId = entity.etatSante.id,
        etatSanteLibelle = entity.etatSante.description,
        boxId = entity.box.id,
        boxCode = entity.box.code,
        batimentNom = entity.box.batiment.nom,
        observations = entity.observations,
        vendu = entity.vendu,
        photoUrl = entity.photoUrl
    )
}