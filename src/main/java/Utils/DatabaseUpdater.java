package Utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.Statement;

/**
 * Utilitaire pour mettre à jour la structure de la base de données
 */
public class DatabaseUpdater {
    
    /**
     * Exécute les scripts SQL de mise à jour de la base de données
     */
    public static void updateDatabase() {
        try {
            Connection conn = DataSource.getInstance().getConnection();
            
            // Exécuter le script de mise à jour de la table reservation
            executeScript(conn, "/update_reservation_table.sql");
            
            System.out.println("Base de données mise à jour avec succès !");
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour de la base de données: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Exécute un script SQL depuis les ressources
     */
    private static void executeScript(Connection conn, String scriptPath) throws Exception {
        InputStream is = DatabaseUpdater.class.getResourceAsStream(scriptPath);
        if (is == null) {
            throw new Exception("Script SQL introuvable: " + scriptPath);
        }
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is));
             Statement stmt = conn.createStatement()) {
            
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                // Ignorer les commentaires et les lignes vides
                if (line.trim().isEmpty() || line.trim().startsWith("--")) {
                    continue;
                }
                
                sb.append(line);
                
                // Si la ligne se termine par un point-virgule, c'est la fin d'une instruction
                if (line.trim().endsWith(";")) {
                    String sql = sb.toString();
                    stmt.execute(sql);
                    sb = new StringBuilder();
                }
            }
        }
    }
} 