-- Script de mise à jour de la structure de la base de données
-- À exécuter sur la base de données artyphoria

-- S'assurer que la colonne prix_total existe dans la table reservation
ALTER TABLE reservation ADD COLUMN IF NOT EXISTS prix_total DOUBLE DEFAULT 0.0;

-- S'assurer que la colonne prix existe dans la table evenement
ALTER TABLE evenement ADD COLUMN IF NOT EXISTS prix DOUBLE DEFAULT 0.0;

-- S'assurer que la colonne capacity existe dans la table session
ALTER TABLE session ADD COLUMN IF NOT EXISTS capacity INT DEFAULT 0;

-- S'assurer que la colonne available_seats existe dans la table session
ALTER TABLE session ADD COLUMN IF NOT EXISTS available_seats INT DEFAULT 0;

-- S'assurer que la colonne location existe dans la table session
ALTER TABLE session ADD COLUMN IF NOT EXISTS location VARCHAR(255) DEFAULT '';

-- Mettre à jour la capacité des sessions existantes avec les valeurs de nbPlace des événements
UPDATE session s 
JOIN evenement e ON s.evenement_id = e.id 
SET s.capacity = e.nbPlace 
WHERE s.capacity = 0 AND e.nbPlace > 0;

-- Mettre à jour le nombre de places disponibles s'il est à 0
UPDATE session 
SET available_seats = capacity 
WHERE available_seats = 0 AND capacity > 0;

-- Ajouter le lieu de l'événement aux sessions qui n'en ont pas
UPDATE session s
JOIN evenement e ON s.evenement_id = e.id
SET s.location = COALESCE(e.location, 'Non spécifié')
WHERE (s.location IS NULL OR s.location = ''); 