package com.oki.gestion_parc_backend.dto
//
//data class AnimalResponseDTO(
//    val id: Long,
//    val codeAnimal: String,
//    val typeAnimal: TypeAnimalResponseDTO,
//    val dateEntree: String,
//    val poidsInitial: Double,
//    val etatSante: EtatSanteResponseDTO,
//    val batiment: BatimentResponseDTO,
//    val observations: String?,
//    val vendu: Boolean
//
//)

data class AnimalResponseDTO(
    val id: Long,
    val codeAnimal: String,
    val typeAnimalId: Long,
    val typeAnimalNom: String,
    val dateEntree: String,
    val poidsInitial: Double,
    val etatSanteId: Long,
    val etatSanteLibelle: String,
    val boxId: Long,
    val boxCode: String,
    val batimentNom: String,
    val observations: String?,
    val vendu: Boolean,
    val photoUrl: String?
)
