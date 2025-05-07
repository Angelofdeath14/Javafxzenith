package tn.esprit.controller;


import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.entities.Evenement;
import tn.esprit.entities.Session;
import tn.esprit.service.EvenementService;
import tn.esprit.service.SessionService;
import tn.esprit.utils.AnimationUtils;
import tn.esprit.utils.MainStyleFixer;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class AffichageSessionsController implements Initializable {
    @FXML private TableView<Session> tableView;
    @FXML private TableColumn<Session, Integer> colId;
    @FXML private TableColumn<Session, String> colTitle;
    @FXML private TableColumn<Session, String> colEvenement;
    @FXML private TableColumn<Session, String> colStartTime;
    @FXML private TableColumn<Session, String> colEndTime;
    @FXML private TableColumn<Session, String> colLocation;
    @FXML private TableColumn<Session, Integer> colCapacity;
    @FXML private TableColumn<Session, Integer> colAvailable;
    @FXML private TableColumn<Session, Void> colActions;
    @FXML private ComboBox<String> filterTypeComboBox;
    @FXML private TextField searchField;
    @FXML private Button btnRefresh;
    @FXML private Button btnAdd;
    @FXML private Button btnBack;
    @FXML private Label statusLabel;

    private SessionService sessionService;
    private EvenementService evenementService;
    private ObservableList<Session> sessionsList;
    private Evenement currentEvenement;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            System.out.println("Initialisation du contrôleur AffichageSessionsController...");
            sessionService = new SessionService();
            evenementService = new EvenementService();
            sessionsList = FXCollections.observableArrayList();

            setupTableColumns();
            loadSessions();
            setupEventFilter();
            setupSearch();
            setupButtons();
            
            // Afficher les statistiques des vues
            showSessionStats();
            
            System.out.println("AffichageSessionsController initialisé avec succès");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'initialisation: " + e.getMessage());
            e.printStackTrace();
            showError("Erreur de connexion", "Impossible de se connecter à la base de données : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Erreur générale: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        
        colEvenement.setCellValueFactory(cellData -> {
            int evenementId = cellData.getValue().getEvenementId();
            Evenement evenement = evenementService.getOne(evenementId);
            return new SimpleStringProperty(evenement != null ? evenement.getTitre() : "");
        });
        
        colStartTime.setCellValueFactory(cellData -> {
            LocalDateTime startTime = cellData.getValue().getStartTime();
            return new SimpleStringProperty(startTime != null ? startTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "");
        });
        
        colEndTime.setCellValueFactory(cellData -> {
            LocalDateTime endTime = cellData.getValue().getEndTime();
            return new SimpleStringProperty(endTime != null ? endTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "");
        });
        
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colCapacity.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        colAvailable.setCellValueFactory(new PropertyValueFactory<>("availableSeats"));
        
        setupActionColumn();
    }

    private void setupActionColumn() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");
            private final Button statsButton = new Button("Stats");
            private final HBox buttonsBox = new HBox(5, editButton, deleteButton, statsButton);

            {
                buttonsBox.setAlignment(Pos.CENTER);
                
                // Style pour les boutons
                editButton.getStyleClass().add("button");
                editButton.getStyleClass().add("secondary");
                editButton.setMinWidth(75);
                
                deleteButton.getStyleClass().add("button");
                deleteButton.getStyleClass().add("danger");
                deleteButton.setMinWidth(75);
                
                statsButton.getStyleClass().add("button");
                statsButton.setMinWidth(75);

                editButton.setOnAction(event -> {
                    Session session = getTableView().getItems().get(getIndex());
                    handleModifierSession(session);
                });

                deleteButton.setOnAction(event -> {
                    Session session = getTableView().getItems().get(getIndex());
                    handleSupprimerSession(session);
                });
                
                statsButton.setOnAction(event -> {
                    Session session = getTableView().getItems().get(getIndex());
                    showSessionDetailedStats(session);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttonsBox);
            }
        });
    }

    private void loadSessions() {
        try {
            List<Session> sessions = sessionService.getAllSessions();
            sessionsList.setAll(sessions);
            tableView.setItems(sessionsList);
            
            // Mettre à jour le label de statut
            statusLabel.setText("Total de sessions: " + sessions.size());
        } catch (SQLException e) {
            showError("Erreur lors du chargement des sessions", e.getMessage());
        }
    }

    private void setupEventFilter() {
        // Récupérer tous les types d'événements pour le filtre
        List<String> eventTypes = evenementService.getAllEventTypes();
        eventTypes.add(0, "Tous"); // Ajouter l'option "Tous" au début
        
        filterTypeComboBox.setItems(FXCollections.observableArrayList(eventTypes));
        filterTypeComboBox.setValue("Tous");

        filterTypeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                if ("Tous".equals(newVal)) {
                    loadSessions();
            } else {
                    filterSessionsByEventType(newVal);
                }
            }
        });
    }

    private void setupSearch() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                filterSessionsBySearch(newVal);
            }
        });
    }

    private void setupButtons() {
        btnRefresh.setOnAction(e -> loadSessions());
        
        btnAdd.setOnAction(e -> handleAjouterSession());
        
        AnimationUtils.addClickEffect(btnRefresh);
        AnimationUtils.addClickEffect(btnAdd);
        AnimationUtils.addClickEffect(btnBack);
    }

    private void filterSessionsByEventType(String eventType) {
        try {
            // Filtrer les sessions par type d'événement en utilisant les sessions disponibles
            // Nous devons implémenter le filtrage côté client puisque la méthode n'existe pas dans le service
            List<Session> allSessions = sessionService.getAllSessions();
            List<Session> filteredSessions = allSessions.stream()
                .filter(session -> {
                    try {
                        // Obtenir tous les événements et filtrer par ID
                        EvenementService eventService = new EvenementService();
                        List<Evenement> allEvents = eventService.getAllEvenements();
                        
                        // Trouver l'événement correspondant à cette session
                        Evenement matchingEvent = allEvents.stream()
                            .filter(event -> event.getId() == session.getEvenementId())
                            .findFirst()
                            .orElse(null);
                            
                        return matchingEvent != null && matchingEvent.getType().equalsIgnoreCase(eventType);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return false;
                    }
                })
                .collect(Collectors.toList());
            
            sessionsList.setAll(filteredSessions);
            statusLabel.setText("Sessions de type \"" + eventType + "\": " + filteredSessions.size());
        } catch (SQLException e) {
            showError("Erreur lors du filtrage des sessions", e.getMessage());
        }
    }

    private void filterSessionsBySearch(String searchText) {
        try {
            List<Session> filteredSessions = sessionService.searchSessions(searchText);
            sessionsList.setAll(filteredSessions);
            statusLabel.setText("Résultats pour \"" + searchText + "\": " + filteredSessions.size());
        } catch (SQLException e) {
            showError("Erreur lors de la recherche", e.getMessage());
        }
    }

    @FXML
    private void handleAjouterSession() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterSession.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setTitle("Ajouter une session");
            Scene scene = new Scene(root);
            stage.setScene(scene);
            
            // Appliquer le style professionnel
            MainStyleFixer.applyProfessionalStyle(scene);
            
            stage.show();
            
            stage.setOnHidden(e -> loadSessions());
        } catch (IOException e) {
            e.printStackTrace(); // Pour le débogage
            showError("Erreur", "Impossible d'ouvrir la fenêtre d'ajout de session: " + e.getMessage());
        }
    }

    private void handleModifierSession(Session session) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterSession.fxml"));
            Parent root = loader.load();
            
            AjouterSessionController controller = loader.getController();
            controller.initializeForEdit(session);
            
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            
            // Appliquer le style professionnel
            MainStyleFixer.applyProfessionalStyle(scene);
            
            stage.show();
            
            stage.setOnHidden(e -> loadSessions());
        } catch (IOException e) {
            showError("Erreur", "Impossible d'ouvrir la fenêtre de modification");
        }
    }

    private void handleSupprimerSession(Session session) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation de suppression");
        confirmDialog.setHeaderText("Êtes-vous sûr de vouloir supprimer cette session ?");
        confirmDialog.setContentText("Cette action est irréversible.");
        
        MainStyleFixer.styleProfessionalDialog(confirmDialog.getDialogPane());
        
        ButtonType btnOui = new ButtonType("Oui", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnNon = new ButtonType("Non", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmDialog.getButtonTypes().setAll(btnOui, btnNon);
        
        confirmDialog.showAndWait().ifPresent(type -> {
            if (type == btnOui) {
                try {
                    sessionService.supprimerSession(session.getId());
                    loadSessions();
                    showInfo("Succès", "La session a été supprimée avec succès");
                } catch (SQLException e) {
                    showError("Erreur", "Impossible de supprimer la session: " + e.getMessage());
                }
            }
        });
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        MainStyleFixer.styleProfessionalDialog(alert.getDialogPane());
        alert.showAndWait();
    }

    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        MainStyleFixer.styleProfessionalDialog(alert.getDialogPane());
        alert.showAndWait();
    }

    /**
     * Méthode pour afficher des statistiques globales sur les sessions
     */
    private void showSessionStats() {
        try {
            // Créer une fenêtre contextuelle pour les statistiques
            Dialog<Void> statsDialog = new Dialog<>();
            statsDialog.setTitle("Statistiques des sessions");
            statsDialog.setHeaderText("Vue d'ensemble des statistiques");
            
            // Appliquer le style professionnel
            MainStyleFixer.styleProfessionalDialog(statsDialog.getDialogPane());
            
            // Créer un conteneur pour les statistiques
            BorderPane statsContent = new BorderPane();
            statsContent.setPrefSize(800, 600);
            
            // Créer un graphique en camembert pour les types de sessions
            PieChart pieChart = createSessionTypesChart();
            
            // Créer un graphique à barres pour les vues par session
            BarChart<String, Number> barChart = createSessionViewsChart();
            
            // Diviser la vue en deux colonnes
            HBox chartsContainer = new HBox(20);
            chartsContainer.setAlignment(Pos.CENTER);
            chartsContainer.getChildren().addAll(pieChart, barChart);
            
            // Ajouter les graphiques à la vue
            statsContent.setCenter(chartsContainer);
            
            // Créer une section avec des cartes de statistiques
            HBox statsCards = createStatsCards();
            statsContent.setTop(statsCards);
            
            // Configurer le dialogue
            statsDialog.getDialogPane().setContent(statsContent);
            statsDialog.getDialogPane().setPrefSize(850, 650);
            statsDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            
            // Afficher le dialogue
            statsDialog.show();
        } catch (Exception e) {
            showError("Erreur", "Impossible d'afficher les statistiques: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Créer un graphique en camembert des types de sessions
     */
    private PieChart createSessionTypesChart() {
        try {
            // Données fictives pour la démonstration
            Map<String, Integer> sessionTypeStats = getSessionTypeStats();
            
            // Créer des données pour le graphique
            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            for (Map.Entry<String, Integer> entry : sessionTypeStats.entrySet()) {
                pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
            }
            
            // Créer le graphique
            PieChart chart = new PieChart(pieChartData);
            chart.setTitle("Répartition par type");
            chart.setLegendVisible(true);
            chart.setLabelsVisible(true);
            chart.setStartAngle(90);
            chart.setPrefSize(300, 300);
            
            return chart;
        } catch (Exception e) {
            e.printStackTrace();
            PieChart chart = new PieChart();
            chart.setTitle("Erreur de chargement des données");
            return chart;
        }
    }
    
    /**
     * Créer un graphique à barres des vues par session
     */
    private BarChart<String, Number> createSessionViewsChart() {
        // Créer des axes
        javafx.scene.chart.CategoryAxis xAxis = new javafx.scene.chart.CategoryAxis();
        javafx.scene.chart.NumberAxis yAxis = new javafx.scene.chart.NumberAxis();
        xAxis.setLabel("Session");
        yAxis.setLabel("Nombre de vues");
        
        // Créer le graphique
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Vues par session");
        
        // Créer une série de données (fictives pour la démonstration)
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Vues");
        
        // Ajouter des données fictives
        Map<String, Integer> sessionViews = getSessionViewsStats();
        for (Map.Entry<String, Integer> entry : sessionViews.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        
        barChart.getData().add(series);
        barChart.setPrefSize(400, 300);
        
        return barChart;
    }
    
    /**
     * Créer des cartes de statistiques avec des indicateurs clés
     */
    private HBox createStatsCards() {
        HBox cardsContainer = new HBox(20);
        cardsContainer.setAlignment(Pos.CENTER);
        cardsContainer.setPadding(new Insets(20));
        
        // Carte 1: Nombre total de sessions
        VBox totalSessionsCard = createStatsCard("Total Sessions", "42", "+5%", true);
        
        // Carte 2: Vues totales
        VBox totalViewsCard = createStatsCard("Vues Totales", "1,248", "+12%", true);
        
        // Carte 3: Taux de conversion
        VBox conversionRateCard = createStatsCard("Taux de Conversion", "38%", "-2%", false);
        
        // Carte 4: Nombre moyen de participants
        VBox avgParticipantsCard = createStatsCard("Participants Moy.", "28", "+8%", true);
        
        cardsContainer.getChildren().addAll(totalSessionsCard, totalViewsCard, conversionRateCard, avgParticipantsCard);
        
        return cardsContainer;
    }
    
    /**
     * Créer une carte de statistique individuelle
     */
    private VBox createStatsCard(String title, String value, String change, boolean isPositive) {
        VBox card = new VBox(5);
        card.setPadding(new Insets(15));
        card.setMinWidth(150);
        card.getStyleClass().add("stats-card");
        
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("stats-title");
        
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add("stats-value");
        
        HBox changeBox = new HBox(5);
        changeBox.setAlignment(Pos.CENTER);
        
        Label changeIcon = new Label(isPositive ? "▲" : "▼");
        changeIcon.setStyle("-fx-text-fill: " + (isPositive ? "#4caf50" : "#f44336") + ";");
        
        Label changeLabel = new Label(change);
        changeLabel.setStyle("-fx-text-fill: " + (isPositive ? "#4caf50" : "#f44336") + ";");
        
        changeBox.getChildren().addAll(changeIcon, changeLabel);
        
        card.getChildren().addAll(titleLabel, valueLabel, changeBox);
        
        return card;
    }
    
    /**
     * Affiche les statistiques détaillées pour une session spécifique
     */
    private void showSessionDetailedStats(Session session) {
        try {
            // Créer une fenêtre contextuelle pour les statistiques détaillées
            Dialog<Void> statsDialog = new Dialog<>();
            statsDialog.setTitle("Statistiques détaillées");
            statsDialog.setHeaderText("Statistiques pour la session: " + (session.getTitre() != null ? session.getTitre() : "Session " + session.getId()));
            
            // Appliquer le style professionnel
            MainStyleFixer.styleProfessionalDialog(statsDialog.getDialogPane());
            
            // Créer un contenu pour le dialogue
            VBox content = new VBox(20);
            content.setPadding(new Insets(20));
            
            // Données de fréquentation (fictives pour la démonstration)
            int totalRegistrations = new Random().nextInt(100) + 50;
            int attended = (int)(totalRegistrations * (0.7 + 0.3 * Math.random()));
            int noShow = totalRegistrations - attended;
            
            // Créer un graphique pour la fréquentation
            PieChart attendanceChart = new PieChart();
            attendanceChart.setTitle("Fréquentation");
            attendanceChart.getData().add(new PieChart.Data("Présents", attended));
            attendanceChart.getData().add(new PieChart.Data("Absents", noShow));
            
            // Créer un tableau pour les statistiques détaillées
            GridPane statsGrid = new GridPane();
            statsGrid.setHgap(10);
            statsGrid.setVgap(10);
            statsGrid.setPadding(new Insets(10));
            
            statsGrid.add(new Label("Date:"), 0, 0);
            statsGrid.add(new Label(session.getStartTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))), 1, 0);
            
            statsGrid.add(new Label("Durée:"), 0, 1);
            long durationHours = java.time.Duration.between(session.getStartTime(), session.getEndTime()).toHours();
            statsGrid.add(new Label(durationHours + " heures"), 1, 1);
            
            statsGrid.add(new Label("Capacité:"), 0, 2);
            statsGrid.add(new Label(String.valueOf(session.getCapacity())), 1, 2);
            
            statsGrid.add(new Label("Places disponibles:"), 0, 3);
            statsGrid.add(new Label(String.valueOf(session.getAvailableSeats())), 1, 3);
            
            statsGrid.add(new Label("Taux de remplissage:"), 0, 4);
            double fillRate = 100.0 * (1 - (double)session.getAvailableSeats() / session.getCapacity());
            statsGrid.add(new Label(String.format("%.1f%%", fillRate)), 1, 4);
            
            content.getChildren().addAll(
                new Label("Statistiques de fréquentation"),
                attendanceChart,
                new Separator(),
                new Label("Informations détaillées"),
                statsGrid
            );
            
            statsDialog.getDialogPane().setContent(content);
            statsDialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            statsDialog.show();
        } catch (Exception e) {
            showError("Erreur", "Impossible d'afficher les statistiques détaillées: " + e.getMessage());
        }
    }
    
    /**
     * Obtenir des statistiques sur les types de sessions (données fictives)
     */
    private Map<String, Integer> getSessionTypeStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("Concert", 12);
        stats.put("Théâtre", 8);
        stats.put("Exposition", 15);
        stats.put("Conférence", 7);
        return stats;
    }
    
    /**
     * Obtenir des statistiques sur les vues par session (données fictives)
     */
    private Map<String, Integer> getSessionViewsStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("Concert Jazz", 480);
        stats.put("Théâtre Moderne", 350);
        stats.put("Exposition Art", 520);
        stats.put("Concert Rock", 620);
        stats.put("Conférence", 280);
        return stats;
    }
    
    /**
     * Méthode pour gérer le bouton retour
     */
    @FXML
    public void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FrontEvents.fxml"));
            Parent root = loader.load();
            Scene scene = btnBack.getScene();
            
            // Appliquer une transition d'animation
            AnimationUtils.fadeTransition(scene.getRoot(), root, scene);
            
            // Appliquer le style professionnel
            MainStyleFixer.applyProfessionalStyle(scene);
        } catch (IOException e) {
            showError("Erreur de navigation", "Impossible de charger la page principale: " + e.getMessage());
        }
    }
    
    /**
     * Définit l'événement courant pour filtrer les sessions
     */
    public void setEvenement(Evenement evenement) {
        this.currentEvenement = evenement;
        
        if (evenement != null) {
            try {
                List<Session> sessions = sessionService.getSessionsByEvenementId(evenement.getId());
                sessionsList.setAll(sessions);
                tableView.setItems(sessionsList);
                statusLabel.setText("Sessions pour \"" + evenement.getTitre() + "\": " + sessions.size());
            } catch (SQLException e) {
                showError("Erreur", "Impossible de charger les sessions pour cet événement: " + e.getMessage());
            }
        }
    }
} 