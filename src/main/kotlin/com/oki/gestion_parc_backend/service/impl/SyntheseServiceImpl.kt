package com.oki.gestion_parc_backend.service.impl
import com.oki.gestion_parc_backend.dto.SyntheseFinanciereDTO
import com.oki.gestion_parc_backend.mapper.SyntheseMapper
import com.oki.gestion_parc_backend.repository.AlimentationRepository
import com.oki.gestion_parc_backend.repository.ChargeDiversesRepository
import com.oki.gestion_parc_backend.repository.DepenseRepository
import com.oki.gestion_parc_backend.repository.SoinAnimalRepository
import com.oki.gestion_parc_backend.repository.TraitementRepository
import com.oki.gestion_parc_backend.repository.VenteRepository
import com.oki.gestion_parc_backend.service.SyntheseService
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class SyntheseServiceImpl(
    private val venteRepository: VenteRepository,
    private val chargeRepository: ChargeDiversesRepository,
    private val alimentationRepository: AlimentationRepository,
    private val traitementRepository: TraitementRepository,
    private val soinAnimalRepository: SoinAnimalRepository,
    private val depenseRepository: DepenseRepository

) : SyntheseService {

//    override fun getSynthese(): SyntheseFinanciereDTO {
//        val totalVentes = venteRepository.sumTotalVentes()
//        val totalCharges = chargeRepository.sumTotalCharges()
//        return SyntheseMapper.toDto(totalVentes, totalCharges)
//    }

    override fun getSynthese(): SyntheseFinanciereDTO {

        val totalVentes = venteRepository.sumTotalVentes()

        val chargesDiverses = chargeRepository.sumTotalCharges()
        val alimentation = alimentationRepository.sumTotalAlimentation()
        val soins = soinAnimalRepository.sumTotalSoins()
        val depenses = depenseRepository.sumTotalDepenses()

        val totalCharges = chargesDiverses + alimentation  + soins + depenses

        return SyntheseMapper.toDto(totalVentes, totalCharges)
    }

    override fun getSyntheseBetween(start: LocalDate, end: LocalDate): SyntheseFinanciereDTO {

        val totalVentes = venteRepository.sumTotalVentesBetween(start, end)

        val chargesDiverses = chargeRepository.sumTotalChargesBetween(start, end)
        val alimentation = alimentationRepository.sumTotalAlimentationBetween(start, end)
        val soins = soinAnimalRepository.sumTotalSoinsBetween(start, end)
        val depenses = depenseRepository.sumTotalDepensesBetween(start, end)
        val totalCharges = chargesDiverses + alimentation  + soins + depenses

        return SyntheseMapper.toDto(totalVentes, totalCharges)
    }

}












