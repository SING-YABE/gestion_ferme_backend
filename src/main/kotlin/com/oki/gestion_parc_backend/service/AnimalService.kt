package com.oki.gestion_parc_backend.service

import com.oki.gestion_parc_backend.dto.AnimalDTO
import com.oki.gestion_parc_backend.dto.AnimalResponseDTO

interface AnimalService {
    fun creerAnimal(dto: AnimalDTO): AnimalResponseDTO
    fun getAllAnimaux(): List<AnimalResponseDTO>
    fun getAnimalById(id: Long): AnimalResponseDTO
    fun updateAnimal(id: Long, dto: AnimalDTO): AnimalResponseDTO
    fun deleteAnimal(id: Long)
    fun countAllAnimals(): Long
    fun countAnimalsByType(): List<Map<String, Any>>
}
