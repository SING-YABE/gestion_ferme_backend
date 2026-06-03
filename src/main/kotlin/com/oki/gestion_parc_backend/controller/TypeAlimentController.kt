package com.oki.gestion_parc_backend.controller

import com.oki.gestion_parc_backend.dto.TypeAlimentDto
import com.oki.gestion_parc_backend.model.TypeAliment
import com.oki.gestion_parc_backend.service.TypeAlimentService
import org.springframework.web.bind.annotation.*
import org.springframework.security.access.prepost.PreAuthorize

@RestController
@RequestMapping("/api/type-aliment")
class TypeAlimentController(
    private val service: TypeAlimentService
) {

    @PostMapping
    @PreAuthorize("hasAuthority('TYPE_ALIMENT_WRITE')")
    fun create(@RequestBody dto: TypeAlimentDto): TypeAliment {
        return service.create(dto)
    }

    @GetMapping
    @PreAuthorize("hasAuthority('TYPE_ALIMENT_READ')")
    fun list(): List<TypeAliment> = service.list()

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('TYPE_ALIMENT_WRITE')")
    fun update(@PathVariable id: Long, @RequestBody dto: TypeAlimentDto): TypeAliment {
        return service.update(id, dto)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('TYPE_ALIMENT_WRITE')")
    fun delete(@PathVariable id: Long) = service.delete(id)
}
