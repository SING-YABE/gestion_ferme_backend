package com.oki.gestion_parc_backend.controller

import com.oki.gestion_parc_backend.dto.AnimalDTO
import com.oki.gestion_parc_backend.dto.AnimalResponseDTO
import com.oki.gestion_parc_backend.service.AnimalService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/animaux")
class AnimalController(private val animalservice: AnimalService) {

    @PostMapping
    fun creer(@RequestBody dto: AnimalDTO): AnimalResponseDTO = animalservice.creerAnimal(dto)

    @GetMapping
    fun getAll(): List<AnimalResponseDTO> = animalservice.getAllAnimaux()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): AnimalResponseDTO = animalservice.getAnimalById(id)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: AnimalDTO): AnimalResponseDTO =
        animalservice.updateAnimal(id, dto)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = animalservice.deleteAnimal(id)

    @GetMapping("/count")
    fun countAllAnimals(): Long {
        return animalservice.countAllAnimals()
    }

    @GetMapping("/count-by-type")
    fun countAnimalsByType(): List<Map<String, Any>> {
        return animalservice.countAnimalsByType()
    }



}
