package com.oki.gestion_parc_backend.repository

import com.oki.gestion_parc_backend.model.PreuveTache
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PreuveTacheRepository : JpaRepository<PreuveTache, Long> {
    fun findByAssignationId(assignationId: Long): List<PreuveTache>
}
