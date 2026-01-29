package com.oki.gestion_parc_backend.service

import com.oki.gestion_parc_backend.model.AppSettings
import org.springframework.web.multipart.MultipartFile

interface AppSettingsService {

    fun getSettings(): AppSettings

    fun updateSettings(farmName: String, contactEmail: String, contactTel: String, slogan: String): AppSettings

    fun uploadLogo(file: MultipartFile): AppSettings
}
