package com.oki.gestion_parc_backend.service.impl

import com.oki.gestion_parc_backend.model.AppSettings
import com.oki.gestion_parc_backend.repository.AppSettingsRepository
import com.oki.gestion_parc_backend.service.AppSettingsService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File

@Service
class AppSettingsServiceImpl(
    private val repository: AppSettingsRepository,
    @Value("\${app.upload.logo-dir}") private val logoDir: String
) : AppSettingsService {

    override fun getSettings(): AppSettings {
        return repository.findFirstByOrderByIdAsc()
            ?: repository.save(
                AppSettings(
                    farmName = "ma ferme",
                    contactEmail = "contact@ferme.bf",
                    contactTel = "65388398",
                    slogan = "Test"
                )
            )
    }

    override fun updateSettings(farmName: String, contactEmail: String, contactTel: String, slogan: String): AppSettings {
        val settings = getSettings()
        settings.farmName = farmName
        settings.contactEmail = contactEmail
        settings.contactTel=contactTel
        settings.slogan=slogan
        return repository.save(settings)
    }
    override fun uploadLogo(file: MultipartFile): AppSettings {
        val settings = getSettings()

        // 1️⃣ Créer le dossier s'il n'existe pas
        val dir = File(logoDir)
        if (!dir.exists()) dir.mkdirs()

        // 2️⃣ Sécuriser le nom du fichier
        val safeName = file.originalFilename
            ?.replace("\\s+".toRegex(), "_")            // espaces → _
            ?.replace("[^a-zA-Z0-9._-]".toRegex(), "")  // caractères spéciaux
            ?: "logo.jpg"

        // 3️⃣ Nom final unique
        val fileName = "logo_${System.currentTimeMillis()}_$safeName"

        // 4️⃣ Sauvegarde du fichier
        val dest = File(dir, fileName)
        file.transferTo(dest)

        // 5️⃣ Sauvegarde en base (on stocke juste le nom)
        settings.logoPath = fileName

        return repository.save(settings)
    }

}
