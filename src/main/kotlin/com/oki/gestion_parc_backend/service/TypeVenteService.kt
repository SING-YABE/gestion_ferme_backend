package com.oki.gestion_parc_backend.service

import com.oki.gestion_parc_backend.dto.BatimentDTO
import com.oki.gestion_parc_backend.dto.BatimentResponseDTO
import com.oki.gestion_parc_backend.dto.TypeVenteDTO
import com.oki.gestion_parc_backend.dto.TypeVenteResponseDTO

interface TypeVenteService {
    fun creerTypeVente(dto: TypeVenteDTO): TypeVenteResponseDTO
    fun getAllTypeVente(): List<TypeVenteResponseDTO>
    fun getTypeVenteById(id: Long): TypeVenteResponseDTO
    fun updateTypeVente(id: Long, dto: TypeVenteDTO): TypeVenteResponseDTO
    fun deleteTypeVente(id: Long)
}
