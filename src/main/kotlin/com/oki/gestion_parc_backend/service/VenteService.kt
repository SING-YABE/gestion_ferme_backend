package com.oki.gestion_parc_backend.service

import com.oki.gestion_parc_backend.dto.VenteCreateDTO
import com.oki.gestion_parc_backend.dto.VenteDetailResponseDTO
import com.oki.gestion_parc_backend.dto.VentesEvolutionDTO
import com.oki.gestion_parc_backend.model.Vente

interface VenteService {
    fun creerVente(dto: VenteCreateDTO): VenteDetailResponseDTO
    fun getAllVentes(): List<VenteDetailResponseDTO>
    fun getVenteById(id: Long): VenteDetailResponseDTO
    fun deleteVente(id: Long)
    fun getVenteEntityById(id: Long): Vente
}