package com.oki.gestion_parc_backend.service.impl

import com.oki.gestion_parc_backend.dto.*
import com.oki.gestion_parc_backend.model.*
import com.oki.gestion_parc_backend.repository.*
import com.oki.gestion_parc_backend.service.TacheService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDate
import java.time.LocalDateTime

@Service
@Transactional
class TacheServiceImpl(
    private val tacheRepo: TacheRepository,
    private val typeTacheRepo: TypeTacheRepository,
    private val assignationRepo: AssignationTacheRepository,
    private val preuveRepo: PreuveTacheRepository,
    private val utilisateurRepo: UtilisateurRepository
) : TacheService {

    @Value("\${app.upload.taches-dir:uploads/taches}")
    private lateinit var tachesUploadDir: String

    // ─── Types de tâches ──────────────────────────────────────────────────────

    override fun creerTypeTache(dto: TypeTacheCreateDTO): TypeTache =
        typeTacheRepo.save(TypeTache(nom = dto.nom, description = dto.description, couleur = dto.couleur, icone = dto.icone))

    override fun listerTypesTaches(): List<TypeTache> = typeTacheRepo.findAll()

    override fun modifierTypeTache(id: Long, dto: TypeTacheCreateDTO): TypeTache {
        val type = typeTacheRepo.findById(id).orElseThrow { IllegalArgumentException("TypeTache $id introuvable") }
        type.nom = dto.nom; type.description = dto.description; type.couleur = dto.couleur; type.icone = dto.icone
        return typeTacheRepo.save(type)
    }

    override fun supprimerTypeTache(id: Long) = typeTacheRepo.deleteById(id)

    // ─── CRUD Tâches ──────────────────────────────────────────────────────────

    override fun creerTache(dto: TacheCreateDTO, createurEmail: String): TacheResponseDTO {
        val createur = utilisateurRepo.findByEmail(createurEmail)
            ?: throw IllegalArgumentException("Utilisateur $createurEmail introuvable")
        val typeTache = dto.typeTacheId?.let { typeTacheRepo.findById(it).orElse(null) }

        val tache = tacheRepo.save(Tache(
            titre = dto.titre, description = dto.description,
            typeTache = typeTache, priorite = dto.priorite,
            dateEcheance = dto.dateEcheance, recurrence = dto.recurrence,
            joursRecurrence = dto.joursRecurrence, batiment = dto.batiment,
            box = dto.box, notes = dto.notes, createur = createur,
            dateCreation = LocalDateTime.now()
        ))

        // Créer une assignation par utilisateur
        dto.assigneeIds.forEach { userId ->
            val user = utilisateurRepo.findById(userId).orElse(null) ?: return@forEach
            assignationRepo.save(AssignationTache(tache = tache, assignee = user, statut = StatutTache.A_FAIRE))
        }

        return toResponseDTO(tacheRepo.findById(tache.id).get())
    }

    override fun modifierTache(id: Long, dto: TacheCreateDTO): TacheResponseDTO {
        val tache = tacheRepo.findById(id).orElseThrow { IllegalArgumentException("Tache $id introuvable") }
        tache.titre = dto.titre; tache.description = dto.description
        tache.typeTache = dto.typeTacheId?.let { typeTacheRepo.findById(it).orElse(null) }
        tache.priorite = dto.priorite; tache.dateEcheance = dto.dateEcheance
        tache.recurrence = dto.recurrence; tache.joursRecurrence = dto.joursRecurrence
        tache.batiment = dto.batiment; tache.box = dto.box; tache.notes = dto.notes

        // Mettre à jour les assignés
        val existingIds = assignationRepo.findByTacheId(id).map { it.assignee?.idUtilisateur }
        val newIds = dto.assigneeIds
        // Ajouter les nouveaux
        newIds.filter { it !in existingIds }.forEach { userId ->
            val user = utilisateurRepo.findById(userId).orElse(null) ?: return@forEach
            assignationRepo.save(AssignationTache(tache = tache, assignee = user, statut = StatutTache.A_FAIRE))
        }
        // Supprimer ceux retirés (seulement si statut A_FAIRE)
        assignationRepo.findByTacheId(id)
            .filter { it.assignee?.idUtilisateur !in newIds && it.statut == StatutTache.A_FAIRE }
            .forEach { assignationRepo.delete(it) }

        return toResponseDTO(tacheRepo.save(tache))
    }

    override fun supprimerTache(id: Long) = tacheRepo.deleteById(id)

    override fun getTache(id: Long): TacheResponseDTO =
        toResponseDTO(tacheRepo.findById(id).orElseThrow { IllegalArgumentException("Tache $id introuvable") })

    // ─── Vues ─────────────────────────────────────────────────────────────────

    override fun tachesJourTous(): List<TacheResponseDTO> {
        val debut = LocalDate.now().atStartOfDay()
        val fin = debut.plusDays(1)
        return tacheRepo.findTachesJour(debut, fin).map { toResponseDTO(it) }
    }

    override fun tachesAVenirTous(): List<TacheResponseDTO> =
        tacheRepo.findTachesAVenir(LocalDate.now().plusDays(1).atStartOfDay()).map { toResponseDTO(it) }

    override fun tachesPasseesTous(): List<TacheResponseDTO> =
        tacheRepo.findTachesPassees(LocalDateTime.now()).map { toResponseDTO(it) }

    override fun tachesJourPourUser(userEmail: String): List<TacheResponseDTO> {
        val user = utilisateurRepo.findByEmail(userEmail) ?: return emptyList()
        val debut = LocalDate.now().atStartOfDay()
        return tacheRepo.findTachesJourPourUser(user.idUtilisateur, debut, debut.plusDays(1))
            .map { toResponseDTO(it) }
    }

    override fun tachesAVenirPourUser(userEmail: String): List<TacheResponseDTO> {
        val user = utilisateurRepo.findByEmail(userEmail) ?: return emptyList()
        return tacheRepo.findTachesAVenirPourUser(user.idUtilisateur, LocalDate.now().plusDays(1).atStartOfDay())
            .map { toResponseDTO(it) }
    }

    override fun tachesPasseesPourUser(userEmail: String): List<TacheResponseDTO> {
        val user = utilisateurRepo.findByEmail(userEmail) ?: return emptyList()
        return tacheRepo.findTachesPasseesPourUser(user.idUtilisateur, LocalDateTime.now())
            .map { toResponseDTO(it) }
    }

    override fun tachesEnAttenteValidation(): List<TacheResponseDTO> {
        val assignations = assignationRepo.findByStatut(StatutTache.EN_ATTENTE_VALIDATION)
        return assignations.mapNotNull { it.tache }.distinct().map { toResponseDTO(it) }
    }

    override fun stats(): TacheStatsDTO {
        val all = assignationRepo.findAll()
        return TacheStatsDTO(
            aFaire              = all.count { it.statut == StatutTache.A_FAIRE },
            enCours             = all.count { it.statut == StatutTache.EN_COURS },
            enAttenteValidation = all.count { it.statut == StatutTache.EN_ATTENTE_VALIDATION },
            validees            = all.count { it.statut == StatutTache.VALIDEE },
            invalidees          = all.count { it.statut == StatutTache.INVALIDEE },
            expirees            = all.count { it.statut == StatutTache.EXPIREE }
        )
    }

    // ─── Actions ouvrier ──────────────────────────────────────────────────────

    override fun demarrer(assignationId: Long, userEmail: String): AssignationTacheDTO {
        val assignation = getAssignation(assignationId)
        verifierAssignee(assignation, userEmail)
        if (assignation.statut != StatutTache.A_FAIRE && assignation.statut != StatutTache.INVALIDEE)
            throw IllegalStateException("Impossible de démarrer une tâche au statut ${assignation.statut}")
        assignation.statut = StatutTache.EN_COURS
        assignation.dateDebut = LocalDateTime.now()
        return toAssignationDTO(assignationRepo.save(assignation))
    }

    override fun soumettre(assignationId: Long, dto: SoumettreDTO, userEmail: String): AssignationTacheDTO {
        val assignation = getAssignation(assignationId)
        verifierAssignee(assignation, userEmail)
        if (assignation.statut != StatutTache.EN_COURS && assignation.statut != StatutTache.A_FAIRE)
            throw IllegalStateException("Impossible de soumettre au statut ${assignation.statut}")
        if (assignation.preuves.isEmpty())
            throw IllegalStateException("Au moins une photo est requise comme preuve")
        assignation.statut = StatutTache.EN_ATTENTE_VALIDATION
        assignation.dateSoumission = LocalDateTime.now()
        assignation.commentaireOuvrier = dto.commentaireOuvrier
        return toAssignationDTO(assignationRepo.save(assignation))
    }

    override fun uploadPreuve(assignationId: Long, file: MultipartFile, userEmail: String): PreuveTacheDTO {
        val assignation = getAssignation(assignationId)
        verifierAssignee(assignation, userEmail)

        val dir = Paths.get(tachesUploadDir)
        if (!Files.exists(dir)) Files.createDirectories(dir)

        val filename = "tache_${assignationId}_${System.currentTimeMillis()}_${file.originalFilename}"
        file.transferTo(dir.resolve(filename).toFile())

        val preuve = preuveRepo.save(PreuveTache(
            assignation = assignation,
            urlFichier = "/uploads/taches/$filename",
            dateUpload = LocalDateTime.now()
        ))
        return PreuveTacheDTO(preuve.id, preuve.urlFichier, preuve.dateUpload)
    }

    // ─── Actions gérant ───────────────────────────────────────────────────────

    override fun valider(assignationId: Long, dto: ValiderDTO, validateurEmail: String): AssignationTacheDTO {
        val assignation = getAssignation(assignationId)
        val validateur = utilisateurRepo.findByEmail(validateurEmail)
            ?: throw IllegalArgumentException("Validateur introuvable")
        if (assignation.statut != StatutTache.EN_ATTENTE_VALIDATION)
            throw IllegalStateException("Seule une tâche EN_ATTENTE_VALIDATION peut être validée")
        assignation.statut = StatutTache.VALIDEE
        assignation.validateur = validateur
        assignation.commentaireValidateur = dto.commentaire
        assignation.dateValidation = LocalDateTime.now()

        val saved = assignationRepo.save(assignation)

        // Génération de la récurrence si c'est la dernière assignation non validée
        genererRecurrence(assignation.tache!!)

        return toAssignationDTO(saved)
    }

    override fun invalider(assignationId: Long, dto: InvaliderDTO, validateurEmail: String): AssignationTacheDTO {
        val assignation = getAssignation(assignationId)
        val validateur = utilisateurRepo.findByEmail(validateurEmail)
            ?: throw IllegalArgumentException("Validateur introuvable")
        if (assignation.statut != StatutTache.EN_ATTENTE_VALIDATION)
            throw IllegalStateException("Seule une tâche EN_ATTENTE_VALIDATION peut être invalidée")
        if (dto.commentaire.isBlank())
            throw IllegalArgumentException("Le commentaire est obligatoire pour invalider une tâche")

        assignation.statut = StatutTache.INVALIDEE
        assignation.validateur = validateur
        assignation.commentaireValidateur = dto.commentaire
        assignation.dateValidation = LocalDateTime.now()
        return toAssignationDTO(assignationRepo.save(assignation))
    }

    // ─── Récurrence ───────────────────────────────────────────────────────────

    private fun genererRecurrence(tache: Tache) {
        if (tache.recurrence == TypeRecurrence.UNIQUE) return

        // Ne générer que si toutes les assignations sont validées
        val assignations = assignationRepo.findByTacheId(tache.id)
        if (assignations.any { it.statut != StatutTache.VALIDEE }) return

        val prochaine = calculerProchainDate(tache)
        // ⚠️ Ne pas utiliser tache.copy() : la liste `assignations` serait partagée
        // entre les deux entités → HibernateException "Found shared references to a collection"
        val nouvelleTache = tacheRepo.save(Tache(
            id              = 0,
            titre           = tache.titre,
            description     = tache.description,
            typeTache       = tache.typeTache,
            priorite        = tache.priorite,
            dateEcheance    = prochaine,
            recurrence      = tache.recurrence,
            joursRecurrence = tache.joursRecurrence,
            batiment        = tache.batiment,
            box             = tache.box,
            notes           = tache.notes,
            createur        = tache.createur,
            dateCreation    = LocalDateTime.now(),
            tacheParente    = tache
            // assignations laissée vide — les nouvelles sont créées ci-dessous
        ))
        assignations.forEach { a ->
            assignationRepo.save(AssignationTache(tache = nouvelleTache, assignee = a.assignee, statut = StatutTache.A_FAIRE))
        }
    }

    private fun calculerProchainDate(tache: Tache): LocalDateTime = when (tache.recurrence) {
        TypeRecurrence.QUOTIDIENNE   -> tache.dateEcheance.plusDays(1)
        TypeRecurrence.HEBDOMADAIRE  -> tache.dateEcheance.plusWeeks(1)
        TypeRecurrence.MENSUELLE     -> tache.dateEcheance.plusMonths(1)
        else                         -> tache.dateEcheance.plusDays(1)
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private fun getAssignation(id: Long): AssignationTache =
        assignationRepo.findById(id).orElseThrow { IllegalArgumentException("Assignation $id introuvable") }

    private fun verifierAssignee(assignation: AssignationTache, email: String) {
        if (assignation.assignee?.email != email)
            throw IllegalAccessException("Vous n'êtes pas l'assigné de cette tâche")
    }

    private fun toResponseDTO(tache: Tache): TacheResponseDTO {
        val assignations = assignationRepo.findByTacheId(tache.id)
        val statutGlobal = calculerStatutGlobal(assignations)
        return TacheResponseDTO(
            id = tache.id, titre = tache.titre, description = tache.description,
            typeTache = tache.typeTache?.let { TypeTacheDTO(it.id, it.nom, it.description, it.couleur, it.icone) },
            priorite = tache.priorite, dateEcheance = tache.dateEcheance,
            recurrence = tache.recurrence, joursRecurrence = tache.joursRecurrence,
            batiment = tache.batiment, box = tache.box, notes = tache.notes,
            createur = tache.createur?.let { toUserLight(it) },
            dateCreation = tache.dateCreation,
            assignations = assignations.map { toAssignationDTO(it) },
            statutGlobal = statutGlobal
        )
    }

    private fun calculerStatutGlobal(assignations: List<AssignationTache>): StatutTache {
        if (assignations.isEmpty()) return StatutTache.A_FAIRE
        return when {
            assignations.all { it.statut == StatutTache.VALIDEE }              -> StatutTache.VALIDEE
            assignations.any { it.statut == StatutTache.EN_ATTENTE_VALIDATION } -> StatutTache.EN_ATTENTE_VALIDATION
            assignations.any { it.statut == StatutTache.EN_COURS }              -> StatutTache.EN_COURS
            assignations.any { it.statut == StatutTache.INVALIDEE }             -> StatutTache.INVALIDEE
            assignations.any { it.statut == StatutTache.EXPIREE }               -> StatutTache.EXPIREE
            else                                                                 -> StatutTache.A_FAIRE
        }
    }

    private fun toAssignationDTO(a: AssignationTache): AssignationTacheDTO {
        val preuves = preuveRepo.findByAssignationId(a.id)
        return AssignationTacheDTO(
            id = a.id,
            assignee = toUserLight(a.assignee!!),
            statut = a.statut,
            dateDebut = a.dateDebut, dateSoumission = a.dateSoumission,
            commentaireOuvrier = a.commentaireOuvrier,
            dateValidation = a.dateValidation,
            validateur = a.validateur?.let { toUserLight(it) },
            commentaireValidateur = a.commentaireValidateur,
            preuves = preuves.map { PreuveTacheDTO(it.id, it.urlFichier, it.dateUpload) }
        )
    }

    private fun toUserLight(u: Utilisateur) = UtilisateurLightDTO(
        u.idUtilisateur, u.nom, u.prenom, u.email, u.poste
    )
}
