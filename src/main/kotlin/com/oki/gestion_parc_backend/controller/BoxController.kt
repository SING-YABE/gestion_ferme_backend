package com.oki.gestion_parc_backend.controller

import com.oki.gestion_parc_backend.dto.BoxDTO
import com.oki.gestion_parc_backend.service.BoxService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/boxes")
class BoxController(private val boxService: BoxService) {

    @PostMapping
    fun creerBox(@RequestBody dto: BoxDTO) =
        ResponseEntity.status(HttpStatus.CREATED).body(boxService.creerBox(dto))

    @GetMapping
    fun getAllBoxes() = ResponseEntity.ok(boxService.getAllBoxes())

    @GetMapping("/{id}")
    fun getBoxById(@PathVariable id: Long) = ResponseEntity.ok(boxService.getBoxById(id))

    @GetMapping("/batiment/{batimentId}")
    fun getBoxesByBatiment(@PathVariable batimentId: Long) =
        ResponseEntity.ok(boxService.getBoxesByBatiment(batimentId))

    @PutMapping("/{id}")
    fun updateBox(@PathVariable id: Long, @RequestBody dto: BoxDTO) =
        ResponseEntity.ok(boxService.updateBox(id, dto))

    @DeleteMapping("/{id}")
    fun deleteBox(@PathVariable id: Long): ResponseEntity<Void> {
        boxService.deleteBox(id)
        return ResponseEntity.noContent().build()
    }
}