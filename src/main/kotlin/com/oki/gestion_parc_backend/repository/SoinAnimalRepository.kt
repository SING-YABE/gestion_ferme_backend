package com.oki.gestion_parc_backend.repository


import com.oki.gestion_parc_backend.model.SoinAnimal
import com.oki.gestion_parc_backend.model.Animal
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface SoinAnimalRepository : JpaRepository<SoinAnimal, Long> {

    @Query("select coalesce(sum(s.cout), 0) from SoinAnimal s")
    fun sumTotalSoins(): Double

    @Query("""
    select coalesce(sum(s.cout), 0)
    from SoinAnimal s 
    where s.dateSoin between :start and :end
""")
    fun sumTotalSoinsBetween(start: LocalDate, end: LocalDate): Double
    fun findByAnimal(animal: Animal): List<SoinAnimal>
    fun findByAnimalIn(animals: List<Animal>): List<SoinAnimal>
}








