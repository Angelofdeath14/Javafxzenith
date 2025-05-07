package tn.esprit.controller;

import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import tn.esprit.entities.Evenement;
import tn.esprit.entities.Session;
import tn.esprit.service.EvenementService;
import tn.esprit.service.SessionService;
import tn.esprit.service.StatisticsService;
import tn.esprit.utils.AnimationUtils;
import tn.esprit.utils.MainStyleFixer;
import tn.esprit.utils.StatsUtils;
import tn.esprit.utils.TTSService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Contrôleur pour la vue détaillée d'un événement
 */
public class EventDetailsController implements Initializable {
    @FXML private ImageView eventImage;
    @FXML private Label eventTitle;
    @FXML private Label eventDate;
    @FXML private Label eventLocation;
    @FXML private Label eventType;
    @FXML private Label eventPrice;
    @FXML private TextArea eventDescription;
    @FXML private Label fillRateLabel;
    @FXML private VBox sessionsContainer;
    @FXML private Button btnReserve;
    @FXML private Button btnClose;
    @FXML private Button btnMeteo;
    @FXML private Button btnShowMap;
    @FXML private Button btnShowSessions;
    @FXML private Button btnModifier;
    @FXML private Button btnSupprimer;
    @FXML private Button btnTTS;
    @FXML private Button btnFullScreen;
    @FXML private Button btnToggleFullScreen;
    @FXML private Label availabilityBadge;
    @FXML private VBox statsContainer;
    
    private Evenement event;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
    private SessionService sessionService;
    private StatisticsService statsService;
    
    private boolean isAdminMode = false;
    private boolean isFullScreen = false;
    
    // Interface fonctionnelle pour la notification de réservation
    private Runnable onReservationCompleteCallback;
    
