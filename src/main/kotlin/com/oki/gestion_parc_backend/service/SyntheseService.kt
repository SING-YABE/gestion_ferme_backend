package com.oki.gestion_parc_backend.service
import com.oki.gestion_parc_backend.dto.SyntheseFinanciereDTO
import java.time.LocalDate

interface SyntheseService {
    fun getSynthese(): SyntheseFinanciereDTO
    fun getSyntheseBetween(start: LocalDate, end: LocalDate): SyntheseFinanciereDTO
}
