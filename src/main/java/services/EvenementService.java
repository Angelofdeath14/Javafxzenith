package services;

import Utils.MyDatabase;
import Entity.Evenement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EvenementService {
    private final Connection connection;

    public EvenementService() throws SQLException {
        this.connection = MyDatabase.getInstance().getConnection();
    }

    public List<Evenement> getAllEvents() {
        List<Evenement> events = new ArrayList<>();
        String query = "SELECT * FROM evenement";
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            
            while (resultSet.next()) {
                Evenement evenement = new Evenement();
                evenement.setId(resultSet.getInt("id"));
                evenement.setTitre(resultSet.getString("titre"));
                evenement.setDescription(resultSet.getString("description"));
                evenement.setType(resultSet.getString("type"));
                evenement.setLocation(resultSet.getString("location"));
                
                // Gestion des dates avec Timestamp pour éviter les problèmes de format
                Timestamp dateD = resultSet.getTimestamp("dateD");
                Timestamp dateF = resultSet.getTimestamp("dateF");
                
                if (dateD != null) {
                    evenement.setDateD(dateD.toLocalDateTime());
                }
                
                if (dateF != null) {
                    evenement.setDateF(dateF.toLocalDateTime());
                }
                
                evenement.setImage(resultSet.getString("image"));
                evenement.setNbPlace(resultSet.getInt("nbPlace"));
                
                // Récupérer le prix s'il existe
                try {
                    evenement.setPrix(resultSet.getDouble("prix"));
                } catch (SQLException e) {
                    // La colonne prix n'existe peut-être pas encore, utiliser la valeur par défaut
                    evenement.setPrix(0.0);
                }
                
                events.add(evenement);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return events;
    }

    // Alias pour getAllEvents
    public List<Evenement> getAllEvenements() {
        return getAllEvents();
    }

    public Evenement getOne(int id) {
        String query = "SELECT * FROM evenement WHERE id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                Evenement evenement = new Evenement();
                evenement.setId(resultSet.getInt("id"));
                evenement.setTitre(resultSet.getString("titre"));
                evenement.setDescription(resultSet.getString("description"));
                evenement.setType(resultSet.getString("type"));
                evenement.setLocation(resultSet.getString("location"));
                
                // Gestion des dates avec Timestamp pour éviter les problèmes de format
                Timestamp dateD = resultSet.getTimestamp("dateD");
                Timestamp dateF = resultSet.getTimestamp("dateF");
                
                if (dateD != null) {
                    evenement.setDateD(dateD.toLocalDateTime());
                }
                
                if (dateF != null) {
                    evenement.setDateF(dateF.toLocalDateTime());
                }
                
                evenement.setImage(resultSet.getString("image"));
                evenement.setNbPlace(resultSet.getInt("nbPlace"));
                
                // Récupérer le prix s'il existe
                try {
                    evenement.setPrix(resultSet.getDouble("prix"));
                } catch (SQLException e) {
                    // La colonne prix n'existe peut-être pas encore, utiliser la valeur par défaut
                    evenement.setPrix(0.0);
                }
                
                return evenement;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public void ajouter(Evenement evenement) {
        try {
            // Vérifier si la colonne prix existe
            boolean prixExists = columnExists("evenement", "prix");
            
            String query;
            if (prixExists) {
                query = "INSERT INTO evenement (titre, description, type, location, dateD, dateF, image, nbPlace, prix) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            } else {
                query = "INSERT INTO evenement (titre, description, type, location, dateD, dateF, image, nbPlace) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            }
            
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, evenement.getTitre());
                statement.setString(2, evenement.getDescription());
                statement.setString(3, evenement.getType());
                statement.setString(4, evenement.getLocation());
                statement.setString(5, evenement.getDateD().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                statement.setString(6, evenement.getDateF().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                statement.setString(7, evenement.getImage());
                statement.setInt(8, evenement.getNbPlace());
                
                if (prixExists) {
                    statement.setDouble(9, evenement.getPrix() != null ? evenement.getPrix() : 0.0);
                }
                
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void modifier(Evenement evenement) {
        try {
            // Vérifier si la colonne prix existe
            boolean prixExists = columnExists("evenement", "prix");
            
            String query;
            if (prixExists) {
                query = "UPDATE evenement SET titre = ?, description = ?, type = ?, location = ?, dateD = ?, dateF = ?, image = ?, nbPlace = ?, prix = ? WHERE id = ?";
            } else {
                query = "UPDATE evenement SET titre = ?, description = ?, type = ?, location = ?, dateD = ?, dateF = ?, image = ?, nbPlace = ? WHERE id = ?";
            }
            
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, evenement.getTitre());
                statement.setString(2, evenement.getDescription());
                statement.setString(3, evenement.getType());
                statement.setString(4, evenement.getLocation());
                statement.setString(5, evenement.getDateD().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                statement.setString(6, evenement.getDateF().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
                statement.setString(7, evenement.getImage());
                statement.setInt(8, evenement.getNbPlace());
                
                if (prixExists) {
                    statement.setDouble(9, evenement.getPrix() != null ? evenement.getPrix() : 0.0);
                    statement.setInt(10, evenement.getId());
                } else {
                    statement.setInt(9, evenement.getId());
                }
                
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode utilitaire pour vérifier si une colonne existe dans une table
    private boolean columnExists(String tableName, String columnName) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        try (ResultSet rs = meta.getColumns(null, null, tableName, columnName)) {
            return rs.next();
        }
    }

    public void supprimer(int id) {
        String query = "DELETE FROM evenement WHERE id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean reserverPlace(int evenementId, int nombrePlaces) {
        String query = "UPDATE evenement SET nbPlace = nbPlace - ? WHERE id = ? AND nbPlace >= ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, nombrePlaces);
            statement.setInt(2, evenementId);
            statement.setInt(3, nombrePlaces);
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    public int countEvents() {
        int count = 0;
        String query = "SELECT COUNT(*) FROM evenement";
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            
            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return count;
    }
    
    /**
     * Récupère tous les types d'événements distincts de la base de données
     * @return Une liste des types d'événements
     */
    public List<String> getAllEventTypes() {
        List<String> types = new ArrayList<>();
        Set<String> uniqueTypes = new HashSet<>();
        
        // Récupérer tous les événements
        List<Evenement> events = getAllEvents();
        
        // Extraire les types uniques
        for (Evenement event : events) {
            if (event.getType() != null && !event.getType().isEmpty()) {
                uniqueTypes.add(event.getType());
            }
        }
        
        // Convertir en liste et trier
        types.addAll(uniqueTypes);
        Collections.sort(types);
        
        return types;
    }
}


