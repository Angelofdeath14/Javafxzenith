package Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import Entity.Evenement;
import services.EvenementService;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.sql.SQLException;
import java.util.List;

public class GestionEvenementsController {
    @FXML
    private TableView<Evenement> evenementsTable;
    @FXML
    private TableColumn<Evenement, String> nomColumn;
    @FXML
    private TableColumn<Evenement, String> dateColumn;
    @FXML
    private TableColumn<Evenement, String> lieuColumn;
    @FXML
    private TableColumn<Evenement, Integer> placesColumn;
    @FXML
    private TextField placesReserveesField;
    @FXML
    private Button reserverButton;

    private EvenementService evenementService;

    @FXML
    public void initialize() {
        try {
            evenementService = new EvenementService();
            setupTableColumns();
            loadEvenements();
        } catch (SQLException e) {
            showAlert("Erreur de connexion", "Impossible de se connecter à la base de données : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void setupTableColumns() {
        nomColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitre()));
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDateD().toString()));
        lieuColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLocation()));
        placesColumn.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getNbPlace()).asObject());
    }

    private void loadEvenements() {
        try {
            List<Evenement> evenements = evenementService.getAllEvenements();
            evenementsTable.getItems().setAll(evenements);
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du chargement des événements : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleReservation() {
        Evenement selectedEvenement = evenementsTable.getSelectionModel().getSelectedItem();
        if (selectedEvenement == null) {
            showAlert("Erreur", "Veuillez sélectionner un événement", Alert.AlertType.ERROR);
            return;
        }

        try {
            int places = Integer.parseInt(placesReserveesField.getText());
            if (places <= 0 || places > selectedEvenement.getNbPlace()) {
                showAlert("Erreur", "Nombre de places invalide", Alert.AlertType.ERROR);
                return;
            }

            boolean success = evenementService.reserverPlace(selectedEvenement.getId(), places);
            if (success) {
                showAlert("Succès", "Réservation effectuée avec succès", Alert.AlertType.INFORMATION);
                loadEvenements();
            } else {
                showAlert("Erreur", "Échec de la réservation", Alert.AlertType.ERROR);
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Veuillez entrer un nombre valide", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 