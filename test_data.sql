-- ============================================================
-- DONNÉES DE TEST — Élevage porcin Burkina Faso
-- Ferme test : "Ferme Kaboré" — Bobo-Dioulasso
-- Toutes les dates sont relatives à NOW() pour rester dans la
-- fenêtre des 12 derniers mois testée par les KPI.
-- ============================================================
-- ORDRE D'EXÉCUTION : respecter l'ordre (contraintes FK)
-- ============================================================

-- ── 0. Nettoyage (optionnel — à décommenter si besoin) ──────
-- TRUNCATE vente_animal, vente, ingredient_alimentation, alimentation,
--          reproduction, animal, box, batiment,
--          etat_sante, type_animal, type_aliment, ingredient,
--          fournisseur, type_vente, parametres_eleveur
-- CASCADE;

-- ============================================================
-- 1. BÂTIMENTS
-- ============================================================
INSERT INTO batiment (id, nom, localisation) VALUES
  (1, 'Bâtiment Truies',    'Secteur Nord'),
  (2, 'Bâtiment Engraissement', 'Secteur Sud')
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 2. BOXES (capacité max utilisée pour taux d'occupation)
-- ============================================================
INSERT INTO box (id, numero, code, capacite_max, batiment_id) VALUES
  (1, 1, 'BOX-T1', 3, 1),   -- Box truies 1
  (2, 2, 'BOX-T2', 3, 1),   -- Box truies 2
  (3, 3, 'BOX-V1', 2, 1),   -- Box verrats
  (4, 4, 'BOX-E1', 6, 2),   -- Box engraissement 1
  (5, 5, 'BOX-E2', 6, 2),   -- Box engraissement 2
  (6, 6, 'BOX-P1', 10, 2)   -- Box porcelets
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 3. TYPE ANIMAUX
-- ============================================================
INSERT INTO type_animal (id, nom, prefix, description) VALUES
  (1, 'Truie',             'TRU', 'Femelle reproductrice'),
  (2, 'Verrat',            'VER', 'Mâle reproducteur'),
  (3, 'Porc engraissement','ENG', 'Porc en phase de croissance/finition'),
  (4, 'Porcelet',          'POR', 'Porcelet sous la mère ou en sevrage')
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 4. ÉTAT DE SANTÉ (lié au type animal)
-- ============================================================
INSERT INTO etat_sante (id, description, type_animal_id) VALUES
  (1, 'Bonne santé',      1),
  (2, 'Malade',           1),
  (3, 'En convalescence', 1),
  (4, 'Bonne santé',      2),
  (5, 'Malade',           2),
  (6, 'Bonne santé',      3),
  (7, 'Malade',           3),
  (8, 'Bonne santé',      4),
  (9, 'Malade',           4)
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 5. PARAMÈTRES ÉLEVEUR (requis par l'advisor Python)
-- ============================================================
INSERT INTO parametres_eleveur (id, seuil_nes_vivants, nb_mises_bas_max,
    seuil_occupation_box_warning, seuil_occupation_box_critique)
VALUES (1, 7, 2, 0.80, 0.90)
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 6. TYPE ALIMENT (4 catégories obligatoires pour FABRICATION)
-- ============================================================
INSERT INTO type_aliment (id, libelle) VALUES
  (1, 'Énergétiques'),
  (2, 'Protéines'),
  (3, 'Minéraux'),
  (4, 'Vitamines')
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 7. INGRÉDIENTS (ressources locales Bobo-Dioulasso)
-- SOURCE: ONG Thamani — prix terrain secteur 24
-- ============================================================
INSERT INTO ingredient (id, nom, type_aliment_id) VALUES
  (1, 'Drèche de dolo',     1),   -- 63 FCFA/kg MS
  (2, 'Drèche de brasserie',1),   -- 59 FCFA/kg MS
  (3, 'Son de maïs',        1),   -- 117 FCFA/kg MS
  (4, 'Tourteau de coton',  2),   -- 158 FCFA/kg MS
  (5, 'Farine de poisson',  2),   -- 250 FCFA/kg MS
  (6, 'Coquillage concassé',3),   -- 90 FCFA/kg MS
  (7, 'Prémix vitamines',   4),   -- complément vitaminé
  (8, 'Sel (NaCl)',         3)    -- sel minéral
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 8. FOURNISSEUR
-- ============================================================
INSERT INTO fournisseur (id, nom) VALUES
  (1, 'AVIPRO SARL / WISIUM — Ouaga Pissy'),
  (2, 'ALF ISSEN — Burkina Faso'),
  (3, 'Marché de Bobo-Dioulasso')
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 9. TYPE VENTE
-- ============================================================
INSERT INTO type_vente (id, nom) VALUES
  (1, 'Vente sur pied'),
  (2, 'Vente boucherie')
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 10. ANIMAUX
-- 6 truies + 2 verrats + 8 porcs engraissement + 6 porcelets
-- Statut = id etat_sante
-- ============================================================

-- Truies (BOX-T1 = 3 places, BOX-T2 = 3 places → 6/6 = 100% occupé → alerte)
INSERT INTO animal (id, code_animal, type_animal_id, date_entree, poids_initial,
                    etat_sante_id, box_id, vendu, observations) VALUES
  (1,  'TRU-001', 1, CURRENT_DATE - INTERVAL '18 months', 55.0, 1, 1, false, 'Truie reproductrice principale'),
  (2,  'TRU-002', 1, CURRENT_DATE - INTERVAL '16 months', 52.0, 1, 1, false, NULL),
  (3,  'TRU-003', 1, CURRENT_DATE - INTERVAL '14 months', 58.0, 2, 1, false, 'Truie malade — traitement en cours'),
  (4,  'TRU-004', 1, CURRENT_DATE - INTERVAL '20 months', 60.0, 1, 2, false, NULL),
  (5,  'TRU-005', 1, CURRENT_DATE - INTERVAL '12 months', 54.0, 1, 2, false, NULL),
  (6,  'TRU-006', 1, CURRENT_DATE - INTERVAL '10 months', 50.0, 1, 2, false, 'Jeune truie'),

  -- Verrats (BOX-V1 = 2 places, 2/2 → 100% occupé)
  (7,  'VER-001', 2, CURRENT_DATE - INTERVAL '24 months', 120.0, 4, 3, false, 'Verrat principal'),
  (8,  'VER-002', 2, CURRENT_DATE - INTERVAL '18 months', 110.0, 4, 3, false, NULL),

  -- Porcs engraissement (BOX-E1 = 6 places → 5/6 = 83%, BOX-E2 = 6 places → 3/6 = 50%)
  (9,  'ENG-001', 3, CURRENT_DATE - INTERVAL '5 months', 20.0, 6, 4, false, NULL),
  (10, 'ENG-002', 3, CURRENT_DATE - INTERVAL '5 months', 22.0, 6, 4, false, NULL),
  (11, 'ENG-003', 3, CURRENT_DATE - INTERVAL '5 months', 19.0, 7, 4, false, 'Porc malade'),
  (12, 'ENG-004', 3, CURRENT_DATE - INTERVAL '4 months', 25.0, 6, 4, false, NULL),
  (13, 'ENG-005', 3, CURRENT_DATE - INTERVAL '4 months', 23.0, 6, 4, false, NULL),
  (14, 'ENG-006', 3, CURRENT_DATE - INTERVAL '4 months', 21.0, 6, 5, false, NULL),
  (15, 'ENG-007', 3, CURRENT_DATE - INTERVAL '3 months', 20.0, 6, 5, false, NULL),
  (16, 'ENG-008', 3, CURRENT_DATE - INTERVAL '3 months', 24.0, 6, 5, false, NULL),

  -- Porcelets sous la mère (BOX-P1 = 10 places)
  (17, 'POR-001', 4, CURRENT_DATE - INTERVAL '6 weeks', 6.0,  8, 6, false, 'Portée TRU-001'),
  (18, 'POR-002', 4, CURRENT_DATE - INTERVAL '6 weeks', 7.0,  8, 6, false, 'Portée TRU-001'),
  (19, 'POR-003', 4, CURRENT_DATE - INTERVAL '6 weeks', 5.5,  8, 6, false, 'Portée TRU-001'),
  (20, 'POR-004', 4, CURRENT_DATE - INTERVAL '6 weeks', 6.5,  9, 6, false, 'Portée TRU-001 — malade'),
  (21, 'POR-005', 4, CURRENT_DATE - INTERVAL '4 weeks', 4.0,  8, 6, false, 'Portée TRU-002'),
  (22, 'POR-006', 4, CURRENT_DATE - INTERVAL '4 weeks', 5.0,  8, 6, false, 'Portée TRU-002'),

  -- Animaux déjà vendus (pour KPI ventes)
  (23, 'ENG-V01', 3, CURRENT_DATE - INTERVAL '9 months', 20.0, 6, 4, true, 'Vendu'),
  (24, 'ENG-V02', 3, CURRENT_DATE - INTERVAL '9 months', 22.0, 6, 4, true, 'Vendu'),
  (25, 'ENG-V03', 3, CURRENT_DATE - INTERVAL '8 months', 20.0, 6, 4, true, 'Vendu'),
  (26, 'ENG-V04', 3, CURRENT_DATE - INTERVAL '8 months', 23.0, 6, 5, true, 'Vendu'),
  (27, 'ENG-V05', 3, CURRENT_DATE - INTERVAL '7 months', 21.0, 6, 5, true, 'Vendu')
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 11. REPRODUCTIONS (12 mois — couvre KPI prolificité, portées/truie, mortalité porcelet)
-- nb_nes_vivants : objectif ≥ 8, nos données oscillent autour de 6-9 (mixte)
-- nb_sevres : utilisé pour mortalité porcelet = (nés_vivants - sevrés) / nés_vivants
-- ============================================================
INSERT INTO reproduction (id, truie_id, verrat_id, date_saillie,
    date_mise_bas_prevue, date_mise_bas_reelle,
    nb_nes_vivants, nb_morts_nes, nb_sevres, observations)
VALUES
  -- TRU-001 : 2 portées sur 12 mois (bonnes performances)
  (1, 1, 7, CURRENT_DATE - INTERVAL '11 months',
     CURRENT_DATE - INTERVAL '7 months 2 weeks',
     CURRENT_DATE - INTERVAL '7 months 2 weeks',
     9, 1, 8, 'Bonne portée'),

  (2, 1, 7, CURRENT_DATE - INTERVAL '5 months',
     CURRENT_DATE - INTERVAL '6 weeks',
     CURRENT_DATE - INTERVAL '6 weeks',
     8, 0, 7, 'En cours allaitement'),

  -- TRU-002 : 2 portées
  (3, 2, 8, CURRENT_DATE - INTERVAL '10 months',
     CURRENT_DATE - INTERVAL '6 months 2 weeks',
     CURRENT_DATE - INTERVAL '6 months 2 weeks',
     7, 2, 5, 'Mortalité élevée — problème alimentaire'),

  (4, 2, 7, CURRENT_DATE - INTERVAL '4 months',
     CURRENT_DATE - INTERVAL '4 weeks',
     CURRENT_DATE - INTERVAL '4 weeks',
     6, 1, 5, NULL),

  -- TRU-003 : 1 portée (truie malade — performance basse)
  (5, 3, 8, CURRENT_DATE - INTERVAL '9 months',
     CURRENT_DATE - INTERVAL '5 months 2 weeks',
     CURRENT_DATE - INTERVAL '5 months 2 weeks',
     5, 3, 3, 'Truie malade — nb nés vivants faible'),

  -- TRU-004 : 2 portées
  (6, 4, 7, CURRENT_DATE - INTERVAL '11 months',
     CURRENT_DATE - INTERVAL '7 months 1 week',
     CURRENT_DATE - INTERVAL '7 months 1 week',
     8, 0, 7, NULL),

  (7, 4, 8, CURRENT_DATE - INTERVAL '5 months',
     CURRENT_DATE - INTERVAL '5 weeks',
     CURRENT_DATE - INTERVAL '5 weeks',
     7, 1, 6, NULL),

  -- TRU-005 : 1 portée
  (8, 5, 7, CURRENT_DATE - INTERVAL '8 months',
     CURRENT_DATE - INTERVAL '4 months 2 weeks',
     CURRENT_DATE - INTERVAL '4 months 2 weeks',
     9, 0, 9, 'Excellente portée'),

  -- TRU-006 : 1 saillie sans résultat (fertilité)
  (9, 6, 8, CURRENT_DATE - INTERVAL '4 months',
     CURRENT_DATE - INTERVAL '2 weeks',
     NULL,   -- pas encore mis bas
     NULL, NULL, NULL, 'Mise bas attendue')
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 12. ALIMENTATIONS — ACHAT (mois récents)
-- SOURCE: Données terrain Bobo-Dioulasso
-- ============================================================
INSERT INTO alimentation (id, date, mode, type_animal_id, fournisseur_id, source_reference) VALUES
  (1, CURRENT_DATE - INTERVAL '2 months', 'ACHAT', 1, 3, 'ONG Thamani — Bobo-Dioulasso'),
  (2, CURRENT_DATE - INTERVAL '2 months', 'ACHAT', 3, 3, 'ONG Thamani — Bobo-Dioulasso'),
  (3, CURRENT_DATE - INTERVAL '1 month',  'ACHAT', 1, 3, 'ONG Thamani — Bobo-Dioulasso'),
  (4, CURRENT_DATE - INTERVAL '1 month',  'ACHAT', 3, 2, 'ALF ISSEN'),
  (5, CURRENT_DATE - INTERVAL '15 days',  'FABRICATION', 1, NULL, 'DGPA/MRAH 2021'),
  (6, CURRENT_DATE - INTERVAL '10 days',  'FABRICATION', 3, NULL, 'DGPA/MRAH 2021')
ON CONFLICT (id) DO NOTHING;

-- Ingrédients des alimentations (utilisés pour calcul coût/kg)
-- Alimentation 1 : achat pour truies (son maïs + drèche dolo)
INSERT INTO ingredient_alimentation (id, alimentation_id, ingredient_id, quantite_kg, prix_unitaire) VALUES
  (1,  1, 3, 10.0, 117.0),  -- Son de maïs : 10 kg × 117 = 1 170 FCFA
  (2,  1, 1,  8.0,  63.0),  -- Drèche dolo : 8 kg × 63 = 504 FCFA
  (3,  1, 5,  2.0, 250.0),  -- Farine poisson : 2 kg × 250 = 500 FCFA

  -- Alimentation 2 : achat pour engraissement
  (4,  2, 3, 15.0, 117.0),  -- Son de maïs
  (5,  2, 2, 10.0,  59.0),  -- Drèche brasserie
  (6,  2, 4,  3.0, 158.0),  -- Tourteau coton

  -- Alimentation 3 : truies (mois dernier)
  (7,  3, 3, 12.0, 117.0),
  (8,  3, 1,  8.0,  63.0),
  (9,  3, 5,  2.5, 250.0),

  -- Alimentation 4 : engraissement (ALF ISSEN)
  (10, 4, 3, 20.0, 110.0),
  (11, 4, 4,  4.0, 158.0),
  (12, 4, 5,  4.0, 250.0),

  -- Alimentation 5 : fabrication truies gestantes (4 types requis)
  -- Ration DGPA/MRAH : son maïs 40% + drèche dolo 35% + farine poisson 5% + coquillage 2% + prémix 1%
  (13, 5, 3, 10.0, 117.0),  -- Énergétique (son maïs)
  (14, 5, 5,  1.5, 250.0),  -- Protéique (farine poisson)
  (15, 5, 6,  0.5,  90.0),  -- Minéraux (coquillage)
  (16, 5, 7,  0.3, 200.0),  -- Vitamines (prémix)

  -- Alimentation 6 : fabrication engraissement
  (17, 6, 3, 12.0, 117.0),  -- Énergétique
  (18, 6, 4,  3.0, 158.0),  -- Protéique
  (19, 6, 6,  0.5,  90.0),  -- Minéraux
  (20, 6, 7,  0.3, 200.0)   -- Vitamines
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- 13. VENTES (pour CA total et statistiques)
-- Prix sur pied Bobo-Dioulasso : 600 FCFA/kg
-- Porcs vendus : ENG-V01 à ENG-V05
-- ============================================================
INSERT INTO vente (id, date_vente, date_enlevement, date_enlevement_au_plus_tard,
                   client, poids_total, montant_total) VALUES
  -- Vente 1 : 2 porcs (ENG-V01 + ENG-V02) — il y a 3 mois
  (1, CURRENT_DATE - INTERVAL '3 months',
     CURRENT_DATE - INTERVAL '3 months',
     CURRENT_DATE - INTERVAL '3 months' + INTERVAL '3 days',
     'Boucher Kaboré — Bobo marché', 162.0, 97200.0),

  -- Vente 2 : 2 porcs (ENG-V03 + ENG-V04) — il y a 2 mois
  (2, CURRENT_DATE - INTERVAL '2 months',
     CURRENT_DATE - INTERVAL '2 months',
     CURRENT_DATE - INTERVAL '2 months' + INTERVAL '3 days',
     'Restaurant Le Savana — Bobo', 170.0, 102000.0),

  -- Vente 3 : 1 porc (ENG-V05) — il y a 1 mois
  (3, CURRENT_DATE - INTERVAL '1 month',
     CURRENT_DATE - INTERVAL '1 month',
     CURRENT_DATE - INTERVAL '1 month' + INTERVAL '2 days',
     'Boucher Traoré — secteur 22', 90.0, 54000.0)
ON CONFLICT (id) DO NOTHING;

-- Détails vente_animal (poids vente, prix unitaire)
INSERT INTO vente_animal (id, vente_id, animal_id, type_vente_id, mode_vente,
                          poids_vente, prix_unitaire, prix_negocie, montant_total) VALUES
  -- Vente 1
  (1, 1, 23, 1, 'AU_POIDS', 80.0, 600.0, NULL, 48000.0),   -- ENG-V01 : 80 kg × 600
  (2, 1, 24, 1, 'AU_POIDS', 82.0, 600.0, NULL, 49200.0),   -- ENG-V02 : 82 kg × 600

  -- Vente 2
  (3, 2, 25, 1, 'AU_POIDS', 85.0, 600.0, NULL, 51000.0),   -- ENG-V03
  (4, 2, 26, 1, 'AU_POIDS', 85.0, 600.0, NULL, 51000.0),   -- ENG-V04

  -- Vente 3
  (5, 3, 27, 1, 'AU_POIDS', 90.0, 600.0, NULL, 54000.0)    -- ENG-V05
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- VÉRIFICATION — requêtes rapides pour valider
-- ============================================================
-- SELECT 'Animaux actifs'      AS kpi, COUNT(*)::text AS valeur FROM animal WHERE NOT vendu;
-- SELECT 'Reproduction 12m'    AS kpi, COUNT(*)::text AS valeur FROM reproduction WHERE date_saillie >= current_date - INTERVAL '12 months';
-- SELECT 'Mises bas réelles'   AS kpi, COUNT(*)::text AS valeur FROM reproduction WHERE date_mise_bas_reelle IS NOT NULL AND date_mise_bas_reelle >= current_date - INTERVAL '12 months';
-- SELECT 'Prolificité moy.'    AS kpi, ROUND(AVG(nb_nes_vivants)::numeric,1)::text AS valeur FROM reproduction WHERE date_mise_bas_reelle IS NOT NULL AND date_mise_bas_reelle >= current_date - INTERVAL '12 months';
-- SELECT 'CA total ventes'     AS kpi, SUM(montant_total)::text AS valeur FROM vente WHERE date_vente >= current_date - INTERVAL '12 months';
-- SELECT 'Boxes occupation'    AS kpi, ROUND(SUM(occ)*100.0/SUM(cap),1)::text || '%' AS valeur FROM (SELECT b.capacite_max cap, COUNT(a.id) occ FROM box b LEFT JOIN animal a ON a.box_id = b.id AND NOT COALESCE(a.vendu,false) GROUP BY b.id, b.capacite_max) s;
