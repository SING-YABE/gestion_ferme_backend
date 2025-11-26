package com.oki.gestion_parc_backend.service

import com.oki.gestion_parc_backend.dto.AlimentationDto
import com.oki.gestion_parc_backend.dto.StatistiqueMensuelleDto

interface AlimentationService {
    fun create(dto: AlimentationDto): AlimentationDto
    fun list(): List<AlimentationDto>
    fun delete(id: Long)
    fun getEvolutionCoutsMensuels(): List<StatistiqueMensuelleDto>
    fun getEvolutionCoutsMensuelsPeriode(dateDebut: String, dateFin: String): List<StatistiqueMensuelleDto>
}