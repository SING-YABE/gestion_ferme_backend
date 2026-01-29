package com.oki.gestion_parc_backend.repository

import com.oki.gestion_parc_backend.model.AppSettings
import org.springframework.data.jpa.repository.JpaRepository

interface AppSettingsRepository : JpaRepository<AppSettings, Long> {
    fun findFirstByOrderByIdAsc(): AppSettings?
}
