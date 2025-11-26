package com.oki.gestion_parc_backend.controller

import com.oki.gestion_parc_backend.dto.TypeAlimentDto
import com.oki.gestion_parc_backend.model.TypeAliment
import com.oki.gestion_parc_backend.service.TypeAlimentService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/type-aliment")
class TypeAlimentController(
    private val service: TypeAlimentService
) {

    @PostMapping
    fun create(@RequestBody dto: TypeAlimentDto): TypeAliment {
        return service.create(dto)
    }

    @GetMapping
    fun list(): List<TypeAliment> = service.list()

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: TypeAlimentDto): TypeAliment {
        return service.update(id, dto)
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = service.delete(id)
}
