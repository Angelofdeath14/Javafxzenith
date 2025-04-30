CREATE TABLE IF NOT EXISTS sessions (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    start_time DATETIME NOT NULL,
    end_time DATETIME NOT NULL,
    event_id INT NOT NULL,
    image VARCHAR(255),
    capacity INT DEFAULT 0,
    available_seats INT DEFAULT 0,
    location VARCHAR(255),
    FOREIGN KEY (event_id) REFERENCES evenement(id) ON DELETE CASCADE
); 