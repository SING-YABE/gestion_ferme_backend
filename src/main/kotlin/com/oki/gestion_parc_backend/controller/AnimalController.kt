package com.oki.gestion_parc_backend.controller

import com.oki.gestion_parc_backend.dto.AnimalDTO
import com.oki.gestion_parc_backend.dto.AnimalResponseDTO
import com.oki.gestion_parc_backend.service.AnimalService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/animaux")
class AnimalController(private val service: AnimalService) {

    @PostMapping
    fun creer(@RequestBody dto: AnimalDTO): AnimalResponseDTO = service.creerAnimal(dto)

    @GetMapping
    fun getAll(): List<AnimalResponseDTO> = service.getAllAnimaux()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): AnimalResponseDTO = service.getAnimalById(id)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: AnimalDTO): AnimalResponseDTO =
        service.updateAnimal(id, dto)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = service.deleteAnimal(id)
}
