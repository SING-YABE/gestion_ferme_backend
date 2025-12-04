package com.oki.gestion_parc_backend.service

import com.oki.gestion_parc_backend.dto.AlerteMiseBasDTO
import com.oki.gestion_parc_backend.dto.ReproductionDTO
import com.oki.gestion_parc_backend.dto.ReproductionResponseDTO
import com.oki.gestion_parc_backend.dto.ReproductionStatsDTO

interface ReproductionService {
    fun creerReproduction(dto: ReproductionDTO): ReproductionResponseDTO
    fun getAllReproductions(): List<ReproductionResponseDTO>
    fun getReproductionById(id: Long): ReproductionResponseDTO
    fun updateReproduction(id: Long, dto: ReproductionDTO): ReproductionResponseDTO
    fun deleteReproduction(id: Long)
    fun getStatistiquesReproduction(): ReproductionStatsDTO
    fun getAlertesMiseBas(): List<AlerteMiseBasDTO>

}
