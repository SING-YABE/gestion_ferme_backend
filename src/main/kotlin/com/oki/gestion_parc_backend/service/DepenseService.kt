package com.oki.gestion_parc_backend.service
import com.oki.gestion_parc_backend.dto.DepenseDTO

interface DepenseService {
    fun create(dto: DepenseDTO): DepenseDTO
    fun list(): List<DepenseDTO>
    fun update(id: Long, dto: DepenseDTO): DepenseDTO
    fun delete(id: Long)
}
