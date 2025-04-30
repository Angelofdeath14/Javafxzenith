package services;

import Entity.Session;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import Utils.DatabaseConnection;

public class SessionService {
    
    public List<Session> getAllSessions() {
        List<Session> sessions = new ArrayList<>();
        String query = "SELECT * FROM sessions";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Session session = new Session();
                session.setId(rs.getInt("id"));
                session.setEvenementId(rs.getInt("event_id"));
                session.setTitre(rs.getString("title"));
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
        String query = "SELECT COUNT(*) FROM sessions WHERE NOW() BETWEEN start_time AND end_time";
        
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
    
    public List<Session> getSessionsByEvent(int eventId) {
        List<Session> sessions = new ArrayList<>();
        String query = "SELECT * FROM sessions WHERE event_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, eventId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Session session = new Session(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("description"),
                    rs.getTimestamp("start_time") != null ? rs.getTimestamp("start_time").toLocalDateTime() : null,
                    rs.getTimestamp("end_time") != null ? rs.getTimestamp("end_time").toLocalDateTime() : null,
                    rs.getInt("event_id"),
                    rs.getString("image")
                );
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
    
    public List<Session> getSessionsByEvenementId(int eventId) {
        List<Session> sessions = new ArrayList<>();
        String query = "SELECT * FROM sessions WHERE event_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, eventId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Session session = new Session();
                session.setId(rs.getInt("id"));
                session.setEvenementId(rs.getInt("event_id"));
                session.setTitre(rs.getString("title"));
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
        String query = "UPDATE sessions SET available_seats = available_seats - ? " +
                      "WHERE id = ? AND available_seats >= ?";
        
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
        String query = "INSERT INTO sessions (event_id, title, description, start_time, end_time, " +
                      "image, capacity, available_seats, location) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, session.getEvenementId());
            pstmt.setString(2, session.getTitre());
            pstmt.setString(3, session.getDescription());
            pstmt.setTimestamp(4, session.getStartTime() != null ? Timestamp.valueOf(session.getStartTime()) : null);
            pstmt.setTimestamp(5, session.getEndTime() != null ? Timestamp.valueOf(session.getEndTime()) : null);
            pstmt.setString(6, session.getImage());
            pstmt.setInt(7, session.getCapacity());
            pstmt.setInt(8, session.getAvailableSeats());
            pstmt.setString(9, session.getLocation());
            
            int insertedRows = pstmt.executeUpdate();
            return insertedRows > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO: Gérer l'exception de manière appropriée
            return false;
        }
    }
    
    public boolean updateSession(Session session) {
        String query = "UPDATE sessions SET title = ?, description = ?, start_time = ?, end_time = ?, " +
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
            
            int updatedRows = pstmt.executeUpdate();
            return updatedRows > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO: Gérer l'exception de manière appropriée
            return false;
        }
    }
    
    public boolean deleteSession(int sessionId) {
        String query = "DELETE FROM sessions WHERE id = ?";
        
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
} 