package com.oki.gestion_parc_backend.controller

import com.oki.gestion_parc_backend.dto.*
import com.oki.gestion_parc_backend.model.TypeTache
import com.oki.gestion_parc_backend.service.TacheService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

// ─── Types de tâches ─────────────────────────────────────────────────────────

@RestController
@RequestMapping("/api/types-taches")
class TypeTacheController(private val service: TacheService) {

    @GetMapping
    @PreAuthorize("hasAuthority('TACHE_READ_OWN')")
    fun list(): List<TypeTache> = service.listerTypesTaches()

    @PostMapping
    @PreAuthorize("hasAuthority('TYPE_TACHE_MANAGE')")
    fun create(@RequestBody dto: TypeTacheCreateDTO): TypeTache = service.creerTypeTache(dto)

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('TYPE_TACHE_MANAGE')")
    fun update(@PathVariable id: Long, @RequestBody dto: TypeTacheCreateDTO): TypeTache =
        service.modifierTypeTache(id, dto)

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('TYPE_TACHE_MANAGE')")
    fun delete(@PathVariable id: Long) = service.supprimerTypeTache(id)
}

// ─── Tâches ───────────────────────────────────────────────────────────────────

@RestController
@RequestMapping("/api/taches")
class TacheController(private val service: TacheService,
                      private val utilisateurRepo: com.oki.gestion_parc_backend.repository.UtilisateurRepository) {

    // ── CRUD ──────────────────────────────────────────────────────────────────

    @PostMapping
    @PreAuthorize("hasAuthority('TACHE_CREATE')")
    fun creer(
        @RequestBody dto: TacheCreateDTO,
        @AuthenticationPrincipal user: UserDetails
    ) = service.creerTache(dto, user.username)

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('TACHE_CREATE')")
    fun modifier(@PathVariable id: Long, @RequestBody dto: TacheCreateDTO) =
        service.modifierTache(id, dto)

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('TACHE_CREATE')")
    fun supprimer(@PathVariable id: Long) = service.supprimerTache(id)

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('TACHE_READ_OWN')")
    fun get(@PathVariable id: Long) = service.getTache(id)

    // ── Vues Admin/Gérant ─────────────────────────────────────────────────────

    @GetMapping("/admin/aujourd-hui")
    @PreAuthorize("hasAuthority('TACHE_READ_ALL')")
    fun tachesJourAdmin() = service.tachesJourTous()

    @GetMapping("/admin/a-venir")
    @PreAuthorize("hasAuthority('TACHE_READ_ALL')")
    fun tachesAVenirAdmin() = service.tachesAVenirTous()

    @GetMapping("/admin/passees")
    @PreAuthorize("hasAuthority('TACHE_READ_ALL')")
    fun tachesPasseesAdmin() = service.tachesPasseesTous()

    @GetMapping("/admin/a-valider")
    @PreAuthorize("hasAuthority('TACHE_VALIDATE')")
    fun tachesAValider() = service.tachesEnAttenteValidation()

    @GetMapping("/admin/stats")
    @PreAuthorize("hasAuthority('TACHE_READ_ALL')")
    fun stats() = service.stats()

    // ── Vues Utilisateur connecté ─────────────────────────────────────────────

    @GetMapping("/moi/aujourd-hui")
    @PreAuthorize("hasAuthority('TACHE_READ_OWN')")
    fun mesAujourdHui(@AuthenticationPrincipal user: UserDetails) =
        service.tachesJourPourUser(user.username)

    @GetMapping("/moi/a-venir")
    @PreAuthorize("hasAuthority('TACHE_READ_OWN')")
    fun mesAVenir(@AuthenticationPrincipal user: UserDetails) =
        service.tachesAVenirPourUser(user.username)

    @GetMapping("/moi/historique")
    @PreAuthorize("hasAuthority('TACHE_READ_OWN')")
    fun monHistorique(@AuthenticationPrincipal user: UserDetails) =
        service.tachesPasseesPourUser(user.username)

    // ── Actions ouvrier ───────────────────────────────────────────────────────

    @PostMapping("/assignations/{id}/demarrer")
    @PreAuthorize("hasAuthority('TACHE_COMPLETE')")
    fun demarrer(
        @PathVariable id: Long,
        @AuthenticationPrincipal user: UserDetails
    ) = service.demarrer(id, user.username)

    @PostMapping("/assignations/{id}/soumettre")
    @PreAuthorize("hasAuthority('TACHE_COMPLETE')")
    fun soumettre(
        @PathVariable id: Long,
        @RequestBody dto: SoumettreDTO,
        @AuthenticationPrincipal user: UserDetails
    ) = service.soumettre(id, dto, user.username)

    @PostMapping("/assignations/{id}/preuves", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @PreAuthorize("hasAuthority('TACHE_COMPLETE')")
    fun uploadPreuve(
        @PathVariable id: Long,
        @RequestParam("file") file: MultipartFile,
        @AuthenticationPrincipal user: UserDetails
    ) = service.uploadPreuve(id, file, user.username)

    // ── Actions gérant ────────────────────────────────────────────────────────

    @PostMapping("/assignations/{id}/valider")
    @PreAuthorize("hasAuthority('TACHE_VALIDATE')")
    fun valider(
        @PathVariable id: Long,
        @RequestBody dto: ValiderDTO,
        @AuthenticationPrincipal user: UserDetails
    ) = service.valider(id, dto, user.username)

    @PostMapping("/assignations/{id}/invalider")
    @PreAuthorize("hasAuthority('TACHE_VALIDATE')")
    fun invalider(
        @PathVariable id: Long,
        @RequestBody dto: InvaliderDTO,
        @AuthenticationPrincipal user: UserDetails
    ) = service.invalider(id, dto, user.username)

    // ── Liste des utilisateurs pour le formulaire de création ─────────────────

    @GetMapping("/assignables")
    @PreAuthorize("hasAuthority('TACHE_CREATE')")
    fun utilisateursAssignables(): List<UtilisateurLightDTO> =
        utilisateurRepo.findAll().map {
            UtilisateurLightDTO(it.idUtilisateur, it.nom, it.prenom, it.email, it.poste)
        }
}
