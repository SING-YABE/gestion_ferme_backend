package com.oki.gestion_parc_backend.service

import com.oki.gestion_parc_backend.dto.TypeAnimalDTO
import com.oki.gestion_parc_backend.dto.TypeAnimalResponseDTO
import com.oki.gestion_parc_backend.dto.TypeDepenseDTO
import com.oki.gestion_parc_backend.dto.TypeDepenseResponseDTO

interface TypeDepenseService {
    fun creerTypeDepense(dto: TypeDepenseDTO): TypeDepenseResponseDTO
    fun getAllTypes(): List<TypeDepenseResponseDTO>
    fun getTypeById(id: Long): TypeDepenseResponseDTO
    fun updateTypeDepense(id: Long, dto: TypeDepenseDTO): TypeDepenseResponseDTO
    fun deleteTypeDepense(id: Long)
}