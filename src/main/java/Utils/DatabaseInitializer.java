package Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * Classe utilitaire pour initialiser la base de données
 */
public class DatabaseInitializer {
    
    /**
     * Initialise la base de données en exécutant les scripts SQL
     */
    public static void initialize() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Exécution du script pour la table evenement
            executeScript(conn, "/database.sql");
            
            // Exécution du script pour la table sessions
            executeScript(conn, "/sessions.sql");
            
            // Exécution du script pour ajouter la colonne prix
            executeScript(conn, "/add_prix_column.sql");
            
            // Exécution du script pour la table de réservation
            executeScript(conn, "/reservation.sql");
            
            // Affichage des tables créées
            printDatabaseTables(conn);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation de la base de données: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Exécute un script SQL
     */
    private static void executeScript(Connection conn, String scriptPath) throws Exception {
        System.out.println("Exécution du script SQL: " + scriptPath);
        
        String script = loadResourceAsString(scriptPath);
        
        if (script == null || script.trim().isEmpty()) {
            System.err.println("Le script SQL est vide ou n'a pas pu être chargé: " + scriptPath);
            return;
        }
        
        try (Statement stmt = conn.createStatement()) {
            // Exécuter le script
            stmt.execute(script);
            System.out.println("Script SQL exécuté avec succès: " + scriptPath);
        } catch (Exception e) {
            // Si le script échoue, afficher l'erreur mais continuer l'exécution
            System.err.println("Erreur lors de l'exécution du script " + scriptPath + ": " + e.getMessage());
        }
    }
    
    /**
     * Charge le contenu d'un fichier ressource en String
     */
    private static String loadResourceAsString(String resourcePath) {
        try (InputStream is = DatabaseInitializer.class.getResourceAsStream(resourcePath)) {
            if (is == null) {
                System.err.println("Ressource non trouvée: " + resourcePath);
                return null;
            }
            
            try (InputStreamReader isr = new InputStreamReader(is);
                 BufferedReader reader = new BufferedReader(isr)) {
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (IOException e) {
            System.err.println("Erreur lors de la lecture de la ressource: " + resourcePath);
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Affiche les tables existantes dans la base de données
     */
    private static void printDatabaseTables(Connection conn) {
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet rs = metaData.getTables(null, null, "%", new String[]{"TABLE"});
            
            System.out.println("Tables dans la base de données:");
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                System.out.println("- " + tableName);
                
                // Afficher les colonnes de la table
                ResultSet columns = metaData.getColumns(null, null, tableName, "%");
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    String columnType = columns.getString("TYPE_NAME");
                    System.out.println("  - " + columnName + " (" + columnType + ")");
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'affichage des tables: " + e.getMessage());
        }
    }
} 