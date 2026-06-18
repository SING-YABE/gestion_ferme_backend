package com.oki.gestion_parc_backend.config

import org.springframework.boot.web.client.RestTemplateBuilder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate
import java.time.Duration

/**
 * Configuration du RestTemplate utilisé par C0de4hopeService.
 *
 * Timeout de 10 secondes en connexion et lecture pour éviter
 * de bloquer un thread en cas d'indisponibilité de l'API c0de4hope.
 */
@Configuration
class RestTemplateConfig {

    @Bean
    fun restTemplate(builder: RestTemplateBuilder): RestTemplate =
        builder
            .connectTimeout(Duration.ofSeconds(10))
            .readTimeout(Duration.ofSeconds(10))
            .build()
}
