package com.oki.gestion_parc_backend.service

import com.oki.gestion_parc_backend.dto.FournissuerDTO

interface FournisseurService {
    fun create(dto: FournissuerDTO): FournissuerDTO
    fun list(): List<FournissuerDTO>
    fun update(id: Long, dto: FournissuerDTO): FournissuerDTO
    fun delete(id: Long)
}

