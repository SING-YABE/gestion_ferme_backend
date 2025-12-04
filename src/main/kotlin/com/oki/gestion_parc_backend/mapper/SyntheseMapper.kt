package com.oki.gestion_parc_backend.mapper
import com.oki.gestion_parc_backend.dto.SyntheseFinanciereDTO

object SyntheseMapper {
    fun toDto(totalVentes: Double, totalCharges: Double): SyntheseFinanciereDTO {
        val benefice = totalVentes - totalCharges
        return SyntheseFinanciereDTO(
            totalVentes = totalVentes,
            totalCharges = totalCharges,
            beneficeNet = benefice
        )
    }
}
