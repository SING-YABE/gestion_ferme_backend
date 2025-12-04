package com.oki.gestion_parc_backend.service

import com.oki.gestion_parc_backend.dto.SoinAnimalDTO
import com.oki.gestion_parc_backend.dto.SoinAnimalResponseDTO

interface SoinAnimalService {
    fun create(dto: SoinAnimalDTO): SoinAnimalResponseDTO

    fun getAll(): List<SoinAnimalResponseDTO>

    fun getByAnimal(codeAnimal: String): List<SoinAnimalResponseDTO>
}
