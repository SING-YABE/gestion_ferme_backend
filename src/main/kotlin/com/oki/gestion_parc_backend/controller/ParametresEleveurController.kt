package com.oki.gestion_parc_backend.controller

import com.oki.gestion_parc_backend.dto.ParametresEleveurDTO
import com.oki.gestion_parc_backend.model.ParametresEleveur
import com.oki.gestion_parc_backend.service.ParametresEleveurService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.security.access.prepost.PreAuthorize

@RestController
@RequestMapping("/api/parametres-eleveur")
@CrossOrigin(origins = ["*"])
class ParametresEleveurController(
    private val service: ParametresEleveurService
) {

    // SAD Python appelle ce endpoint
    @GetMapping
    @PreAuthorize("hasAuthority('PARAMETRES_ELEVEUR_READ')")
    fun getParametres(): ResponseEntity<ParametresEleveur> {
        return ResponseEntity.ok(service.getParametres())
    }

    @PostMapping
    @PreAuthorize("hasAuthority('PARAMETRES_ELEVEUR_WRITE')")
    fun saveParametres(
        @Valid @RequestBody dto: ParametresEleveurDTO
    ): ResponseEntity<ParametresEleveur> {
        return ResponseEntity.ok(service.saveParametres(dto))
    }
}