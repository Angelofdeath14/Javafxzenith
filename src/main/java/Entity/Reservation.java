package Entity;

import java.time.LocalDateTime;

public class Reservation {
    private int id;
    private int userId;
    private int eventId;
    private int sessionId;
    private int nombrePlaces;
    private LocalDateTime dateReservation;
    private double prixTotal;
    private String statut;

    // Constructeur par défaut
    public Reservation() {
        this.dateReservation = LocalDateTime.now();
        this.statut = "confirmé";
    }

    // Constructeur complet
    public Reservation(int id, int userId, int eventId, int sessionId, int nombrePlaces, 
                       LocalDateTime dateReservation, double prixTotal, String statut) {
        this.id = id;
        this.userId = userId;
        this.eventId = eventId;
        this.sessionId = sessionId;
        this.nombrePlaces = nombrePlaces;
        this.dateReservation = dateReservation;
        this.prixTotal = prixTotal;
        this.statut = statut;
    }

    // Constructeur sans ID pour les nouvelles réservations
    public Reservation(int userId, int eventId, int sessionId, int nombrePlaces, 
                      double prixTotal) {
        this.userId = userId;
        this.eventId = eventId;
        this.sessionId = sessionId;
        this.nombrePlaces = nombrePlaces;
        this.dateReservation = LocalDateTime.now();
        this.prixTotal = prixTotal;
        this.statut = "confirmé";
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public int getNombrePlaces() {
        return nombrePlaces;
    }

    public void setNombrePlaces(int nombrePlaces) {
        this.nombrePlaces = nombrePlaces;
    }

    public LocalDateTime getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(LocalDateTime dateReservation) {
        this.dateReservation = dateReservation;
    }

    public double getPrixTotal() {
        return prixTotal;
    }

    public void setPrixTotal(double prixTotal) {
        this.prixTotal = prixTotal;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", userId=" + userId +
                ", eventId=" + eventId +
                ", sessionId=" + sessionId +
                ", nombrePlaces=" + nombrePlaces +
                ", dateReservation=" + dateReservation +
                ", prixTotal=" + prixTotal +
                ", statut='" + statut + '\'' +
                '}';
    }
} 