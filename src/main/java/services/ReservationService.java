package services;

import Entity.Reservation;
import Utils.DataSource;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReservationService implements IService<Reservation> {
    private Connection conn;

    public ReservationService() throws SQLException {
        conn = DataSource.getInstance().getConnection();
    }

    @Override
    public void add(Reservation reservation) throws SQLException {
        String query = "INSERT INTO reservation (user_id, event_id, session_id, nombre_places, date_reservation, prix_total, statut) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement pst = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, reservation.getUserId());
            pst.setInt(2, reservation.getEventId());
            pst.setInt(3, reservation.getSessionId());
            pst.setInt(4, reservation.getNombrePlaces());
            pst.setTimestamp(5, Timestamp.valueOf(reservation.getDateReservation()));
            pst.setDouble(6, reservation.getPrixTotal());
            pst.setString(7, reservation.getStatut());
            
            pst.executeUpdate();
            
            try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    reservation.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public void update(Reservation reservation) throws SQLException {
        String query = "UPDATE reservation SET user_id = ?, event_id = ?, session_id = ?, " +
                      "nombre_places = ?, date_reservation = ?, prix_total = ?, statut = ? " +
                      "WHERE id = ?";
        
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, reservation.getUserId());
            pst.setInt(2, reservation.getEventId());
            pst.setInt(3, reservation.getSessionId());
            pst.setInt(4, reservation.getNombrePlaces());
            pst.setTimestamp(5, Timestamp.valueOf(reservation.getDateReservation()));
            pst.setDouble(6, reservation.getPrixTotal());
            pst.setString(7, reservation.getStatut());
            pst.setInt(8, reservation.getId());
            
            pst.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String query = "DELETE FROM reservation WHERE id = ?";
        
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, id);
            pst.executeUpdate();
        }
    }

    @Override
    public List<Reservation> readList() throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT * FROM reservation";
        
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            
            while (rs.next()) {
                Reservation reservation = mapResultSetToReservation(rs);
                reservations.add(reservation);
            }
        }
        
        return reservations;
    }
    
    @Override
    public void addP(Reservation reservation) throws SQLException {
        // Même implémentation que add() pour le moment
        add(reservation);
    }
    
    // Méthodes pour maintenir la compatibilité avec le code existant
    public void ajouter(Reservation reservation) throws SQLException {
        add(reservation);
    }
    
    public void modifier(Reservation reservation) throws SQLException {
        update(reservation);
    }
    
    public void supprimer(int id) throws SQLException {
        delete(id);
    }
    
    public List<Reservation> afficher() throws SQLException {
        return readList();
    }
    
    public Reservation getReservationById(int id) throws SQLException {
        String query = "SELECT * FROM reservation WHERE id = ?";
        
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, id);
            
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReservation(rs);
                }
            }
        }
        
        return null;
    }
    
    public List<Reservation> getReservationsByUser(int userId) throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT * FROM reservation WHERE user_id = ?";
        
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, userId);
            
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Reservation reservation = mapResultSetToReservation(rs);
                    reservations.add(reservation);
                }
            }
        }
        
        return reservations;
    }
    
    public List<Reservation> getReservationsByEvent(int eventId) throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT * FROM reservation WHERE event_id = ?";
        
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, eventId);
            
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Reservation reservation = mapResultSetToReservation(rs);
                    reservations.add(reservation);
                }
            }
        }
        
        return reservations;
    }
    
    public List<Reservation> getReservationsBySession(int sessionId) throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String query = "SELECT * FROM reservation WHERE session_id = ?";
        
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, sessionId);
            
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    Reservation reservation = mapResultSetToReservation(rs);
                    reservations.add(reservation);
                }
            }
        }
        
        return reservations;
    }
    
    public boolean updateReservation(int reservationId, int newPlacesCount) throws SQLException {
        String query = "UPDATE reservation SET nombre_places = ? WHERE id = ?";
        
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, newPlacesCount);
            pst.setInt(2, reservationId);
            
            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    public boolean deleteReservation(int reservationId) throws SQLException {
        String query = "DELETE FROM reservation WHERE id = ?";
        
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, reservationId);
            
            int rowsAffected = pst.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    private Reservation mapResultSetToReservation(ResultSet rs) throws SQLException {
        int id = rs.getInt("id");
        int userId = rs.getInt("user_id");
        int eventId = rs.getInt("event_id");
        int sessionId = rs.getInt("session_id");
        int nombrePlaces = rs.getInt("nombre_places");
        LocalDateTime dateReservation = rs.getTimestamp("date_reservation").toLocalDateTime();
        double prixTotal = rs.getDouble("prix_total");
        String statut = rs.getString("statut");
        
        return new Reservation(id, userId, eventId, sessionId, nombrePlaces, dateReservation, prixTotal, statut);
    }
} 