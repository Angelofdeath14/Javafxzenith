package Service;

import Entity.Session;
import Entity.Evenement;
import Utils.DataSource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SessionService {
    private Connection conn;
    private PreparedStatement pst;

    public SessionService() {
        conn = DataSource.getInstance().getConnection();
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