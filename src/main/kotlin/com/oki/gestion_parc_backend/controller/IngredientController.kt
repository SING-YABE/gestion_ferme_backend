package com.oki.gestion_parc_backend.controller


import com.oki.gestion_parc_backend.dto.IngredientDto
import com.oki.gestion_parc_backend.service.IngredientService
import org.springframework.web.bind.annotation.*
import org.springframework.security.access.prepost.PreAuthorize

@RestController
@RequestMapping("/api/ingredients")
class IngredientController(
    private val service: IngredientService
) {

    @GetMapping
    @PreAuthorize("hasAuthority('INGREDIENT_READ')")
    fun list(): List<IngredientDto> = service.list()

    @GetMapping("/type/{typeAlimentId}")
    @PreAuthorize("hasAuthority('INGREDIENT_READ')")
    fun listByType(@PathVariable typeAlimentId: Long): List<IngredientDto> =
        service.listByType(typeAlimentId)

    @PostMapping
    @PreAuthorize("hasAuthority('INGREDIENT_WRITE')")
    fun create(@RequestBody dto: IngredientDto): IngredientDto = service.create(dto)
}