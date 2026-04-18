package com.oki.gestion_parc_backend.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.nio.file.Paths

@Configuration
class WebConfig : WebMvcConfigurer {

    @Value("\${app.upload.animaux-dir}")
    private lateinit var animauxDir: String

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        // Remonter d'un niveau : animaux-dir = /Users/patrick/uploads/animaux
        // On veut servir              /Users/patrick/uploads/
        val uploadsRoot = Paths.get(animauxDir).parent.toAbsolutePath().toString()

        registry.addResourceHandler("/uploads/**")
            .addResourceLocations("file:$uploadsRoot/")
    }
}
