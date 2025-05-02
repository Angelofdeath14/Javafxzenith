package Controller;

import Entity.Evenement;
import Entity.Session;
import Utils.AnimationUtils;
import Utils.MainStyleFixer;
import Utils.StatsUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import services.EvenementService;
import services.SessionService;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

public class EventStatsController implements Initializable {
    @FXML private ComboBox<Evenement> eventSelector;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private ToggleButton darkModeToggle;
    
    // Cartes de statistiques
    @FXML private VBox eventsCard;
    @FXML private VBox sessionsCard;
    @FXML private VBox reservationsCard;
    @FXML private VBox reservationRateCard;
    @FXML private VBox trendChartContainer;
    
    // Graphiques
    @FXML private PieChart categoryChart;
    @FXML private BarChart<String, Number> reservationChart;
    
    // Tableau
    @FXML private TableView<Evenement> eventsTable;
    @FXML private TableColumn<Evenement, String> colTitle;
    @FXML private TableColumn<Evenement, String> colCategory;
    @FXML private TableColumn<Evenement, String> colDate;
    @FXML private TableColumn<Evenement, Integer> colSessions;
    @FXML private TableColumn<Evenement, Integer> colReservations;
    @FXML private TableColumn<Evenement, String> colTrend;
    @FXML private TableColumn<Evenement, Void> colActions;
    
    private boolean isDarkMode = false;
    private EvenementService evenementService;
    private SessionService sessionService;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    // Constructeur qui gère l'exception SQLException
    public EventStatsController() {
        try {
            this.evenementService = new EvenementService();
            this.sessionService = new SessionService();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de connexion", "Erreur de connexion à la base de données",
                    "Impossible de se connecter à la base de données: " + e.getMessage());
        }
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Initialiser les contrôles
            setupControls();
            
            // Charger les données initiales
            loadData();
            
