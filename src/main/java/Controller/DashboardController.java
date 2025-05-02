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
import javafx.scene.chart.*;
import services.EvenementService;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
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
    @FXML private PieChart categoryChart;
    @FXML private BarChart<String, Number> reservationChart;
    @FXML private LineChart<String, Number> monthlyChart;
    @FXML private Label lblTotalCapacity;
    @FXML private Label lblActiveSessions;
    @FXML private Label lblReservationRate;

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
    
    /**
     * Rafraîchit les données du tableau de bord
     */
    @FXML
    public void refreshDashboard() {
        System.out.println("Actualisation du tableau de bord...");
        loadDashboardData();
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
            
            // Mise à jour du nombre total de places disponibles
            try {
                int totalCapacity = allEvents.stream()
                    .mapToInt(Evenement::getNbPlace)
                    .sum();
                if (lblTotalCapacity != null) {
                    lblTotalCapacity.setText(String.valueOf(totalCapacity));
                }
            } catch (Exception e) {
                System.err.println("Erreur lors du calcul des places: " + e.getMessage());
            }
            
            // Mise à jour du nombre de sessions actives
            try {
                if (lblActiveSessions != null) {
                    lblActiveSessions.setText(String.valueOf(upcomingEvents.size()));
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de la mise à jour des sessions: " + e.getMessage());
            }
            
            // Calcul du taux de réservation (fictif pour l'exemple)
            try {
                if (lblReservationRate != null) {
                    lblReservationRate.setText("0%");
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de la mise à jour du taux: " + e.getMessage());
            }

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
            
            // Chargement des graphiques
            loadCharts(allEvents);

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR,
                     "Erreur",
                     "Erreur de chargement",
                     "Impossible de charger les données: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadCharts(List<Evenement> events) {
        try {
            // Graphique des événements par catégorie
            if (categoryChart != null) {
                Map<String, Long> eventsByType = events.stream()
                    .collect(Collectors.groupingBy(
                        e -> e.getType() != null ? e.getType() : "Non défini", 
                        Collectors.counting()
                    ));
                    
                ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
                eventsByType.forEach((type, count) -> 
                    pieChartData.add(new PieChart.Data(type + " (" + count + ")", count))
                );
                
                categoryChart.setData(pieChartData);
                categoryChart.setTitle("Répartition par type");
            }
            
            // Graphique des top événements par réservation (fictif pour l'exemple)
            if (reservationChart != null) {
                XYChart.Series<String, Number> series = new XYChart.Series<>();
                series.setName("Réservations");
                
                // Ajoutons quelques données fictives basées sur les événements existants
                events.stream().limit(5).forEach(event -> 
                    series.getData().add(new XYChart.Data<>(
                        event.getTitre().length() > 10 ? 
                            event.getTitre().substring(0, 10) + "..." : 
                            event.getTitre(), 
                        Math.round(Math.random() * 100) // Nombre aléatoire de réservations
                    ))
                );
                
                reservationChart.getData().clear();
                reservationChart.getData().add(series);
                reservationChart.setTitle("Top 5 événements");
            }
            
            // Graphique d'évolution mensuelle (fictif pour l'exemple)
            if (monthlyChart != null) {
                XYChart.Series<String, Number> eventsSeries = new XYChart.Series<>();
                eventsSeries.setName("Événements");
                
                XYChart.Series<String, Number> reservationsSeries = new XYChart.Series<>();
                reservationsSeries.setName("Réservations");
                
                // Mois fictifs
                String[] months = {"Jan", "Fév", "Mar", "Avr", "Mai", "Juin"};
                
                for (String month : months) {
                    eventsSeries.getData().add(new XYChart.Data<>(month, Math.round(Math.random() * 10)));
                    reservationsSeries.getData().add(new XYChart.Data<>(month, Math.round(Math.random() * 100)));
                }
                
                monthlyChart.getData().clear();
                monthlyChart.getData().add(eventsSeries);
                monthlyChart.getData().add(reservationsSeries);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement des graphiques: " + e.getMessage());
        }
    }

    @FXML
    private void goToEvents() {
        try {
            FXMLLoader loader = new FXMLLoader();
            // Charger la nouvelle grille d'événements stylisée
            URL url = getClass().getResource("/EventsGrid.fxml");
            if (url == null) {
                url = getClass().getClassLoader().getResource("EventsGrid.fxml");
            }
            if (url == null) {
                String resourcePath = "file:" + System.getProperty("user.dir") + "/target/classes/EventsGrid.fxml";
                url = new URL(resourcePath);
            }
            
            loader.setLocation(url);
            Parent root = loader.load();
            
            Stage stage = (Stage) btnEvents.getScene().getWindow();
            stage.setTitle("Événements Artistiques");
            Scene scene = new Scene(root);
            
            // Appliquer le style professionnel
            String cssUrl = getClass().getResource("/professional_style.css").toExternalForm();
            scene.getStylesheets().add(cssUrl);
            
            stage.setScene(scene);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR,
                     "Erreur",
                     "Erreur de navigation",
                     "Impossible de naviguer vers les événements: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Méthode pour accéder à l'ancienne interface de gestion des événements
     */
    @FXML
    private void goToEventsAdmin() {
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
    
    /**
     * Méthode pour naviguer vers l'interface d'ajout d'événement
     */
    @FXML
    private void goToAddEvent() {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL url = getClass().getResource("/AjouterEvent.fxml");
            if (url == null) {
                url = getClass().getClassLoader().getResource("AjouterEvent.fxml");
            }
            if (url == null) {
                String resourcePath = "file:" + System.getProperty("user.dir") + "/target/classes/AjouterEvent.fxml";
                url = new URL(resourcePath);
            }
            
            loader.setLocation(url);
            Parent root = loader.load();
            
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.setTitle("Ajouter un événement");
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR,
                     "Erreur",
                     "Erreur de navigation",
                     "Impossible de naviguer vers l'ajout d'événement: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Méthode pour naviguer vers l'interface utilisateur frontale
     */
    @FXML
    private void goToFrontView() {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL url = getClass().getResource("/UserInterface.fxml");
            if (url == null) {
                url = getClass().getClassLoader().getResource("UserInterface.fxml");
            }
            if (url == null) {
                String resourcePath = "file:" + System.getProperty("user.dir") + "/target/classes/UserInterface.fxml";
                url = new URL(resourcePath);
            }
            
            loader.setLocation(url);
            Parent root = loader.load();
            
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.setTitle("Artphoria - Interface Utilisateur");
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR,
                     "Erreur",
                     "Erreur de navigation",
                     "Impossible de naviguer vers l'interface utilisateur: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void logout() {
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