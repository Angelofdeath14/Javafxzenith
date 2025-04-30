package services;

import Utils.MyDatabase;
import Entity.Evenement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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
                return evenement;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }

    public void ajouter(Evenement evenement) {
        String query = "INSERT INTO evenement (titre, description, type, location, dateD, dateF, image, nbPlace) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, evenement.getTitre());
            statement.setString(2, evenement.getDescription());
            statement.setString(3, evenement.getType());
            statement.setString(4, evenement.getLocation());
            statement.setString(5, evenement.getDateD().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            statement.setString(6, evenement.getDateF().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            statement.setString(7, evenement.getImage());
            statement.setInt(8, evenement.getNbPlace());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void modifier(Evenement evenement) {
        String query = "UPDATE evenement SET titre = ?, description = ?, type = ?, location = ?, dateD = ?, dateF = ?, image = ?, nbPlace = ? WHERE id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, evenement.getTitre());
            statement.setString(2, evenement.getDescription());
            statement.setString(3, evenement.getType());
            statement.setString(4, evenement.getLocation());
            statement.setString(5, evenement.getDateD().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            statement.setString(6, evenement.getDateF().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            statement.setString(7, evenement.getImage());
            statement.setInt(8, evenement.getNbPlace());
            statement.setInt(9, evenement.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
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
}


