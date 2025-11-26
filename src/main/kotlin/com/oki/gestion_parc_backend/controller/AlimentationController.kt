package com.oki.gestion_parc_backend.controller


import com.oki.gestion_parc_backend.dto.AlimentationDto
import com.oki.gestion_parc_backend.service.AlimentationService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/alimentations")
class AlimentationController(
    private val service: AlimentationService
) {

    @PostMapping
    fun create(@RequestBody dto: AlimentationDto) = service.create(dto)

    @GetMapping
    fun list() = service.list()

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) = service.delete(id)
    @GetMapping("/statistiques/couts-mensuels")
    fun getEvolutionCoutsMensuels() = service.getEvolutionCoutsMensuels()

    @GetMapping("/statistiques/couts-mensuels-periode")
    fun getEvolutionCoutsMensuelsPeriode(
        @RequestParam dateDebut: String,
        @RequestParam dateFin: String
    ) = service.getEvolutionCoutsMensuelsPeriode(dateDebut, dateFin)
}