package com.oki.gestion_parc_backend.controller

import com.oki.gestion_parc_backend.dto.EtatSanteDTO
import com.oki.gestion_parc_backend.dto.EtatSanteResponseDTO
import com.oki.gestion_parc_backend.service.EtatSanteService
import org.springframework.web.bind.annotation.*
import org.springframework.security.access.prepost.PreAuthorize

@RestController
@RequestMapping("/api/etat-sante")
class EtatSanteController(private val service: EtatSanteService) {

    @PostMapping
    @PreAuthorize("hasAuthority('ETAT_SANTE_WRITE')")
    fun creer(@RequestBody dto: EtatSanteDTO): EtatSanteResponseDTO = service.creerEtat(dto)

    @GetMapping
    @PreAuthorize("hasAuthority('ETAT_SANTE_READ')")
    fun getAll(): List<EtatSanteResponseDTO> = service.getAllEtats()

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ETAT_SANTE_READ')")
    fun getById(@PathVariable id: Long): EtatSanteResponseDTO = service.getEtatById(id)

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ETAT_SANTE_WRITE')")
    fun update(@PathVariable id: Long, @RequestBody dto: EtatSanteDTO): EtatSanteResponseDTO =
        service.updateEtat(id, dto)

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ETAT_SANTE_DELETE')")
    fun delete(@PathVariable id: Long) = service.deleteEtat(id)

    @GetMapping("/by-type/{typeAnimalId}")
    @PreAuthorize("hasAuthority('ETAT_SANTE_READ')")
    fun getByType(@PathVariable typeAnimalId: Long): List<EtatSanteResponseDTO> {
        return service.getEtatsByTypeAnimal(typeAnimalId)
    }
}
