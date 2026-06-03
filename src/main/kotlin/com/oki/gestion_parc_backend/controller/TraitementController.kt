package com.oki.gestion_parc_backend.controller
import com.oki.gestion_parc_backend.dto.TraitementDTO
import com.oki.gestion_parc_backend.service.TraitementService
import org.springframework.web.bind.annotation.*
import org.springframework.security.access.prepost.PreAuthorize

@RestController
@RequestMapping("/api/traitement")
class TraitementController(private val service: TraitementService) {

    @PostMapping
    @PreAuthorize("hasAuthority('TRAITEMENT_WRITE')")
    fun create(@RequestBody dto: TraitementDTO) = service.create(dto)

    @GetMapping
    @PreAuthorize("hasAuthority('TRAITEMENT_READ')")
    fun list() = service.list()

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('TRAITEMENT_WRITE')")
    fun update(@PathVariable id: Long, @RequestBody dto: TraitementDTO) = service.update(id, dto)

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('TRAITEMENT_WRITE')")
    fun delete(@PathVariable id: Long) = service.delete(id)
}
