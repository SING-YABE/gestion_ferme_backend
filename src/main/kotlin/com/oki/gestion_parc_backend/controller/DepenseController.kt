package com.oki.gestion_parc_backend.controller
import com.oki.gestion_parc_backend.dto.DepenseDTO
import com.oki.gestion_parc_backend.service.DepenseService
import org.springframework.web.bind.annotation.*
import org.springframework.security.access.prepost.PreAuthorize

@RestController
@RequestMapping("/api/depense")
class DepenseController(private val service: DepenseService) {

    @PostMapping
    @PreAuthorize("hasAuthority('DEPENSE_WRITE')")
    fun create(@RequestBody dto: DepenseDTO) = service.create(dto)

    @GetMapping
    @PreAuthorize("hasAuthority('DEPENSE_READ')")
    fun list() = service.list()

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('DEPENSE_WRITE')")
    fun update(@PathVariable id: Long, @RequestBody dto: DepenseDTO) = service.update(id, dto)

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DEPENSE_DELETE')")
    fun delete(@PathVariable id: Long) = service.delete(id)
}
