-- Script pour ajouter la colonne prix à la table evenement
ALTER TABLE evenement ADD COLUMN IF NOT EXISTS prix DOUBLE DEFAULT 0.0; 