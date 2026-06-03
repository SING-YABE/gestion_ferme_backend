package com.oki.gestion_parc_backend.controller

import com.oki.gestion_parc_backend.dto.UpdateSettingsDto
import com.oki.gestion_parc_backend.model.AppSettings
import com.oki.gestion_parc_backend.service.AppSettingsService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.security.access.prepost.PreAuthorize

@RestController
@RequestMapping("/api/settings")
class AppSettingsController(
    private val appSettingsService: AppSettingsService
) {

    @GetMapping
    @PreAuthorize("hasAuthority('SETTINGS_READ')")
    fun getSettings(): ResponseEntity<AppSettings> =
        ResponseEntity.ok(appSettingsService.getSettings())

    @PutMapping
    @PreAuthorize("hasAuthority('SETTINGS_WRITE')")
    fun updateSettings(
        @RequestBody dto: UpdateSettingsDto
    ): ResponseEntity<AppSettings> =
        ResponseEntity.ok(
            appSettingsService.updateSettings(dto.farmName, dto.contactEmail, dto.contactTel,dto.slogan)
        )

    @PostMapping("/logo")
    @PreAuthorize("hasAuthority('SETTINGS_LOGO')")
    fun uploadLogo(
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<AppSettings> {
        val settings = appSettingsService.uploadLogo(file)
        return ResponseEntity.ok(settings)
    }

}
