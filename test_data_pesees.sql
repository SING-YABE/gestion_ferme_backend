-- ============================================================
-- DONNÉES DE TEST — Pesées et dates de naissance
-- Complément de test_data.sql
-- Exécuter APRÈS test_data.sql
-- ============================================================

-- ============================================================
-- 1. AJOUTER date_naissance AUX ANIMAUX EXISTANTS
-- (les porcs engraissement qui ont été / seront vendus)
-- date_naissance = date_entree - 21 jours (mise en box après sevrage)
-- ============================================================

UPDATE animal SET date_naissance = date_entree - INTERVAL '21 days'
WHERE id IN (9, 10, 11, 12, 13, 14, 15, 16);  -- ENG actifs

UPDATE animal SET date_naissance = date_entree - INTERVAL '21 days'
WHERE id IN (23, 24, 25, 26, 27);  -- ENG vendus (pour âge à la vente)

-- Porcelets : nés il y a 6 semaines (= date de la mise bas de TRU-001)
UPDATE animal SET date_naissance = CURRENT_DATE - INTERVAL '6 weeks'
WHERE id IN (17, 18, 19, 20);

UPDATE animal SET date_naissance = CURRENT_DATE - INTERVAL '4 weeks'
WHERE id IN (21, 22);

-- ============================================================
-- 2. TABLE PESÉE — Créée automatiquement par Spring Boot JPA
-- Si elle n'existe pas encore, créer manuellement :
-- ============================================================
CREATE TABLE IF NOT EXISTS pesee (
    id           BIGSERIAL PRIMARY KEY,
    animal_id    BIGINT NOT NULL REFERENCES animal(id),
    poids        DOUBLE PRECISION NOT NULL,
    date_pesee   DATE NOT NULL,
    observations VARCHAR(255)
);

-- ============================================================
-- 3. PESÉES DES PORCS EN ENGRAISSEMENT
-- Objectif GMQ simulé : ~450–550 g/jour (objectif BF : 400-600)
-- Chaque animal a au moins 2 pesées espacées pour calcul GMQ
-- ============================================================

-- ENG-001 (id=9) — entré il y a 5 mois, poids initial 20 kg
INSERT INTO pesee (animal_id, poids, date_pesee, observations) VALUES
  (9, 20.0, CURRENT_DATE - INTERVAL '5 months',         'Pesée d''entrée'),
  (9, 41.5, CURRENT_DATE - INTERVAL '3 months 15 days', 'Pesée intermédiaire'),  -- +21.5 kg en 45j → GMQ ~478 g/j
  (9, 62.0, CURRENT_DATE - INTERVAL '2 months',          'Pesée mensuelle');      -- +20.5 kg en 45j → GMQ ~456 g/j

-- ENG-002 (id=10) — poids initial 22 kg
INSERT INTO pesee (animal_id, poids, date_pesee, observations) VALUES
  (10, 22.0, CURRENT_DATE - INTERVAL '5 months',         'Pesée d''entrée'),
  (10, 46.0, CURRENT_DATE - INTERVAL '3 months',         'Pesée 2 mois'),  -- +24 kg en 60j → GMQ 400 g/j
  (10, 68.5, CURRENT_DATE - INTERVAL '1 month',          'Pesée récente'); -- +22.5 kg en 60j → GMQ 375 g/j

-- ENG-003 (id=11) — MALADE, croissance plus lente
INSERT INTO pesee (animal_id, poids, date_pesee, observations) VALUES
  (11, 19.0, CURRENT_DATE - INTERVAL '5 months', 'Pesée d''entrée'),
  (11, 35.0, CURRENT_DATE - INTERVAL '2 months', 'Pesée — animal malade, retard de croissance'); -- +16 kg en 90j → GMQ ~178 g/j (alerte)

-- ENG-004 (id=12) — entré il y a 4 mois
INSERT INTO pesee (animal_id, poids, date_pesee, observations) VALUES
  (12, 25.0, CURRENT_DATE - INTERVAL '4 months',         'Pesée d''entrée'),
  (12, 49.0, CURRENT_DATE - INTERVAL '2 months',          'Pesée 2 mois'),  -- +24 kg en 60j → GMQ 400 g/j
  (12, 74.0, CURRENT_DATE - INTERVAL '2 weeks',           'Pesée récente'); -- +25 kg en ~50j → GMQ 500 g/j

-- ENG-005 (id=13)
INSERT INTO pesee (animal_id, poids, date_pesee, observations) VALUES
  (13, 23.0, CURRENT_DATE - INTERVAL '4 months',  'Pesée d''entrée'),
  (13, 53.5, CURRENT_DATE - INTERVAL '2 months',  'Pesée 2 mois'),   -- +30.5 kg en 60j → GMQ ~508 g/j
  (13, 77.0, CURRENT_DATE - INTERVAL '10 days',   'Pesée récente');  -- +23.5 kg en ~50j → GMQ ~470 g/j

