package com.oki.gestion_parc_backend.controller
import com.oki.gestion_parc_backend.dto.TraitementDTO
import com.oki.gestion_parc_backend.service.TraitementService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/traitement")
class TraitementController(private val service: TraitementService) {

    @PostMapping
    fun create(@RequestBody dto: TraitementDTO) = service.create(dto)

    @GetMapping
    fun list() = service.list()

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: TraitementDTO) = service.update(id, dto)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = service.delete(id)
}
