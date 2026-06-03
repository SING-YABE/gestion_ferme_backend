package com.oki.gestion_parc_backend.controller

import com.oki.gestion_parc_backend.service.VenteService
import org.springframework.web.bind.annotation.*

import com.oki.gestion_parc_backend.dto.VenteCreateDTO
import com.oki.gestion_parc_backend.dto.VenteDetailResponseDTO
import com.oki.gestion_parc_backend.service.FacturePdfService
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.security.access.prepost.PreAuthorize

@RestController
@RequestMapping("/api/ventes")
class VenteController(
    private val venteService: VenteService,
    private val facturePdfService: FacturePdfService
) {

    @PostMapping
    @PreAuthorize("hasAuthority('VENTE_WRITE')")
    fun creerVente(@RequestBody dto: VenteCreateDTO): VenteDetailResponseDTO {
        return venteService.creerVente(dto)
    }

    @GetMapping
    @PreAuthorize("hasAuthority('VENTE_READ')")
    fun getAllVentes(): List<VenteDetailResponseDTO> {
        return venteService.getAllVentes()
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('VENTE_READ')")
    fun getVenteById(@PathVariable id: Long): VenteDetailResponseDTO {
        return venteService.getVenteById(id)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('VENTE_DELETE')")
    fun deleteVente(@PathVariable id: Long) {
        venteService.deleteVente(id)
    }

    @GetMapping("/{id}/facture")
    @PreAuthorize("hasAuthority('FACTURE_READ')")
    fun genererFacture(@PathVariable id: Long): ResponseEntity<ByteArray> {
        val vente = venteService.getVenteEntityById(id)
        val pdfBytes = facturePdfService.genererFacturePdf(vente)

        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_PDF
        headers.setContentDispositionFormData("inline", "facture_${id}.pdf")

        return ResponseEntity.ok()
            .headers(headers)
            .body(pdfBytes)
    }
}