            // Appliquer les animations
            applyAnimations();
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur d'initialisation", 
                    "Impossible d'initialiser la vue: " + e.getMessage());
        }
    }
    
    private void setupControls() {
        // Configurer le sélecteur d'événements
        try {
            ObservableList<Evenement> events = FXCollections.observableArrayList(evenementService.getAllEvents());
            eventSelector.setItems(events);
            eventSelector.valueProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    loadDataForEvent(newVal);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Configurer le toggle de mode sombre
        darkModeToggle.setOnAction(e -> toggleDarkMode());
        
        // Initialiser les colonnes du tableau
        initializeTableColumns();
    }
    
    private void toggleDarkMode() {
        isDarkMode = darkModeToggle.isSelected();
        if (isDarkMode) {
            darkModeToggle.setText("☀️ Mode clair");
        } else {
            darkModeToggle.setText("🌙 Mode sombre");
        }
        
        // Appliquer le mode sombre/clair à la scène
        if (darkModeToggle.getScene() != null) {
            if (isDarkMode) {
                darkModeToggle.getScene().getStylesheets().add(getClass().getResource("/map_style_dark.css").toExternalForm());
            } else {
                darkModeToggle.getScene().getStylesheets().removeIf(s -> s.contains("map_style_dark.css"));
            }
        }
    }
    
    private void initializeTableColumns() {
        colTitle.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colCategory.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getType()));
        
        // Formater la date
        colDate.setCellValueFactory(cellData -> {
            Evenement event = cellData.getValue();
            String dateStr = "N/A";
            if (event.getDateD() != null) {
                dateStr = event.getDateD().format(dateFormatter);
                if (event.getDateF() != null && !event.getDateF().equals(event.getDateD())) {
                    dateStr += " - " + event.getDateF().format(dateFormatter);
                }
            }
            return new javafx.beans.property.SimpleStringProperty(dateStr);
        });
        
        // Compter les sessions
        colSessions.setCellValueFactory(cellData -> {
            try {
                int sessionCount = sessionService.getSessionsByEvent(cellData.getValue().getId()).size();
                return new javafx.beans.property.SimpleObjectProperty<>(sessionCount);
            } catch (Exception e) {
                return new javafx.beans.property.SimpleObjectProperty<>(0);
            }
        });
        
        // Compter les réservations (simulé pour l'exemple)
        colReservations.setCellValueFactory(cellData -> {
            // Simuler des réservations aléatoires pour la démonstration
            Random random = new Random(cellData.getValue().getId()); // Utiliser l'ID comme seed pour avoir toujours le même nombre
            int reservationCount = 20 + random.nextInt(100);
            return new javafx.beans.property.SimpleObjectProperty<>(reservationCount);
        });
        
        // Tendance (simulée pour l'exemple)
        colTrend.setCellValueFactory(cellData -> {
            Random random = new Random(cellData.getValue().getId() + 1); // Différent seed
            boolean isPositive = random.nextBoolean();
            double percentage = 5 + random.nextDouble() * 20;
            String trend = isPositive ? "▲ +" : "▼ -";
            trend += String.format("%.1f%%", percentage);
            return new javafx.beans.property.SimpleStringProperty(trend);
        });
        
        // Configurer les actions
        colActions.setCellFactory(col -> {
            TableCell<Evenement, Void> cell = new TableCell<>() {
                private final Button btnView = new Button("Voir");
                {
                    btnView.getStyleClass().add("secondary");
                    btnView.setStyle("-fx-font-size: 11px; -fx-padding: 3 8;");
                    
                    btnView.setOnAction(e -> {
                        Evenement event = getTableView().getItems().get(getIndex());
                        // Pour cet exemple, simplement sélectionner l'événement dans le ComboBox
                        eventSelector.setValue(event);
                    });
                }
                
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(btnView);
                    }
                }
            };
            return cell;
        });
    }
    
    private void loadData() {
        try {
            // Charger tous les événements pour le tableau
            ObservableList<Evenement> events = FXCollections.observableArrayList(evenementService.getAllEvents());
            eventsTable.setItems(events);
            
            // Générer des statistiques agrégées pour tous les événements
            int totalEvents = events.size();
            int totalSessions = 0;
            int totalReservations = 0;
            int previousEvents = Math.max(1, totalEvents - 3); // Simuler une période précédente avec ~3 événements de moins
            
            // Données pour les graphiques et cartes
            List<StatsUtils.PieChartData> categoryData = new ArrayList<>();
            List<StatsUtils.BarChartData> reservationData = new ArrayList<>();
            List<Double> trendData = new ArrayList<>();
            
            // Pour la démonstration, simuler des données
            Random random = new Random();
            
            // Types d'événements pour le camembert
            int concertCount = 0, expositionCount = 0, theatreCount = 0, autreCount = 0;
            
            // Collecter des données par événement
            for (Evenement event : events) {
                // Compter par type
                switch (event.getType().toLowerCase()) {
                    case "concert": concertCount++; break;
                    case "exposition": expositionCount++; break;
                    case "theatre": theatreCount++; break;
                    default: autreCount++;
                }
                
                // Simuler le nombre de sessions par événement
                int sessionCount = 1 + random.nextInt(4); // 1-4 sessions par événement
                totalSessions += sessionCount;
                
                // Simuler le nombre de réservations par événement
                int reservationCount = 10 + random.nextInt(50) * sessionCount;
                totalReservations += reservationCount;
                
                // Ajouter des données pour l'histogramme (top 5)
                if (reservationData.size() < 5) {
                    reservationData.add(new StatsUtils.BarChartData(
                        event.getTitre().length() > 12 ? event.getTitre().substring(0, 10) + "..." : event.getTitre(),
                        reservationCount
                    ));
                }
            }
            
            // Simuler les données de réservation historiques pour la courbe de tendance
            double baseValue = totalReservations * 0.7; // Démarrer à 70% du total actuel
            for (int i = 0; i < 6; i++) {
                double growthFactor = 0.7 + (i * 0.06); // Croissance progressive
                trendData.add(baseValue * growthFactor);
            }
            trendData.add((double) totalReservations); // Valeur actuelle
            
            // Données pour le camembert des catégories
            if (concertCount > 0) categoryData.add(new StatsUtils.PieChartData("Concerts", concertCount));
            if (expositionCount > 0) categoryData.add(new StatsUtils.PieChartData("Expositions", expositionCount));
            if (theatreCount > 0) categoryData.add(new StatsUtils.PieChartData("Théâtre", theatreCount));
            if (autreCount > 0) categoryData.add(new StatsUtils.PieChartData("Autres", autreCount));
            
            // Calculer le taux de réservation
            double capacityTotal = totalSessions * 100; // Simuler 100 places par session
            double reservationRate = totalReservations * 100.0 / capacityTotal;
            
            // Simuler les données historiques
            int previousSessions = Math.max(1, totalSessions - 5);
            int previousReservations = (int)(totalReservations * 0.8);
            
            // Définir quelques icônes SVG pour les statistiques
            String eventsIcon = "M7,5H21V7H7V5M7,13V11H21V13H7M4,4.5A1.5,1.5 0 0,1 5.5,6A1.5,1.5 0 0,1 4,7.5A1.5,1.5 0 0,1 2.5,6A1.5,1.5 0 0,1 4,4.5M4,10.5A1.5,1.5 0 0,1 5.5,12A1.5,1.5 0 0,1 4,13.5A1.5,1.5 0 0,1 2.5,12A1.5,1.5 0 0,1 4,10.5M7,19V17H21V19H7M4,16.5A1.5,1.5 0 0,1 5.5,18A1.5,1.5 0 0,1 4,19.5A1.5,1.5 0 0,1 2.5,18A1.5,1.5 0 0,1 4,16.5Z"; // icône liste
            String sessionsIcon = "M3,5H9V11H3V5M5,7V9H7V7H5M11,7H21V9H11V7M11,15H21V17H11V15M5,13V15H7V13H5M3,13H9V19H3V13Z"; // icône sessions
            String reservationsIcon = "M14,12H15.5V14.82L17.94,16.23L17.19,17.53L14,15.69V12M4,2H18A2,2 0 0,1 20,4V10.1C17.9,8.6 15,8 12,8C7,8 2,10 2,16V4A2,2 0 0,1 4,2M4,20A2,2 0 0,1 2,18V16A10,10 0 0,1 12,6A10,10 0 0,1 22,16V18A2,2 0 0,1 20,20H4Z"; // icône réservations
            
            // Créer des cartes de statistiques avancées
            eventsCard.getChildren().setAll(StatsUtils.createAdvancedStatCard(
                "ÉVÉNEMENTS TOTAUX",
                totalEvents,
                previousEvents,
                "Événements enregistrés",
                eventsIcon
            ));
            
            sessionsCard.getChildren().setAll(StatsUtils.createAdvancedStatCard(
                "SESSIONS",
                totalSessions,
                previousSessions,
                "Sessions programmées",
                sessionsIcon
            ));
            
            reservationsCard.getChildren().setAll(StatsUtils.createAdvancedStatCard(
                "RÉSERVATIONS",
                totalReservations,
                previousReservations,
                "Places réservées",
                reservationsIcon
            ));
            
            // Créer la carte de taux de remplissage avec style de pourcentage
            reservationRateCard.getChildren().setAll(StatsUtils.createPercentageCard(
                "TAUX DE REMPLISSAGE", 
                String.format("%d / %d", totalReservations, (int)capacityTotal), 
                reservationRate
            ));
            
            // Créer le graphique de tendance pour les réservations
            trendChartContainer.getChildren().setAll(StatsUtils.createTrendCard(
                "ÉVOLUTION DES RÉSERVATIONS", 
                totalReservations, 
                trendData, 
                "Croissance", 
                true
            ));
            
            // Charger les graphiques
            loadPieChart(categoryData);
            loadBarChart(reservationData);
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement", 
                    "Impossible de charger les données: " + e.getMessage());
        }
    }
    
    private void loadDataForEvent(Evenement event) {
        try {
            // Récupérer les sessions de l'événement
            List<Session> sessions = sessionService.getSessionsByEvent(event.getId());
            
            // Calculer les statistiques
            int totalSessions = sessions.size();
            Random random = new Random(event.getId()); // Pour simuler des données cohérentes
            
            // Simuler les données de réservation pour l'événement
            int capacityPerSession = 80 + random.nextInt(50); // 80-130 places par session
            int totalCapacity = totalSessions * capacityPerSession;
            int totalReservations = 0;
            
            List<StatsUtils.BarChartData> sessionData = new ArrayList<>();
            
            for (int i = 0; i < sessions.size(); i++) {
                Session session = sessions.get(i);
                // Simuler un taux d'occupation de 50-90% pour chaque session
                int reservationCount = (int)(capacityPerSession * (0.5 + random.nextDouble() * 0.4));
                totalReservations += reservationCount;
                
                sessionData.add(new StatsUtils.BarChartData(
                    "Session " + (i+1), 
                    reservationCount
                ));
            }
            
            // Calculer le taux de remplissage
            double fillRate = totalCapacity > 0 ? (double) totalReservations / totalCapacity * 100 : 0;
            
            // Simuler les données historiques
            int previousReservations = (int)(totalReservations * 0.7);
            
            // Données pour le camembert (répartition des places)
            List<StatsUtils.PieChartData> capacityData = new ArrayList<>();
            capacityData.add(new StatsUtils.PieChartData("Places réservées", totalReservations));
            capacityData.add(new StatsUtils.PieChartData("Places disponibles", totalCapacity - totalReservations));
            
            // Simuler des données historiques de réservation pour la tendance
            List<Double> trendData = new ArrayList<>();
            double baseValue = totalReservations * 0.6;
            for (int i = 0; i < 6; i++) {
                double growthFactor = 0.6 + (i * 0.07);
                trendData.add(baseValue * growthFactor);
            }
            trendData.add((double) totalReservations);
            
            // Mettre à jour les cartes avec les données de l'événement
            String sessionsIcon = "M3,5H9V11H3V5M5,7V9H7V7H5M11,7H21V9H11V7M11,15H21V17H11V15M5,13V15H7V13H5M3,13H9V19H3V13Z";
            String capacityIcon = "M12,5.5A3.5,3.5 0 0,1 15.5,9A3.5,3.5 0 0,1 12,12.5A3.5,3.5 0 0,1 8.5,9A3.5,3.5 0 0,1 12,5.5M5,8C5.56,8 6.08,8.15 6.53,8.42C6.38,9.85 6.8,11.27 7.66,12.38C7.16,13.34 6.16,14 5,14A3,3 0 0,1 2,11A3,3 0 0,1 5,8M19,8A3,3 0 0,1 22,11A3,3 0 0,1 19,14C17.84,14 16.84,13.34 16.34,12.38C17.2,11.27 17.62,9.85 17.47,8.42C17.92,8.15 18.44,8 19,8M5.5,18.25C5.5,16.18 8.41,14.5 12,14.5C15.59,14.5 18.5,16.18 18.5,18.25V20H5.5V18.25M0,20V18.5C0,17.11 1.89,15.94 4.45,15.6C3.86,16.28 3.5,17.22 3.5,18.25V20H0M24,20H20.5V18.25C20.5,17.22 20.14,16.28 19.55,15.6C22.11,15.94 24,17.11 24,18.5V20Z";
            String reservationsIcon = "M14,12H15.5V14.82L17.94,16.23L17.19,17.53L14,15.69V12M4,2H18A2,2 0 0,1 20,4V10.1C17.9,8.6 15,8 12,8C7,8 2,10 2,16V4A2,2 0 0,1 4,2M4,20A2,2 0 0,1 2,18V16A10,10 0 0,1 12,6A10,10 0 0,1 22,16V18A2,2 0 0,1 20,20H4Z";
            
            // Mettre à jour les cartes
            sessionsCard.getChildren().setAll(StatsUtils.createAdvancedStatCard(
                "SESSIONS",
                totalSessions,
                totalSessions > 0 ? totalSessions - 1 : 0, // Simuler une augmentation
                "Sessions pour \"" + event.getTitre() + "\"",
                sessionsIcon
            ));
            
            eventsCard.getChildren().setAll(StatsUtils.createAdvancedStatCard(
                "CAPACITÉ TOTALE",
                totalCapacity,
                totalCapacity,
                "Places disponibles",
                capacityIcon
            ));
            
            reservationsCard.getChildren().setAll(StatsUtils.createAdvancedStatCard(
                "RÉSERVATIONS",
                totalReservations,
                previousReservations,
                "Places réservées",
                reservationsIcon
            ));
            
            // Mettre à jour la carte de pourcentage
            reservationRateCard.getChildren().setAll(StatsUtils.createPercentageCard(
                "TAUX DE REMPLISSAGE", 
                String.format("%d / %d", totalReservations, totalCapacity), 
                fillRate
            ));
            
            // Créer le graphique de tendance pour les réservations
            trendChartContainer.getChildren().setAll(StatsUtils.createTrendCard(
                "ÉVOLUTION DES RÉSERVATIONS", 
                totalReservations, 
                trendData, 
                "Croissance", 
                true
            ));
            
            // Mettre à jour les graphiques
            loadPieChart(capacityData);
            loadBarChart(sessionData);
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement", 
                    "Impossible de charger les données pour l'événement sélectionné: " + e.getMessage());
        }
    }
    
    private void loadPieChart(List<StatsUtils.PieChartData> data) {
        categoryChart.getData().clear();
        for (StatsUtils.PieChartData item : data) {
            categoryChart.getData().add(new PieChart.Data(item.getLabel(), item.getValue()));
        }
        
        // Appliquer des styles
        categoryChart.setAnimated(true);
        categoryChart.setLabelsVisible(true);
        categoryChart.setLegendVisible(true);
        categoryChart.setStartAngle(90);
    }
    
    private void loadBarChart(List<StatsUtils.BarChartData> data) {
        reservationChart.getData().clear();
        javafx.scene.chart.XYChart.Series<String, Number> series = new javafx.scene.chart.XYChart.Series<>();
        
        for (StatsUtils.BarChartData item : data) {
            series.getData().add(new javafx.scene.chart.XYChart.Data<>(item.getCategory(), item.getValue()));
        }
        
        reservationChart.getData().add(series);
        reservationChart.setAnimated(true);
    }
    
    private void applyAnimations() {
        // Animer les cartes de statistiques
        List<javafx.scene.Node> cards = new ArrayList<>();
        cards.add(eventsCard);
        cards.add(sessionsCard);
        cards.add(reservationsCard);
        cards.add(reservationRateCard);
        cards.add(trendChartContainer);
        
        AnimationUtils.sequentialFadeIn(cards.toArray(new javafx.scene.Node[0]), 100, 500);
    }
    
    @FXML
    void handleApplyFilter() {
        // Pour cet exemple, simplement recharger les données
        if (eventSelector.getValue() != null) {
            loadDataForEvent(eventSelector.getValue());
        } else {
            loadData();
        }
    }
    
    @FXML
    void handleExportPDF() {
        showAlert(Alert.AlertType.INFORMATION, "Exporter PDF", "Fonction en développement", 
                "L'exportation PDF sera disponible dans une prochaine version.");
    }
    
    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        
        // Appliquer un style professionnel à l'alerte
        MainStyleFixer.styleProfessionalDialog(alert.getDialogPane());
        
        alert.showAndWait();
    }
} 