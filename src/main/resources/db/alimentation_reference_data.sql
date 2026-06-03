-- =============================================================================
-- SEED : Données de référence pour le module Alimentation — Élevage porcin BF
--
-- SOURCE: Fiche technique n°3 alimentation porcs — ONG Thamani, Bobo-Dioulasso — secteur 24
-- SOURCE: Fiche simplifiée alimentation des porcs — DGPA/MRAH, Burkina Faso — Juin 2021
--         (Projet PADEL-B, Banque Mondiale, Crédit IDA P159476)
-- SOURCE: Formule Prémix 3% Porcelet 1er âge — AVIPRO SARL / WISIUM, Ouagadougou Pissy
-- SOURCE: Formule Prémix 3% Truie Allaitante — AVIPRO SARL / WISIUM, Ouagadougou Pissy
-- SOURCE: Formule Prémix 2.7% Croissance — AVIPRO SARL / WISIUM, Ouagadougou Pissy
-- SOURCE: Formule Prémix 2.5% Truie Gestante — AVIPRO SARL / WISIUM, Ouagadougou Pissy
-- SOURCE: Formule Aliment Porc avec Prémix ALF ISSEN — ALF ISSEN, Burkina Faso
-- SOURCE: Formule VITALAC — CCTRE / VITALAC, Burkina Faso
-- Disponible dans les documents joints du projet
-- =============================================================================


-- -----------------------------------------------------------------------------
-- TABLE : type_aliment — 4 catégories nutritionnelles de base
-- SOURCE: Fiche technique n°3 alimentation porcs — ONG Thamani, Bobo-Dioulasso — secteur 24
-- -----------------------------------------------------------------------------
INSERT INTO type_aliment (id, libelle) VALUES
    (1, 'Énergétique'),
    (2, 'Protéique'),
    (3, 'Minéraux'),
    (4, 'Vitamines / Prémix')
ON CONFLICT (id) DO NOTHING;


-- -----------------------------------------------------------------------------
-- TABLE : ingredient — Ingrédients locaux disponibles à Bobo-Dioulasso
--
-- SOURCE: Fiche technique n°3 alimentation porcs — ONG Thamani, Bobo-Dioulasso — secteur 24
-- Données collectées sur le terrain, secteur 24, Bobo-Dioulasso
-- Prix en FCFA par kg de Matière Sèche (MS)
-- -----------------------------------------------------------------------------
INSERT INTO ingredient (id, nom, type_aliment_id) VALUES
-- Énergétiques
    (101, 'Drèche de Dolo',          1),   -- 63 FCFA/kg MS  — 100 FCFA/14 litres, 113 g MS/litre
    (102, 'Drèche de Bière',         1),   -- 59 FCFA/kg MS  — 20 000 FCFA/benne (3120 l), 109 g MS/litre
    (103, 'Son de Maïs',             1),   -- 117 FCFA/kg MS — 75 FCFA/boîte 2 litres, 320 g MS/litre
-- Protéiques
    (104, 'Tourteau de Coton',       2),   -- 158 FCFA/kg MS — 3 000 FCFA/sac 50 kg (volume)
    (105, 'Poisson Séché',           2),   -- 250 FCFA/kg MS — 12 500 FCFA/sac 50 kg (poids)
    (106, 'Sang Séché',              2),   -- 150 FCFA/kg MS — données terrain Thamani
    (107, 'Farine de Poisson 65%',   2),   -- utilisée dans formules WISIUM / ALF ISSEN
    (108, 'Tourteau de Soja',        2),   -- utilisé dans formules WISIUM / ALF ISSEN / VITALAC
-- Minéraux
    (109, 'Coquillage Concassé',     3),   -- 90 FCFA/kg MS — 4 500 FCFA/sac 50 kg (poids)
    (110, 'Sel (NaCl)',              3),   -- max 3% de la ration
    (111, 'Calcaire',                3),   -- utilisé dans formules WISIUM
