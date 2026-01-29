package com.oki.gestion_parc_backend.controller

import com.oki.gestion_parc_backend.dto.UpdateSettingsDto
import com.oki.gestion_parc_backend.model.AppSettings
import com.oki.gestion_parc_backend.service.AppSettingsService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/settings")
class AppSettingsController(
    private val appSettingsService: AppSettingsService
) {

    @GetMapping
    fun getSettings(): ResponseEntity<AppSettings> =
        ResponseEntity.ok(appSettingsService.getSettings())

    @PutMapping
    fun updateSettings(
        @RequestBody dto: UpdateSettingsDto
    ): ResponseEntity<AppSettings> =
        ResponseEntity.ok(
            appSettingsService.updateSettings(dto.farmName, dto.contactEmail, dto.contactTel,dto.slogan)
        )

    @PostMapping("/logo")
    fun uploadLogo(
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<AppSettings> {
        val settings = appSettingsService.uploadLogo(file)
        return ResponseEntity.ok(settings)
    }

}
