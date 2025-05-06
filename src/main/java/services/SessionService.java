package services;

import Entity.Session;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Utils.DatabaseConnection;

public class SessionService {
    
    public List<Session> getAllSessions() {
        List<Session> sessions = new ArrayList<>();
        String query = "SELECT * FROM session";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Session session = new Session();
                session.setId(rs.getInt("id"));
                session.setEvenementId(rs.getInt("evenement_id"));
                session.setTitre(rs.getString("titre"));
                session.setDescription(rs.getString("description"));
                session.setStartTime(rs.getTimestamp("start_time") != null ? rs.getTimestamp("start_time").toLocalDateTime() : null);
                session.setEndTime(rs.getTimestamp("end_time") != null ? rs.getTimestamp("end_time").toLocalDateTime() : null);
                session.setImage(rs.getString("image"));
                session.setCapacity(rs.getInt("capacity"));
                session.setAvailableSeats(rs.getInt("available_seats"));
                session.setLocation(rs.getString("location"));
                sessions.add(session);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return sessions;
    }

    public int countActiveSessions() {
        int count = 0;
        String query = "SELECT COUNT(*) FROM session WHERE NOW() BETWEEN start_time AND end_time";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                count = rs.getInt(1);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return count;
    }
    
    public List<Session> getSessionsByEvent(int eventId) throws SQLException {
        List<Session> sessions = new ArrayList<>();
        String query = "SELECT * FROM session WHERE evenement_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, eventId);
            System.out.println("Exécution de la requête pour récupérer les sessions de l'événement ID " + eventId);
            System.out.println("Requête SQL: " + query.replace("?", String.valueOf(eventId)));
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Session session = new Session(
                    rs.getInt("id"),
                    rs.getString("titre"),
                    rs.getString("description"),
                    rs.getTimestamp("start_time") != null ? rs.getTimestamp("start_time").toLocalDateTime() : null,
                    rs.getTimestamp("end_time") != null ? rs.getTimestamp("end_time").toLocalDateTime() : null,
                    rs.getInt("evenement_id"),
                    rs.getString("image")
                );
                session.setCapacity(rs.getInt("capacity"));
                session.setAvailableSeats(rs.getInt("available_seats"));
                session.setLocation(rs.getString("location"));
                sessions.add(session);
                System.out.println("Session trouvée - ID: " + session.getId() + ", Titre: " + session.getTitre());
            }
        }
        
        return sessions;
    }
    
    public List<Session> getSessionsByEvenementId(int eventId) {
        List<Session> sessions = new ArrayList<>();
        String query = "SELECT * FROM session WHERE evenement_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, eventId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Session session = new Session();
                session.setId(rs.getInt("id"));
                session.setEvenementId(rs.getInt("evenement_id"));
                session.setTitre(rs.getString("titre"));
                session.setDescription(rs.getString("description"));
                session.setStartTime(rs.getTimestamp("start_time") != null ? rs.getTimestamp("start_time").toLocalDateTime() : null);
                session.setEndTime(rs.getTimestamp("end_time") != null ? rs.getTimestamp("end_time").toLocalDateTime() : null);
                session.setImage(rs.getString("image"));
                session.setCapacity(rs.getInt("capacity"));
                session.setAvailableSeats(rs.getInt("available_seats"));
                session.setLocation(rs.getString("location"));
                sessions.add(session);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO: Gérer l'exception de manière appropriée
        }
        
        return sessions;
    }
    
    public boolean reserveSeats(int sessionId, int numberOfSeats) {
        String query = "UPDATE session SET capacity = capacity - ? " +
                      "WHERE id = ? AND capacity >= ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, numberOfSeats);
            pstmt.setInt(2, sessionId);
            pstmt.setInt(3, numberOfSeats);
            
            int updatedRows = pstmt.executeUpdate();
            return updatedRows > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO: Gérer l'exception de manière appropriée
            return false;
        }
    }
    
    public boolean createSession(Session session) {
        // Vérifier d'abord si la colonne location existe dans la table session
        boolean locationExists = false;
        try (Connection conn = DatabaseConnection.getConnection()) {
            DatabaseMetaData metaData = conn.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, "session", "location");
            locationExists = columns.next();
            System.out.println("Colonne 'location' existe dans la table session: " + locationExists);
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de la structure de la table: " + e.getMessage());
        }
        
        // Construire la requête en fonction de la présence de la colonne location
        String query;
        if (locationExists) {
            query = "INSERT INTO session (evenement_id, titre, description, start_time, end_time, " +
                   "image, capacity, available_seats, location) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        } else {
            query = "INSERT INTO session (evenement_id, titre, description, start_time, end_time, " +
                   "image, capacity, available_seats) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        }
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            // Débuggage pour voir les valeurs
            System.out.println("Création de session avec les valeurs:");
            System.out.println("EvenementId: " + session.getEvenementId());
            System.out.println("Titre: " + session.getTitre());
            System.out.println("Description: " + session.getDescription());
            System.out.println("StartTime: " + session.getStartTime());
            System.out.println("EndTime: " + session.getEndTime());
            System.out.println("Image: " + session.getImage());
            System.out.println("Capacity: " + session.getCapacity());
            System.out.println("AvailableSeats: " + session.getAvailableSeats());
            if (locationExists) {
                System.out.println("Location: " + session.getLocation());
            }
            
            // Afficher la requête SQL complète
            System.out.println("Requête SQL: " + query);
            
            pstmt.setInt(1, session.getEvenementId());
            pstmt.setString(2, session.getTitre());
            pstmt.setString(3, session.getDescription());
            pstmt.setTimestamp(4, session.getStartTime() != null ? Timestamp.valueOf(session.getStartTime()) : null);
            pstmt.setTimestamp(5, session.getEndTime() != null ? Timestamp.valueOf(session.getEndTime()) : null);
            pstmt.setString(6, session.getImage());
            pstmt.setInt(7, session.getCapacity());
            pstmt.setInt(8, session.getAvailableSeats());
            
            if (locationExists) {
                pstmt.setString(9, session.getLocation());
            }
            
            int insertedRows = pstmt.executeUpdate();
            System.out.println("Résultat de l'insertion: " + insertedRows + " ligne(s) insérée(s)");
            return insertedRows > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la création de session: " + e.getMessage());
            e.printStackTrace();
            // Afficher la requête SQL pour débuggage
            System.err.println("Requête SQL: " + query);
            return false;
        } catch (Exception e) {
            System.err.println("Erreur générale lors de la création de session: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public void updateSession(Session session) throws SQLException {
        String query = "UPDATE session SET titre = ?, description = ?, start_time = ?, end_time = ?, " +
                      "image = ?, capacity = ?, available_seats = ?, location = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, session.getTitre());
            pstmt.setString(2, session.getDescription());
            pstmt.setTimestamp(3, session.getStartTime() != null ? Timestamp.valueOf(session.getStartTime()) : null);
            pstmt.setTimestamp(4, session.getEndTime() != null ? Timestamp.valueOf(session.getEndTime()) : null);
            pstmt.setString(5, session.getImage());
            pstmt.setInt(6, session.getCapacity());
            pstmt.setInt(7, session.getAvailableSeats());
            pstmt.setString(8, session.getLocation());
            pstmt.setInt(9, session.getId());
            
            pstmt.executeUpdate();
        }
    }
    
    public boolean deleteSession(int sessionId) {
        String query = "DELETE FROM session WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, sessionId);
            
            int deletedRows = pstmt.executeUpdate();
            return deletedRows > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO: Gérer l'exception de manière appropriée
            return false;
        }
    }

    public Session getOneById(int sessionId) {
        String query = "SELECT * FROM session WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, sessionId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Session session = new Session();
                session.setId(rs.getInt("id"));
                session.setEvenementId(rs.getInt("evenement_id"));
                session.setTitre(rs.getString("titre"));
                session.setDescription(rs.getString("description"));
                session.setStartTime(rs.getTimestamp("start_time") != null ? rs.getTimestamp("start_time").toLocalDateTime() : null);
                session.setEndTime(rs.getTimestamp("end_time") != null ? rs.getTimestamp("end_time").toLocalDateTime() : null);
                session.setImage(rs.getString("image"));
                session.setCapacity(rs.getInt("capacity"));
                session.setAvailableSeats(rs.getInt("available_seats"));
                session.setLocation(rs.getString("location"));
                return session;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
} 