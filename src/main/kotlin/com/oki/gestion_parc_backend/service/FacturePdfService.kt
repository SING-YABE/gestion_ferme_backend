package com.oki.gestion_parc_backend.service

import com.oki.gestion_parc_backend.model.Vente

interface FacturePdfService {
    fun genererFacturePdf(vente: Vente): ByteArray
}