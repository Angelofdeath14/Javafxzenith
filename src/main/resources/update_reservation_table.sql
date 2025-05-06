-- Ajouter la colonne prix_total avec une valeur par défaut de 0
ALTER TABLE reservation ADD COLUMN IF NOT EXISTS prix_total DOUBLE DEFAULT 0.0; 