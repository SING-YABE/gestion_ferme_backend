package com.oki.gestion_parc_backend.service

import com.oki.gestion_parc_backend.model.Invitation
import com.oki.gestion_parc_backend.model.Role
import com.oki.gestion_parc_backend.model.Utilisateur
import com.oki.gestion_parc_backend.repository.InvitationRepository
import com.oki.gestion_parc_backend.repository.RoleRepository
import com.oki.gestion_parc_backend.repository.TenantRepository
import com.oki.gestion_parc_backend.repository.UtilisateurRepository
import com.oki.gestion_parc_backend.security.JwtUtil
import com.oki.gestion_parc_backend.security.TenantContext
import com.oki.gestion_parc_backend.service.impl.CustomUserDetailsService
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.UUID

/**
 * Service de gestion des invitations utilisateur.
 *
 * createInvitation() :
 *   - Crée un compte inactif (password = token chiffré, inutilisable directement)
 *   - Génère un token UUID 72h
 *   - Envoie un email avec le lien d'activation
 *
 * validateInvitation() :
 *   - Vérifie le token (existant, non expiré, non utilisé)
 *   - Active le compte (le user peut maintenant définir son mot de passe)
 *   - Retourne directement un JWT → l'utilisateur est connecté
 */
@Service
class InvitationService(
    private val invitationRepository: InvitationRepository,
    private val utilisateurRepository: UtilisateurRepository,
    private val roleRepository: RoleRepository,
    private val tenantRepository: TenantRepository,
    private val passwordEncoder: PasswordEncoder,
    private val mailSender: JavaMailSender,
    private val jwtUtil: JwtUtil,
    private val userDetailsService: CustomUserDetailsService,

    @Value("\${app.frontend.url:http://localhost:4200}")
    private val frontendUrl: String,

    @Value("\${spring.mail.username:noreply@ferme.bf}")
    private val fromEmail: String
) {

    // ─── DTO d'entrée ─────────────────────────────────────────────────────────
    data class CreateInvitationRequest(
        val prenom: String,
        val nom: String,
        val email: String,
        val poste: String = "",
        val telephone: String = "",
        val roleId: Long
    )

    data class InvitationResult(val token: String, val message: String)
    data class ValidateResult(val token: String, val role: String?, val username: String, val nomFerme: String)

    // ─── Créer une invitation ─────────────────────────────────────────────────
    /**
     * Appelé par l'admin (TenantContext déjà positionné par le JwtFilter).
     * Crée un compte utilisateur "en attente" + envoie l'email.
     */
    fun createInvitation(req: CreateInvitationRequest): InvitationResult {
        val schemaName = TenantContext.getTenant()
            ?: throw IllegalStateException("Aucun tenant dans le contexte")

        // Récupérer le nom de la ferme pour personnaliser l'email
        val tenant = tenantRepository.findAll().firstOrNull { it.schemaName == schemaName }
            ?: throw IllegalStateException("Tenant introuvable pour le schéma $schemaName")

        // Vérifier que l'email n'existe pas déjà dans cette ferme
        if (utilisateurRepository.findByEmail(req.email) != null) {
            throw IllegalArgumentException("Un utilisateur avec cet email existe déjà dans cette ferme.")
        }

        val role: Role = roleRepository.findById(req.roleId).orElseThrow {
            IllegalArgumentException("Rôle introuvable.")
        }

        // Générer le token UUID
        val token = UUID.randomUUID().toString().replace("-", "")

        // Créer le compte utilisateur avec un mot de passe inutilisable (sera défini via l'invitation)
        val user = Utilisateur(
            prenom    = req.prenom,
            nom       = req.nom,
            email     = req.email,
            poste     = req.poste,
            telephone = req.telephone,
            password  = passwordEncoder.encode("INVITATION_PENDING_$token"), // inutilisable directement
            role      = role
        )
        utilisateurRepository.save(user)

        // Sauvegarder l'invitation
        val invitation = Invitation(
            token      = token,
            email      = req.email,
            prenom     = req.prenom,
            nom        = req.nom,
            schemaName = schemaName,
            nomFerme   = tenant.nomFerme,
            roleId     = req.roleId,
            expiresAt  = LocalDateTime.now().plusHours(72)
        )
        invitationRepository.save(invitation)

        // Envoyer l'email
        sendInvitationEmail(req.email, req.prenom, tenant.nomFerme, token)

        return InvitationResult(token, "Invitation envoyée à ${req.email}")
    }

    // ─── Valider un token d'invitation ───────────────────────────────────────
    /**
     * Appelé publiquement depuis la page /invitation?token=xxx
     * Retourne un JWT si le token est valide → l'utilisateur est connecté directement.
     */
    fun validateInvitation(token: String, newPassword: String): ValidateResult {
        // Chercher l'invitation dans tous les tenants (token global unique)
        val allTenants = tenantRepository.findAllByActiveTrue()
        var invitation: Invitation? = null
        var foundSchema: String? = null

        for (tenant in allTenants) {
            TenantContext.setTenant(tenant.schemaName)
            try {
                val inv = invitationRepository.findByToken(token)
                if (inv != null) {
                    invitation = inv
                    foundSchema = tenant.schemaName
                    break
                }
            } finally {
                TenantContext.clear()
            }
        }

        if (invitation == null || foundSchema == null) {
            throw IllegalArgumentException("Lien d'invitation invalide ou expiré.")
        }
        if (invitation.used) {
            throw IllegalArgumentException("Ce lien a déjà été utilisé.")
        }
        if (invitation.expiresAt.isBefore(LocalDateTime.now())) {
            throw IllegalArgumentException("Ce lien a expiré (72h). Demandez une nouvelle invitation.")
        }

        // Activer le compte + définir le vrai mot de passe
        TenantContext.setTenant(foundSchema)
        try {
            val user = utilisateurRepository.findByEmail(invitation.email)
                ?: throw IllegalStateException("Utilisateur introuvable.")

            user.password = passwordEncoder.encode(newPassword)
            utilisateurRepository.save(user)

            // Marquer l'invitation comme utilisée
            invitation.used = true
            invitationRepository.save(invitation)

            // Générer JWT → connexion directe
            val userDetails = userDetailsService.loadUserByUsername(invitation.email)
            val role = userDetails.authorities.firstOrNull { it.authority.startsWith("ROLE_") }?.authority
            val jwt = jwtUtil.generateToken(userDetails, foundSchema)

            return ValidateResult(jwt, role, invitation.email, invitation.nomFerme)
        } finally {
            TenantContext.clear()
        }
    }

    // ─── Envoi de l'email ─────────────────────────────────────────────────────
    private fun sendInvitationEmail(toEmail: String, prenom: String, nomFerme: String, token: String) {
        val lien = "$frontendUrl/invitation?token=$token"

        val message = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message, true, "UTF-8")

        helper.setFrom(fromEmail, "Gestion Ferme")
        helper.setTo(toEmail)
        helper.setSubject("Invitation à rejoindre $nomFerme")
        helper.setText(buildEmailHtml(prenom, nomFerme, lien), true)

        mailSender.send(message)
    }

    private fun buildEmailHtml(prenom: String, nomFerme: String, lien: String): String = """
        <!DOCTYPE html>
        <html lang="fr">
        <body style="font-family: Arial, sans-serif; background: #f5f5f5; padding: 24px;">
          <div style="max-width: 520px; margin: auto; background: #fff; border-radius: 10px; overflow: hidden;">

            <div style="background: linear-gradient(135deg, #2d5016, #4a7c2c); padding: 28px 32px;">
              <h1 style="color: #fff; margin: 0; font-size: 1.4rem;">🐷 Gestion Ferme</h1>
            </div>

            <div style="padding: 32px;">
              <p style="font-size: 1rem; color: #374151;">Bonjour <strong>$prenom</strong>,</p>
              <p style="color: #6b7280;">
                Vous avez été invité(e) à rejoindre <strong>$nomFerme</strong> sur la plateforme de gestion d'élevage porcin.
              </p>
              <p style="color: #6b7280;">
                Cliquez sur le bouton ci-dessous pour activer votre compte et définir votre mot de passe.
                Ce lien est valable <strong>72 heures</strong>.
              </p>

              <div style="text-align: center; margin: 32px 0;">
                <a href="$lien"
                   style="background: #4a7c2c; color: #fff; text-decoration: none;
                          padding: 14px 32px; border-radius: 8px; font-size: 1rem;
                          font-weight: bold; display: inline-block;">
                  Activer mon compte
                </a>
              </div>

              <p style="font-size: 0.8rem; color: #9ca3af;">
                Si vous n'attendiez pas cette invitation, ignorez simplement cet email.<br/>
                Lien : <a href="$lien" style="color: #4a7c2c;">$lien</a>
              </p>
            </div>

            <div style="background: #f9fafb; padding: 16px 32px; text-align: center;">
              <p style="font-size: 0.75rem; color: #9ca3af; margin: 0;">
                © 2025 Gestion Ferme — Burkina Faso
              </p>
            </div>
          </div>
        </body>
        </html>
    """.trimIndent()
}
