package com.oki.gestion_parc_backend.controller

import com.oki.gestion_parc_backend.dto.EtatSanteDTO
import com.oki.gestion_parc_backend.dto.EtatSanteResponseDTO
import com.oki.gestion_parc_backend.service.EtatSanteService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/etat-sante")
class EtatSanteController(private val service: EtatSanteService) {

    @PostMapping
    fun creer(@RequestBody dto: EtatSanteDTO): EtatSanteResponseDTO = service.creerEtat(dto)

    @GetMapping
    fun getAll(): List<EtatSanteResponseDTO> = service.getAllEtats()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): EtatSanteResponseDTO = service.getEtatById(id)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: EtatSanteDTO): EtatSanteResponseDTO =
        service.updateEtat(id, dto)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = service.deleteEtat(id)

    @GetMapping("/by-type/{typeAnimalId}")
    fun getByType(@PathVariable typeAnimalId: Long): List<EtatSanteResponseDTO> {
        return service.getEtatsByTypeAnimal(typeAnimalId)
    }
}
