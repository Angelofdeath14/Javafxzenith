package models;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Evenement {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty nom;
    private final SimpleStringProperty date;
    private final SimpleStringProperty lieu;
    private final SimpleIntegerProperty placesDisponibles;

    public Evenement(int id, String nom, String date, String lieu, int placesDisponibles) {
        this.id = new SimpleIntegerProperty(id);
        this.nom = new SimpleStringProperty(nom);
        this.date = new SimpleStringProperty(date);
        this.lieu = new SimpleStringProperty(lieu);
        this.placesDisponibles = new SimpleIntegerProperty(placesDisponibles);
    }

    public int getId() {
        return id.get();
    }

    public SimpleIntegerProperty idProperty() {
        return id;
    }

    public String getNom() {
        return nom.get();
    }

    public SimpleStringProperty nomProperty() {
        return nom;
    }

    public String getDate() {
        return date.get();
    }

    public SimpleStringProperty dateProperty() {
        return date;
    }

    public String getLieu() {
        return lieu.get();
    }

    public SimpleStringProperty lieuProperty() {
        return lieu;
    }

    public int getPlacesDisponibles() {
        return placesDisponibles.get();
    }

    public SimpleIntegerProperty placesDisponiblesProperty() {
        return placesDisponibles;
    }
} 