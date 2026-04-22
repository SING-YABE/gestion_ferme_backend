package com.oki.gestion_parc_backend.controller

import com.oki.gestion_parc_backend.dto.ParametresEleveurDTO
import com.oki.gestion_parc_backend.model.ParametresEleveur
import com.oki.gestion_parc_backend.service.ParametresEleveurService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/parametres-eleveur")
@CrossOrigin(origins = ["*"])
class ParametresEleveurController(
    private val service: ParametresEleveurService
) {

    // SAD Python appelle ce endpoint
    @GetMapping
    fun getParametres(): ResponseEntity<ParametresEleveur> {
        return ResponseEntity.ok(service.getParametres())
    }

    @PostMapping
    fun saveParametres(
        @Valid @RequestBody dto: ParametresEleveurDTO
    ): ResponseEntity<ParametresEleveur> {
        return ResponseEntity.ok(service.saveParametres(dto))
    }
}