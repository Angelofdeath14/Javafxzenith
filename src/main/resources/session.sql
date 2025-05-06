CREATE TABLE IF NOT EXISTS session (
    id INT PRIMARY KEY AUTO_INCREMENT,
    titre VARCHAR(255) NOT NULL,
    description TEXT,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    evenement_id INT NOT NULL,
    image VARCHAR(255),
    capacity INT DEFAULT 0,
    available_seats INT DEFAULT 0,
    location VARCHAR(255),
    FOREIGN KEY (evenement_id) REFERENCES evenement(id) ON DELETE CASCADE
); 