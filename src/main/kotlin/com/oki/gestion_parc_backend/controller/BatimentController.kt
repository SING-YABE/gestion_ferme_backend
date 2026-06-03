package com.oki.gestion_parc_backend.controller

import com.oki.gestion_parc_backend.dto.BatimentDTO
import com.oki.gestion_parc_backend.dto.BatimentResponseDTO
import com.oki.gestion_parc_backend.service.BatimentService
import org.springframework.web.bind.annotation.*
import org.springframework.security.access.prepost.PreAuthorize

@RestController
@RequestMapping("/api/batiments")
class BatimentController(private val service: BatimentService) {

    @PostMapping
    @PreAuthorize("hasAuthority('BATIMENT_WRITE')")
    fun creer(@RequestBody dto: BatimentDTO): BatimentResponseDTO = service.creerBatiment(dto)

    @GetMapping
    @PreAuthorize("hasAuthority('BATIMENT_READ')")
    fun getAll(): List<BatimentResponseDTO> = service.getAllBatiments()

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('BATIMENT_READ')")
    fun getById(@PathVariable id: Long): BatimentResponseDTO = service.getBatimentById(id)

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('BATIMENT_WRITE')")
    fun update(@PathVariable id: Long, @RequestBody dto: BatimentDTO): BatimentResponseDTO =
        service.updateBatiment(id, dto)

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('BATIMENT_WRITE')")
    fun delete(@PathVariable id: Long) = service.deleteBatiment(id)
}
