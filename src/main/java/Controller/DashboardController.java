package Controller;

import Entity.Evenement;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.EvenementService;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class DashboardController implements Initializable {

    @FXML private Button btnEvents;
    @FXML private Button btnUsers;
    @FXML private Button btnLogout;
    @FXML private ScrollPane scrollPane;
    @FXML private VBox mainContainer;
    @FXML private Label lblTotalEvents;
    @FXML private Label lblUpcomingEvents;
    @FXML private Label lblTotalUsers;
    @FXML private TableView<Evenement> recentEventsTable;
    @FXML private TableColumn<Evenement, String> colTitre;
    @FXML private TableColumn<Evenement, String> colType;
    @FXML private TableColumn<Evenement, String> colDate;

    private EvenementService evenementService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Configuration du ScrollPane
            scrollPane.setFitToWidth(true);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

            // Initialisation du service
            evenementService = new EvenementService();

            // Configuration des colonnes
            colTitre.setCellValueFactory(new PropertyValueFactory<>("titre"));
            colType.setCellValueFactory(new PropertyValueFactory<>("type"));
            colDate.setCellValueFactory(cellData -> {
                if (cellData.getValue().getDateD() != null) {
                    return new SimpleStringProperty(
                        cellData.getValue().getDateD().format(
                            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                        )
                    );
                }
                return new SimpleStringProperty("");
            });

            // Chargement des données
            loadDashboardData();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR,
                     "Erreur",
                     "Erreur de chargement",
                     "Impossible de charger les données du tableau de bord: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadDashboardData() {
        try {
            // Récupération des événements
            List<Evenement> allEvents = evenementService.getAllEvents();
            lblTotalEvents.setText(String.valueOf(allEvents.size()));

            // Calcul des événements à venir
            LocalDateTime now = LocalDateTime.now();
            List<Evenement> upcomingEvents = allEvents.stream()
                .filter(e -> e.getDateD() != null && e.getDateD().isAfter(now))
                .collect(Collectors.toList());
            lblUpcomingEvents.setText(String.valueOf(upcomingEvents.size()));

            // Affichage des événements récents (limité à 5)
            ObservableList<Evenement> recentEvents = FXCollections.observableArrayList(
                allEvents.stream()
                    .limit(5)
                    .collect(Collectors.toList())
            );
            recentEventsTable.setItems(recentEvents);

            // Pour le nombre d'utilisateurs, on met une valeur factice car
            // nous n'avons pas implémenté la gestion des utilisateurs
            lblTotalUsers.setText("0");

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR,
                     "Erreur",
                     "Erreur de chargement",
                     "Impossible de charger les données: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void goToEvents() {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL url = getClass().getResource("/AffichageEvent.fxml");
            if (url == null) {
                url = getClass().getClassLoader().getResource("AffichageEvent.fxml");
            }
            if (url == null) {
                String resourcePath = "file:" + System.getProperty("user.dir") + "/target/classes/AffichageEvent.fxml";
                url = new URL(resourcePath);
            }
            
            loader.setLocation(url);
            Parent root = loader.load();
            
            Stage stage = (Stage) btnEvents.getScene().getWindow();
            stage.setTitle("Gestion des Événements");
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR,
                     "Erreur",
                     "Erreur de navigation",
                     "Impossible de naviguer vers les événements: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void goToUsers() {
        // À implémenter si nécessaire
        showAlert(Alert.AlertType.INFORMATION,
                 "Information",
                 "Fonctionnalité non implémentée",
                 "La gestion des utilisateurs n'est pas encore implémentée.");
    }

    @FXML
    private void Logout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL url = getClass().getResource("/Login.fxml");
            if (url == null) {
                url = getClass().getClassLoader().getResource("Login.fxml");
            }
            if (url == null) {
                String resourcePath = "file:" + System.getProperty("user.dir") + "/target/classes/Login.fxml";
                url = new URL(resourcePath);
            }
            
            loader.setLocation(url);
            Parent root = loader.load();
            
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.setTitle("Connexion");
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR,
                     "Erreur",
                     "Erreur de déconnexion",
                     "Impossible de se déconnecter: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 