package com.oki.gestion_parc_backend.controller

import com.oki.gestion_parc_backend.dto.BatimentDTO
import com.oki.gestion_parc_backend.dto.BatimentResponseDTO
import com.oki.gestion_parc_backend.dto.TypeVenteDTO
import com.oki.gestion_parc_backend.dto.TypeVenteResponseDTO
import com.oki.gestion_parc_backend.service.BatimentService
import com.oki.gestion_parc_backend.service.TypeVenteService
import org.springframework.web.bind.annotation.*
import org.springframework.security.access.prepost.PreAuthorize

@RestController
@RequestMapping("/api/typevente")
class TypeVenteController(private val service: TypeVenteService) {

    @PostMapping
    @PreAuthorize("hasAuthority('TYPE_VENTE_WRITE')")
    fun creer(@RequestBody dto: TypeVenteDTO): TypeVenteResponseDTO = service.creerTypeVente(dto)

    @GetMapping
    @PreAuthorize("hasAuthority('TYPE_VENTE_READ')")
    fun getAll(): List<TypeVenteResponseDTO> = service.getAllTypeVente()

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('TYPE_VENTE_READ')")
    fun getById(@PathVariable id: Long): TypeVenteResponseDTO = service.getTypeVenteById(id)

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('TYPE_VENTE_WRITE')")
    fun update(@PathVariable id: Long, @RequestBody dto: TypeVenteDTO): TypeVenteResponseDTO =
        service.updateTypeVente(id, dto)

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('TYPE_VENTE_WRITE')")
    fun delete(@PathVariable id: Long) = service.deleteTypeVente(id)
}
