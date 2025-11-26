package com.oki.gestion_parc_backend.controller

import com.oki.gestion_parc_backend.dto.BatimentDTO
import com.oki.gestion_parc_backend.dto.BatimentResponseDTO
import com.oki.gestion_parc_backend.service.BatimentService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/batiments")
class BatimentController(private val service: BatimentService) {

    @PostMapping
    fun creer(@RequestBody dto: BatimentDTO): BatimentResponseDTO = service.creerBatiment(dto)

    @GetMapping
    fun getAll(): List<BatimentResponseDTO> = service.getAllBatiments()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): BatimentResponseDTO = service.getBatimentById(id)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: BatimentDTO): BatimentResponseDTO =
        service.updateBatiment(id, dto)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = service.deleteBatiment(id)
}
