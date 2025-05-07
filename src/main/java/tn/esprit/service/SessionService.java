package tn.esprit.service;


import tn.esprit.entities.Session;
import tn.esprit.utils.MyDataBase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SessionService {
    private final Connection conn;

    public SessionService() {
        conn = MyDataBase.getInstance().getCon();
    }

    /**
     * Retrieves all sessions from the database.
     */
    public List<Session> getAllSessions() {
        List<Session> sessions = new ArrayList<>();
        String sql = "SELECT * FROM session";
        try (PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                sessions.add(mapRowToSession(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching all sessions", e);
        }
        return sessions;
    }
    public List<Session> searchSessions(String searchText) {
        List<Session> sessions = new ArrayList<>();
        String sql = "SELECT * FROM session WHERE description LIKE ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, "%" + searchText + "%");
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    sessions.add(mapRowToSession(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error searching sessions", e);
        }
        return sessions;
    }
    public int countActiveSessions() {
        int count = 0;
        String query = "SELECT COUNT(*) FROM session WHERE NOW() BETWEEN start_time AND end_time";

        try (
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
    public List<Session> getSessionsByEvenementId(int eventId) {
        List<Session> sessions = new ArrayList<>();
        String query = "SELECT * FROM session WHERE evenement_id = ?";

        try (
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


    /**
     * Inserts a new session.
     */
    public void ajouterSession(Session s) {
        String sql = "INSERT INTO session (evenement_id, titre, description, start_time, end_time, image, capacity, available_seats, location) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, s.getEvenementId());
            pst.setString(2, s.getTitre());
            pst.setString(3, s.getDescription());
            pst.setTimestamp(4, s.getStartTime() != null ? Timestamp.valueOf(s.getStartTime()) : null);
            pst.setTimestamp(5, s.getEndTime()   != null ? Timestamp.valueOf(s.getEndTime())   : null);
            pst.setString(6, s.getImage());
            pst.setInt(7, s.getCapacity());
            pst.setInt(8, s.getAvailableSeats());
            pst.setString(9, s.getLocation());
            pst.executeUpdate();
            try (ResultSet keys = pst.getGeneratedKeys()) {
                if (keys.next()) {
                    s.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error adding session", e);
        }
    }

    /**
     * Updates an existing session.
     */
    public void modifierSession(Session s) {
        String sql = "UPDATE session SET evenement_id=?, titre=?, description=?, start_time=?, end_time=?, image=?, capacity=?, available_seats=?, location=? WHERE id=?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, s.getEvenementId());
            pst.setString(2, s.getTitre());
            pst.setString(3, s.getDescription());
            pst.setTimestamp(4, s.getStartTime() != null ? Timestamp.valueOf(s.getStartTime()) : null);
            pst.setTimestamp(5, s.getEndTime()   != null ? Timestamp.valueOf(s.getEndTime())   : null);
            pst.setString(6, s.getImage());
            pst.setInt(7, s.getCapacity());
            pst.setInt(8, s.getAvailableSeats());
            pst.setString(9, s.getLocation());
            pst.setInt(10, s.getId());
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating session", e);
        }
    }

    /**
     * Deletes a session by its ID.
     */
    public boolean supprimerSession(int id) {
        String sql = "DELETE FROM session WHERE id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting session", e);
        }
    }

    /**
     * Finds sessions for a specific event.
     */
    public List<Session> getSessionsByEvent(int eventId) {
        List<Session> sessions = new ArrayList<>();
        String sql = "SELECT * FROM session WHERE evenement_id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, eventId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    sessions.add(mapRowToSession(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching sessions by event", e);
        }
        return sessions;
    }

    /**
     * Retrieves a single session by ID.
     */
    public Session getOneById(int id) {
        String sql = "SELECT * FROM session WHERE id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapRowToSession(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching session by ID", e);
        }
        return null;
    }

    /**
     * Reserves seats for a session if capacity allows.
     */
    public boolean reserveSeats(int sessionId, int seats) {
        String sql = "UPDATE session SET available_seats = available_seats - ? WHERE id = ? AND available_seats >= ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, seats);
            pst.setInt(2, sessionId);
            pst.setInt(3, seats);
            return pst.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Error reserving seats", e);
        }
    }

    /**
     * Maps a JDBC row to a Session entity.
     */
    private Session mapRowToSession(ResultSet rs) throws SQLException {
        Session s = new Session();
        s.setId(rs.getInt("id"));
        s.setEvenementId(rs.getInt("evenement_id"));
        s.setTitre(rs.getString("titre"));
        s.setDescription(rs.getString("description"));
        s.setStartTime(rs.getTimestamp("start_time") != null ? rs.getTimestamp("start_time").toLocalDateTime() : null);
        s.setEndTime(rs.getTimestamp("end_time")   != null ? rs.getTimestamp("end_time").toLocalDateTime()   : null);
        s.setImage(rs.getString("image"));
        s.setCapacity(rs.getInt("capacity"));
        s.setAvailableSeats(rs.getInt("available_seats"));
        s.setLocation(rs.getString("location"));
        return s;
    }
}
