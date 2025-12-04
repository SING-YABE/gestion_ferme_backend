package com.oki.gestion_parc_backend.controller
import com.oki.gestion_parc_backend.dto.SoinAnimalDTO
import com.oki.gestion_parc_backend.service.SoinAnimalService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/soins")
class SoinAnimalController(
    private val soinService: SoinAnimalService
) {

    @PostMapping
    fun create(@Valid @RequestBody dto: SoinAnimalDTO) =
        soinService.create(dto)

    @GetMapping
    fun getAll() = soinService.getAll()

    @GetMapping("/{codeAnimal}")
    fun getByAnimal(@PathVariable codeAnimal: String) =
        soinService.getByAnimal(codeAnimal)
}
