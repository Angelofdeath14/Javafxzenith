-- Création de la table de réservation
CREATE TABLE IF NOT EXISTS reservation (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    event_id INT NOT NULL,
    session_id INT NOT NULL,
    nombre_places INT NOT NULL,
    date_reservation DATETIME NOT NULL,
    statut VARCHAR(50) NOT NULL,
    prix_total DOUBLE DEFAULT 0.0,
    FOREIGN KEY (event_id) REFERENCES evenement(id) ON DELETE CASCADE,
    FOREIGN KEY (session_id) REFERENCES session(id) ON DELETE CASCADE
); 

-- S'assurer que la colonne prix_total existe
ALTER TABLE reservation ADD COLUMN IF NOT EXISTS prix_total DOUBLE DEFAULT 0.0; 