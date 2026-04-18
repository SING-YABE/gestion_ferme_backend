package com.oki.gestion_parc_backend.controller
import com.oki.gestion_parc_backend.dto.DeplacementDTO
import com.oki.gestion_parc_backend.service.DeplacementService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/deplacements")
class DeplacementController(private val deplacementService: DeplacementService) {

    @PostMapping
    fun deplacerAnimal(@RequestBody dto: DeplacementDTO) =
        ResponseEntity.ok(deplacementService.deplacerAnimal(dto))

    @GetMapping
    fun getAllHistorique() = ResponseEntity.ok(deplacementService.getAllHistorique())

    @GetMapping("/animal/{animalId}")
    fun getHistoriqueByAnimal(@PathVariable animalId: Long) =
        ResponseEntity.ok(deplacementService.getHistoriqueByAnimal(animalId))
}