-- Vitamines / Prémix
    (112, 'Prémix Général',          4),   -- CMV / Prémix commercial générique
    (113, 'Prémix WISIUM Porcelet 3%',  4), -- AVIPRO SARL / WISIUM — Ouaga Pissy
    (114, 'Prémix WISIUM Allaitante 3%',4), -- AVIPRO SARL / WISIUM — Ouaga Pissy
    (115, 'Prémix WISIUM Croissance 2.7%', 4), -- AVIPRO SARL / WISIUM — Ouaga Pissy
    (116, 'Prémix WISIUM Gestante 2.5%',   4), -- AVIPRO SARL / WISIUM — Ouaga Pissy
    (117, 'Prémix ALF ISSEN Porcelets 4%', 4), -- ALF ISSEN, Burkina Faso
    (118, 'Prémix ALF ISSEN Engraissement 2%', 4), -- ALF ISSEN, Burkina Faso
    (119, 'Prémix ALF ISSEN Truies 2%', 4),   -- ALF ISSEN, Burkina Faso
    (120, 'AMV Truie (VITALAC)',     4),   -- VITALAC / CCTRE — 15–20 kg/tonne
    (121, 'AMV Porc (VITALAC)',      4)    -- VITALAC / CCTRE — 20 kg/tonne
ON CONFLICT (id) DO NOTHING;