    /**
     * Initialise le contrôleur
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            sessionService = new SessionService();
            statsService = new StatisticsService();
            
            // Configurer les actions des boutons
            btnClose.setOnAction(e -> closeWindow());
            btnReserve.setOnAction(e -> reserveEvent());
            
            // Configurer les actions des nouveaux boutons
            if (btnMeteo != null) btnMeteo.setOnAction(e -> showWeather());
            if (btnShowMap != null) btnShowMap.setOnAction(e -> showMap());
            if (btnShowSessions != null) btnShowSessions.setOnAction(e -> showAllSessions());
            if (btnModifier != null) btnModifier.setOnAction(e -> modifyEvent());
            if (btnSupprimer != null) btnSupprimer.setOnAction(e -> deleteEvent());
            if (btnTTS != null) btnTTS.setOnAction(e -> readDescriptionAloud());
            
            // Configurer les boutons de plein écran
            if (btnFullScreen != null) btnFullScreen.setOnAction(e -> toggleFullScreen());
            if (btnToggleFullScreen != null) btnToggleFullScreen.setOnAction(e -> toggleFullScreen());
            
            // Masquer les boutons d'administration par défaut
            if (btnModifier != null) btnModifier.setVisible(false);
            if (btnSupprimer != null) btnSupprimer.setVisible(false);
            
            // Ajouter des animations
            AnimationUtils.addClickEffect(btnReserve);
            AnimationUtils.addClickEffect(btnClose);
            if (btnMeteo != null) AnimationUtils.addClickEffect(btnMeteo);
            if (btnShowMap != null) AnimationUtils.addClickEffect(btnShowMap);
            if (btnShowSessions != null) AnimationUtils.addClickEffect(btnShowSessions);
            if (btnModifier != null) AnimationUtils.addClickEffect(btnModifier);
            if (btnSupprimer != null) AnimationUtils.addClickEffect(btnSupprimer);
            if (btnTTS != null) AnimationUtils.addClickEffect(btnTTS);
            if (btnFullScreen != null) AnimationUtils.addClickEffect(btnFullScreen);
            if (btnToggleFullScreen != null) AnimationUtils.addClickEffect(btnToggleFullScreen);
            
            // Réduire la taille de la description
            if (eventDescription != null) {
                eventDescription.setPrefHeight(25);
                eventDescription.setMaxHeight(50);
                eventDescription.setStyle("-fx-font-size: 6pt;");
            }
            
            // Configurer la fenêtre au chargement complet de la scène
            eventTitle.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    // Appliquer immédiatement les styles compacts à tous les nœuds
                    applyCompactStyles(newScene.getRoot());
                    
                    // Configurer la fenêtre
                    Stage stage = (Stage) newScene.getWindow();
                    if (stage != null) {
                        // Configurer la taille et les limites de la fenêtre (dimensions réduites)
                        stage.setWidth(400);
                        stage.setHeight(320);
                        stage.setMinWidth(350);
                        stage.setMinHeight(300);
                        stage.setMaxWidth(450);
                        stage.setMaxHeight(350);
                        stage.setTitle("Détails de l'événement");
                        
                        // Utiliser les méthodes existantes de MainStyleFixer pour appliquer le style
                        MainStyleFixer.applyProfessionalStyle(newScene.getRoot());
                        MainStyleFixer.enhanceButtons(newScene.getRoot());
                        
                        // S'assurer que les boutons du bas sont visibles et correctement dimensionnés
                        if (btnMeteo != null) {
                            btnMeteo.setMinSize(18, 18);
                            btnMeteo.setPrefSize(18, 18);
                            btnMeteo.setMaxSize(18, 18);
                            btnMeteo.setStyle("-fx-font-size: 8pt;");
                            btnMeteo.setVisible(true);
                        }
                        
                        if (btnShowMap != null) {
                            btnShowMap.setMinSize(18, 18);
                            btnShowMap.setPrefSize(18, 18);
                            btnShowMap.setMaxSize(18, 18);
                            btnShowMap.setStyle("-fx-font-size: 8pt;");
                            btnShowMap.setVisible(true);
                        }
                        
                        if (btnToggleFullScreen != null) {
                            btnToggleFullScreen.setMinSize(18, 18);
                            btnToggleFullScreen.setPrefSize(18, 18);
                            btnToggleFullScreen.setMaxSize(18, 18);
                            btnToggleFullScreen.setStyle("-fx-font-size: 8pt;");
                            btnToggleFullScreen.setVisible(true);
                        }
                    }
                }
            });
            
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Bascule entre le mode plein écran et le mode normal
     */
    private void toggleFullScreen() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        if (stage != null) {
            isFullScreen = !isFullScreen;
            
            if (isFullScreen) {
                // Sauvegarder la position et taille actuelle avant de passer en plein écran
                double oldX = stage.getX();
                double oldY = stage.getY();
                double oldWidth = stage.getWidth();
                double oldHeight = stage.getHeight();
                
                // Stocker ces valeurs pour la restauration
                stage.setUserData(new double[] {oldX, oldY, oldWidth, oldHeight});
                
                // Passer en plein écran
                stage.setFullScreen(true);
                stage.setFullScreenExitHint("");  // Supprimer le message de sortie du plein écran
                
                // Mettre à jour les icônes des boutons
                if (btnFullScreen != null) btnFullScreen.setText("⬇");
                if (btnToggleFullScreen != null) btnToggleFullScreen.setText("⬇");
                
                // Mettre à jour les tooltips
                if (btnFullScreen != null) {
                    btnFullScreen.setTooltip(new Tooltip("Quitter le plein écran"));
                }
                if (btnToggleFullScreen != null) {
                    btnToggleFullScreen.setTooltip(new Tooltip("Quitter le plein écran"));
                }
            } else {
                // Désactiver le mode plein écran
                stage.setFullScreen(false);
                
                // Restaurer la taille et position d'origine si disponibles
                if (stage.getUserData() instanceof double[] savedData) {
                    stage.setX(savedData[0]);
                    stage.setY(savedData[1]);
                    stage.setWidth(savedData[2]);
                    stage.setHeight(savedData[3]);
                } else {
                    // Si pas de données sauvegardées, utiliser des valeurs par défaut
                    stage.setWidth(400);
                    stage.setHeight(320);
                    stage.centerOnScreen();
                }
                
                // Mettre à jour les icônes des boutons
                if (btnFullScreen != null) btnFullScreen.setText("🔍");
                if (btnToggleFullScreen != null) btnToggleFullScreen.setText("🔍");
                
                // Mettre à jour les tooltips
                if (btnFullScreen != null) {
                    btnFullScreen.setTooltip(new Tooltip("Plein écran"));
                }
                if (btnToggleFullScreen != null) {
                    btnToggleFullScreen.setTooltip(new Tooltip("Plein écran"));
                }
            }
            
            // Animer la transition
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), btnClose.getScene().getRoot());
            scaleTransition.setFromX(0.95);
            scaleTransition.setFromY(0.95);
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
            scaleTransition.play();
        }
    }
    
    /**
     * Applique les styles compacts à tous les éléments de l'interface
     */
    private void applyCompactStyles(Parent root) {
        // Récursivement parcourir les nœuds et masquer les barres de défilement
        if (root == null) return;
        
        // Appliquer le style aux ScrollPane
        for (javafx.scene.Node node : root.getChildrenUnmodifiable()) {
            if (node instanceof javafx.scene.control.ScrollPane scrollPane) {
                scrollPane.setVbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
                scrollPane.setHbarPolicy(javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER);
                scrollPane.setPadding(new javafx.geometry.Insets(0));
                scrollPane.setFitToWidth(true);
                scrollPane.setFitToHeight(true);
            }
            
            // Réduire la taille des TextArea
            if (node instanceof TextArea textArea) {
                textArea.setPrefHeight(20);
                textArea.setMaxHeight(80);
                textArea.setWrapText(true);
                textArea.setStyle("-fx-font-size: 7pt;");
            }
            
            // Réduire la taille des Labels
            if (node instanceof Label label) {
                String style = label.getStyle();
                if (!style.contains("-fx-font-size")) {
                    label.setStyle(style + "-fx-font-size: 7pt;");
                }
            }
            
            // Réduire la taille des Buttons
            if (node instanceof Button button) {
                button.setPrefHeight(15);
                button.setMinHeight(15);
                String style = button.getStyle();
                if (!style.contains("-fx-font-size")) {
                    button.setStyle(style + "-fx-font-size: 7pt;");
                }
            }
            
            // Réduire les marges des VBox et HBox
            if (node instanceof javafx.scene.layout.Region region) {
                if (region.getPadding() != null && region.getPadding().getTop() > 3) {
                    region.setPadding(new javafx.geometry.Insets(1));
                }
                
                if (region instanceof VBox vbox) {
                    if (vbox.getSpacing() > 3) {
                        vbox.setSpacing(1);
                    }
                }
                
                if (region instanceof HBox hbox) {
                    if (hbox.getSpacing() > 3) {
                        hbox.setSpacing(2);
                    }
                }
            }
            
            // Réduire la taille des ImageView
            if (node instanceof ImageView imageView) {
                if (imageView.getFitWidth() > 100) {
                    imageView.setFitWidth(70);
                }
                if (imageView.getFitHeight() > 100) {
                    imageView.setFitHeight(50);
                }
            }
            
            // Récursivement parcourir les enfants
            if (node instanceof Parent parent) {
                applyCompactStyles(parent);
            }
        }
    }
    
    /**
     * Définit l'événement à afficher et configure le mode administrateur
     * @param event L'événement à afficher
     * @param adminMode Indique si le mode administrateur est activé
     */
    public void setEvent(Evenement event, boolean adminMode) {
        this.event = event;
        this.isAdminMode = adminMode;
        loadEventDetails();
        if (adminMode) {
            updateAdminButtons();
        }
    }
    
    // Rétrocompatibilité avec l'ancienne méthode
    public void setEvent(Evenement event) {
        setEvent(event, false);
    }
    
    /**
     * Charge les détails de l'événement
     */
    private void loadEventDetails() {
        // Titre de l'événement
        eventTitle.setText(event.getTitre());
        
        // Date formatée
        String dateText = "";
        if (event.getDateD() != null) {
            dateText = event.getDateD().format(formatter);
            if (event.getDateF() != null && !event.getDateD().equals(event.getDateF())) {
                dateText += " au " + event.getDateF().format(formatter);
            }
        }
        eventDate.setText(dateText);
        
        // Lieu de l'événement
        eventLocation.setText(event.getLocation());
        
        // Type d'événement
        eventType.setText(event.getType());
        
        // Description
        eventDescription.setText(event.getDescription());
        
        // Prix (nouveau)
        if (eventPrice != null) {
            double prix = event.getPrix() != null ? event.getPrix() : 0.0;
            eventPrice.setText(String.format("%.2f DT", prix));
        }
        
        // Image
        loadEventImage();
        
        // Charger les sessions associées
        loadSessionsData();
        
        // Charger les statistiques
        loadStatistics();
        
        // Mettre à jour le badge de disponibilité
        updateAvailabilityBadge();
        
        // Afficher/masquer les boutons d'administration
        updateAdminButtons();
    }
    
    /**
     * Charge l'image de l'événement
     */
    private void loadEventImage() {
        try {
            String imagePath = event.getImage();
            
            if (imagePath != null && !imagePath.isEmpty()) {
                Image image = null;
                boolean imageLoaded = false;
                
                // Vérifier si c'est une URL ou un chemin local
                if (imagePath.startsWith("http") || imagePath.startsWith("www")) {
                    // Image à partir d'une URL
                    image = new Image(imagePath, true);
                    imageLoaded = true;
                } else {
                    // Image locale - d'abord essayer le chemin direct
                    File file = new File(imagePath);
                    if (file.exists()) {
                        image = new Image(file.toURI().toString());
                        imageLoaded = true;
                    } else {
                        // Essayer dans le répertoire d'images XAMPP
                        String uploadDir = "C:\\xampp\\htdocs\\imageP\\";
                        File uploadedImage = new File(uploadDir + imagePath);
                        System.out.println("Tentative de chargement de l'image EventDetails: " + uploadedImage.getAbsolutePath());
                        
                        if (uploadedImage.exists()) {
                            image = new Image(uploadedImage.toURI().toString());
                            imageLoaded = true;
                            System.out.println("✅ Image EventDetails chargée avec succès depuis le répertoire XAMPP");
                        } else {
                            System.out.println("❌ Image EventDetails non trouvée: " + uploadedImage.getAbsolutePath());
                        }
                    }
                }
                
                if (imageLoaded && image != null) {
                    eventImage.setImage(image);
                    
                    // Gérer les erreurs de chargement d'image
                    image.errorProperty().addListener((obs, oldError, error) -> {
                        if (error) {
                            loadDefaultImage(event.getType());
                        }
                    });
                } else {
                    loadDefaultImage(event.getType());
                }
            } else {
                loadDefaultImage(event.getType());
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image EventDetails: " + e.getMessage());
            loadDefaultImage(event.getType());
        }
    }
    
    /**
     * Charge une image par défaut basée sur le type d'événement
     * @param type Type d'événement
     */
    private void loadDefaultImage(String type) {
        String defaultImage;
        
        // Choisir une image par défaut selon le type d'événement
        switch (type.toLowerCase()) {
            case "concert":
                defaultImage = "/images/default_concert.jpg";
                break;
            case "exposition":
                defaultImage = "/images/default_exposition.jpg";
                break;
            case "théâtre":
            case "theatre":
                defaultImage = "/images/default_theatre.jpg";
                break;
            case "festival":
                defaultImage = "/images/default_festival.jpg";
                break;
            default:
                defaultImage = "/images/default_event.jpg";
        }
        
        try {
            Image image = new Image(getClass().getResourceAsStream(defaultImage));
            eventImage.setImage(image);
        } catch (Exception e) {
            // Si l'image par défaut ne peut pas être chargée, utiliser un placeholder de couleur
            eventImage.setImage(null);
            eventImage.setStyle("-fx-background-color: #e0e0e0;");
        }
    }
    
    /**
     * Charge les données des sessions associées à cet événement
     */
    private void loadSessionsData() {
        try {
            List<Session> sessions = sessionService.getSessionsByEvent(event.getId());
            
            // Vider le conteneur
            sessionsContainer.getChildren().clear();
            
            // Ajouter chaque session
            for (Session session : sessions) {
                HBox sessionRow = createSessionRow(session);
                sessionsContainer.getChildren().add(sessionRow);
            }
            
            if (sessions.isEmpty()) {
                Label noSessionsLabel = new Label("Aucune session disponible pour cet événement");
                noSessionsLabel.getStyleClass().add("empty-message");
                sessionsContainer.getChildren().add(noSessionsLabel);
            }
        } catch (SQLException e) {
            Label errorLabel = new Label("Impossible de charger les sessions: " + e.getMessage());
            errorLabel.getStyleClass().add("error-message");
            sessionsContainer.getChildren().add(errorLabel);
        }
    }
    
    /**
     * Crée une ligne pour afficher une session
     * @param session La session à afficher
     * @return Un composant HBox contenant les informations de la session
     */
    private HBox createSessionRow(Session session) {
        HBox row = new HBox(15);
        row.getStyleClass().add("session-row");
        
        // Date et heure
        VBox dateTimeBox = new VBox(5);
        dateTimeBox.getStyleClass().add("session-date-time");
        
        String date = session.getStartTime() != null ? 
                session.getStartTime().format(DateTimeFormatter.ofPattern("dd MMM")) : "";
        String time = session.getStartTime() != null ? 
                session.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")) : "";
        
        Label dateLabel = new Label(date);
        dateLabel.getStyleClass().add("session-date");
        
        Label timeLabel = new Label(time);
        timeLabel.getStyleClass().add("session-time");
        
        dateTimeBox.getChildren().addAll(dateLabel, timeLabel);
        
        // Informations de la session
        VBox infoBox = new VBox(5);
        infoBox.getStyleClass().add("session-info");
        
        Label titleLabel = new Label(session.getTitre() != null ? session.getTitre() : "Session " + session.getId());
        titleLabel.getStyleClass().add("session-title");
        
        Label locationLabel = new Label(session.getLocation() != null ? session.getLocation() : "");
        locationLabel.getStyleClass().add("session-location");
        
        infoBox.getChildren().addAll(titleLabel, locationLabel);
        
        // Places disponibles
        VBox seatsBox = new VBox(5);
        seatsBox.getStyleClass().add("session-seats");
        
        int availableSeats = session.getCapacity();
        int totalSeats = session.getAvailableSeats();
        int reservedSeats = totalSeats - availableSeats;
        
        Label seatsLabel = new Label(availableSeats + " / " + totalSeats);
        seatsLabel.getStyleClass().add("session-seats-count");
        
        Label seatsTextLabel = new Label("places disponibles");
        seatsTextLabel.getStyleClass().add("session-seats-text");
        
        seatsBox.getChildren().addAll(seatsLabel, seatsTextLabel);
        
        // Bouton de réservation
        Button reserveButton = new Button("Réserver");
        reserveButton.getStyleClass().add("session-reserve-button");
        reserveButton.setDisable(availableSeats <= 0);
        
        reserveButton.setOnAction(e -> reserveSession(session));
        
        // Ajouter tous les éléments à la ligne
        row.getChildren().addAll(dateTimeBox, infoBox, seatsBox, reserveButton);
        
        return row;
    }
    
    /**
     * Charge les statistiques pour cet événement
     */
    private void loadStatistics() {
        try {
            // Vider le conteneur
            statsContainer.getChildren().clear();
            
            // Taux de remplissage
            double fillRate = statsService.getEventFillRate(event.getId());
            fillRateLabel.setText(String.format("%.1f%%", fillRate));
            
            // Statistiques de réservation par session
            List<StatsUtils.BarChartData> sessionStats = statsService.getSessionReservationsStats(event.getId());
            if (!sessionStats.isEmpty()) {
                Label statsTitle = new Label("Réservations par session");
                statsTitle.getStyleClass().add("stats-section-title");
                
                // Créer le graphique en barres manuellement
                VBox barChart = new VBox(10);
                barChart.getStyleClass().add("stats-chart");
                barChart.setPadding(new javafx.geometry.Insets(10));
                
                // Construire le graphique avec des éléments visuels simples
                for (StatsUtils.BarChartData data : sessionStats) {
                    HBox barRow = new HBox(10);
                    barRow.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                    
                    Label nameLabel = new Label(data.getCategory());
                    nameLabel.setPrefWidth(150);
                    nameLabel.setWrapText(true);
                    
                    // La barre de progression
                    double maxValue = sessionStats.stream().mapToDouble(StatsUtils.BarChartData::getValue).max().orElse(100);
                    double percent = data.getValue() / maxValue;
                    
                    javafx.scene.layout.Region bar = new javafx.scene.layout.Region();
                    bar.setPrefHeight(25);
                    bar.setPrefWidth(percent * 300); // largeur maximale de la barre: 300px
                    bar.setStyle("-fx-background-color: #3f51b5;");
                    
                    Label valueLabel = new Label(String.valueOf(data.getValue()));
                    valueLabel.setTextFill(javafx.scene.paint.Color.WHITE);
                    valueLabel.setTranslateX(-30);
                    
                    barRow.getChildren().addAll(nameLabel, bar, valueLabel);
                    barChart.getChildren().add(barRow);
                }
                
                statsContainer.getChildren().addAll(statsTitle, barChart);
            }
            
            // Tendance des réservations
            List<StatsUtils.TrendData> trendData = statsService.getReservationTrend(event.getId());
            if (!trendData.isEmpty()) {
                Label trendTitle = new Label("Tendance des réservations");
                trendTitle.getStyleClass().add("stats-section-title");
                
                // Utiliser le LineChart existant dans StatsUtils
                VBox lineChart = StatsUtils.createTrendChart("", trendData);
                
                statsContainer.getChildren().addAll(trendTitle, lineChart);
            }
        } catch (SQLException e) {
            Label errorLabel = new Label("Impossible de charger les statistiques: " + e.getMessage());
            errorLabel.getStyleClass().add("error-message");
            statsContainer.getChildren().add(errorLabel);
        }
    }
    
    /**
     * Met à jour le badge de disponibilité
     */
    private void updateAvailabilityBadge() {
        try {
            // Récupérer les sessions associées à cet événement
            List<Session> sessions = sessionService.getSessionsByEvent(event.getId());
            
            int totalCapacity = 0;
            int totalAvailable = 0;
            
            for (Session session : sessions) {
                totalCapacity += session.getAvailableSeats();
                totalAvailable += session.getCapacity();
            }
            
            int reservedSeats = totalCapacity - totalAvailable;
            double fillRate = totalCapacity > 0 ? (double) reservedSeats / totalCapacity * 100 : 0;
            
            // Configurer le texte et la classe CSS selon la disponibilité
            if (fillRate >= 98) {
                availabilityBadge.setText("COMPLET");
                availabilityBadge.getStyleClass().add("badge-sold-out");
            } else if (fillRate >= 85) {
                availabilityBadge.setText("DERNIÈRES PLACES");
                availabilityBadge.getStyleClass().add("badge-last-seats");
            } else {
                int placesRestantes = totalAvailable;
                availabilityBadge.setText(placesRestantes + " PLACES");
                availabilityBadge.getStyleClass().add("badge-available");
            }
        } catch (SQLException e) {
            // En cas d'erreur, masquer le badge
            availabilityBadge.setVisible(false);
        }
    }
    
    /**
     * Ferme la fenêtre
     */
    private void closeWindow() {
        try {
            // Obtenir la fenêtre actuelle
            Stage stage = (Stage) btnClose.getScene().getWindow();
            if (stage != null) {
                // Créer un effet de fondu avant de fermer
                AnimationUtils.fadeOut(btnClose.getScene().getRoot(), 300);
                
                // Fermer après un court délai
                javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(Duration.millis(300));
                delay.setOnFinished(event -> stage.close());
                delay.play();
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la fermeture de la fenêtre: " + e.getMessage());
            // En cas d'erreur, tenter une fermeture directe
            Stage stage = (Stage) btnClose.getScene().getWindow();
            if (stage != null) {
                stage.close();
            }
        }
    }
    
    /**
     * Ouvre l'interface de réservation pour l'événement courant
     */
    private void reserveEvent() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Reservation.fxml"));
            Parent root = loader.load();
            
            ReservationController controller = loader.getController();
            controller.setEvent(this.event);
            
            // Configurer un callback pour mettre à jour l'interface après réservation
            controller.setOnReservationComplete(() -> {
                try {
                    // Recharger les données de l'événement
                    event = new EvenementService().getOne(event.getId());
                    loadEventDetails();
                    
                    // Notifier le contrôleur parent que la réservation est complétée
                    notifyReservationComplete();
                } catch (SQLException e) {
                    System.err.println("Erreur lors du rechargement des données: " + e.getMessage());
                }
            });
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.setTitle("Réservation");
            
            Scene scene = new Scene(root);
            MainStyleFixer.applyProfessionalStyle(scene);
            
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors de l'ouverture de la fenêtre de réservation: " + e.getMessage());
        }
    }
    
    /**
     * Ouvre la fenêtre de réservation pour une session spécifique
     * @param session La session à réserver
     */
    private void reserveSession(Session session) {
        // Cette méthode serait implémentée pour ouvrir la fenêtre de réservation
        System.out.println("Réserver la session " + session.getTitre());
    }
    
    /**
     * Affiche/masque les boutons d'administration selon le mode
     */
    private void updateAdminButtons() {
        if (btnModifier != null) btnModifier.setVisible(isAdminMode);
        if (btnSupprimer != null) btnSupprimer.setVisible(isAdminMode);
    }
    
    /**
     * Affiche les informations météo pour le lieu de l'événement
     */
    private void showWeather() {
        // TODO: Implémenter l'affichage de la météo
        System.out.println("Affichage des informations météo pour " + event.getLocation());
    }
    
    /**
     * Affiche la carte avec l'emplacement de l'événement
     */
    private void showMap() {
        // TODO: Implémenter l'affichage de la carte
        System.out.println("Affichage de la carte pour " + event.getLocation());
    }
    
    /**
     * Affiche toutes les sessions de l'événement
     */
    private void showAllSessions() {
        // TODO: Implémenter l'affichage de toutes les sessions
        System.out.println("Affichage de toutes les sessions pour " + event.getTitre());
    }
    
    /**
     * Modifie l'événement
     */
    private void modifyEvent() {
        System.out.println("Modification de l'événement: " + event.getTitre());
        // Code pour modifier l'événement
    }
    
    /**
     * Supprime l'événement
     */
    private void deleteEvent() {
        System.out.println("Suppression de l'événement: " + event.getTitre());
        // Code pour supprimer l'événement
    }
    
    /**
     * Définit le rappel à exécuter lorsqu'une réservation est complétée
     * @param callback Le callback à exécuter
     */
    public void setOnReservationComplete(Runnable callback) {
        this.onReservationCompleteCallback = callback;
    }
    
    /**
     * Appelé lorsqu'une réservation est complétée
     */
    private void notifyReservationComplete() {
        if (onReservationCompleteCallback != null) {
            onReservationCompleteCallback.run();
        }
    }
    
    /**
     * Lit la description de l'événement à voix haute
     */
    private void readDescriptionAloud() {
        if (event != null && event.getDescription() != null && !event.getDescription().isEmpty()) {
            try {
                // Indique que la lecture va commencer
                Label btnLabel = (Label) btnTTS.getGraphic();
                btnLabel.setText("⏳");
                btnTTS.setDisable(true);
                
                // Utiliser le service TTS pour lire la description
                TTSService ttsService = TTSService.getInstance();
                
                // Si la lecture est en cours, l'arrêter
                if (ttsService.isSpeaking()) {
                    ttsService.stop();
                    btnLabel.setText("🔊");
                    btnTTS.setDisable(false);
                    return;
                }
                
                // Tester le son d'abord avec un bip simple
                boolean soundWorks = ttsService.testSound();
                System.out.println("Test son: " + (soundWorks ? "OK" : "Échec"));
                
                // Limiter le texte pour éviter les problèmes
                String textToRead = event.getDescription();
                if (textToRead.length() > 2000) {
                    textToRead = textToRead.substring(0, 2000) + "...";
                }
                
                // Utiliser la méthode améliorée qui nettoie le texte et utilise un fichier temporaire
                System.out.println("Tentative de lecture TTS via fichier temporaire");
                ttsService.speakFromFile(textToRead, true);
                
                // Réactiver le bouton après un court délai
                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                        javafx.application.Platform.runLater(() -> {
                            btnLabel.setText("🔊");
                            btnTTS.setDisable(false);
                        });
                    } catch (InterruptedException ex) {
                        Thread.currentThread().interrupt();
                    }
                }).start();
            } catch (Exception ex) {
                System.err.println("Erreur lors de la lecture vocale: " + ex.getMessage());
                ex.printStackTrace(); // Afficher la trace complète pour déboguer
                Label btnLabel = (Label) btnTTS.getGraphic();
                btnLabel.setText("🔊");
                btnTTS.setDisable(false);
                
                // Afficher un message d'erreur
                showAlert("Erreur de lecture", "Problème de synthèse vocale", 
                         "Une erreur est survenue: " + ex.getMessage());
            }
        } else {
            showAlert("Pas de description", "Aucun texte à lire", "Cet événement n'a pas de description.");
        }
    }

    /**
     * Affiche une alerte
     */
    private void showAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 