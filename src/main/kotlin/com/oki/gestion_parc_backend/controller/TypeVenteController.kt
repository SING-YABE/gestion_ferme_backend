package com.oki.gestion_parc_backend.controller

import com.oki.gestion_parc_backend.dto.BatimentDTO
import com.oki.gestion_parc_backend.dto.BatimentResponseDTO
import com.oki.gestion_parc_backend.dto.TypeVenteDTO
import com.oki.gestion_parc_backend.dto.TypeVenteResponseDTO
import com.oki.gestion_parc_backend.service.BatimentService
import com.oki.gestion_parc_backend.service.TypeVenteService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/typevente")
class TypeVenteController(private val service: TypeVenteService) {

    @PostMapping
    fun creer(@RequestBody dto: TypeVenteDTO): TypeVenteResponseDTO = service.creerTypeVente(dto)

    @GetMapping
    fun getAll(): List<TypeVenteResponseDTO> = service.getAllTypeVente()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): TypeVenteResponseDTO = service.getTypeVenteById(id)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: TypeVenteDTO): TypeVenteResponseDTO =
        service.updateTypeVente(id, dto)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = service.deleteTypeVente(id)
}
