package com.oki.gestion_parc_backend.service

import com.oki.gestion_parc_backend.dto.ParametresEleveurDTO
import com.oki.gestion_parc_backend.model.ParametresEleveur

interface ParametresEleveurService {
    fun getParametres(): ParametresEleveur
    fun saveParametres(dto: ParametresEleveurDTO): ParametresEleveur
}