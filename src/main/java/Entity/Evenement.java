package Entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Evenement {
    private int id;
    private String titre;
    private String description;
    private String type;
    private String location;
    private LocalDateTime dateD;
    private LocalDateTime dateF;
    private String image;
    private int nbPlace;
    private List<Session> sessions;

    // Constructeur par défaut
    public Evenement() {
        this.sessions = new ArrayList<>();
    }

    // Constructeur avec paramètres
    public Evenement(String titre, String description, String type, String location, 
                    LocalDateTime dateD, LocalDateTime dateF, String image, int nbPlace) {
        this.titre = titre;
        this.description = description;
        this.type = type;
        this.location = location;
        this.dateD = dateD;
        this.dateF = dateF;
        this.image = image;
        this.nbPlace = nbPlace;
        this.sessions = new ArrayList<>();
    }

    // Getters et Setters
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

    // Ajout d'une méthode getNom() pour la compatibilité
    public String getNom() {
        return getTitre();
    }

    // Ajout d'une méthode setNom() pour la compatibilité
    public void setNom(String nom) {
        setTitre(nom);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getDateD() {
        return dateD;
    }

    public void setDateD(LocalDateTime dateD) {
        this.dateD = dateD;
    }

    public LocalDateTime getDateF() {
        return dateF;
    }

    public void setDateF(LocalDateTime dateF) {
        this.dateF = dateF;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getNbPlace() {
        return nbPlace;
    }

    public void setNbPlace(int nbPlace) {
        this.nbPlace = nbPlace;
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public void setSessions(List<Session> sessions) {
        this.sessions = sessions;
    }

    public void addSession(Session session) {
        if (this.sessions == null) {
            this.sessions = new ArrayList<>();
        }
        this.sessions.add(session);
    }

    public void removeSession(Session session) {
        if (this.sessions != null) {
            this.sessions.remove(session);
        }
    }

    @Override
    public String toString() {
        return "Evenement{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", description='" + description + '\'' +
                ", type='" + type + '\'' +
                ", location='" + location + '\'' +
                ", dateD=" + dateD +
                ", dateF=" + dateF +
                ", image='" + image + '\'' +
                ", nbPlace=" + nbPlace +
                ", sessions=" + sessions +
                '}';
    }
}
