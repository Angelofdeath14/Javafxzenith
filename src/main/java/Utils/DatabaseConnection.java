package Utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/artyphoria?useSSL=false&allowPublicKeyRetrieval=true";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    
    private static Connection connection = null;
    
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                System.out.println("DatabaseConnection: Tentative de connexion à " + URL);
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("DatabaseConnection: Connexion réussie à la base de données artyphoria");
                
                // Vérifier si la table session existe
                try (java.sql.Statement stmt = connection.createStatement()) {
                    java.sql.ResultSet rs = stmt.executeQuery("SHOW TABLES LIKE 'session'");
                    if (rs.next()) {
                        System.out.println("DatabaseConnection: La table 'session' existe dans la base de données");
                    } else {
                        System.err.println("DatabaseConnection: La table 'session' n'existe PAS dans la base de données!");
                    }
                }
            } catch (ClassNotFoundException e) {
                System.err.println("DatabaseConnection: MySQL JDBC Driver non trouvé!");
                throw new SQLException("MySQL JDBC Driver not found.", e);
            } catch (SQLException e) {
                System.err.println("DatabaseConnection: Erreur de connexion à la base de données: " + e.getMessage());
                throw e;
            }
        }
        return connection;
    }
    
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("DatabaseConnection: Connexion fermée");
            } catch (SQLException e) {
                System.err.println("DatabaseConnection: Erreur lors de la fermeture de la connexion: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
} 