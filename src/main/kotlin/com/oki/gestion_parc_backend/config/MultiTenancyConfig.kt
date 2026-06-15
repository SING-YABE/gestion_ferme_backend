package com.oki.gestion_parc_backend.config

import com.oki.gestion_parc_backend.security.CurrentTenantIdentifierResolverImpl
import com.oki.gestion_parc_backend.security.MultiTenantConnectionProviderImpl
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Active le mode multi-tenant SCHEMA dans Hibernate via HibernatePropertiesCustomizer.
 *
 * Approche : on enrichit la configuration auto de Spring Boot sans remplacer
 * l'EntityManagerFactory — plus simple et moins risqué qu'un @Bean personnalisé.
 *
 * Propriétés injectées :
 *   - hibernate.multiTenancy               = "SCHEMA"
 *   - hibernate.tenant_identifier_resolver = CurrentTenantIdentifierResolverImpl
 *   - hibernate.multi_tenant_connection_provider = MultiTenantConnectionProviderImpl
 */
@Configuration
class MultiTenancyConfig(
    private val resolver: CurrentTenantIdentifierResolverImpl,
    private val provider: MultiTenantConnectionProviderImpl
) {

    @Bean
    fun hibernateMultiTenancyCustomizer(): HibernatePropertiesCustomizer =
        HibernatePropertiesCustomizer { props ->
            props["hibernate.multiTenancy"]                    = "SCHEMA"
            props["hibernate.tenant_identifier_resolver"]      = resolver
            props["hibernate.multi_tenant_connection_provider"] = provider
        }
}
