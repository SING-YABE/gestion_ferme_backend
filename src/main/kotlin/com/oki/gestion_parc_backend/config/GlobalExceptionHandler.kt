package com.oki.gestion_parc_backend.config

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

/**
 * Gestionnaire global d'exceptions — intercepte les erreurs non gérées dans les controllers.
 *
 * Rôle principal : transformer les exceptions en réponses JSON lisibles
 * au lieu de laisser Spring Security renvoyer un 403 générique.
 *
 * Note : AccessDeniedException est normalement gérée par Spring Security (→ 403).
 * En la gérant ici, on récupère le message exact pour faciliter le debug.
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    /**
     * Intercepte les accès refusés et renvoie un 403 avec le message exact.
     * Permet de distinguer "token invalide" de "role manquant" de "exception inattendue".
     */
    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(ex: AccessDeniedException): ResponseEntity<Map<String, Any?>> {
        println("[GlobalExceptionHandler] ⛔ AccessDeniedException : ${ex.message}")
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            mapOf(
                "error"   to "Accès refusé",
                "detail"  to ex.message,
                "type"    to ex.javaClass.name
            )
        )
    }

    /**
     * Intercepte toutes les autres exceptions non gérées.
     * Renvoie un 500 avec le message de l'exception pour faciliter le debug en production.
     */
    @ExceptionHandler(Exception::class)
    fun handleGeneral(ex: Exception): ResponseEntity<Map<String, Any?>> {
        println("[GlobalExceptionHandler] 💥 Exception non gérée : ${ex.javaClass.name} — ${ex.message}")
        ex.printStackTrace()
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            mapOf(
                "error"  to "Erreur interne du serveur",
                "detail" to ex.message,
                "cause"  to ex.cause?.message,
                "type"   to ex.javaClass.name
            )
        )
    }
}
