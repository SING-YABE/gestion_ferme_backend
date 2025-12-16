package com.oki.gestion_parc_backend.repository
import com.oki.gestion_parc_backend.model.Depense
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface DepenseRepository : JpaRepository<Depense, Long>{
    @Query("select coalesce(sum(d.montant), 0) from Depense d")
    fun sumTotalDepenses(): Double

    @Query("""
    select coalesce(sum(d.montant), 0)
    from Depense d 
    where d.date between :start and :end
""")
    fun sumTotalDepensesBetween(start: LocalDate, end: LocalDate): Double

}
