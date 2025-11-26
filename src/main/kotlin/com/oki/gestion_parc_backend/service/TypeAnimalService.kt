package com.oki.gestion_parc_backend.service

import com.oki.gestion_parc_backend.dto.TypeAnimalDTO
import com.oki.gestion_parc_backend.dto.TypeAnimalResponseDTO

interface TypeAnimalService {
    fun creerTypeAnimal(dto: TypeAnimalDTO): TypeAnimalResponseDTO
    fun getAllTypes(): List<TypeAnimalResponseDTO>
    fun getTypeById(id: Long): TypeAnimalResponseDTO
    fun updateTypeAnimal(id: Long, dto: TypeAnimalDTO): TypeAnimalResponseDTO
    fun deleteTypeAnimal(id: Long)
}
