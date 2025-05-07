package tn.esprit.entities;

import java.time.LocalDateTime;
import java.util.Objects;

public class Reclamation {
    private int id;
    private String titre;
    private String description;
    private LocalDateTime date_creation;
    private int id_user;
    public Reclamation() {}

    public Reclamation(int id, String titre, String description, LocalDateTime date_creation, int id_user) {
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.date_creation = date_creation;
        this.id_user = id_user;
    }

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
    }

    public LocalDateTime getDate_creation() {
        return date_creation;
    }

    public void setDate_creation(LocalDateTime date_creation) {
        this.date_creation = date_creation;
    }

    public int getId_user() {
        return id_user;
    }

    public void setId_user(int id_user) {
        this.id_user = id_user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reclamation that = (Reclamation) o;
        return id_user == that.id_user && Objects.equals(titre, that.titre) && Objects.equals(description, that.description) && Objects.equals(date_creation, that.date_creation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(titre, description, date_creation, id_user);
    }

    @Override
    public String toString() {
        return String.format(
                "Reclamation{%n" +
                        "  titre='%s'%n" +
                        "  description='%s'%n" +
                        "  date_creation=%s%n" +
                        "  id_user=%d%n" +
                        "}",
                titre,
                description,
                date_creation,
                id_user
        );
    }

}
