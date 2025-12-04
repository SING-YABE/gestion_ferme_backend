package com.oki.gestion_parc_backend.service.impl
import com.oki.gestion_parc_backend.dto.SyntheseFinanciereDTO
import com.oki.gestion_parc_backend.mapper.SyntheseMapper
import com.oki.gestion_parc_backend.repository.ChargeDiversesRepository
import com.oki.gestion_parc_backend.repository.VenteRepository
import com.oki.gestion_parc_backend.service.SyntheseService
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class SyntheseServiceImpl(
    private val venteRepository: VenteRepository,
    private val chargeRepository: ChargeDiversesRepository
) : SyntheseService {

    override fun getSynthese(): SyntheseFinanciereDTO {
        val totalVentes = venteRepository.sumTotalVentes()
        val totalCharges = chargeRepository.sumTotalCharges()
        return SyntheseMapper.toDto(totalVentes, totalCharges)
    }

    override fun getSyntheseBetween(start: LocalDate, end: LocalDate): SyntheseFinanciereDTO {
        val totalVentes = venteRepository.sumTotalVentesBetween(start, end)
        val totalCharges = chargeRepository.sumTotalChargesBetween(start, end)
        return SyntheseMapper.toDto(totalVentes, totalCharges)
    }
}
