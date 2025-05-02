package Service;

import Entity.Evenement;
import Utils.DataSource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EvenementService {
    private Connection conn;
    private PreparedStatement pst;

    public EvenementService() {
        conn = DataSource.getInstance().getConnection();
    }

    public List<Evenement> getAllEvenements() throws SQLException {
        ObservableList<Evenement> evenements = FXCollections.observableArrayList();
        String query = "SELECT * FROM evenement";
        try {
            pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Evenement evenement = new Evenement();
                evenement.setId(rs.getInt("id"));
                evenement.setTitre(rs.getString("titre"));
                evenement.setDescription(rs.getString("description"));
                evenement.setType(rs.getString("type"));
                evenement.setLocation(rs.getString("location"));
                evenement.setDateD(rs.getTimestamp("dateD").toLocalDateTime());
                evenement.setDateF(rs.getTimestamp("dateF").toLocalDateTime());
                evenement.setImage(rs.getString("image"));
                evenement.setNbPlace(rs.getInt("nbPlace"));
                evenements.add(evenement);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            throw ex;
        }
        return evenements;
    }

    /**
     * Récupère un événement par son ID
     * @param id ID de l'événement
     * @return L'événement ou null s'il n'existe pas
     */
    public Evenement getOneById(int id) throws SQLException {
        String query = "SELECT * FROM evenement WHERE id = ?";
        try {
            pst = conn.prepareStatement(query);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                Evenement evenement = new Evenement();
                evenement.setId(rs.getInt("id"));
                evenement.setTitre(rs.getString("titre"));
                evenement.setDescription(rs.getString("description"));
                evenement.setType(rs.getString("type"));
                evenement.setLocation(rs.getString("location"));
                evenement.setDateD(rs.getTimestamp("dateD").toLocalDateTime());
                evenement.setDateF(rs.getTimestamp("dateF").toLocalDateTime());
                evenement.setImage(rs.getString("image"));
                evenement.setNbPlace(rs.getInt("nbPlace"));
                return evenement;
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            throw ex;
        }
        return null;
    }
    
    /**
     * Récupère tous les types d'événements distincts
     * @return Liste des types d'événements uniques
     */
    public List<String> getAllEventTypes() throws SQLException {
        List<String> types = new ArrayList<>();
        String query = "SELECT DISTINCT type FROM evenement";
        try {
            pst = conn.prepareStatement(query);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                types.add(rs.getString("type"));
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            throw ex;
        }
        return types;
    }
    
    /**
     * Récupère les événements d'un type spécifique
     * @param type Type d'événement recherché
     * @return Liste des événements du type spécifié
     */
    public List<Evenement> getEventsByType(String type) throws SQLException {
        ObservableList<Evenement> evenements = FXCollections.observableArrayList();
        String query = "SELECT * FROM evenement WHERE type = ?";
        try {
            pst = conn.prepareStatement(query);
            pst.setString(1, type);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Evenement evenement = new Evenement();
                evenement.setId(rs.getInt("id"));
                evenement.setTitre(rs.getString("titre"));
                evenement.setDescription(rs.getString("description"));
                evenement.setType(rs.getString("type"));
                evenement.setLocation(rs.getString("location"));
                evenement.setDateD(rs.getTimestamp("dateD").toLocalDateTime());
                evenement.setDateF(rs.getTimestamp("dateF").toLocalDateTime());
                evenement.setImage(rs.getString("image"));
                evenement.setNbPlace(rs.getInt("nbPlace"));
                evenements.add(evenement);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            throw ex;
        }
        return evenements;
    }

    public void supprimer(int id) throws SQLException {
        String query = "DELETE FROM evenement WHERE id = ?";
        try {
            pst = conn.prepareStatement(query);
            pst.setInt(1, id);
            pst.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            throw ex;
        }
    }
    
    /**
     * Ajoute un nouvel événement
     * @param evenement L'événement à ajouter
     * @return L'ID de l'événement ajouté
     */
    public int ajouter(Evenement evenement) throws SQLException {
        String query = "INSERT INTO evenement (titre, description, type, location, dateD, dateF, image, nbPlace) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            pst = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            pst.setString(1, evenement.getTitre());
            pst.setString(2, evenement.getDescription());
            pst.setString(3, evenement.getType());
            pst.setString(4, evenement.getLocation());
            pst.setTimestamp(5, Timestamp.valueOf(evenement.getDateD()));
            pst.setTimestamp(6, Timestamp.valueOf(evenement.getDateF()));
            pst.setString(7, evenement.getImage());
            pst.setInt(8, evenement.getNbPlace());
            pst.executeUpdate();
            
            ResultSet rs = pst.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            throw ex;
        }
        return -1;
    }
    
    /**
     * Met à jour un événement existant
     * @param evenement L'événement à mettre à jour
     */
    public void modifier(Evenement evenement) throws SQLException {
        String query = "UPDATE evenement SET titre = ?, description = ?, type = ?, location = ?, dateD = ?, dateF = ?, image = ?, nbPlace = ? WHERE id = ?";
        try {
            pst = conn.prepareStatement(query);
            pst.setString(1, evenement.getTitre());
            pst.setString(2, evenement.getDescription());
            pst.setString(3, evenement.getType());
            pst.setString(4, evenement.getLocation());
            pst.setTimestamp(5, Timestamp.valueOf(evenement.getDateD()));
            pst.setTimestamp(6, Timestamp.valueOf(evenement.getDateF()));
            pst.setString(7, evenement.getImage());
            pst.setInt(8, evenement.getNbPlace());
            pst.setInt(9, evenement.getId());
            pst.executeUpdate();
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            throw ex;
        }
    }
    
    /**
     * Recherche des événements par mot-clé
     * @param keyword Mot-clé à rechercher dans le titre ou la description
     * @return Liste des événements correspondants
     */
    public List<Evenement> searchEvents(String keyword) throws SQLException {
        ObservableList<Evenement> evenements = FXCollections.observableArrayList();
        String query = "SELECT * FROM evenement WHERE titre LIKE ? OR description LIKE ?";
        try {
            pst = conn.prepareStatement(query);
            pst.setString(1, "%" + keyword + "%");
            pst.setString(2, "%" + keyword + "%");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                Evenement evenement = new Evenement();
                evenement.setId(rs.getInt("id"));
                evenement.setTitre(rs.getString("titre"));
                evenement.setDescription(rs.getString("description"));
                evenement.setType(rs.getString("type"));
                evenement.setLocation(rs.getString("location"));
                evenement.setDateD(rs.getTimestamp("dateD").toLocalDateTime());
                evenement.setDateF(rs.getTimestamp("dateF").toLocalDateTime());
                evenement.setImage(rs.getString("image"));
                evenement.setNbPlace(rs.getInt("nbPlace"));
                evenements.add(evenement);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            throw ex;
        }
        return evenements;
    }
} 