package com.oki.gestion_parc_backend.controller
import com.oki.gestion_parc_backend.dto.SoinAnimalDTO
import com.oki.gestion_parc_backend.service.SoinAnimalService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

import com.oki.gestion_parc_backend.dto.SoinAnimalResponseDTO
import org.springframework.http.ResponseEntity

@RestController
@RequestMapping("/api/soins")
class SoinAnimalController(private val service: SoinAnimalService) {

    @PostMapping
    fun create(@RequestBody dto: SoinAnimalDTO): ResponseEntity<SoinAnimalResponseDTO> {
        val created = service.create(dto)
        return ResponseEntity.ok(created)
    }

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: SoinAnimalDTO): ResponseEntity<SoinAnimalResponseDTO> {
        val updated = (service as? com.oki.gestion_parc_backend.service.impl.SoinAnimalServiceImpl)
            ?.update(id, dto) ?: throw IllegalStateException("Update not supported")
        return ResponseEntity.ok(updated)
    }

    @GetMapping
    fun getAll(): ResponseEntity<List<SoinAnimalResponseDTO>> = ResponseEntity.ok(service.getAll())

    @GetMapping("/animal/{code}")
    fun getByAnimal(@PathVariable code: String): ResponseEntity<List<SoinAnimalResponseDTO>> =
        ResponseEntity.ok(service.getByAnimal(code))
}






