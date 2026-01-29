package com.oki.gestion_parc_backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling

class GestionParcBackendApplication

fun main(args: Array<String>) {
	runApplication<GestionParcBackendApplication>(*args)
}


