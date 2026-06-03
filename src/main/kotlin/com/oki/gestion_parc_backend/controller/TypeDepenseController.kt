package com.oki.gestion_parc_backend.controller

import com.oki.gestion_parc_backend.dto.TypeAnimalDTO
import com.oki.gestion_parc_backend.dto.TypeAnimalResponseDTO
import com.oki.gestion_parc_backend.dto.TypeDepenseDTO
import com.oki.gestion_parc_backend.dto.TypeDepenseResponseDTO
import com.oki.gestion_parc_backend.service.TypeDepenseService
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
@RequestMapping("/api/type-depense")
class TypeDepenseController (private val service: TypeDepenseService){

    @PostMapping
    @PreAuthorize("hasAuthority('TYPE_DEPENSE_WRITE')")
    fun creer(@RequestBody dto: TypeDepenseDTO): TypeDepenseResponseDTO = service.creerTypeDepense(dto)

    @GetMapping
    @PreAuthorize("hasAuthority('TYPE_DEPENSE_READ')")
    fun getAll(): List<TypeDepenseResponseDTO> = service.getAllTypes()

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('TYPE_DEPENSE_READ')")
    fun getById(@PathVariable id: Long): TypeDepenseResponseDTO = service.getTypeById(id)

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('TYPE_DEPENSE_WRITE')")
    fun update(@PathVariable id: Long, @RequestBody dto: TypeDepenseDTO): TypeDepenseResponseDTO =
        service.updateTypeDepense(id, dto)

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('TYPE_DEPENSE_WRITE')")
    fun delete(@PathVariable id: Long) = service.deleteTypeDepense(id)
}
