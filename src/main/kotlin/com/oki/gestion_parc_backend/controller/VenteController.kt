package com.oki.gestion_parc_backend.controller
import com.oki.gestion_parc_backend.dto.VenteDTO
import com.oki.gestion_parc_backend.dto.VenteResponseDTO
import com.oki.gestion_parc_backend.service.VenteService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/ventes")
@CrossOrigin(origins = ["*"])
class VenteController(
    private val service: VenteService
) {

    @PostMapping
    fun create(@RequestBody dto: VenteDTO): VenteResponseDTO =
        service.create(dto)

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody dto: VenteDTO): VenteResponseDTO =
        service.update(id, dto)

    @GetMapping
    fun getAll(): List<VenteResponseDTO> =
        service.getAll()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): VenteResponseDTO =
        service.getById(id)

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long) =
        service.delete(id)

    @GetMapping("/evolution")
    fun evolutionMensuelle() = service.evolutionMensuelle()

}




