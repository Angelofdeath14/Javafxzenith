package tn.esprit.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import tn.esprit.entities.Evenement;
import tn.esprit.entities.Session;
import tn.esprit.utils.MyDataBase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SessionService {
    private Connection conn;
    private PreparedStatement pst;

    public SessionService() {
        conn = MyDataBase.getInstance().getCon();
    }

    public ObservableList<Session> getAllSessions() throws SQLException {
        ObservableList<Session> sessions = FXCollections.observableArrayList();
        String query = "SELECT * FROM session";
        try {
            pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Session session = new Session();
                session.setId(rs.getInt("id"));
                session.setDescription(rs.getString("description"));
                session.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
                session.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
                session.setEvenementId(rs.getInt("evenement_id"));
                session.setImage(rs.getString("image"));
                sessions.add(session);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            throw ex;
        }
        return sessions;
    }

    public void ajouterSession(Session s) throws SQLException {
        String query = "INSERT INTO session (description, start_time, end_time, evenement_id, image) VALUES (?, ?, ?, ?, ?)";
        try {
            pst = conn.prepareStatement(query);
            pst.setString(1, s.getDescription());
            pst.setTimestamp(2, Timestamp.valueOf(s.getStartTime()));
            pst.setTimestamp(3, Timestamp.valueOf(s.getEndTime()));
            pst.setInt(4, s.getEvenementId());
            pst.setString(5, s.getImage());
            pst.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            throw ex;
        }
    }

    public void modifierSession(Session s) throws SQLException {
        String query = "UPDATE session SET description=?, start_time=?, end_time=?, evenement_id=?, image=? WHERE id=?";
        try {
            pst = conn.prepareStatement(query);
            pst.setString(1, s.getDescription());
            pst.setTimestamp(2, Timestamp.valueOf(s.getStartTime()));
            pst.setTimestamp(3, Timestamp.valueOf(s.getEndTime()));
            pst.setInt(4, s.getEvenementId());
            pst.setString(5, s.getImage());
            pst.setInt(6, s.getId());
            pst.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            throw ex;
        }
    }

    public void supprimerSession(int id) throws SQLException {
        String query = "DELETE FROM session WHERE id=?";
        try {
            pst = conn.prepareStatement(query);
            pst.setInt(1, id);
            pst.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            throw ex;
        }
    }
    public Session getOneById(int sessionId) {
        String query = "SELECT * FROM session WHERE id = ?";

        try (
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
    public boolean reserveSeats(int sessionId, int numberOfSeats) {
        String query = "UPDATE session SET capacity = capacity - ? " +
                "WHERE id = ? AND capacity >= ?";

        try (Connection conn = MyDataBase.getInstance().getCon();
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
    public int countActiveSessions() {
        int count = 0;
        String query = "SELECT COUNT(*) FROM session WHERE NOW() BETWEEN start_time AND end_time";

        try (Connection conn = MyDataBase.getInstance().getCon();
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
    public ObservableList<Session> getSessionsByEvenement(Evenement evenement) throws SQLException {
        ObservableList<Session> sessions = FXCollections.observableArrayList();
        String query = "SELECT * FROM session WHERE evenement_id=?";
        try {
            pst = conn.prepareStatement(query);
            pst.setInt(1, evenement.getId());
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Session session = new Session();
                session.setId(rs.getInt("id"));
                session.setDescription(rs.getString("description"));
                session.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
                session.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
                session.setEvenementId(rs.getInt("evenement_id"));
                session.setImage(rs.getString("image"));
                sessions.add(session);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            throw ex;
        }
        return sessions;
    }

    public ObservableList<Session> searchSessions(String searchText) throws SQLException {
        ObservableList<Session> sessions = FXCollections.observableArrayList();
        String query = "SELECT * FROM session WHERE description LIKE ?";
        try {
            pst = conn.prepareStatement(query);
            String searchPattern = "%" + searchText + "%";
            pst.setString(1, searchPattern);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Session session = new Session();
                session.setId(rs.getInt("id"));
                session.setDescription(rs.getString("description"));
                session.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
                session.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
                session.setEvenementId(rs.getInt("evenement_id"));
                session.setImage(rs.getString("image"));
                sessions.add(session);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            throw ex;
        }
        return sessions;
    }

    public List<Session> getSessionsByEvenementId(int evenementId) throws SQLException {
        ObservableList<Session> sessions = FXCollections.observableArrayList();
        String query = "SELECT * FROM session WHERE evenement_id = ?";
        try {
            pst = conn.prepareStatement(query);
            pst.setInt(1, evenementId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Session session = new Session();
                session.setId(rs.getInt("id"));
                session.setDescription(rs.getString("description"));
                session.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
                session.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
                session.setEvenementId(rs.getInt("evenement_id"));
                session.setImage(rs.getString("image"));
                sessions.add(session);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            throw ex;
        }
        return sessions;
    }

    public List<Session> getSessionsByEvent(int eventId) throws SQLException {
        List<Session> sessions = new ArrayList<>();
        String query = "SELECT * FROM session WHERE evenement_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, eventId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Session session = new Session();
                session.setId(rs.getInt("id"));
                session.setDescription(rs.getString("description"));
                session.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
                session.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
                session.setImage(rs.getString("image"));
                session.setEvenementId(rs.getInt("evenement_id"));
                sessions.add(session);
            }
        }

        return sessions;
    }
} 