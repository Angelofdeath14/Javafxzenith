package Controller;

import Entity.Evenement;
import Entity.Session;
import Utils.AnimationUtils;
import Utils.MainStyleFixer;
import Utils.StatsUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import services.SessionService;
import services.EvenementService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;

public class SessionStatsController implements Initializable {
    @FXML private Label pageTitle;
    @FXML private Button btnBack;
    @FXML private Label eventName;
    @FXML private Label eventDetails;
    @FXML private VBox sessionsCard;
    @FXML private VBox capacityCard;
    @FXML private VBox reservedCard;
    @FXML private VBox fillRateCard;
    @FXML private VBox pieChartContainer;
    @FXML private VBox barChartContainer;
    @FXML private VBox trendChartContainer;
    @FXML private TableView<Session> sessionsTable;
    @FXML private TableColumn<Session, Integer> sessionIdColumn;
    @FXML private TableColumn<Session, String> sessionNameColumn;
    @FXML private TableColumn<Session, String> sessionDateColumn;
    @FXML private TableColumn<Session, Integer> sessionCapacityColumn;
    @FXML private TableColumn<Session, Integer> sessionReservedColumn;
    @FXML private TableColumn<Session, String> sessionFillRateColumn;
    @FXML private TableColumn<Session, Void> sessionActionsColumn;
    @FXML private ToggleButton darkModeToggle;

    private Evenement evenement;
    private final SessionService sessionService = new SessionService();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private EvenementService evenementService;
    private boolean isDarkMode = false;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            evenementService = new EvenementService();
            
            // Appliquer des effets aux boutons
            AnimationUtils.addClickEffect(btnBack);
            
            // Initialiser les colonnes du tableau
            initializeTableColumns();
            
