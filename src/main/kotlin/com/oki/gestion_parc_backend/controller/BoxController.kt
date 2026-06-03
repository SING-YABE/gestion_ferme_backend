package com.oki.gestion_parc_backend.controller

import com.oki.gestion_parc_backend.dto.BoxDTO
import com.oki.gestion_parc_backend.service.BoxService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.security.access.prepost.PreAuthorize

@RestController
@RequestMapping("/api/boxes")
class BoxController(private val boxService: BoxService) {

    @PostMapping
    @PreAuthorize("hasAuthority('BOX_WRITE')")
    fun creerBox(@RequestBody dto: BoxDTO) =
        ResponseEntity.status(HttpStatus.CREATED).body(boxService.creerBox(dto))

    @GetMapping
    @PreAuthorize("hasAuthority('BOX_READ')")
    fun getAllBoxes() = ResponseEntity.ok(boxService.getAllBoxes())

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('BOX_READ')")
    fun getBoxById(@PathVariable id: Long) = ResponseEntity.ok(boxService.getBoxById(id))

    @GetMapping("/batiment/{batimentId}")
    @PreAuthorize("hasAuthority('BOX_READ')")
    fun getBoxesByBatiment(@PathVariable batimentId: Long) =
        ResponseEntity.ok(boxService.getBoxesByBatiment(batimentId))

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('BOX_WRITE')")
    fun updateBox(@PathVariable id: Long, @RequestBody dto: BoxDTO) =
        ResponseEntity.ok(boxService.updateBox(id, dto))

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('BOX_WRITE')")
    fun deleteBox(@PathVariable id: Long): ResponseEntity<Void> {
        boxService.deleteBox(id)
        return ResponseEntity.noContent().build()
    }
}