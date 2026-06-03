package com.oki.gestion_parc_backend.controller

import com.oki.gestion_parc_backend.dto.TypeAnimalDTO
import com.oki.gestion_parc_backend.dto.TypeAnimalResponseDTO
import com.oki.gestion_parc_backend.service.TypeAnimalService
import org.springframework.web.bind.annotation.*
import org.springframework.security.access.prepost.PreAuthorize

@RestController
@RequestMapping("/api/type-animaux")
class TypeAnimalController(private val service: TypeAnimalService) {

    @PostMapping
    @PreAuthorize("hasAuthority('TYPE_ANIMAL_WRITE')")
    fun creer(@RequestBody dto: TypeAnimalDTO): TypeAnimalResponseDTO = service.creerTypeAnimal(dto)

    @GetMapping
    @PreAuthorize("hasAuthority('TYPE_ANIMAL_READ')")
    fun getAll(): List<TypeAnimalResponseDTO> = service.getAllTypes()

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('TYPE_ANIMAL_READ')")
    fun getById(@PathVariable id: Long): TypeAnimalResponseDTO = service.getTypeById(id)

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('TYPE_ANIMAL_WRITE')")
    fun update(@PathVariable id: Long, @RequestBody dto: TypeAnimalDTO): TypeAnimalResponseDTO =
        service.updateTypeAnimal(id, dto)

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('TYPE_ANIMAL_WRITE')")
    fun delete(@PathVariable id: Long) = service.deleteTypeAnimal(id)
}
