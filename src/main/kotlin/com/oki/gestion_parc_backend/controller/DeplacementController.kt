package com.oki.gestion_parc_backend.controller
import com.oki.gestion_parc_backend.dto.DeplacementDTO
import com.oki.gestion_parc_backend.service.DeplacementService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.security.access.prepost.PreAuthorize

@RestController
@RequestMapping("/api/deplacements")
class DeplacementController(private val deplacementService: DeplacementService) {

    @PostMapping
    @PreAuthorize("hasAuthority('DEPLACEMENT_WRITE')")
    fun deplacerAnimal(@RequestBody dto: DeplacementDTO) =
        ResponseEntity.ok(deplacementService.deplacerAnimal(dto))

    @GetMapping
    @PreAuthorize("hasAuthority('DEPLACEMENT_READ')")
    fun getAllHistorique() = ResponseEntity.ok(deplacementService.getAllHistorique())

    @GetMapping("/animal/{animalId}")
    @PreAuthorize("hasAuthority('ANIMAL_READ')")
    fun getHistoriqueByAnimal(@PathVariable animalId: Long) =
        ResponseEntity.ok(deplacementService.getHistoriqueByAnimal(animalId))
}

