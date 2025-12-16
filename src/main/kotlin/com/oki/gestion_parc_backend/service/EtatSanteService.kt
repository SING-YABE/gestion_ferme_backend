package com.oki.gestion_parc_backend.service

import com.oki.gestion_parc_backend.dto.EtatSanteDTO
import com.oki.gestion_parc_backend.dto.EtatSanteResponseDTO

interface EtatSanteService {
    fun creerEtat(dto: EtatSanteDTO): EtatSanteResponseDTO
    fun getAllEtats(): List<EtatSanteResponseDTO>
    fun getEtatById(id: Long): EtatSanteResponseDTO
    fun updateEtat(id: Long, dto: EtatSanteDTO): EtatSanteResponseDTO
    fun deleteEtat(id: Long)
    fun getEtatsByTypeAnimal(typeAnimalId: Long): List<EtatSanteResponseDTO>
}
