package com.oki.gestion_parc_backend.controller
import com.oki.gestion_parc_backend.dto.FournissuerDTO
import com.oki.gestion_parc_backend.service.FournisseurService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.security.access.prepost.PreAuthorize

@RestController
@RequestMapping("/api/fournisseur")
class FournisseurController(
    private val service: FournisseurService
) {

    @PostMapping
    @PreAuthorize("hasAuthority('FOURNISSEUR_WRITE')")
    fun create(@RequestBody dto: FournissuerDTO) = service.create(dto)

    @GetMapping
    @PreAuthorize("hasAuthority('FOURNISSEUR_READ')")
    fun list() = service.list()

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('FOURNISSEUR_WRITE')")
    fun update(@PathVariable id: Long, @RequestBody dto: FournissuerDTO) = service.update(id, dto)

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('FOURNISSEUR_DELETE')")
    fun delete(@PathVariable id: Long) = service.delete(id)
}
