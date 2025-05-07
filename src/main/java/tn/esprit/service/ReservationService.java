package tn.esprit.service;

import tn.esprit.entities.Reservation;
import tn.esprit.utils.MyDataBase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReservationService implements IService<Reservation> {
    private Connection conn;

    public ReservationService() {

            conn = MyDataBase.getInstance().getCon();

    }

    @Override
    public void ajouter(Reservation reservation) {
        String sql = "INSERT INTO reservation (user_id, event_id, session_id, nombre_places, date_reservation, statut, prix_total) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, reservation.getUserId());
            pst.setInt(2, reservation.getEventId());
            pst.setInt(3, reservation.getSessionId());
            pst.setInt(4, reservation.getNombrePlaces());
            pst.setTimestamp(5, Timestamp.valueOf(reservation.getDateReservation()));
            pst.setString(6, reservation.getStatut());
            pst.setDouble(7, reservation.getPrixTotal());

            pst.executeUpdate();
            try (ResultSet keys = pst.getGeneratedKeys()) {
                if (keys.next()) {
                    reservation.setId(keys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error adding reservation", e);
        }
    }

    @Override
    public void modifier(Reservation reservation) {
        String sql = "UPDATE reservation SET user_id = ?, event_id = ?, session_id = ?, " +
                "nombre_places = ?, date_reservation = ?, statut = ?, prix_total = ? " +
                "WHERE id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, reservation.getUserId());
            pst.setInt(2, reservation.getEventId());
            pst.setInt(3, reservation.getSessionId());
            pst.setInt(4, reservation.getNombrePlaces());
            pst.setTimestamp(5, Timestamp.valueOf(reservation.getDateReservation()));
            pst.setString(6, reservation.getStatut());
            pst.setDouble(7, reservation.getPrixTotal());
            pst.setInt(8, reservation.getId());

            pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error modifying reservation", e);
        }
    }

    @Override
    public void supprimer(int id) {
        String sql = "DELETE FROM reservation WHERE id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id);
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting reservation", e);
        }
    }

    @Override
    public List<Reservation> afficher() {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM reservation";
        try (Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                reservations.add(mapResultSetToReservation(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching reservations", e);
        }
        return reservations;
    }

    public Reservation getReservationById(int id) {
        String sql = "SELECT * FROM reservation WHERE id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToReservation(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching reservation by ID", e);
        }
        return null;
    }

    public List<Reservation> getReservationsByUser(int userId) {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservation WHERE user_id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, userId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToReservation(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching reservations by user", e);
        }
        return list;
    }

    public List<Reservation> getReservationsByEvent(int eventId) {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservation WHERE event_id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, eventId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToReservation(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching reservations by event", e);
        }
        return list;
    }

    public List<Reservation> getReservationsBySession(int sessionId) {
        List<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM reservation WHERE session_id = ?";
        try (PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setInt(1, sessionId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToReservation(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching reservations by session", e);
        }
        return list;
    }

    private Reservation mapResultSetToReservation(ResultSet rs) {
        try {
            int id = rs.getInt("id");
            int userId = rs.getInt("user_id");
            int eventId = rs.getInt("event_id");
            int sessionId = rs.getInt("session_id");
            int nombrePlaces = rs.getInt("nombre_places");
            LocalDateTime dateReservation = rs.getTimestamp("date_reservation").toLocalDateTime();
            String statut = rs.getString("statut");

            Reservation reservation = new Reservation(id, userId, eventId, sessionId, nombrePlaces, dateReservation, statut);
            try {
                reservation.setPrixTotal(rs.getDouble("prix_total"));
            } catch (SQLException e) {
                reservation.setPrixTotal(0.0);
            }
            return reservation;
        } catch (SQLException e) {
            throw new RuntimeException("Error mapping reservation result set", e);
        }
    }
}
