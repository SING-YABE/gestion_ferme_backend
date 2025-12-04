package com.oki.gestion_parc_backend.repository
import com.oki.gestion_parc_backend.model.ChargeDiverses
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface ChargeDiversesRepository : JpaRepository<ChargeDiverses, Long>
{
    @Query("select coalesce(sum(c.montant), 0) from ChargeDiverses c")
    fun sumTotalCharges(): Double

    @Query("select coalesce(sum(c.montant), 0) from ChargeDiverses c where c.date between :start and :end")
    fun sumTotalChargesBetween(@Param("start") start: LocalDate, @Param("end") end: LocalDate): Double
//charge par type
@Query("""
        select c.typeDepense.nom, coalesce(sum(c.montant), 0)
        from ChargeDiverses c
        group by c.typeDepense.nom
    """)
fun sumMontantByType(): List<Array<Any>>
}


