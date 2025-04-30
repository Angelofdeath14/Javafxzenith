package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDatabase {
    // Configuration de base de données XAMPP par défaut
    private final String URL = "jdbc:mysql://localhost:3306/artyphoria?useSSL=false&allowPublicKeyRetrieval=true";
    private final String USERNAME = "root";
    private final String PASSWORD = ""; // Mot de passe vide pour XAMPP par défaut
    
    private Connection connection;
    private static MyDatabase instance;

    private MyDatabase() {
        try {
            // Tentative de connexion
            System.out.println("Tentative de connexion à la base de données...");
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            System.out.println("Connexion établie avec succès!");
            
            // Créer la base de données si elle n'existe pas
            try (java.sql.Statement stmt = connection.createStatement()) {
                // Créer la base de données
                stmt.execute("CREATE DATABASE IF NOT EXISTS artyphoria");
                stmt.execute("USE artyphoria");
                
                // Création de la table evenement
                String createTableSQL = "CREATE TABLE IF NOT EXISTS evenement (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY," +
                    "titre VARCHAR(255) NOT NULL," +
                    "description TEXT," +
                    "type VARCHAR(100)," +
                    "location VARCHAR(255)," +
                    "dateD DATETIME," +
                    "dateF DATETIME," +
                    "image VARCHAR(255)," +
                    "nbPlace INT" +
                ")";
                
                System.out.println("Création de la table avec la requête : " + createTableSQL);
                stmt.execute(createTableSQL);
                System.out.println("Table 'evenement' créée avec succès!");
                
                // Vérifier la structure de la table
                java.sql.ResultSet rs = stmt.executeQuery("DESCRIBE evenement");
                System.out.println("\nStructure de la table 'evenement' :");
                while (rs.next()) {
                    System.out.println(rs.getString("Field") + " - " + rs.getString("Type"));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("Erreur de connexion : " + e.getMessage());
            System.err.println("Code d'erreur : " + e.getErrorCode());
            System.err.println("État SQL : " + e.getSQLState());
            e.printStackTrace();
            
            // Tentative de connexion alternative
            try {
                String alternativeURL = "jdbc:mysql://127.0.0.1:3306/artyphoria?useSSL=false&allowPublicKeyRetrieval=true";
                System.out.println("Tentative de connexion alternative...");
                connection = DriverManager.getConnection(alternativeURL, USERNAME, PASSWORD);
                System.out.println("Connexion alternative établie avec succès!");
            } catch (SQLException e2) {
                System.err.println("Échec de la connexion alternative : " + e2.getMessage());
                e2.printStackTrace();
            }
        }
    }

    public static MyDatabase getInstance() {
        if (instance == null) {
            instance = new MyDatabase();
        }
        return instance;
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                instance = new MyDatabase();
            }
        } catch (SQLException e) {
            System.err.println("Erreur de vérification de la connexion : " + e.getMessage());
            e.printStackTrace();
        }
        return connection;
    }
}
