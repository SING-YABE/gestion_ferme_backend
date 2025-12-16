package com.oki.gestion_parc_backend.controller


import com.oki.gestion_parc_backend.dto.IngredientDto
import com.oki.gestion_parc_backend.service.IngredientService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/ingredients")
class IngredientController(
    private val service: IngredientService
) {

    @GetMapping
    fun list(): List<IngredientDto> = service.list()

    @GetMapping("/type/{typeAlimentId}")
    fun listByType(@PathVariable typeAlimentId: Long): List<IngredientDto> =
        service.listByType(typeAlimentId)

    @PostMapping
    fun create(@RequestBody dto: IngredientDto): IngredientDto = service.create(dto)
}