package Utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Classe utilitaire pour mettre à jour la structure de la base de données
 */
public class DatabaseUpdateScript {
    
    public static void main(String[] args) {
        try {
            // Obtenir une connexion à la base de données
            Connection connection = MyDatabase.getInstance().getConnection();
            LogUtils.info("DatabaseUpdate", "Connexion à la base de données établie");
            
            // Vérifier si la colonne prix_total existe déjà dans la table reservation
            if (!columnExists(connection, "reservation", "prix_total")) {
                LogUtils.info("DatabaseUpdate", "La colonne 'prix_total' n'existe pas, ajout en cours...");
                
                // Exécuter le script SQL pour ajouter la colonne
                Statement statement = connection.createStatement();
                statement.executeUpdate("ALTER TABLE reservation ADD COLUMN prix_total DOUBLE DEFAULT 0.0");
                LogUtils.info("DatabaseUpdate", "Colonne 'prix_total' ajoutée avec succès");
            } else {
                LogUtils.info("DatabaseUpdate", "La colonne 'prix_total' existe déjà");
            }
            
            // Vérifier les autres structures de tables
            checkEventTable(connection);
            checkSessionTable(connection);
            
            LogUtils.info("DatabaseUpdate", "Mise à jour de la base de données terminée avec succès");
            
        } catch (SQLException e) {
            LogUtils.error("DatabaseUpdate", "Erreur lors de la mise à jour de la base de données", e);
        } catch (Exception e) {
            LogUtils.error("DatabaseUpdate", "Erreur inattendue", e);
        }
    }
    
    /**
     * Vérifie si une colonne existe dans une table
     */
    private static boolean columnExists(Connection connection, String tableName, String columnName) throws SQLException {
        DatabaseMetaData metadata = connection.getMetaData();
        ResultSet resultSet = metadata.getColumns(null, null, tableName, columnName);
        boolean exists = resultSet.next();
        resultSet.close();
        return exists;
    }
    
    /**
     * Vérifie et met à jour la table evenement si nécessaire
     */
    private static void checkEventTable(Connection connection) throws SQLException {
        if (!columnExists(connection, "evenement", "prix")) {
            LogUtils.info("DatabaseUpdate", "La colonne 'prix' n'existe pas dans la table 'evenement', ajout en cours...");
            Statement statement = connection.createStatement();
            statement.executeUpdate("ALTER TABLE evenement ADD COLUMN prix DOUBLE DEFAULT 0.0");
            LogUtils.info("DatabaseUpdate", "Colonne 'prix' ajoutée avec succès à la table 'evenement'");
        }
    }
    
    /**
     * Vérifie et met à jour la table session si nécessaire
     */
    private static void checkSessionTable(Connection connection) throws SQLException {
        if (!columnExists(connection, "session", "capacity")) {
            LogUtils.info("DatabaseUpdate", "La colonne 'capacity' n'existe pas dans la table 'session', ajout en cours...");
            Statement statement = connection.createStatement();
            statement.executeUpdate("ALTER TABLE session ADD COLUMN capacity INT DEFAULT 0");
            LogUtils.info("DatabaseUpdate", "Colonne 'capacity' ajoutée avec succès à la table 'session'");
        }
    }
} 