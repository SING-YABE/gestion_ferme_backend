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

        val tenant = tenantRepository.findAll().firstOrNull { it.schemaName == schemaName }
            ?: throw IllegalStateException("Tenant introuvable pour le schéma $schemaName")

        val role: Role = roleRepository.findById(req.roleId).orElseThrow {
            IllegalArgumentException("Rôle introuvable.")
        }

        val existingUser = utilisateurRepository.findByEmail(req.email)
        if (existingUser != null) {
            val existingInvitation = invitationRepository.findByEmail(req.email)

            // Compte déjà actif (invitation utilisée)
            if (existingInvitation != null && existingInvitation.used) {
                throw IllegalArgumentException("Cet utilisateur a déjà activé son compte.")
            }

            // Invitation en attente (email échoué ou expiré) → renouveler et renvoyer
            if (existingInvitation != null && !existingInvitation.used) {
                val newToken = UUID.randomUUID().toString().replace("-", "")
                existingInvitation.token     = newToken
                existingInvitation.expiresAt = LocalDateTime.now().plusHours(72)
                invitationRepository.save(existingInvitation)
                try {
                    sendInvitationEmail(req.email, req.prenom, tenant.nomFerme, newToken)
                } catch (e: Exception) {
                    println("[InvitationService] ⚠ Échec renvoi email à ${req.email} : ${e.message}")
                    throw IllegalStateException("Token renouvelé mais échec d'envoi d'email : ${e.message}")
                }
                return InvitationResult(newToken, "Invitation renvoyée à ${req.email}")
            }

            // Utilisateur orphelin sans invitation → nettoyer pour recréer proprement
            utilisateurRepository.delete(existingUser)
        }

        // Créer l'utilisateur + l'invitation, puis envoyer l'email
        val token = UUID.randomUUID().toString().replace("-", "")

        val user = Utilisateur(
            prenom    = req.prenom,
            nom       = req.nom,
            email     = req.email,
            poste     = req.poste,
            telephone = req.telephone,
            password  = passwordEncoder.encode("INVITATION_PENDING_$token"),
            role      = role
        )
        val savedUser = utilisateurRepository.save(user)

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
        val savedInvitation = invitationRepository.save(invitation)

        try {
            sendInvitationEmail(req.email, req.prenom, tenant.nomFerme, token)
        } catch (e: Exception) {
            // Rollback manuel : supprimer user + invitation si l'email échoue
            println("[InvitationService] ⚠ Échec envoi email à ${req.email} : ${e.message}")
            invitationRepository.delete(savedInvitation)
            utilisateurRepository.delete(savedUser)
            throw IllegalStateException("Échec d'envoi d'email — aucune donnée sauvegardée. Vérifiez la config SMTP : ${e.message}")
        }

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
        <head><meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1.0"></head>
        <body style="margin:0;padding:0;background-color:#f0f4f8;font-family:Arial,Helvetica,sans-serif;">
          <table width="100%" cellpadding="0" cellspacing="0" style="background:#f0f4f8;padding:32px 16px;">
            <tr><td align="center">
              <table width="560" cellpadding="0" cellspacing="0" style="max-width:560px;width:100%;background:#ffffff;border-radius:12px;overflow:hidden;box-shadow:0 4px 24px rgba(0,0,0,0.08);">

                <!-- HEADER -->
                <tr>
                  <td style="background:linear-gradient(135deg,#1a3a0a 0%,#2d5016 50%,#3d6b1f 100%);padding:32px;text-align:center;">
                    <table width="100%" cellpadding="0" cellspacing="0">
                      <tr><td align="center">
                        <!-- Logo circulaire -->
                        <div style="display:inline-block;width:68px;height:68px;border-radius:50%;background:rgba(255,255,255,0.15);text-align:center;line-height:68px;margin-bottom:14px;">
                          <img src="https://img.icons8.com/color/64/000000/barn.png"
                               alt="Farm" width="44" height="44"
                               style="vertical-align:middle;margin-top:12px;border-radius:4px;"
                               onerror="this.style.display='none'"/>
                        </div>
                        <br/>
                        <span style="color:#ffffff;font-size:22px;font-weight:bold;letter-spacing:1.5px;text-transform:uppercase;">Gestion Ferme</span>
                        <br/>
                        <span style="color:rgba(255,255,255,0.65);font-size:12px;letter-spacing:0.5px;">Plateforme d'élevage porcin — Burkina Faso</span>
                      </td></tr>
                    </table>
                  </td>
                </tr>

                <!-- BODY -->
                <tr>
                  <td style="padding:36px 40px 24px;">
                    <p style="font-size:16px;color:#1f2937;margin:0 0 8px;">
                      Bonjour <strong style="color:#2d5016;">$prenom</strong>,
                    </p>
                    <p style="font-size:14px;color:#4b5563;line-height:1.6;margin:16px 0;">
                      Vous avez été invité(e) à rejoindre
                      <strong style="color:#1f2937;">$nomFerme</strong>
                      sur notre plateforme de gestion d'élevage porcin.
                    </p>
                    <p style="font-size:14px;color:#4b5563;line-height:1.6;margin:0 0 28px;">
                      Cliquez sur le bouton ci-dessous pour activer votre compte
                      et définir votre mot de passe.
                      Ce lien est valable <strong>72 heures</strong>.
                    </p>

                    <!-- CTA BUTTON -->
                    <table width="100%" cellpadding="0" cellspacing="0">
                      <tr><td align="center" style="padding:8px 0 32px;">
                        <a href="$lien"
                           style="display:inline-block;background:linear-gradient(135deg,#2d5016,#4a7c2c);
                                  color:#ffffff;text-decoration:none;padding:16px 40px;
                                  border-radius:8px;font-size:15px;font-weight:bold;
                                  letter-spacing:0.5px;box-shadow:0 4px 12px rgba(45,80,22,0.35);">
                          ✅ Activer mon compte
                        </a>
                      </td></tr>
                    </table>

                    <!-- DIVIDER -->
                    <hr style="border:none;border-top:1px solid #e5e7eb;margin:0 0 20px;"/>

                    <p style="font-size:12px;color:#9ca3af;line-height:1.5;margin:0;">
                      Si vous n'attendiez pas cette invitation, ignorez simplement cet email.<br/>
                      Ce lien expire automatiquement dans 72h et ne peut être utilisé qu'une seule fois.
                    </p>
                  </td>
                </tr>

                <!-- FOOTER -->
                <tr>
                  <td style="background:#f9fafb;border-top:1px solid #e5e7eb;padding:18px 40px;text-align:center;">
                    <p style="font-size:11px;color:#9ca3af;margin:0;">
                      © 2025 <strong>Gestion Ferme</strong> — Burkina Faso &nbsp;|&nbsp;
                      Ce message est confidentiel et destiné uniquement à son destinataire.
                    </p>
                  </td>
                </tr>

              </table>
            </td></tr>
          </table>
        </body>
        </html>
    """.trimIndent()
}
