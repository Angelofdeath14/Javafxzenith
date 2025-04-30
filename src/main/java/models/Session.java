package models;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Session {
    private int id;
    private int eventId;
    private String title;
    private Timestamp dateTime;
    private String location;
    private int capacity;
    private int availableSeats;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String image;
    private String description;
    private final StringProperty descriptionProperty = new SimpleStringProperty();

    public Session(int id, int eventId, String title, Timestamp dateTime, String location, 
                  int capacity, int availableSeats, LocalDateTime startTime, LocalDateTime endTime, 
                  String image, String description) {
        this.id = id;
        this.eventId = eventId;
        this.title = title;
        this.dateTime = dateTime;
        this.location = location;
        this.capacity = capacity;
        this.availableSeats = availableSeats;
        this.startTime = startTime;
        this.endTime = endTime;
        this.image = image;
        this.description = description;
        this.descriptionProperty.set(description);
    }

    // Getters
    public int getId() { return id; }
    public int getEventId() { return eventId; }
    public String getTitle() { return title; }
    public Timestamp getDateTime() { return dateTime; }
    public String getLocation() { return location; }
    public int getCapacity() { return capacity; }
    public int getAvailableSeats() { return availableSeats; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public String getImage() { return image; }
    public String getDescription() { return description; }
    public StringProperty descriptionProperty() { return descriptionProperty; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setEventId(int eventId) { this.eventId = eventId; }
    public void setTitle(String title) { this.title = title; }
    public void setDateTime(Timestamp dateTime) { this.dateTime = dateTime; }
    public void setLocation(String location) { this.location = location; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public void setAvailableSeats(int availableSeats) { this.availableSeats = availableSeats; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public void setImage(String image) { this.image = image; }
    public void setDescription(String description) { 
        this.description = description;
        this.descriptionProperty.set(description);
    }
} 