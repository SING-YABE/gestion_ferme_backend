package com.oki.gestion_parc_backend.controller

import com.oki.gestion_parc_backend.dto.ChargeDiversesDto
import com.oki.gestion_parc_backend.service.ChargeDiversesService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/charges-diverses")
class ChargeDiversesController(
    private val service: ChargeDiversesService
) {

    @PostMapping
    fun create(@RequestBody dto: ChargeDiversesDto) = service.create(dto)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: ChargeDiversesDto) = service.update(id, dto)

    @GetMapping
    fun getAll() = service.getAll()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long) = service.getById(id)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = service.delete(id)
}
