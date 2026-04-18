package com.oki.gestion_parc_backend.service.impl


import com.oki.gestion_parc_backend.dto.ParametresEleveurDTO
import com.oki.gestion_parc_backend.model.ParametresEleveur
import com.oki.gestion_parc_backend.repository.ParametresEleveurRepository
import com.oki.gestion_parc_backend.service.ParametresEleveurService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ParametresEleveurServiceImpl(
    private val repository: ParametresEleveurRepository
): ParametresEleveurService {

    override fun getParametres(): ParametresEleveur {
        return repository.findFirstBy()
            .orElseThrow {
                IllegalStateException("Aucun paramètre configuré. L'éleveur doit d'abord définir ses paramètres.")
            }
    }

    @Transactional
    override fun saveParametres(dto: ParametresEleveurDTO): ParametresEleveur {
        // Si paramètres existent on met à jour, sinon on crée
        val entite = repository.findFirstBy()
            .map { existing ->
                existing.copy(
                    seuilNesVivants = dto.seuilNesVivants,
                    nbMisesBasMax = dto.nbMisesBasMax,
                    seuilOccupationBoxWarning = dto.seuilOccupationBoxWarning,
                    seuilOccupationBoxCritique = dto.seuilOccupationBoxCritique
                )
            }
            .orElseGet {
                ParametresEleveur(
                    seuilNesVivants = dto.seuilNesVivants,
                    nbMisesBasMax = dto.nbMisesBasMax,
                    seuilOccupationBoxWarning = dto.seuilOccupationBoxWarning,
                    seuilOccupationBoxCritique = dto.seuilOccupationBoxCritique
                )
            }
        return repository.save(entite)
    }
}