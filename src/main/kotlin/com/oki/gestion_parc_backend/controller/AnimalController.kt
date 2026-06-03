package com.oki.gestion_parc_backend.controller

import com.oki.gestion_parc_backend.dto.AnimalDTO
import com.oki.gestion_parc_backend.dto.AnimalResponseDTO
import com.oki.gestion_parc_backend.repository.AnimalRepository
import com.oki.gestion_parc_backend.service.AnimalService
import org.springframework.beans.factory.annotation.Value
import org.springframework.web.bind.annotation.*
import org.springframework.http.MediaType
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import org.springframework.security.access.prepost.PreAuthorize
@RestController
@RequestMapping("/api/animaux")
class AnimalController(
    private val animalservice: AnimalService,
    private val animalRepository: AnimalRepository
) {
    // Chemin absolu configurable pour les uploads des photos d'animaux
    @Value("\${app.upload.animaux-dir}")
    private lateinit var animauxUploadDir: String

    @PostMapping
    @PreAuthorize("hasAuthority('ANIMAL_WRITE')")
    fun creer(@RequestBody dto: AnimalDTO): AnimalResponseDTO = animalservice.creerAnimal(dto)

    @GetMapping
    @PreAuthorize("hasAuthority('ANIMAL_READ')")
    fun getAll(): List<AnimalResponseDTO> = animalservice.getAllAnimaux()

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ANIMAL_READ')")
    fun getById(@PathVariable id: Long): AnimalResponseDTO = animalservice.getAnimalById(id)

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ANIMAL_WRITE')")
    fun update(@PathVariable id: Long, @RequestBody dto: AnimalDTO): AnimalResponseDTO =
        animalservice.updateAnimal(id, dto)

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ANIMAL_DELETE')")
    fun delete(@PathVariable id: Long) = animalservice.deleteAnimal(id)

    @GetMapping("/count")
    @PreAuthorize("hasAuthority('ANIMAL_READ')")
    fun countAllAnimals(): Long {
        return animalservice.countAllAnimals()
    }

    @GetMapping("/count-by-type")
    @PreAuthorize("hasAuthority('ANIMAL_STATS')")
    fun countAnimalsByType(): List<Map<String, Any>> {
        return animalservice.countAnimalsByType()
    }

    @PostMapping("/{id}/photo", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @PreAuthorize("hasAuthority('ANIMAL_PHOTO')")
    fun uploadPhoto(
        @PathVariable id: Long,
        @RequestParam("file") file: MultipartFile
    ): Map<String, String> {

        // Vérifier que l'animal existe
        val animal = animalRepository.findById(id)
            .orElseThrow { IllegalArgumentException("Animal $id non trouvé") }

        // Créer le dossier si nécessaire
        val uploadDir: Path = Paths.get(animauxUploadDir)
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir)
        }

        // Nom de fichier unique
        val filename = "${animal.codeAnimal}_${System.currentTimeMillis()}.jpg"
        val filePath = uploadDir.resolve(filename)

        // Sauvegarde du fichier sur le disque
        file.transferTo(filePath.toFile())

        // Mise à jour du chemin de la photo dans la base
        val photoUrl = "/uploads/animaux/$filename" // URL relative pour le frontend
        animalRepository.save(animal.copy(photoUrl = photoUrl))

        return mapOf("photoUrl" to photoUrl)
    }



}