-- -----------------------------------------------------------------------------
-- TABLE : ration_reference — Rations officielles par stade physiologique
--         (proportions % MS pour 1 kg d'aliment)
--
-- SOURCE: Fiche simplifiée alimentation des porcs — DGPA/MRAH, Burkina Faso — Juin 2021
--         Tableau 1 : Exemples de formulation de rations alimentaires
-- SOURCE: Fiche technique n°3 alimentation porcs — ONG Thamani, Bobo-Dioulasso — secteur 24
--         Tableau des rations alimentaires (synthèse)
-- -----------------------------------------------------------------------------
-- NB : Cette table est optionnelle si les données sont gérées en mémoire via AlimentationReferenceData.kt
--      Elle est fournie ici pour les besoins de reporting SQL ou d'audit documentaire.

CREATE TABLE IF NOT EXISTS ration_reference (
    id                    BIGSERIAL PRIMARY KEY,
    stade                 VARCHAR(50)  NOT NULL UNIQUE,
    son_mais_pct          NUMERIC(5,2) NOT NULL,
    dreche_brasserie_pct  NUMERIC(5,2) NOT NULL,
    dreche_dolo_pct       NUMERIC(5,2) NOT NULL,
    tourteau_coton_pct    NUMERIC(5,2) NOT NULL,
    farine_poisson_pct    NUMERIC(5,2) NOT NULL,
    coquillage_pct        NUMERIC(5,2) NOT NULL,
    sel_pct               NUMERIC(5,2) NOT NULL,
    qte_journaliere_min_kg NUMERIC(5,2) NOT NULL,
    qte_journaliere_max_kg NUMERIC(5,2) NOT NULL,
    eau_litres             NUMERIC(5,2) NOT NULL,
    source_reference       VARCHAR(300) NOT NULL
);

-- SOURCE: DGPA/MRAH Juin 2021 (Tableau 1) + ONG Thamani (Tableau synthèse)
INSERT INTO ration_reference (stade, son_mais_pct, dreche_brasserie_pct, dreche_dolo_pct,
    tourteau_coton_pct, farine_poisson_pct, coquillage_pct, sel_pct,
    qte_journaliere_min_kg, qte_journaliere_max_kg, eau_litres, source_reference)
VALUES
-- Porcelet sevrage 7–25 kg
('PORCELET_SEVRAGE',  35.0, 25.0, 20.0, 10.0, 10.0, 1.0, 0.3, 0.5, 0.7, 2.0,
 'DGPA/MRAH 2021 (Tableau 1) + ONG Thamani — Porcelet 7–25 kg'),

-- Croissance 25–60 kg
('CROISSANCE',        35.0, 25.0, 20.0, 10.0,  5.0, 1.0, 0.3, 1.0, 2.5, 4.0,
 'DGPA/MRAH 2021 (Tableau 1) + ONG Thamani — Croissance 25–60 kg'),

-- Finition > 60 kg
('FINITION',          35.0, 35.0, 20.0,  5.0,  5.0, 1.0, 0.3, 2.5, 3.0, 6.0,
 'DGPA/MRAH 2021 (Tableau 1) + ONG Thamani — Finition > 60 kg'),

-- Truie gestante
('TRUIE_GESTANTE',    40.0, 15.0, 35.0,  0.0,  5.0, 2.0, 0.3, 2.5, 3.5, 10.0,
 'DGPA/MRAH 2021 (Tableau 1) + ONG Thamani — Truie gestante (sans tourteau de coton)'),

-- Truie allaitante
('TRUIE_ALLAITANTE',  40.0, 15.0, 30.0,  5.0, 10.0, 0.2, 0.3, 4.5, 5.0, 25.0,
 'DGPA/MRAH 2021 (Tableau 1) + ONG Thamani — Truie allaitante (besoin élevé protéines)'),

-- Truie vide (ration gestante par défaut — DGPA/MRAH ne précise pas de formule distincte)
('TRUIE_VIDE',        40.0, 15.0, 35.0,  0.0,  5.0, 2.0, 0.3, 2.0, 2.7, 6.0,
 'DGPA/MRAH 2021 — Truie vide (ration similaire truie gestante par défaut)'),

-- Verrat (ration croissance par défaut — DGPA/MRAH)
('VERRAT',            35.0, 25.0, 20.0, 10.0,  5.0, 1.0, 0.3, 2.0, 2.7, 6.0,
 'DGPA/MRAH 2021 — Verrat (ration similaire croissance par défaut)')

ON CONFLICT (stade) DO NOTHING;


-- -----------------------------------------------------------------------------
-- TABLE : ingredient_prix_reference — Prix terrain des ingrédients locaux (FCFA/kg MS)
--
-- SOURCE: Fiche technique n°3 alimentation porcs — ONG Thamani, Bobo-Dioulasso — secteur 24
-- Données collectées à Bobo-Dioulasso, secteur 24
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS ingredient_prix_reference (
    id                   BIGSERIAL PRIMARY KEY,
    cle                  VARCHAR(50)  NOT NULL UNIQUE,
    nom                  VARCHAR(100) NOT NULL,
    poids_par_litre_g    NUMERIC(8,2),    -- g de produit frais par litre (si applicable)
    ms_pour_un_litre_g   NUMERIC(8,2),    -- g de MS par litre (si applicable)
    prix_par_kg_ms_fcfa  NUMERIC(8,2) NOT NULL,
    type_aliment         VARCHAR(50)  NOT NULL,
    source_reference     VARCHAR(300) NOT NULL
);

-- SOURCE: ONG Thamani — Fiche technique n°3, Bobo-Dioulasso, secteur 24
INSERT INTO ingredient_prix_reference (cle, nom, poids_par_litre_g, ms_pour_un_litre_g,
    prix_par_kg_ms_fcfa, type_aliment, source_reference)
VALUES
('dreche_dolo',    'Drèche de Dolo',      520,  113,  63,  'Énergétique',
 'ONG Thamani — 100 FCFA/14 litres, 113 g MS/litre, Bobo-Dioulasso secteur 24'),

('dreche_biere',   'Drèche de Bière',     443,  109,  59,  'Énergétique',
 'ONG Thamani — 20 000 FCFA/benne (3120 l), 109 g MS/litre, Bobo-Dioulasso secteur 24'),

('son_mais',       'Son de Maïs',         320,  320,  117, 'Énergétique',
 'ONG Thamani — 75 FCFA/boîte 2 litres (non trié), 320 g MS/litre, Bobo-Dioulasso secteur 24'),

('tourteau_coton', 'Tourteau de Coton',   380,  380,  158, 'Protéique',
 'ONG Thamani — 3 000 FCFA/sac 50 kg (volume), Bobo-Dioulasso secteur 24'),

('poisson_seche',  'Poisson Séché',       600,  600,  250, 'Protéique',
 'ONG Thamani — 12 500 FCFA/sac 50 kg (poids), Bobo-Dioulasso secteur 24'),

('sang_seche',     'Sang Séché',          NULL, NULL, 150, 'Protéique',
 'ONG Thamani — données terrain, Bobo-Dioulasso secteur 24'),

('coquillage',     'Coquillage Concassé', 1300, 1300, 90,  'Minéraux',
 'ONG Thamani — 4 500 FCFA/sac 50 kg (poids), Bobo-Dioulasso secteur 24'),

('sel',            'Sel (NaCl)',          NULL, NULL, 0,   'Minéraux',
 'ONG Thamani — max 3% de la ration, prix non renseigné')

ON CONFLICT (cle) DO NOTHING;


-- -----------------------------------------------------------------------------
-- COMMENTAIRE RÉCAPITULATIF — Règle économique clé
--
-- SOURCE: Fiche technique n°3 alimentation porcs — ONG Thamani, Bobo-Dioulasso — secteur 24
--
-- "Le prix de revient d'1 kg d'aliment ne doit pas dépasser 1/6 du prix de vente du kg sur pied."
-- À Bobo-Dioulasso : prix vente = 600 FCFA/kg → coût aliment cible ≤ 100 FCFA/kg
-- Cette règle est implémentée dans AlimentationServiceImpl.calculerCoutRation()
-- et dans AlimentationReferenceData.COUT_MAX_KG_ALIMENT_FCFA
-- -----------------------------------------------------------------------------
