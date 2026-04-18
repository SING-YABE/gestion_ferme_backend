package com.oki.gestion_parc_backend.repository
import com.oki.gestion_parc_backend.model.ParametresEleveur
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ParametresEleveurRepository : JpaRepository<ParametresEleveur, Long> {
    fun findFirstBy(): Optional<ParametresEleveur>
}