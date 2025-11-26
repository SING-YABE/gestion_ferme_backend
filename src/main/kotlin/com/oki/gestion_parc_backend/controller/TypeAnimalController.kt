package com.oki.gestion_parc_backend.controller

import com.oki.gestion_parc_backend.dto.TypeAnimalDTO
import com.oki.gestion_parc_backend.dto.TypeAnimalResponseDTO
import com.oki.gestion_parc_backend.service.TypeAnimalService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/type-animaux")
class TypeAnimalController(private val service: TypeAnimalService) {

    @PostMapping
    fun creer(@RequestBody dto: TypeAnimalDTO): TypeAnimalResponseDTO = service.creerTypeAnimal(dto)

    @GetMapping
    fun getAll(): List<TypeAnimalResponseDTO> = service.getAllTypes()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): TypeAnimalResponseDTO = service.getTypeById(id)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: TypeAnimalDTO): TypeAnimalResponseDTO =
        service.updateTypeAnimal(id, dto)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = service.deleteTypeAnimal(id)
}
