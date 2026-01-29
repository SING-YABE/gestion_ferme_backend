package com.oki.gestion_parc_backend.service.impl

import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.borders.SolidBorder
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.HorizontalAlignment
import com.itextpdf.layout.properties.TextAlignment
import com.itextpdf.layout.properties.UnitValue
import com.oki.gestion_parc_backend.model.AppSettings
import com.oki.gestion_parc_backend.model.Vente
import com.oki.gestion_parc_backend.repository.AppSettingsRepository
import com.oki.gestion_parc_backend.service.FacturePdfService
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class FacturePdfServiceImpl(
    private val settingsRepository: AppSettingsRepository,
    @Value("\${app.upload.logo-dir:uploads}") private val logoDir: String
) : FacturePdfService {

    private val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.FRENCH)

    // Palette de couleurs sobre et professionnelle
    private val NOIR = DeviceRgb(40, 40, 40)
    private val GRIS_FONCE = DeviceRgb(80, 80, 80)
    private val GRIS_MOYEN = DeviceRgb(150, 150, 150)
    private val GRIS_CLAIR = DeviceRgb(245, 245, 245)
    private val VERT_ACCENT = DeviceRgb(34, 139, 34)
    private val ROUGE_ALERTE = DeviceRgb(200, 0, 0)

    override fun genererFacturePdf(vente: Vente): ByteArray {
        val output = ByteArrayOutputStream()
        val document = Document(PdfDocument(PdfWriter(output)))
        document.setMargins(30f, 40f, 30f, 40f)

        val settings = settingsRepository.findById(1L)
            .orElseThrow { IllegalStateException("Paramètres non configurés") }

        ajouterEntete(document, settings, vente)
        ajouterInfosGroupees(document, settings, vente)
        ajouterTableauAnimaux(document, vente)
        ajouterTotaux(document, vente)
        ajouterSignatures(document)
        ajouterPiedPage(document, settings)

        document.close()
        return output.toByteArray()
    }

    /* ==================== EN-TÊTE COMPACT ==================== */
    private fun ajouterEntete(document: Document, settings: AppSettings, vente: Vente) {
        val table = Table(floatArrayOf(2f, 1f))
            .setWidth(UnitValue.createPercentValue(100f))
            .setBorder(Border.NO_BORDER)
            .setMarginBottom(15f)

        // Colonne gauche - Logo + Nom
        val leftCell = Cell().setBorder(Border.NO_BORDER)

        chargerLogo(settings)?.let { logo ->
            leftCell.add(logo)
        }

        leftCell.add(
            Paragraph(settings.farmName)
                .setBold()
                .setFontSize(16f)
                .setFontColor(NOIR)
                .setMarginTop(3f)
        )

        if (!settings.slogan.isNullOrBlank()) {
            leftCell.add(
                Paragraph(settings.slogan)
                    .setFontSize(8f)
                    .setItalic()
                    .setFontColor(GRIS_MOYEN)
                    .setMarginTop(1f)
            )
        }

        table.addCell(leftCell)

        // Colonne droite - Info facture
        val rightCell = Cell()
            .setBorder(Border.NO_BORDER)
            .setTextAlignment(TextAlignment.RIGHT)

        rightCell.add(
            Paragraph("FACTURE")
                .setBold()
                .setFontSize(18f)
                .setFontColor(NOIR)
        )

        rightCell.add(
            Paragraph("N° ${String.format("%06d", vente.id ?: 0)}")
                .setFontSize(10f)
                .setFontColor(GRIS_FONCE)
                .setMarginTop(3f)
        )

        rightCell.add(
            Paragraph("Date: ${vente.dateVente.format(formatter)}")
                .setFontSize(9f)
                .setFontColor(GRIS_MOYEN)
                .setMarginTop(2f)
        )

        table.addCell(rightCell)
        document.add(table)

        // Ligne de séparation fine
        val ligne = Table(1)
            .setWidth(UnitValue.createPercentValue(100f))
            .setMarginBottom(12f)

        ligne.addCell(
            Cell()
                .setHeight(1f)
                .setBackgroundColor(GRIS_MOYEN)
                .setBorder(Border.NO_BORDER)
        )

        document.add(ligne)
    }

    /* ==================== INFOS GROUPÉES COMPACTES ==================== */
    private fun ajouterInfosGroupees(document: Document, settings: AppSettings, vente: Vente) {
        val table = Table(floatArrayOf(1f, 1f))
            .setWidth(UnitValue.createPercentValue(100f))
            .setMarginBottom(15f)

        // Bloc Émetteur - compact
        val emetteurCell = Cell()
            .setPadding(10f)
            .setBackgroundColor(GRIS_CLAIR)
            .setBorder(Border.NO_BORDER)

        emetteurCell.add(
            Paragraph("ÉMETTEUR")
                .setBold()
                .setFontSize(8f)
                .setFontColor(GRIS_MOYEN)
                .setMarginBottom(5f)
        )

        emetteurCell.add(
            Paragraph(settings.farmName)
                .setBold()
                .setFontSize(11f)
                .setFontColor(NOIR)
        )

        if (!settings.contactTel.isNullOrBlank()) {
            emetteurCell.add(
                Paragraph("Tél: ${settings.contactTel}")
                    .setFontSize(9f)
                    .setMarginTop(2f)
            )
        }

        emetteurCell.add(
            Paragraph(settings.contactEmail)
                .setFontSize(9f)
                .setMarginTop(2f)
        )

        table.addCell(emetteurCell)

        // Bloc Client - compact
        val clientCell = Cell()
            .setPadding(10f)
            .setBackgroundColor(GRIS_CLAIR)
            .setBorder(Border.NO_BORDER)

        clientCell.add(
            Paragraph("CLIENT")
                .setBold()
                .setFontSize(8f)
                .setFontColor(GRIS_MOYEN)
                .setMarginBottom(5f)
        )

        clientCell.add(
            Paragraph(vente.client)
                .setBold()
                .setFontSize(11f)
                .setFontColor(NOIR)
        )

        table.addCell(clientCell)

        document.add(table)

        // Dates d'enlèvement groupées - une seule ligne compacte
        val dateEnlevement: LocalDate? = vente.dateEnlevement
        val dateEnlevementMax: LocalDate? = vente.dateEnlevementAuPlusTard

        if (dateEnlevement != null || dateEnlevementMax != null) {
            val datesTable = Table(if (dateEnlevement != null && dateEnlevementMax != null) 2 else 1)
                .setWidth(UnitValue.createPercentValue(100f))
                .setMarginBottom(15f)

            if (dateEnlevement != null) {
                datesTable.addCell(
                    Cell()
                        .setPadding(8f)
                        .setBackgroundColor(DeviceRgb(255, 250, 240))
                        .setBorder(Border.NO_BORDER)
                        .add(
                            Paragraph("📅 Date prévue: ${dateEnlevement.format(formatter)}")
                                .setFontSize(9f)
                                .setFontColor(DeviceRgb(150, 100, 0))
                        )
                )
            }

            if (dateEnlevementMax != null) {
                datesTable.addCell(
                    Cell()
                        .setPadding(8f)
                        .setBackgroundColor(DeviceRgb(255, 240, 240))
                        .setBorder(Border.NO_BORDER)
                        .add(
                            Paragraph("⚠ Enlèvement au plus tard: ${dateEnlevementMax.format(formatter)}")
                                .setBold()
                                .setFontSize(9f)
                                .setFontColor(ROUGE_ALERTE)
                        )
                )
            }

            document.add(datesTable)
        }
    }

    /* ==================== TABLEAU DES ANIMAUX (sans Type de vente) ==================== */
    private fun ajouterTableauAnimaux(document: Document, vente: Vente) {
        document.add(
            Paragraph("DÉTAIL DES ANIMAUX VENDUS")
                .setBold()
                .setFontSize(11f)
                .setFontColor(NOIR)
                .setMarginBottom(8f)
        )

        val table = Table(floatArrayOf(2.5f, 1.5f, 1.5f, 2f))
            .setWidth(UnitValue.createPercentValue(100f))

        // En-têtes (sans "Type de vente")
        val headers = listOf("Code Animal", "Poids (kg)", "Prix/kg", "Montant (FCFA)")
        headers.forEach { header ->
            table.addHeaderCell(
                Cell()
                    .add(Paragraph(header).setBold().setFontSize(9f).setFontColor(NOIR))
                    .setBackgroundColor(GRIS_CLAIR)
                    .setPadding(6f)
                    .setBorder(Border.NO_BORDER)
                    .setTextAlignment(TextAlignment.CENTER)
            )
        }

        // Lignes de données (sans la colonne Type de vente)
        vente.animaux.forEachIndexed { index, animal ->
            val bgColor = if (index % 2 == 0) DeviceRgb(255, 255, 255) else GRIS_CLAIR

            table.addCell(
                creerCellule(animal.animal.codeAnimal, bgColor, TextAlignment.LEFT, true)
            )

            table.addCell(
                creerCellule(String.format("%.2f", animal.poidsVente), bgColor, TextAlignment.RIGHT)
            )

            table.addCell(
                creerCellule(String.format("%,.0f", animal.prixUnitaire), bgColor, TextAlignment.RIGHT)
            )

            table.addCell(
                creerCellule(String.format("%,.0f", animal.montantTotal), bgColor, TextAlignment.RIGHT, true)
            )
        }

        document.add(table)
    }

    private fun creerCellule(
        texte: String,
        bgColor: DeviceRgb,
        align: TextAlignment = TextAlignment.LEFT,
        bold: Boolean = false
    ): Cell {
        val para = Paragraph(texte).setFontSize(9f)
        if (bold) para.setBold()

        return Cell()
            .add(para)
            .setBackgroundColor(bgColor)
            .setPadding(6f)
            .setBorder(SolidBorder(DeviceRgb(220, 220, 220), 0.5f))
            .setTextAlignment(align)
    }

    /* ==================== TOTAUX ==================== */
    private fun ajouterTotaux(document: Document, vente: Vente) {
        val table = Table(floatArrayOf(3f, 2f))
            .setWidth(UnitValue.createPercentValue(50f))
            .setHorizontalAlignment(HorizontalAlignment.RIGHT)
            .setMarginTop(15f)

        // Poids total
        table.addCell(
            Cell()
                .add(Paragraph("Poids total:").setFontSize(10f))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT)
                .setPaddingRight(15f)
        )

        table.addCell(
            Cell()
                .add(Paragraph("${String.format("%.2f", vente.poidsTotal)} kg").setBold().setFontSize(10f))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT)
        )

        // Séparateur
        table.addCell(
            Cell(1, 2)
                .setHeight(1f)
                .setBackgroundColor(GRIS_MOYEN)
                .setBorder(Border.NO_BORDER)
                .setMarginTop(6f)
                .setMarginBottom(6f)
        )

        // TOTAL GÉNÉRAL
        table.addCell(
            Cell()
                .add(Paragraph("MONTANT TOTAL:").setBold().setFontSize(12f).setFontColor(NOIR))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT)
                .setPaddingRight(15f)
        )

        table.addCell(
            Cell()
                .add(
                    Paragraph("${String.format("%,d", vente.montantTotal.toLong())} FCFA")
                        .setBold()
                        .setFontSize(14f)
                        .setFontColor(VERT_ACCENT)
                )
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT)
                .setBackgroundColor(GRIS_CLAIR)
                .setPadding(8f)
        )

        document.add(table)
    }

    /* ==================== SIGNATURES ==================== */
    private fun ajouterSignatures(document: Document) {
        val table = Table(2)
            .setWidth(UnitValue.createPercentValue(100f))
            .setMarginTop(40f)

        table.addCell(
            Cell()
                .add(Paragraph("Signature du vendeur").setFontSize(8f).setFontColor(GRIS_MOYEN))
                .add(Paragraph("\n\n"))
                .add(Paragraph("_____________________").setFontSize(8f).setFontColor(GRIS_MOYEN))
                .setBorder(Border.NO_BORDER)
        )

        table.addCell(
            Cell()
                .add(Paragraph("Signature du client").setFontSize(8f).setFontColor(GRIS_MOYEN))
                .add(Paragraph("\n\n"))
                .add(Paragraph("_____________________").setFontSize(8f).setFontColor(GRIS_MOYEN))
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(TextAlignment.RIGHT)
        )

        document.add(table)
    }

    /* ==================== PIED DE PAGE ==================== */
    private fun ajouterPiedPage(document: Document, settings: AppSettings) {
        document.add(
            Paragraph("Merci pour votre confiance • ${settings.farmName}")
                .setFontSize(8f)
                .setItalic()
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(GRIS_MOYEN)
                .setMarginTop(15f)
        )
    }

    /* ==================== CHARGEMENT LOGO ==================== */
    private fun chargerLogo(settings: AppSettings): Image? {
        if (settings.logoPath.isNullOrBlank()) {
            println("⚠️ Aucun logo configuré")
            return null
        }

        // Essai 1: avec logoDir
        val logoFile1 = File(logoDir, settings.logoPath)
        if (logoFile1.exists()) {
            return creerImageLogo(logoFile1)
        }

        // Essai 2: chemin direct
        val logoFile2 = File(settings.logoPath)
        if (logoFile2.exists()) {
            return creerImageLogo(logoFile2)
        }

        // Essai 3: avec "uploads/"
        val logoFile3 = File("uploads/${settings.logoPath}")
        if (logoFile3.exists()) {
            return creerImageLogo(logoFile3)
        }

        println("❌ Logo introuvable: ${settings.logoPath}")
        return null
    }

    private fun creerImageLogo(file: File): Image? {
        return try {
            val imageData = ImageDataFactory.create(file.readBytes())
            println("✅ Logo chargé: ${file.absolutePath}")

            Image(imageData)
                .setWidth(50f)
                .setHeight(50f)
                .setMarginBottom(5f)
        } catch (e: Exception) {
            println("❌ Erreur chargement logo: ${e.message}")
            null
        }
    }
}