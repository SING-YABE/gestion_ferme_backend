package com.oki.gestion_parc_backend.controller

import com.oki.gestion_parc_backend.dto.AlerteMiseBasDTO
import com.oki.gestion_parc_backend.dto.ReproductionDTO
import com.oki.gestion_parc_backend.dto.ReproductionResponseDTO
import com.oki.gestion_parc_backend.service.ReproductionService
import org.springframework.web.bind.annotation.*
import org.springframework.security.access.prepost.PreAuthorize

@RestController
@RequestMapping("/api/reproductions")
class ReproductionController(private val service: ReproductionService) {

    @PostMapping
    @PreAuthorize("hasAuthority('REPRODUCTION_WRITE')")
    fun creer(@RequestBody dto: ReproductionDTO): ReproductionResponseDTO = service.creerReproduction(dto)

    @GetMapping
    @PreAuthorize("hasAuthority('REPRODUCTION_READ')")
    fun getAll(): List<ReproductionResponseDTO> = service.getAllReproductions()

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('REPRODUCTION_READ')")
    fun getById(@PathVariable id: Long): ReproductionResponseDTO = service.getReproductionById(id)

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('REPRODUCTION_WRITE')")
    fun update(@PathVariable id: Long, @RequestBody dto: ReproductionDTO): ReproductionResponseDTO =
        service.updateReproduction(id, dto)

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('REPRODUCTION_DELETE')")
    fun delete(@PathVariable id: Long) = service.deleteReproduction(id)

    @GetMapping("/stats")
    @PreAuthorize("hasAuthority('REPRODUCTION_STATS')")
    fun statistiques() = service.getStatistiquesReproduction()
    @GetMapping("/alertes")
    @PreAuthorize("hasAuthority('REPRODUCTION_ALERTES')")
    fun alertesMiseBas(): List<AlerteMiseBasDTO> = service.getAlertesMiseBas()

    @GetMapping("/verrat/{code}/performances")
    @PreAuthorize("hasAuthority('REPRODUCTION_STATS')")
    fun performancesVerrat(@PathVariable code: String) =
        service.getPerformancesVerrat(code)

    @GetMapping("/truie/{code}/carriere")
    @PreAuthorize("hasAuthority('REPRODUCTION_STATS')")
    fun carriereTruie(@PathVariable code: String) =
        service.getCarriereTruie(code)

}
