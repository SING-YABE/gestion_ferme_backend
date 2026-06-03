package com.oki.gestion_parc_backend.controller

import com.oki.gestion_parc_backend.dto.ChargeDiversesDto
import com.oki.gestion_parc_backend.service.ChargeDiversesService
import org.springframework.web.bind.annotation.*
import org.springframework.security.access.prepost.PreAuthorize

@RestController
@RequestMapping("/api/charges-diverses")
class ChargeDiversesController(
    private val service: ChargeDiversesService
) {

    @PostMapping
    @PreAuthorize("hasAuthority('CHARGE_WRITE')")
    fun create(@RequestBody dto: ChargeDiversesDto) = service.create(dto)

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('CHARGE_WRITE')")
    fun update(@PathVariable id: Long, @RequestBody dto: ChargeDiversesDto) = service.update(id, dto)

    @GetMapping
    @PreAuthorize("hasAuthority('CHARGE_READ')")
    fun getAll() = service.getAll()

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('CHARGE_READ')")
    fun getById(@PathVariable id: Long) = service.getById(id)

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('CHARGE_WRITE')")
    fun delete(@PathVariable id: Long) = service.delete(id)

    @GetMapping("/pourcentage-par-type")
    @PreAuthorize("hasAuthority('CHARGE_STATS')")
    fun getPourcentageParType() = service.getPourcentageParType()
}


