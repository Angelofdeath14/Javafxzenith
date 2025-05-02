package Entity;

import java.time.LocalDateTime;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Session {
    private int id;
    private String titre;
    private String description;
    // Ces champs stockent les dates de début et fin de session
    // Ils sont accessibles via getDateDebut/setDateDebut pour la compatibilité
    // Mais aussi via getStartTime/setStartTime pour correspondre aux noms de colonnes dans la BD
    private LocalDateTime dateDebut;  // Correspond à start_time dans la BD
    private LocalDateTime dateFin;    // Correspond à end_time dans la BD
    private int evenementId;
    private Evenement evenement;
    private String image;
    private final StringProperty descriptionProperty = new SimpleStringProperty();
    private int capacity;
    private int availableSeats;
    private String location;

    public Session() {
    }

    public Session(int id, String titre, String description, LocalDateTime dateDebut, LocalDateTime dateFin, 
                  int evenementId, String image) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.evenementId = evenementId;
        this.image = image;
        this.descriptionProperty.set(description);
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.descriptionProperty.set(description);
    }

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDateTime getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
    }

    public int getEvenementId() {
        return evenementId;
    }

    public void setEvenementId(int evenementId) {
        this.evenementId = evenementId;
    }

    public Evenement getEvenement() {
        return evenement;
    }

    public void setEvenement(Evenement evenement) {
        this.evenement = evenement;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public StringProperty descriptionProperty() {
        return descriptionProperty;
    }

    // Alias de getDateDebut pour correspondre au nom de colonne de la BD
    public LocalDateTime getStartTime() {
        return dateDebut;
    }

    // Alias de setDateDebut pour correspondre au nom de colonne de la BD
    public void setStartTime(LocalDateTime startTime) {
        this.dateDebut = startTime;
    }

    // Alias de getDateFin pour correspondre au nom de colonne de la BD
    public LocalDateTime getEndTime() {
        return dateFin;
    }

    // Alias de setDateFin pour correspondre au nom de colonne de la BD
    public void setEndTime(LocalDateTime endTime) {
        this.dateFin = endTime;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    public void setAvailableSeats(int availableSeats) {
        this.availableSeats = availableSeats;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
} 