package Controller;

import Entity.Evenement;
import Entity.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import services.EvenementService;
import services.SessionService;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class EventStatsController implements Initializable {
    
    @FXML private ComboBox<Evenement> eventSelector;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    
    @FXML private Label lblTotalEvents;
    @FXML private Label lblTotalSessions;
    @FXML private Label lblTotalReservations;
    @FXML private Label lblReservationRate;
    
    @FXML private PieChart categoryChart;
    @FXML private BarChart<String, Number> reservationChart;
    @FXML private CategoryAxis eventAxis;
    @FXML private NumberAxis reservationAxis;
    
    @FXML private TableView<Evenement> eventsTable;
    @FXML private TableColumn<Evenement, String> colTitle;
    @FXML private TableColumn<Evenement, String> colCategory;
    @FXML private TableColumn<Evenement, String> colDate;
    @FXML private TableColumn<Evenement, Integer> colSessions;
    @FXML private TableColumn<Evenement, Integer> colReservations;
    
    private EvenementService evenementService;
    private SessionService sessionService;
    private List<Evenement> allEvents;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            evenementService = new EvenementService();
            sessionService = new SessionService();
            
            // Initialisation des composants
            setupDatePickers();
            setupTable();
            
            // Chargement des données initiales
            loadEvents();
            updateStats();
            updateCharts();
            
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, 
                    "Erreur de connexion", 
                    "Impossible de se connecter à la base de données", 
                    e.getMessage());
        }
    }
    
    private void setupDatePickers() {
        // Date par défaut: début du mois courant
        LocalDate now = LocalDate.now();
        LocalDate firstDayOfMonth = LocalDate.of(now.getYear(), now.getMonth(), 1);
        startDatePicker.setValue(firstDayOfMonth);
        endDatePicker.setValue(now);
    }
    
    private void setupTable() {
        colTitle.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("type"));
        
        // Formateur de date
        colDate.setCellValueFactory(cellData -> {
            Evenement event = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                event.getDateD().format(formatter) + " - " + event.getDateF().format(formatter));
        });
        
        // Nombre de sessions
        colSessions.setCellValueFactory(cellData -> {
            Evenement event = cellData.getValue();
            try {
                int sessionCount = sessionService.getSessionsByEvenementId(event.getId()).size();
                return new javafx.beans.property.SimpleIntegerProperty(sessionCount).asObject();
            } catch (Exception e) {
                return new javafx.beans.property.SimpleIntegerProperty(0).asObject();
            }
        });
        
        // Nombre de réservations (simulé pour le moment)
        colReservations.setCellValueFactory(cellData -> {
            Evenement event = cellData.getValue();
            // Dans un système réel, on récupérerait les réservations depuis la base de données
            // Pour l'exemple, on utilise un nombre aléatoire
            int randomReservations = new Random().nextInt(100);
            return new javafx.beans.property.SimpleIntegerProperty(randomReservations).asObject();
        });
    }
    
    private void loadEvents() {
        try {
            // Chargement de tous les événements
            allEvents = evenementService.getAllEvents();
            
            // Filtrage des événements selon les dates sélectionnées
            filterEventsByDate();
            
            // Mise à jour du sélecteur d'événements
            ObservableList<Evenement> eventItems = FXCollections.observableArrayList(allEvents);
            eventSelector.setItems(eventItems);
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement", 
                    "Une erreur est survenue lors du chargement des événements: " + e.getMessage());
        }
    }
    
    private void filterEventsByDate() {
        if (allEvents == null) return;
        
        LocalDate startDate = startDatePicker.getValue();
        LocalDate endDate = endDatePicker.getValue();
        
        if (startDate != null && endDate != null) {
            List<Evenement> filteredEvents = allEvents.stream()
                    .filter(event -> {
                        LocalDate eventStart = event.getDateD().toLocalDate();
                        LocalDate eventEnd = event.getDateF().toLocalDate();
                        
                        return (eventStart.isEqual(startDate) || eventStart.isAfter(startDate)) && 
                               (eventEnd.isEqual(endDate) || eventEnd.isBefore(endDate)) ||
                               (eventStart.isBefore(startDate) && eventEnd.isAfter(endDate));
                    })
                    .collect(Collectors.toList());
            
            // Mise à jour du tableau
            eventsTable.setItems(FXCollections.observableArrayList(filteredEvents));
        }
    }
    
    @FXML
    private void handleApplyFilter() {
        filterEventsByDate();
        updateStats();
        updateCharts();
    }
    
    private void updateStats() {
        try {
            // Récupération des événements filtrés
            ObservableList<Evenement> filteredEvents = eventsTable.getItems();
            
            // Nombre total d'événements
            int eventCount = filteredEvents.size();
            lblTotalEvents.setText(String.valueOf(eventCount));
            
            // Gestion des cas où la table n'existe pas encore
            int sessionCount = 0;
            try {
                // Nombre total de sessions
                for (Evenement event : filteredEvents) {
                    List<Session> sessions = sessionService.getSessionsByEvenementId(event.getId());
                    if (sessions != null) {
                        sessionCount += sessions.size();
                    }
                }
            } catch (Exception e) {
                System.err.println("Erreur lors du comptage des sessions: " + e.getMessage());
                // Ne pas propager l'exception pour éviter de bloquer l'interface
            }
            
            lblTotalSessions.setText(String.valueOf(sessionCount));
            
            // Nombre total de réservations (simulé)
            int reservationCount = new Random().nextInt(1000);
            lblTotalReservations.setText(String.valueOf(reservationCount));
            
            // Taux de réservation (simulé)
            int totalCapacity = 0;
            for (Evenement event : filteredEvents) {
                totalCapacity += event.getNbPlace();
            }
            
            double reservationRate = totalCapacity > 0 ? (double) reservationCount / totalCapacity * 100 : 0;
            lblReservationRate.setText(String.format("%.1f%%", reservationRate));
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void updateCharts() {
        updateCategoryChart();
        updateReservationChart();
    }
    
    private void updateCategoryChart() {
        try {
            // Récupération des événements filtrés
            ObservableList<Evenement> filteredEvents = eventsTable.getItems();
            
            // Comptage des catégories
            Map<String, Integer> categoryCounts = new HashMap<>();
            for (Evenement event : filteredEvents) {
                String category = event.getType();
                categoryCounts.put(category, categoryCounts.getOrDefault(category, 0) + 1);
            }
            
            // Création des données du graphique
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            for (Map.Entry<String, Integer> entry : categoryCounts.entrySet()) {
                pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
            }
            
            // Mise à jour du graphique
            categoryChart.setData(pieChartData);
            categoryChart.setTitle("Répartition par catégorie");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void updateReservationChart() {
        try {
            // Récupération des événements filtrés
            ObservableList<Evenement> filteredEvents = eventsTable.getItems();
            
            // Limiter à 10 événements pour la lisibilité
            List<Evenement> topEvents = filteredEvents.stream()
                    .limit(10)
                    .collect(Collectors.toList());
            
            // Création des données du graphique
            reservationChart.getData().clear();
            
            BarChart.Series<String, Number> series = new BarChart.Series<>();
            series.setName("Réservations");
            
            for (Evenement event : topEvents) {
                // Dans un système réel, on récupérerait les réservations depuis la base de données
                int randomReservations = new Random().nextInt(100);
                series.getData().add(new BarChart.Data<>(event.getTitre(), randomReservations));
            }
            
            reservationChart.getData().add(series);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleClose() {
        Stage stage = (Stage) lblTotalEvents.getScene().getWindow();
        stage.close();
    }
    
    @FXML
    private void handleExportPDF() {
        try {
            // Cette méthode simule l'exportation des statistiques en PDF
            // Dans une application réelle, vous utiliseriez une bibliothèque comme iText ou PDFBox
            
            File tempFile = File.createTempFile("event_stats_", ".txt");
            try (FileOutputStream out = new FileOutputStream(tempFile)) {
                String content = "STATISTIQUES DES ÉVÉNEMENTS\n\n" +
                        "Période: " + startDatePicker.getValue().format(formatter) + 
                        " au " + endDatePicker.getValue().format(formatter) + "\n\n" +
                        "Nombre total d'événements: " + lblTotalEvents.getText() + "\n" +
                        "Nombre total de sessions: " + lblTotalSessions.getText() + "\n" +
                        "Nombre total de réservations: " + lblTotalReservations.getText() + "\n" +
                        "Taux de réservation: " + lblReservationRate.getText() + "\n\n";
                
                // Ajouter la liste des événements
                content += "LISTE DES ÉVÉNEMENTS\n\n";
                for (Evenement event : eventsTable.getItems()) {
                    content += "- " + event.getTitre() + " (" + event.getType() + ")\n";
                    content += "  Date: " + event.getDateD().format(formatter) + 
                             " au " + event.getDateF().format(formatter) + "\n";
                    content += "  Lieu: " + event.getLocation() + "\n\n";
                }
                
                out.write(content.getBytes());
            }
            
            // Ouvrir le fichier
            Desktop.getDesktop().open(tempFile);
            
            showAlert(Alert.AlertType.INFORMATION, "Exportation", "Exportation réussie", 
                    "Les statistiques ont été exportées avec succès.");
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur d'exportation", 
                    "Une erreur est survenue lors de l'exportation: " + e.getMessage());
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 