-- Création de la base de données si elle n'existe pas déjà
-- Le JDBC se connecte déjà à la base de données artyphoria, donc nous n'avons pas besoin de l'instruction USE

-- Table des événements
CREATE TABLE IF NOT EXISTS evenement (
    id INT PRIMARY KEY AUTO_INCREMENT,
    titre VARCHAR(255) NOT NULL,
    description TEXT,
    type VARCHAR(100),
    location VARCHAR(255),
    dateD DATETIME,
    dateF DATETIME,
    image VARCHAR(255),
    nbPlace INT
);

-- Pas d'insertion de données de test pour le moment, car le schéma a changé

-- Table des sessions - définition supprimée car nous utilisons sessions.sql 