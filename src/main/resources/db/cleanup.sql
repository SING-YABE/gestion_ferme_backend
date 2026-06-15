-- TEMPORAIRE : supprime l'ancien schéma avec nommage de colonnes legacy (idrole au lieu de id_role)
-- À retirer après le premier déploiement réussi.
DROP SCHEMA IF EXISTS ferme_default CASCADE;
DROP TABLE IF EXISTS public.tenants CASCADE;
DROP TABLE IF EXISTS public.invitations CASCADE;