            // Initialiser le bouton de mode sombre
            setupDarkModeToggle();
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur d'initialisation", 
                    "Impossible d'initialiser les services: " + e.getMessage());
        }
    }
    
    private void setupDarkModeToggle() {
        if (darkModeToggle != null) {
            darkModeToggle.setSelected(isDarkMode);
            darkModeToggle.setOnAction(e -> toggleDarkMode());
        }
    }
    
    private void toggleDarkMode() {
        isDarkMode = !isDarkMode;
        Scene scene = btnBack.getScene();
        if (scene != null) {
            if (isDarkMode) {
                scene.getStylesheets().add(getClass().getResource("/dark_mode.css").toExternalForm());
            } else {
                scene.getStylesheets().removeIf(stylesheet -> stylesheet.contains("dark_mode.css"));
            }
        }
    }
    
    public void setEvenement(Evenement evenement) {
        this.evenement = evenement;
        updateEventInfo();
        loadSessionStats();
    }
    
    private void updateEventInfo() {
        if (evenement == null) return;
        
        // Mettre à jour les informations de l'événement
        eventName.setText(evenement.getTitre());
        
        String details = String.format(
            "Type: %s | Lieu: %s | Du %s au %s",
            evenement.getType(),
            evenement.getLocation(),
            evenement.getDateD().format(formatter),
            evenement.getDateF().format(formatter)
        );
        
        eventDetails.setText(details);
    }
    
    private void loadSessionStats() {
        try {
            // Récupérer les sessions de l'événement
            List<Session> sessions = sessionService.getSessionsByEvent(evenement.getId());
            
            // Calculer les statistiques
            int totalSessions = sessions.size();
            int totalCapacity = 0;
            int totalReserved = 0;
            double fillRate = 0.0;
            
            // Pour la simulation de données historiques (à remplacer par des données réelles dans un environnement de production)
            int previousTotalCapacity = 0;
            int previousTotalReserved = 0;
            double previousFillRate = 0.0;
            
            // Données pour les graphiques
            List<StatsUtils.PieChartData> pieChartData = new ArrayList<>();
            List<StatsUtils.BarChartData> barChartData = new ArrayList<>();
            List<StatsUtils.TrendData> trendData = new ArrayList<>();
            
            // Générer des données de tendance simulées pour les derniers jours (dans un environnement réel, ces données proviendraient de la base de données)
            for (int i = 30; i >= 0; i--) {
                // Simuler une tendance à la hausse avec quelques fluctuations aléatoires
                Random random = new Random();
                double randomFactor = 0.8 + (random.nextDouble() * 0.4); // Entre 0.8 et 1.2
                int reservedForDay = Math.min(totalCapacity, (int)(totalReserved * (0.5 + ((30.0 - i) / 30.0) * 0.5) * randomFactor));
                trendData.add(new StatsUtils.TrendData(
                    String.format("J-%d", i),
                    reservedForDay,
                    totalCapacity
                ));
            }
            
            for (Session session : sessions) {
                // Capacité totale initiale de chaque session
                int initialCapacity = session.getAvailableSeats();
                // Capacité actuelle (places restantes)
                int currentCapacity = session.getCapacity();
                // Places réservées
                int reserved = initialCapacity - currentCapacity;
                
                totalCapacity += initialCapacity;
                totalReserved += reserved;
                
                // Ajouter des données pour les graphiques
                String sessionLabel = session.getTitre() != null ? 
                    session.getTitre() : String.format("Session %d", session.getId());
                
                // Données pour le graphique circulaire
                pieChartData.add(new StatsUtils.PieChartData(
                    sessionLabel,
                    reserved
                ));
                
                // Données pour l'histogramme
                barChartData.add(new StatsUtils.BarChartData(
                    sessionLabel, 
                    reserved
                ));
            }
            
            // Calculer le taux de remplissage
            if (totalCapacity > 0) {
                fillRate = (double) totalReserved / totalCapacity * 100;
            }
            
            // Simuler les données historiques (environ 50-60% des valeurs actuelles)
            // Dans un environnement réel, ces données proviendraient de l'historique
            Random random = new Random();
            previousTotalCapacity = totalCapacity; // Même capacité
            previousTotalReserved = (int)(totalReserved * 0.55) + random.nextInt(5); // Environ 55% des réservations actuelles
            
            if (previousTotalCapacity > 0) {
                previousFillRate = (double) previousTotalReserved / previousTotalCapacity * 100;
            }
            
            // Créer des cartes de statistiques avancées avec des pourcentages calculés correctement
            double sessionsChange = totalSessions > 0 && totalSessions > 1 ? 100.0 : 0.0; // 100% d'augmentation si c'est le premier événement
            
            double capacityChange = 0.0; // Pas de changement dans la capacité
            
            double reservedChange = previousTotalReserved > 0 ? 
                ((double)(totalReserved - previousTotalReserved) / previousTotalReserved) * 100 : 100.0;
            
            double fillRateChange = previousFillRate > 0 ? 
                fillRate - previousFillRate : fillRate;
            
            // Définir quelques icônes SVG pour les statistiques
            String sessionsIcon = "M3,5H9V11H3V5M5,7V9H7V7H5M11,7H21V9H11V7M11,15H21V17H11V15M5,13V15H7V13H5M3,13H9V19H3V13Z"; // icône sessions
            String capacityIcon = "M20,10V14H4V10H20M20,8H4C2.9,8 2,8.9 2,10V14C2,15.1 2.9,16 4,16H20C21.1,16 22,15.1 22,14V10C22,8.9 21.1,8 20,8Z"; // icône capacité
            String reservedIcon = "M12,11.5A2.5,2.5 0 0,1 9.5,9A2.5,2.5 0 0,1 12,6.5A2.5,2.5 0 0,1 14.5,9A2.5,2.5 0 0,1 12,11.5M12,2A7,7 0 0,0 5,9C5,14.25 12,22 12,22C12,22 19,14.25 19,9A7,7 0 0,0 12,2Z"; // icône réservation
            String fillRateIcon = "M19,3H5C3.9,3 3,3.9 3,5V19C3,20.1 3.9,21 5,21H19C20.1,21 21,20.1 21,19V5C21,3.9 20.1,3 19,3M19,19H5V5H19V19M16.5,17.75V16.25H7.5V17.75H16.5M16.5,15.75V14.25H7.5V15.75H16.5M16.5,13.75V12.25H7.5V13.75H16.5M16.5,11.75V10.25H7.5V11.75H16.5M16.5,8V9.5H7.5V8H16.5Z"; // icône taux
            
            // Créer des cartes de statistiques avancées
            sessionsCard.getChildren().setAll(StatsUtils.createAdvancedStatCard(
                "SESSIONS",
                totalSessions,
                Math.max(1, totalSessions - 1), // simuler une session précédente
                "Nombre total de sessions",
                sessionsIcon
            ));
            
            capacityCard.getChildren().setAll(StatsUtils.createAdvancedStatCard(
                "CAPACITÉ TOTALE",
                totalCapacity,
                previousTotalCapacity,
                "Places disponibles",
                capacityIcon
            ));
            
            reservedCard.getChildren().setAll(StatsUtils.createAdvancedStatCard(
                "PLACES RÉSERVÉES",
                totalReserved,
                previousTotalReserved,
                "Places réservées",
                reservedIcon
            ));
            
            // Ajouter le graphique de taux de remplissage
            fillRateCard.getChildren().setAll(StatsUtils.createFillRateCard(
                fillRate,
                fillRateChange,
                "TAUX DE REMPLISSAGE",
                "Pourcentage de places réservées",
                fillRateIcon
            ));
            
            // Ajouter des graphiques
            if (pieChartContainer != null) {
                pieChartContainer.getChildren().setAll(
                    StatsUtils.createPieChart(
                        "Répartition des réservations par session",
                        pieChartData
                    )
                );
            }
            
            if (barChartContainer != null) {
                barChartContainer.getChildren().setAll(
                    StatsUtils.createBarChart(
                        "Nombre de réservations par session",
                        barChartData
                    )
                );
            }
            
            if (trendChartContainer != null) {
                trendChartContainer.getChildren().setAll(
                    StatsUtils.createTrendChart(
                        "Évolution des réservations sur 30 jours",
                        trendData
                    )
                );
            }
            
            // Mettre à jour le tableau des sessions
            updateSessionsTable(sessions);
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement", 
                    "Impossible de charger les statistiques: " + e.getMessage());
        }
    }
    
    private void initializeTableColumns() {
        // Configuration des colonnes
        sessionIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        
        sessionNameColumn.setCellValueFactory(cellData -> {
            Session session = cellData.getValue();
            String name = session.getTitre();
            return javafx.beans.binding.Bindings.createStringBinding(() -> 
                name != null ? name : "Session " + session.getId());
        });
        
        sessionDateColumn.setCellValueFactory(cellData -> {
            Session session = cellData.getValue();
            return javafx.beans.binding.Bindings.createStringBinding(() -> 
                session.getDateDebut() != null ? session.getDateDebut().format(formatter) : "Non définie");
        });
        
        sessionCapacityColumn.setCellValueFactory(cellData -> {
            Session session = cellData.getValue();
            return javafx.beans.binding.Bindings.createObjectBinding(() -> session.getAvailableSeats());
        });
        
        sessionReservedColumn.setCellValueFactory(cellData -> {
            Session session = cellData.getValue();
            int reserved = session.getAvailableSeats() - session.getCapacity();
            return javafx.beans.binding.Bindings.createObjectBinding(() -> reserved);
        });
        
        sessionFillRateColumn.setCellValueFactory(cellData -> {
            Session session = cellData.getValue();
            int available = session.getAvailableSeats();
            int current = session.getCapacity();
            double rate = available > 0 ? ((double)(available - current) / available) * 100 : 0;
            return javafx.beans.binding.Bindings.createStringBinding(() -> 
                String.format("%.1f%%", rate));
        });
        
        // Ajouter une coloration conditionnelle aux colonnes de taux
        sessionFillRateColumn.setCellFactory(column -> new TableCell<Session, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    
                    // Extraire le pourcentage de la chaîne
                    try {
                        double percentage = Double.parseDouble(item.replace("%", ""));
                        
                        // Appliquer un style en fonction du taux de remplissage
                        if (percentage >= 90) {
                            setStyle("-fx-text-fill: #2ecc71; -fx-font-weight: bold;"); // vert
                        } else if (percentage >= 70) {
                            setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;"); // vert foncé
                        } else if (percentage >= 50) {
                            setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;"); // orange
                        } else if (percentage >= 30) {
                            setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;"); // orange foncé
                        } else {
                            setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold;"); // rouge
                        }
                    } catch (NumberFormatException e) {
                        setStyle("");
                    }
                }
            }
        });
        
        // Configurer la colonne d'actions
        sessionActionsColumn.setCellFactory(createActionButtonCellFactory());
    }
    
    private Callback<TableColumn<Session, Void>, TableCell<Session, Void>> createActionButtonCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<Session, Void> call(TableColumn<Session, Void> param) {
                return new TableCell<>() {
                    private final Button viewButton = new Button("Voir");
                    {
                        viewButton.getStyleClass().add("secondary");
                        viewButton.setStyle("-fx-font-size: 10px; -fx-padding: 2 5;");
                        AnimationUtils.addClickEffect(viewButton);
                        
                        viewButton.setOnAction(event -> {
                            Session session = getTableView().getItems().get(getIndex());
                            showSessionDetails(session);
                        });
                    }
                    
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox box = new HBox(5);
                            box.getChildren().add(viewButton);
                            setGraphic(box);
                        }
                    }
                };
            }
        };
    }
    
    private void showSessionDetails(Session session) {
        // Afficher les détails de la session dans une boîte de dialogue
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails de la session");
        alert.setHeaderText("Session: " + session.getId());
        
        String details = String.format(
            "ID: %d\n" +
            "Date: %s\n" +
            "Capacité initiale: %d\n" +
            "Places restantes: %d\n" +
            "Places réservées: %d\n" +
            "Taux de remplissage: %.1f%%",
            session.getId(),
            evenement.getDateD().format(formatter), // Utiliser la date de l'événement
            session.getAvailableSeats(),
            session.getCapacity(),
            session.getAvailableSeats() - session.getCapacity(),
            session.getAvailableSeats() > 0 
                ? (double)(session.getAvailableSeats() - session.getCapacity()) / session.getAvailableSeats() * 100 
                : 0
        );
        
        alert.setContentText(details);
        
        // Appliquer un style professionnel à la boîte de dialogue
        MainStyleFixer.styleProfessionalDialog(alert.getDialogPane());
        
        alert.showAndWait();
    }
    
    @FXML
    private void handleBack() {
        try {
            // Créer un effet de transition pour la navigation
            Scene currentScene = btnBack.getScene();
            if (currentScene != null) {
                AnimationUtils.fadeOut(currentScene.getRoot(), 300);
            }
            
            // Planifier la navigation après la fin de l'animation
            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.millis(300));
            pause.setOnFinished(event -> {
                try {
                    // Charger la vue de gestion des sessions
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionSessions.fxml"));
                    Parent root = loader.load();
                    
                    // Initialiser le contrôleur
                    GestionSessionsController controller = loader.getController();
                    controller.setEvenement(evenement);
                    
                    // Animation d'entrée pour la nouvelle vue
                    AnimationUtils.fadeIn(root, 500);
                    
                    // Changer la scène
                    if (currentScene != null) {
                        currentScene.setRoot(root);
                        
                        // Appliquer le style professionnel
                        MainStyleFixer.applyProfessionalStyle(currentScene);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de navigation", 
                            "Impossible de revenir à la vue précédente: " + e.getMessage());
                }
            });
            pause.play();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de navigation", 
                    "Impossible de revenir à la vue précédente: " + e.getMessage());
        }
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

    /**
     * Met à jour le tableau des sessions avec les données fournies
     */
    private void updateSessionsTable(List<Session> sessions) {
        sessionsTable.getItems().clear();
        sessionsTable.getItems().addAll(sessions);
    }
} 