-- ENG-006 (id=14)
INSERT INTO pesee (animal_id, poids, date_pesee, observations) VALUES
  (14, 21.0, CURRENT_DATE - INTERVAL '4 months', 'Pesée d''entrée'),
  (14, 51.0, CURRENT_DATE - INTERVAL '2 months', 'Pesée 2 mois'),   -- GMQ 500 g/j
  (14, 72.5, CURRENT_DATE - INTERVAL '2 weeks',  'Pesée récente');

-- ENG-007 (id=15) — entré il y a 3 mois
INSERT INTO pesee (animal_id, poids, date_pesee, observations) VALUES
  (15, 20.0, CURRENT_DATE - INTERVAL '3 months',  'Pesée d''entrée'),
  (15, 47.0, CURRENT_DATE - INTERVAL '1 month',   'Pesée 2 mois');  -- +27 kg en 60j → GMQ 450 g/j

-- ENG-008 (id=16)
INSERT INTO pesee (animal_id, poids, date_pesee, observations) VALUES
  (16, 24.0, CURRENT_DATE - INTERVAL '3 months', 'Pesée d''entrée'),
  (16, 52.0, CURRENT_DATE - INTERVAL '1 month',  'Pesée 2 mois');   -- +28 kg en 60j → GMQ ~467 g/j

-- Animaux vendus — pesées au moment de la vente (pour historique)
INSERT INTO pesee (animal_id, poids, date_pesee, observations) VALUES
  (23, 20.0, CURRENT_DATE - INTERVAL '9 months',  'Pesée d''entrée'),
  (23, 80.0, CURRENT_DATE - INTERVAL '3 months',  'Pesée avant vente'); -- 60 kg en 180j → GMQ 333 g/j

INSERT INTO pesee (animal_id, poids, date_pesee, observations) VALUES
  (24, 22.0, CURRENT_DATE - INTERVAL '9 months',  'Pesée d''entrée'),
  (24, 82.0, CURRENT_DATE - INTERVAL '3 months',  'Pesée avant vente'); -- 60 kg en 180j → GMQ 333 g/j

INSERT INTO pesee (animal_id, poids, date_pesee, observations) VALUES
  (25, 20.0, CURRENT_DATE - INTERVAL '8 months',  'Pesée d''entrée'),
  (25, 85.0, CURRENT_DATE - INTERVAL '2 months',  'Pesée avant vente'); -- 65 kg en 180j → GMQ 361 g/j

INSERT INTO pesee (animal_id, poids, date_pesee, observations) VALUES
  (26, 23.0, CURRENT_DATE - INTERVAL '8 months',  'Pesée d''entrée'),
  (26, 85.0, CURRENT_DATE - INTERVAL '2 months',  'Pesée avant vente');

INSERT INTO pesee (animal_id, poids, date_pesee, observations) VALUES
  (27, 21.0, CURRENT_DATE - INTERVAL '7 months',  'Pesée d''entrée'),
  (27, 90.0, CURRENT_DATE - INTERVAL '1 month',   'Pesée avant vente'); -- 69 kg en 180j → GMQ 383 g/j

-- ============================================================
-- VÉRIFICATION RAPIDE
-- ============================================================
-- Décommenter pour tester après insertion :

-- SELECT 'GMQ moyen' AS kpi,
--   ROUND(AVG((poids_final - poids_initial)
--     / NULLIF(EXTRACT(DAY FROM (date_derniere - date_premiere)), 0)
--     * 1000)::numeric, 1)::text || ' g/jour' AS valeur
-- FROM (
--   SELECT animal_id,
--     MIN(poids) AS poids_initial, MAX(poids) AS poids_final,
--     MIN(date_pesee) AS date_premiere, MAX(date_pesee) AS date_derniere
--   FROM pesee
--   WHERE date_pesee >= current_date - INTERVAL '12 months'
--   GROUP BY animal_id
--   HAVING COUNT(*) >= 2 AND MAX(poids) > MIN(poids)
-- ) sub;

-- SELECT 'Âge moyen vente' AS kpi,
--   ROUND(AVG(EXTRACT(DAY FROM (v.date_vente - a.date_naissance)))::numeric, 0)::text || ' jours' AS valeur
-- FROM vente v
-- JOIN vente_animal va ON va.vente_id = v.id
-- JOIN animal a ON a.id = va.animal_id
-- WHERE v.date_vente >= current_date - INTERVAL '12 months'
--   AND a.date_naissance IS NOT NULL;

-- SELECT 'Poids moyen vente' AS kpi,
--   ROUND(AVG(va.poids_vente)::numeric, 1)::text || ' kg' AS valeur
-- FROM vente_animal va
-- JOIN vente v ON v.id = va.vente_id
-- WHERE v.date_vente >= current_date - INTERVAL '12 months'
--   AND va.poids_vente IS NOT NULL;
