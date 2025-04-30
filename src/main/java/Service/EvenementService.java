package Service;

import Entity.Evenement;
import Utils.DataSource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.sql.*;
import java.util.List;

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
                evenement.setDateD(rs.getTimestamp("date_debut").toLocalDateTime());
                evenement.setDateF(rs.getTimestamp("date_fin").toLocalDateTime());
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
} 