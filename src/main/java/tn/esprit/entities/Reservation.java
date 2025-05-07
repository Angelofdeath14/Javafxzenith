package tn.esprit.entities;

import java.time.LocalDateTime;

/**
 * Représente une réservation dans le système
 * Cette entité permet de gérer toutes les réservations des utilisateurs
 * pour les événements et sessions.
 */
public class Reservation {
    private int id;
    private int userId;
    private int eventId;
    private int sessionId;
    private int nombrePlaces;
    private LocalDateTime dateReservation;
    private String statut;
    private double prixTotal;
    
    /**
     * Constructeur par défaut
     */
    public Reservation() {
        // Par défaut, toute nouvelle réservation est "En attente"
        this.statut = "En attente";
        this.dateReservation = LocalDateTime.now();
        this.prixTotal = 0.0;
    }
    
    /**
     * Constructeur avec paramètres
     */
    public Reservation(int id, int userId, int eventId, int sessionId, 
                      int nombrePlaces, LocalDateTime dateReservation, 
                      String statut) {
        this.id = id;
        this.userId = userId;
        this.eventId = eventId;
        this.sessionId = sessionId;
        this.nombrePlaces = nombrePlaces;
        this.dateReservation = dateReservation;
        this.statut = statut;
        this.prixTotal = 0.0;
    }
    
    /**
     * Constructeur utilisé lors de la création d'une nouvelle réservation
     */
    public Reservation(int userId, int eventId, int sessionId, 
                      int nombrePlaces) {
        this.userId = userId;
        this.eventId = eventId;
        this.sessionId = sessionId;
        this.nombrePlaces = nombrePlaces;
        this.dateReservation = LocalDateTime.now();
        this.statut = "En attente";
        this.prixTotal = 0.0;
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

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }
    
    public double getPrixTotal() {
        return prixTotal;
    }
    
    public void setPrixTotal(double prixTotal) {
        this.prixTotal = prixTotal;
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
                ", statut='" + statut + '\'' +
                ", prixTotal=" + prixTotal +
                '}';
    }
} 