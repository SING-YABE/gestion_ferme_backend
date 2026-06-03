package com.oki.gestion_parc_backend.controller

import com.oki.gestion_parc_backend.dto.PeseeCreateDto
import com.oki.gestion_parc_backend.dto.PeseeResponseDto
import com.oki.gestion_parc_backend.service.PeseeService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.security.access.prepost.PreAuthorize

/**
 * Controller REST — Pesées animales
 *
 * Base URL : /api/pesees
 *
 * Endpoints :
 *   POST   /api/pesees              → enregistrer une pesée (retourne GMQ vs pesée précédente)
 *   GET    /api/pesees/animal/{id}  → historique d'un animal (avec GMQ par intervalle)
 *   GET    /api/pesees              → toutes les pesées (admin)
 *   DELETE /api/pesees/{id}         → supprimer une pesée
 */
@RestController
@RequestMapping("/api/pesees")
class PeseeController(private val service: PeseeService) {

    /**
     * Enregistre une nouvelle pesée.
     * Le GMQ par rapport à la pesée précédente est calculé et retourné automatiquement.
     *
     * Body : { animalId, poids, datePesee (dd/MM/yyyy), observations? }
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('PESEE_WRITE')")
    fun enregistrer(@RequestBody dto: PeseeCreateDto): PeseeResponseDto =
        service.enregistrer(dto)

    /**
     * Retourne l'historique complet des pesées d'un animal, trié par date.
     * Chaque pesée inclut le GMQ calculé par rapport à la pesée précédente.
     */
    @GetMapping("/animal/{animalId}")
    @PreAuthorize("hasAuthority('PESEE_READ')")
    fun listeParAnimal(@PathVariable animalId: Long): List<PeseeResponseDto> =
        service.listeParAnimal(animalId)

    /** Toutes les pesées de l'élevage (usage admin / export) */
    @GetMapping
    @PreAuthorize("hasAuthority('PESEE_READ')")
    fun listerTout(): List<PeseeResponseDto> = service.listerTout()

    /** Supprimer une pesée (correction d'erreur de saisie) */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('PESEE_WRITE')")
    fun supprimer(@PathVariable id: Long) = service.supprimer(id)
}
