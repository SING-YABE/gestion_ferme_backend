package com.oki.gestion_parc_backend.controller
import com.oki.gestion_parc_backend.dto.DepenseDTO
import com.oki.gestion_parc_backend.service.DepenseService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/depense")
class DepenseController(private val service: DepenseService) {

    @PostMapping
    fun create(@RequestBody dto: DepenseDTO) = service.create(dto)

    @GetMapping
    fun list() = service.list()

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: DepenseDTO) = service.update(id, dto)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = service.delete(id)
}
