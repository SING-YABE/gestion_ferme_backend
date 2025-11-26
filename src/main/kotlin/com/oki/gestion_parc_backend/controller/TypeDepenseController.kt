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

@RestController
@RequestMapping("/api/type-depense")
class TypeDepenseController (private val service: TypeDepenseService){

    @PostMapping
    fun creer(@RequestBody dto: TypeDepenseDTO): TypeDepenseResponseDTO = service.creerTypeDepense(dto)

    @GetMapping
    fun getAll(): List<TypeDepenseResponseDTO> = service.getAllTypes()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): TypeDepenseResponseDTO = service.getTypeById(id)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: TypeDepenseDTO): TypeDepenseResponseDTO =
        service.updateTypeDepense(id, dto)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = service.deleteTypeDepense(id)
}
