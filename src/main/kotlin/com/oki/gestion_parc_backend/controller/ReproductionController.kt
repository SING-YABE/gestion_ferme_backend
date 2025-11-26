package com.oki.gestion_parc_backend.controller

import com.oki.gestion_parc_backend.dto.ReproductionDTO
import com.oki.gestion_parc_backend.dto.ReproductionResponseDTO
import com.oki.gestion_parc_backend.service.ReproductionService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/reproductions")
class ReproductionController(private val service: ReproductionService) {

    @PostMapping
    fun creer(@RequestBody dto: ReproductionDTO): ReproductionResponseDTO = service.creerReproduction(dto)

    @GetMapping
    fun getAll(): List<ReproductionResponseDTO> = service.getAllReproductions()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ReproductionResponseDTO = service.getReproductionById(id)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: ReproductionDTO): ReproductionResponseDTO =
        service.updateReproduction(id, dto)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = service.deleteReproduction(id)
}
