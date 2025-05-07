package tn.esprit.controller;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import tn.esprit.entities.Evenement;
import tn.esprit.service.EvenementService;
import tn.esprit.service.SessionService;
import tn.esprit.utils.AnimationUtils;
import tn.esprit.utils.LocationUtils;
import tn.esprit.utils.MainStyleFixer;
import tn.esprit.utils.TTSService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class FrontEventsController implements Initializable {
    
    @FXML private FlowPane eventsContainer;
    @FXML private FlowPane thisWeekEventsContainer;
    @FXML private FlowPane upcomingEventsContainer;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterTypeComboBox;
    @FXML private ComboBox<String> filterDate;
    @FXML private Button btnBack;
    @FXML private Button btnAdmin;
    
    private EvenementService evenementService;
    private SessionService sessionService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            System.out.println("FrontEventsController - Initialisation...");
            
            // Initialiser les services
        try {
            evenementService = new EvenementService();
            sessionService = new SessionService();
                System.out.println("Services initialisés avec succès");
            } catch (SQLException e) {
                System.err.println("Erreur lors de l'initialisation des services: " + e.getMessage());
                e.printStackTrace();
                showAlert(AlertType.ERROR,
                         "Erreur de connexion", 
                         "Impossible de se connecter à la base de données", 
                         e.getMessage());
                return;
            }
            
            // Configuration des filtres
            setupFilters();
            
            // Chargement des événements
            loadEvents();
            
            // Configuration du bouton retour
            if (btnBack != null) {
                btnBack.setOnAction(event -> handleBack());
                // Appliquer un style spécial au bouton retour
                btnBack.getStyleClass().add("button-neutral");
                // Ajouter un effet de clic
                AnimationUtils.addClickEffect(btnBack);
            }
            
            // Styliser le bouton d'administration
            if (btnAdmin != null) {
                btnAdmin.getStyleClass().add("button-info");
                AnimationUtils.addClickEffect(btnAdmin);
            }
            
            // Appliquer le style professionnel à la scène après son chargement
            setupSceneListener();
            
            System.out.println("FrontEventsController initialisé avec succès");
            
        } catch (Exception e) {
            System.err.println("Erreur d'initialisation globale: " + e.getMessage());
            e.printStackTrace();
            showAlert(AlertType.ERROR,
                     "Erreur d'initialisation", 
                     "Impossible d'initialiser la vue des événements", 
                     e.getMessage());
        }
    }
    
    private void setupSceneListener() {
        // Attendez que les composants soient attachés à une scène
        if (eventsContainer != null) {
            eventsContainer.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    // Appliquer les styles modernes et colorés
                    MainStyleFixer.applyProfessionalStyle(newScene);
                    MainStyleFixer.applyColorfulButtonsStyle(newScene);
                    MainStyleFixer.styleButtonsByText(newScene.getRoot());
                    
                    // Ajouter un fond dégradé pour un meilleur aspect visuel
                    newScene.getRoot().setStyle(
                        "-fx-background-color: linear-gradient(to bottom, #f5f7fa, #e4e7eb);"
                    );
                }
            });
        } else if (thisWeekEventsContainer != null) {
            thisWeekEventsContainer.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    // Appliquer les styles modernes et colorés
                    MainStyleFixer.applyProfessionalStyle(newScene);
                    MainStyleFixer.applyColorfulButtonsStyle(newScene);
                    MainStyleFixer.styleButtonsByText(newScene.getRoot());
                    
                    // Ajouter un fond dégradé pour un meilleur aspect visuel
                    newScene.getRoot().setStyle(
                        "-fx-background-color: linear-gradient(to bottom, #f5f7fa, #e4e7eb);"
                    );
                }
            });
        } else if (upcomingEventsContainer != null) {
            upcomingEventsContainer.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    // Appliquer les styles modernes et colorés
                    MainStyleFixer.applyProfessionalStyle(newScene);
                    MainStyleFixer.applyColorfulButtonsStyle(newScene);
                    MainStyleFixer.styleButtonsByText(newScene.getRoot());
                    
                    // Ajouter un fond dégradé pour un meilleur aspect visuel
                    newScene.getRoot().setStyle(
                        "-fx-background-color: linear-gradient(to bottom, #f5f7fa, #e4e7eb);"
                    );
                }
            });
        }
    }
    
    private void setupFilters() {
        try {
            System.out.println("Configuration des filtres...");
            
        // Configuration des catégories
            if (filterTypeComboBox != null) {
        filterTypeComboBox.getItems().addAll("Tous", "Concert", "Théâtre", "Exposition", "Sport", "Conférence");
        filterTypeComboBox.setValue("Tous");
                
                // Configurez l'action de filtrage
                filterTypeComboBox.setOnAction(event -> filterEvents());
            }
        
        // Configuration des dates
            if (filterDate != null) {
        filterDate.getItems().addAll("Tous", "Aujourd'hui", "Cette semaine", "Ce mois-ci");
        filterDate.setValue("Tous");
        
                // Configurez l'action de filtrage
                filterDate.setOnAction(event -> filterEvents());
            }
            
            // Configuration du champ de recherche
            if (searchField != null) {
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterEvents();
        });
            }
            
            System.out.println("Filtres configurés avec succès");
        } catch (Exception e) {
            System.err.println("Erreur lors de la configuration des filtres: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void loadEvents() {
        try {
            System.out.println("Chargement des événements...");
            
            // Vérifier si les services sont initialisés
            if (evenementService == null) {
                System.err.println("ERREUR: Le service d'événements n'est pas initialisé");
                evenementService = new EvenementService();
                System.out.println("Service d'événements initialisé avec succès");
            }
            
            // Récupérer les événements
            List<Evenement> events = evenementService.getAllEvents();
            System.out.println("Événements récupérés: " + events.size() + " événements");
            
            // Afficher les détails des événements pour le débogage
            for (Evenement event : events) {
                System.out.println("- Événement ID: " + event.getId() + ", Titre: " + event.getTitre() + 
                                  ", Type: " + event.getType() + ", Date: " + event.getDateD());
            }
            
            // Vérifier si les conteneurs existent
            if (eventsContainer == null) {
                System.err.println("ERREUR: eventsContainer est null");
            } else {
                System.out.println("eventsContainer est disponible");
                populateEventsContainer(events);
            }
            
            if (thisWeekEventsContainer == null) {
                System.err.println("ERREUR: thisWeekEventsContainer est null");
            } else {
                System.out.println("thisWeekEventsContainer est disponible");
                populateThisWeekEventsContainer(events);
            }
            
            if (upcomingEventsContainer == null) {
                System.err.println("ERREUR: upcomingEventsContainer est null");
            } else {
                System.out.println("upcomingEventsContainer est disponible");
                populateUpcomingEventsContainer(events);
            }
            
            System.out.println("Événements chargés avec succès");
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des événements: " + e.getMessage());
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Erreur", "Erreur de chargement",
                     "Impossible de charger les événements: " + e.getMessage());
        }
    }
    
    private void populateEventsContainer(List<Evenement> events) {
        if (eventsContainer == null) {
            System.out.println("eventsContainer est null, impossible d'afficher les événements");
            return;
        }
        
        eventsContainer.getChildren().clear();
        
        // Ajouter un titre de section
        VBox headerBox = new VBox(5);
        headerBox.setPrefWidth(eventsContainer.getPrefWidth());
        headerBox.setStyle("-fx-padding: 10 0 20 10;");
        
        Label sectionTitle = new Label("Tous les événements");
        sectionTitle.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Label sectionSubtitle = new Label("Découvrez tous nos événements disponibles");
        sectionSubtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
        
        headerBox.getChildren().addAll(sectionTitle, sectionSubtitle);
        eventsContainer.getChildren().add(headerBox);
        
        // Ajouter les cartes d'événements
            for (Evenement event : events) {
                VBox eventCard = createEventCard(event);
                eventsContainer.getChildren().add(eventCard);
            }
    }
    
    private void populateThisWeekEventsContainer(List<Evenement> events) {
        if (thisWeekEventsContainer == null) {
            System.out.println("thisWeekEventsContainer est null, impossible d'afficher les événements de la semaine");
            return;
        }
        
        thisWeekEventsContainer.getChildren().clear();
        
        // Filtrer les événements de cette semaine
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endOfWeek = now.plusDays(7);
        
        List<Evenement> thisWeekEvents = new ArrayList<>();
        for (Evenement event : events) {
            if (event.getDateD() != null && 
                (event.getDateD().isAfter(now) || event.getDateD().equals(now)) && 
                event.getDateD().isBefore(endOfWeek)) {
                thisWeekEvents.add(event);
            }
        }
        
        if (thisWeekEvents.isEmpty()) {
            // Afficher un message s'il n'y a pas d'événements cette semaine
            VBox emptyBox = new VBox();
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPrefWidth(thisWeekEventsContainer.getPrefWidth());
            emptyBox.setPrefHeight(200);
            emptyBox.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 0 0 10 10;");
            
            Label emptyLabel = new Label("Aucun événement cette semaine");
            emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #95a5a6; -fx-font-style: italic;");
            
            emptyBox.getChildren().add(emptyLabel);
            thisWeekEventsContainer.getChildren().add(emptyBox);
        } else {
            // Ajouter les cartes d'événements
            for (Evenement event : thisWeekEvents) {
                VBox eventCard = createEventCard(event);
                thisWeekEventsContainer.getChildren().add(eventCard);
            }
        }
    }
    
    private void populateUpcomingEventsContainer(List<Evenement> events) {
        if (upcomingEventsContainer == null) {
            System.out.println("upcomingEventsContainer est null, impossible d'afficher les événements à venir");
            return;
        }
        
        upcomingEventsContainer.getChildren().clear();
        
        // Ajouter un titre de section
        VBox headerBox = new VBox(5);
        headerBox.setPrefWidth(upcomingEventsContainer.getPrefWidth());
        headerBox.setStyle("-fx-padding: 10 0 20 10; -fx-background-color: #f0f8ff; -fx-background-radius: 10 10 0 0;");
        
        Label sectionTitle = new Label("À venir");
        sectionTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Label sectionSubtitle = new Label("Les événements à ne pas manquer dans les prochaines semaines");
        sectionSubtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
        
        headerBox.getChildren().addAll(sectionTitle, sectionSubtitle);
        upcomingEventsContainer.getChildren().add(headerBox);
        
        // Filtrer les événements à venir (après la semaine en cours)
        LocalDateTime startOfNextWeek = LocalDateTime.now().plusDays(7);
        
        List<Evenement> upcomingEvents = new ArrayList<>();
        for (Evenement event : events) {
            if (event.getDateD() != null && event.getDateD().isAfter(startOfNextWeek)) {
                upcomingEvents.add(event);
            }
        }
        
        if (upcomingEvents.isEmpty()) {
            // Afficher un message s'il n'y a pas d'événements à venir
            VBox emptyBox = new VBox();
            emptyBox.setAlignment(Pos.CENTER);
            emptyBox.setPrefWidth(upcomingEventsContainer.getPrefWidth());
            emptyBox.setPrefHeight(200);
            emptyBox.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 0 0 10 10;");
            
            Label emptyLabel = new Label("Aucun événement à venir");
            emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #95a5a6; -fx-font-style: italic;");
            
            emptyBox.getChildren().add(emptyLabel);
            upcomingEventsContainer.getChildren().add(emptyBox);
        } else {
            // Ajouter les cartes d'événements
            for (Evenement event : upcomingEvents) {
                VBox eventCard = createEventCard(event);
                upcomingEventsContainer.getChildren().add(eventCard);
            }
        }
    }
    
    // Créer une carte d'événement avec des émojis au lieu d'images
    private VBox createEventCard(Evenement event) {
        try {
            System.out.println("Création de la carte pour l'événement: " + event.getTitre());
            
            // Créer la carte d'événement avec de nouveaux styles modernes et encadrement
            VBox card = new VBox(10);
            card.getStyleClass().add("card");
            card.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 10); "
                         + "-fx-background-radius: 10; -fx-padding: 15; -fx-pref-width: 260; -fx-pref-height: 320; "
                         + "-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 10;");
            
            // Ajouter un effet de survol avec bordure colorée
            card.setOnMouseEntered(e -> {
                card.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 15, 0, 0, 15); "
                             + "-fx-background-radius: 10; -fx-padding: 15; -fx-pref-width: 260; -fx-pref-height: 320; "
                             + "-fx-border-color: #3498db; -fx-border-width: 2; -fx-border-radius: 10;");
            });
            
            card.setOnMouseExited(e -> {
                card.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 10); "
                             + "-fx-background-radius: 10; -fx-padding: 15; -fx-pref-width: 260; -fx-pref-height: 320; "
                             + "-fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 10;");
            });
            
            // Image de l'événement avec des coins plus arrondis
            ImageView imageView = new ImageView();
            imageView.setFitWidth(230);
            imageView.setFitHeight(130);
            
            try {
                String imagePath = event.getImage();
                if (imagePath != null && !imagePath.isEmpty()) {
                    // Essayer d'abord comme chemin absolu
                    File file = new File(imagePath);
                    if (file.exists()) {
                        Image image = new Image(file.toURI().toString());
                        imageView.setImage(image);
                    } else {
                        // Essayer dans le répertoire d'images de XAMPP
                        String uploadDir = "C:\\xampp\\htdocs\\imageP\\";
                        File uploadedImage = new File(uploadDir + imagePath);
                        System.out.println("Tentative de chargement de l'image: " + uploadedImage.getAbsolutePath());
                        
                        if (uploadedImage.exists()) {
                            Image image = new Image(uploadedImage.toURI().toString());
                            imageView.setImage(image);
                            System.out.println("✅ Image chargée avec succès depuis le répertoire XAMPP");
                        } else {
                            // Couleur rose par défaut si le fichier n'existe pas
                            System.out.println("❌ Image non trouvée: " + uploadedImage.getAbsolutePath());
                            createDefaultPinkBackground(imageView);
                        }
                    }
                } else {
                    // Couleur rose par défaut si pas de chemin spécifié
                    createDefaultPinkBackground(imageView);
                }
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement de l'image: " + e.getMessage());
                // Utiliser un fond rose en cas d'erreur
                createDefaultPinkBackground(imageView);
            }
            
            // Coin arrondis pour l'image
            Rectangle clip = new Rectangle(imageView.getFitWidth(), imageView.getFitHeight());
            clip.setArcWidth(15);
            clip.setArcHeight(15);
            imageView.setClip(clip);
            imageView.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 5, 0, 0, 0);");
            
            // Titre avec style moderne
            Label titleLabel = new Label(event.getTitre());
            titleLabel.getStyleClass().add("card-title");
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
            titleLabel.setWrapText(true);
            
            // Badge pour le type d'événement
            Label typeLabel = new Label(event.getType());
            typeLabel.getStyleClass().addAll("badge", "badge-primary");
            typeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: white; -fx-background-color: #3498db; -fx-padding: 3 8; -fx-background-radius: 10;");
            
            // Système d'étoiles (rating)
            int nbEtoiles = calculerEtoiles(event);
            Label ratingLabel = new Label(genererChaineEtoiles(nbEtoiles));
            ratingLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #f39c12;"); // Couleur jaune-orange pour les étoiles
            
            // Date avec icône améliorée et colorée
            HBox dateBox = new HBox(5);
            dateBox.setAlignment(Pos.CENTER_LEFT);
            Label dateIcon = new Label("📅");
            dateIcon.setStyle("-fx-font-size: 14px; -fx-text-fill: #3498db;"); // Couleur bleue pour l'icône calendrier
            Label dateLabel = new Label(event.getDateD() != null ? event.getDateD().format(formatter) : "Date non définie");
            dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
            dateBox.getChildren().addAll(dateIcon, dateLabel);
            
            // Lieu avec icône améliorée et colorée
            HBox locationBox = new HBox(5);
            locationBox.setAlignment(Pos.CENTER_LEFT);
            Label locationIcon = new Label("📍");
            locationIcon.setStyle("-fx-font-size: 14px; -fx-text-fill: #e74c3c;"); // Couleur rouge pour l'icône localisation
            Label locationLabel = new Label(event.getLocation());
            locationLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
            locationBox.getChildren().addAll(locationIcon, locationLabel);
            
            // Places disponibles avec style conditionnelle et icône colorée
            HBox capacityBox = new HBox(5);
            capacityBox.setAlignment(Pos.CENTER_LEFT);
            Label capacityIcon = new Label("👥");
            capacityIcon.setStyle("-fx-font-size: 14px; -fx-text-fill: #9b59b6;"); // Couleur violette pour l'icône participants
            
            Label capacityLabel = new Label("Places: " + event.getNbPlace());
            
            // Couleur du texte selon disponibilité
            if (event.getNbPlace() <= 0) {
                capacityLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #e74c3c; -fx-font-weight: bold;"); // Rouge
            } else if (event.getNbPlace() < 5) {
                capacityLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #f39c12; -fx-font-weight: bold;"); // Orange
            } else {
                capacityLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #2ecc71; -fx-font-weight: bold;"); // Vert
            }
            
            capacityBox.getChildren().addAll(capacityIcon, capacityLabel);
            
            // Description courte avec style amélioré
            Label descriptionLabel = new Label();
            String shortDesc = event.getDescription();
            if (shortDesc != null && shortDesc.length() > 100) {
                shortDesc = shortDesc.substring(0, 97) + "...";
            }
            descriptionLabel.setText(shortDesc != null ? shortDesc : "Aucune description");
            descriptionLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d; -fx-padding: 5 0;");
            descriptionLabel.setWrapText(true);
            
            // Boutons d'action avec classes de style moderne
            HBox actionButtons = new HBox(10);
            actionButtons.setAlignment(Pos.CENTER);
            actionButtons.setPadding(new Insets(10, 0, 0, 0));
            
            Button detailsButton = new Button("Détails");
            detailsButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 3, 0, 0, 1); -fx-padding: 8 15;");
            detailsButton.setOnAction(e -> showEventDetails(event));
            
            Button sessionsButton = new Button("Sessions");
            sessionsButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 3, 0, 0, 1); -fx-padding: 8 15;");
            sessionsButton.setOnAction(e -> showSessionsModal(event));
            
            Button reserveButton = new Button("Réserver");
            reserveButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 3, 0, 0, 1); -fx-padding: 8 15;");
            reserveButton.setOnAction(e -> showReservationDialog(event));
            
            // Désactiver le bouton réserver si l'événement est complet
            if (event.getNbPlace() <= 0) {
                reserveButton.setDisable(true);
                reserveButton.setText("Complet");
                reserveButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 8 15;");
            }
            
            // Ajouter des effets de survol
            detailsButton.setOnMouseEntered(e -> detailsButton.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2); -fx-padding: 8 15; -fx-translate-y: -1;"));
            detailsButton.setOnMouseExited(e -> detailsButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 3, 0, 0, 1); -fx-padding: 8 15;"));
            
            sessionsButton.setOnMouseEntered(e -> sessionsButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2); -fx-padding: 8 15; -fx-translate-y: -1;"));
            sessionsButton.setOnMouseExited(e -> sessionsButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 3, 0, 0, 1); -fx-padding: 8 15;"));
            
            if (event.getNbPlace() > 0) {
                reserveButton.setOnMouseEntered(e -> reserveButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 5, 0, 0, 2); -fx-padding: 8 15; -fx-translate-y: -1;"));
                reserveButton.setOnMouseExited(e -> reserveButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 3, 0, 0, 1); -fx-padding: 8 15;"));
            }
            
            // Ajouter effets de clic
            AnimationUtils.addClickEffect(detailsButton);
            AnimationUtils.addClickEffect(sessionsButton);
            AnimationUtils.addClickEffect(reserveButton);
            
            actionButtons.getChildren().addAll(detailsButton, sessionsButton, reserveButton);
            
            // Ajouter tous les éléments à la carte
            card.getChildren().addAll(
                imageView,
                titleLabel,
                typeLabel,
                ratingLabel, // Ajout de la note avec étoiles
                dateBox,
                locationBox,
                capacityBox,
                descriptionLabel,
                actionButtons
            );
            
            System.out.println("Carte créée avec succès pour: " + event.getTitre());
            return card;
        } catch (Exception e) {
            System.err.println("Erreur lors de la création de la carte d'événement: " + e.getMessage());
            e.printStackTrace();
            
            // Créer une carte d'erreur avec style
            VBox errorCard = new VBox(10);
            errorCard.getStyleClass().addAll("card", "border-danger");
            errorCard.setStyle("-fx-background-color: #ffeeee; -fx-padding: 15; -fx-border-color: #e74c3c; -fx-border-width: 1; -fx-border-radius: 5;");
            
            Label errorIcon = new Label("⚠️");
            errorIcon.setStyle("-fx-font-size: 24px; -fx-text-fill: #e74c3c;");
            
            Label errorLabel = new Label("Erreur: Impossible d'afficher l'événement");
            errorLabel.getStyleClass().add("text-danger");
            
            errorCard.getChildren().addAll(errorIcon, errorLabel);
            errorCard.setAlignment(Pos.CENTER);
            
            return errorCard;
        }
    }
    
    /**
     * Crée un fond rose par défaut pour une ImageView quand aucune image n'est disponible
     * @param imageView L'ImageView à modifier
     */
    private void createDefaultPinkBackground(ImageView imageView) {
        // Créer un canvas de la taille de l'ImageView
        javafx.scene.canvas.Canvas canvas = new javafx.scene.canvas.Canvas(
            imageView.getFitWidth(), imageView.getFitHeight());
        
        // Obtenir le contexte graphique
        javafx.scene.canvas.GraphicsContext gc = canvas.getGraphicsContext2D();
        
        // Remplir avec un dégradé rose
        javafx.scene.paint.LinearGradient gradient = new javafx.scene.paint.LinearGradient(
            0, 0, 1, 1, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
            new javafx.scene.paint.Stop(0, Color.rgb(255, 182, 193, 1.0)),  // Rose clair
            new javafx.scene.paint.Stop(1, Color.rgb(219, 112, 147, 1.0))   // Rose foncé
        );
        
        gc.setFill(gradient);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        
        // Ajouter un texte centré
        gc.setFill(Color.WHITE);
        gc.setTextAlign(TextAlignment.CENTER);
        // Au lieu d'utiliser VPos.CENTER, centrer manuellement le texte verticalement
        double textHeight = 16; // Hauteur approximative du texte (taille de police)
        double yPosition = canvas.getHeight() / 2 + textHeight / 4; // Position Y ajustée pour centrer verticalement
        gc.setFont(new Font("Arial", 16));
        gc.fillText("Pas d'image disponible", canvas.getWidth() / 2, yPosition);
        
        // Convertir le canvas en image
        javafx.scene.image.WritableImage writableImage = 
            new javafx.scene.image.WritableImage((int)canvas.getWidth(), (int)canvas.getHeight());
        canvas.snapshot(null, writableImage);
        
        // Définir l'image sur l'ImageView
        imageView.setImage(writableImage);
    }
    
    private void filterEvents() {
        try {
            System.out.println("Filtrage des événements...");
            
            // Récupérer les valeurs de filtre
            String searchText = searchField != null ? searchField.getText().toLowerCase() : "";
            String category = filterTypeComboBox != null ? filterTypeComboBox.getValue() : "Tous";
            String dateFilter = filterDate != null ? filterDate.getValue() : "Tous";
            
            // Vider les conteneurs
            if (eventsContainer != null) {
            eventsContainer.getChildren().clear();
            }
            
            if (thisWeekEventsContainer != null) {
                thisWeekEventsContainer.getChildren().clear();
            }
            
            if (upcomingEventsContainer != null) {
                upcomingEventsContainer.getChildren().clear();
            }
            
            // Obtenir tous les événements
            List<Evenement> allEvents = evenementService.getAllEvents();
            
            // Date actuelle
            LocalDate now = LocalDate.now();
            LocalDate nextWeek = now.plusDays(7);
            LocalDate nextMonth = now.plusMonths(1);
            
            // Filtrer et ajouter les événements
            for (Evenement event : allEvents) {
                boolean matchesSearch = searchText.isEmpty() ||
                    event.getTitre().toLowerCase().contains(searchText) ||
                    event.getDescription().toLowerCase().contains(searchText) ||
                    event.getType().toLowerCase().contains(searchText) ||
                    event.getLocation().toLowerCase().contains(searchText);
                
                boolean matchesCategory = "Tous".equals(category) || event.getType().equals(category);
                
                // Filtrage par date
                LocalDate eventDate = event.getDateD().toLocalDate();
                boolean matchesDate = true;
                
                if ("Aujourd'hui".equals(dateFilter)) {
                    matchesDate = eventDate.isEqual(now);
                } else if ("Cette semaine".equals(dateFilter)) {
                    matchesDate = eventDate.isBefore(nextWeek) && eventDate.isAfter(now.minusDays(1));
                } else if ("Ce mois-ci".equals(dateFilter)) {
                    matchesDate = eventDate.isBefore(nextMonth) && eventDate.isAfter(now.minusDays(1));
                }
                
                if (matchesSearch && matchesCategory && matchesDate) {
                    VBox eventCard = createEventCard(event);
                    
                    // Ajouter au conteneur principal
                    if (eventsContainer != null) {
                    eventsContainer.getChildren().add(eventCard);
                }
                    
                    // Classer l'événement selon sa date (pour les filtres "Tous" uniquement)
                    if ("Tous".equals(dateFilter)) {
                        if (eventDate.isBefore(nextWeek) && eventDate.isAfter(now.minusDays(1))) {
                            // Cette semaine
                            if (thisWeekEventsContainer != null) {
                                thisWeekEventsContainer.getChildren().add(createEventCard(event));
                            }
                        } else if (eventDate.isAfter(now)) {
                            // À venir
                            if (upcomingEventsContainer != null) {
                                upcomingEventsContainer.getChildren().add(createEventCard(event));
                            }
                        }
                    }
                }
            }
            
            System.out.println("Filtrage terminé");
        } catch (Exception e) {
            System.err.println("Erreur lors du filtrage des événements: " + e.getMessage());
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Erreur", "Erreur de filtrage",
                     "Une erreur est survenue lors du filtrage des événements: " + e.getMessage());
        }
    }
    
    private void showSessionsModal(Evenement event) {
        try {
            System.out.println("Affichage des sessions pour l'événement " + event.getId());
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FrontSessions.fxml"));
            Parent root = loader.load();
            
            FrontSessionsController controller = loader.getController();
            controller.setEvenement(event);
            
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Sessions de l'événement : " + event.getTitre());
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            System.err.println("Erreur lors de l'affichage des sessions: " + e.getMessage());
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Erreur", "Erreur d'affichage",
                     "Impossible d'afficher les sessions de l'événement : " + e.getMessage());
        }
    }
    
    private void showReservationDialog(Evenement event) {
        try {
            System.out.println("Affichage du dialogue de réservation pour l'événement " + event.getId());

            // Vérifier si l'événement est complet
            if (event.getNbPlace() <= 0) {
                showAlert(AlertType.INFORMATION,
                         "Événement complet", 
                         "Cet événement est complet", 
                         "Il n'y a plus de places disponibles pour cet événement.");
                return;
            }
            
            // Créer un dialogue personnalisé avec style amélioré
            Dialog<Integer> dialog = new Dialog<>();
            dialog.setTitle("Réserver des places");
            dialog.setHeaderText("Réservation pour : " + event.getTitre());
            
            // Style du dialogue
            DialogPane dialogPane = dialog.getDialogPane();
            dialogPane.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 15, 0, 0, 5);");
            dialogPane.setPrefWidth(550);
            
            // Configurer les boutons avec style
            ButtonType reserveButtonType = new ButtonType("Réserver", ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(reserveButtonType, ButtonType.CANCEL);
            
            // Créer le contenu du dialogue
            BorderPane mainContent = new BorderPane();
            mainContent.setPadding(new Insets(20));
            
            // Partie gauche: informations sur l'événement
            VBox eventInfoBox = new VBox(15);
            eventInfoBox.setPrefWidth(250);
            eventInfoBox.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);");
            
            // Image de l'événement avec animation de chargement
            StackPane imageContainer = new StackPane();
            ImageView eventImage = new ImageView();
            eventImage.setFitWidth(220);
            eventImage.setFitHeight(130);
            eventImage.setPreserveRatio(true);
            
            // Indicateur de chargement
            ProgressIndicator loadingIndicator = new ProgressIndicator();
            loadingIndicator.setPrefSize(50, 50);
            loadingIndicator.setVisible(true);
            
            imageContainer.getChildren().addAll(loadingIndicator, eventImage);
            
            // Label pour afficher le prix total - défini ici pour être accessible dans toute la méthode
            Label totalPriceLabel = new Label();
            totalPriceLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            
            // Charger l'image de manière asynchrone
            new Thread(() -> {
                Image img = null;
                try {
                    String imagePath = event.getImage();
                    if (imagePath != null && !imagePath.isEmpty()) {
                        // Essayer d'abord comme chemin absolu
                        File imageFile = new File(imagePath);
                        if (imageFile.exists()) {
                            img = new Image(imageFile.toURI().toString(), true);
                        } else {
                            // Essayer dans le répertoire d'images XAMPP
                            String uploadDir = "C:\\xampp\\htdocs\\imageP\\";
                            File uploadedImage = new File(uploadDir + imagePath);
                            System.out.println("Tentative de chargement de l'image de réservation: " + uploadedImage.getAbsolutePath());
                            
                            if (uploadedImage.exists()) {
                                img = new Image(uploadedImage.toURI().toString(), true);
                                System.out.println("✅ Image de réservation chargée avec succès depuis le répertoire XAMPP");
                            } else {
                                // Image par défaut
                                System.out.println("❌ Image de réservation non trouvée: " + uploadedImage.getAbsolutePath());
                                URL defaultImageUrl = getClass().getResource("/images/default-session.png");
                                if (defaultImageUrl != null) {
                                    img = new Image(defaultImageUrl.toExternalForm(), true);
                                }
                            }
                        }
                    } else {
                        // Pas d'image spécifiée, essayer d'utiliser une image par défaut
                        URL defaultImageUrl = getClass().getResource("/images/default-session.png");
                        if (defaultImageUrl != null) {
                            img = new Image(defaultImageUrl.toExternalForm(), true);
                        }
                    }
                    
                    // Image finale à afficher
                    final Image finalImage = img;
                    if (finalImage != null) {
                        javafx.application.Platform.runLater(() -> {
                            eventImage.setImage(finalImage);
                            loadingIndicator.setVisible(false);
                            
                            // Animation d'apparition de l'image
                            FadeTransition fadeIn = new FadeTransition(Duration.millis(500), eventImage);
                            fadeIn.setFromValue(0.0);
                            fadeIn.setToValue(1.0);
                            fadeIn.play();
                        });
                    } else {
                        javafx.application.Platform.runLater(() -> {
                            loadingIndicator.setVisible(false);
                            // Afficher une icône ou un message à la place de l'image
                            Label noImageLabel = new Label("🖼️ Image non disponible");
                            noImageLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d;");
                            imageContainer.getChildren().add(noImageLabel);
                        });
                    }
                } catch (Exception e) {
                    javafx.application.Platform.runLater(() -> {
                        loadingIndicator.setVisible(false);
                        System.err.println("Erreur de chargement d'image: " + e.getMessage());
                    });
                }
            }).start();
            
            // Clip pour arrondir les coins de l'image
            Rectangle clip = new Rectangle(eventImage.getFitWidth(), eventImage.getFitHeight());
            clip.setArcWidth(20);
            clip.setArcHeight(20);
            eventImage.setClip(clip);
            eventImage.setOpacity(0); // Commence invisible pour l'animation
            eventImage.setStyle("-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);");
            
            // Titre de l'événement
            Label eventTitleLabel = new Label(event.getTitre());
            eventTitleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
            eventTitleLabel.setWrapText(true);
            
            // Dates et lieu
            VBox detailsBox = new VBox(8);
            detailsBox.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-padding: 10; -fx-border-color: #e9ecef; -fx-border-radius: 8; -fx-border-width: 1;");
            
            // Date
            HBox dateBox = new HBox(10);
            dateBox.setAlignment(Pos.CENTER_LEFT);
            Label dateIcon = new Label("📅");
            dateIcon.setStyle("-fx-font-size: 16px;");
            
            String dateStr = "Date non spécifiée";
            if (event.getDateD() != null) {
                if (event.getDateF() != null && !event.getDateD().equals(event.getDateF())) {
                    dateStr = "Du " + event.getDateD().format(formatter) + " au " + event.getDateF().format(formatter);
                    } else {
                    dateStr = event.getDateD().format(formatter);
                }
            }
            
            Label dateLabel = new Label(dateStr);
            dateLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #34495e;");
            dateLabel.setWrapText(true);
            dateBox.getChildren().addAll(dateIcon, dateLabel);
            
            // Lieu
            HBox locationBox = new HBox(10);
            locationBox.setAlignment(Pos.CENTER_LEFT);
            Label locationIcon = new Label("📍");
            locationIcon.setStyle("-fx-font-size: 16px;");
            Label locationLabel = new Label(event.getLocation());
            locationLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #34495e;");
            locationBox.getChildren().addAll(locationIcon, locationLabel);
            
            // Prix
            HBox priceBox = new HBox(10);
            priceBox.setAlignment(Pos.CENTER_LEFT);
            Label priceIcon = new Label("💲");
            priceIcon.setStyle("-fx-font-size: 16px;");
            Label priceLabel = new Label(String.format("%.2f €", event.getPrix()));
            priceLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #34495e; -fx-font-weight: bold;");
            priceBox.getChildren().addAll(priceIcon, priceLabel);
            
            detailsBox.getChildren().addAll(dateBox, locationBox, priceBox);
            
            // Badge de disponibilité
            Label availabilityLabel = new Label(event.getNbPlace() + " places disponibles");
            availabilityLabel.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 15; -fx-background-radius: 20;");
            
            // Assembler la partie info événement
            eventInfoBox.getChildren().addAll(imageContainer, eventTitleLabel, detailsBox, availabilityLabel);
            
            // Partie droite: formulaire de réservation
            VBox formBox = new VBox(15);
            formBox.setPrefWidth(250);
            formBox.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-border-color: #e9ecef; -fx-border-radius: 10; -fx-border-width: 1;");
            
            // Titre du formulaire
            Label formTitle = new Label("Informations de réservation");
            formTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #34495e;");
            
            // Places à réserver
            Label placesLabel = new Label("Nombre de places :");
            placesLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e;");
            
            // Spinner stylisé
            HBox spinnerBox = new HBox(10);
            spinnerBox.setAlignment(Pos.CENTER);
            
            Button decreaseButton = new Button("-");
            decreaseButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 35px; -fx-min-height: 35px; -fx-background-radius: 5;");
            
            Spinner<Integer> placesSpinner = new Spinner<>(1, event.getNbPlace(), 1);
            placesSpinner.setEditable(true);
            placesSpinner.setPrefWidth(70);
            
            Button increaseButton = new Button("+");
            increaseButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 35px; -fx-min-height: 35px; -fx-background-radius: 5;");
            
            // Ajout des écouteurs d'événements avec effets visuels améliorés
            decreaseButton.setOnAction(e -> {
                int currentValue = placesSpinner.getValue();
                if (currentValue > 1) {
                    placesSpinner.getValueFactory().setValue(currentValue - 1);
                    updateTotalPriceUI(currentValue - 1, totalPriceLabel, event.getPrix());
                }
            });
            
            // Effet de survol sur le bouton -
            decreaseButton.setOnMouseEntered(e -> {
                decreaseButton.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 35px; -fx-min-height: 35px; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 3, 0, 0, 1);");
            });
            
            decreaseButton.setOnMouseExited(e -> {
                decreaseButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 35px; -fx-min-height: 35px; -fx-background-radius: 5;");
            });
            
            increaseButton.setOnAction(e -> {
                int currentValue = placesSpinner.getValue();
                if (currentValue < event.getNbPlace()) {
                    placesSpinner.getValueFactory().setValue(currentValue + 1);
                    updateTotalPriceUI(currentValue + 1, totalPriceLabel, event.getPrix());
                }
            });
            
            // Effet de survol sur le bouton +
            increaseButton.setOnMouseEntered(e -> {
                increaseButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 35px; -fx-min-height: 35px; -fx-background-radius: 5; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 3, 0, 0, 1);");
            });
            
            increaseButton.setOnMouseExited(e -> {
                increaseButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 35px; -fx-min-height: 35px; -fx-background-radius: 5;");
            });
            
            // Ajouter un listener pour mettre à jour le prix total quand le spinner change
            placesSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
                updateTotalPriceUI(newVal, totalPriceLabel, event.getPrix());
            });
            
            spinnerBox.getChildren().addAll(decreaseButton, placesSpinner, increaseButton);
            
            // Section prix total
            Label totalPriceCaption = new Label("Prix total :");
            totalPriceCaption.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e; -fx-font-weight: bold;");
            
            // Initialiser le prix total
            updateTotalPriceUI(1, totalPriceLabel, event.getPrix());
            
            // Assemblage de la partie formulaire
            formBox.getChildren().addAll(formTitle, placesLabel, spinnerBox, totalPriceCaption, totalPriceLabel);
            
            // Disposition horizontale des deux parties
            HBox contentLayout = new HBox(20, eventInfoBox, formBox);
            contentLayout.setAlignment(Pos.CENTER);
            
            mainContent.setCenter(contentLayout);
            
            // Définir le contenu du dialogue
            dialog.getDialogPane().setContent(mainContent);
            
            // Styler les boutons du dialogue
            Button reserveButton = (Button) dialog.getDialogPane().lookupButton(reserveButtonType);
            if (reserveButton != null) {
                reserveButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 8 15;");
                
                // Ajouter des effets de survol
                reserveButton.setOnMouseEntered(e -> {
                    reserveButton.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 8 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 3, 0, 0, 1); -fx-translate-y: -1;");
                });
                
                reserveButton.setOnMouseExited(e -> {
                    reserveButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 8 15;");
                });
            }
            
            Button cancelButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
            if (cancelButton != null) {
                cancelButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 8 15;");
                
                // Ajouter des effets de survol
                cancelButton.setOnMouseEntered(e -> {
                    cancelButton.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 8 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 3, 0, 0, 1); -fx-translate-y: -1;");
                });
                
                cancelButton.setOnMouseExited(e -> {
                    cancelButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-background-radius: 5; -fx-padding: 8 15;");
                });
            }
            
            // Convertir le résultat
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == reserveButtonType) {
                    return placesSpinner.getValue();
                }
                return null;
            });
            
            // Appliquer des styles additionnels
            MainStyleFixer.styleProfessionalDialog(dialog.getDialogPane());
            
            // Afficher le dialogue et traiter le résultat
            Optional<Integer> result = dialog.showAndWait();
            result.ifPresent(places -> {
                try {
                    if (places > 0 && places <= event.getNbPlace()) {
                        // Afficher un indicateur de chargement pendant le traitement
                        ProgressDialog progressDialog = new ProgressDialog();
                        progressDialog.setTitle("Traitement en cours");
                        progressDialog.setHeaderText("Réservation en cours...");
                        progressDialog.show();
                        
                        // Effectuer la réservation de manière asynchrone
                        new Thread(() -> {
                            try {
                                // Attendre un peu pour montrer le dialogue de progression (simuler un traitement)
                                Thread.sleep(800);
                                
                                // Effectuer la réservation
                                boolean success = evenementService.reserverPlace(event.getId(), places);
                                
                                // Fermer le dialogue de progression
                                javafx.application.Platform.runLater(() -> {
                                    progressDialog.close();
                                    
                                    if (success) {
                                        // Mettre à jour l'affichage
                                        event.setNbPlace(event.getNbPlace() - places);
                                        
                                        // Rafraîchir l'affichage
                                        filterEvents();
                                        
                                        // Confirmation colorée et détaillée
                                        showSuccessReservationDialog(event, places);
                                    } else {
                                        showAlert(AlertType.ERROR,
                                                "Erreur de réservation", 
                                                "La réservation a échoué", 
                                                "Impossible de réserver des places pour cet événement. Veuillez réessayer.");
                                    }
                                });
                            } catch (Exception ex) {
                                javafx.application.Platform.runLater(() -> {
                                    progressDialog.close();
                                    showAlert(AlertType.ERROR,
                                            "Erreur", 
                                            "Erreur lors de la réservation", 
                                            "Une erreur est survenue : " + ex.getMessage());
                                });
                            }
                        }).start();
                    }
                } catch (Exception e) {
                    System.err.println("Erreur lors de la réservation: " + e.getMessage());
                    e.printStackTrace();
                    showAlert(AlertType.ERROR,
                            "Erreur", 
                            "Erreur lors de la réservation", 
                            "Une erreur est survenue : " + e.getMessage());
                }
            });
        } catch (Exception e) {
            System.err.println("Erreur lors de l'affichage du dialogue de réservation: " + e.getMessage());
            e.printStackTrace();
            showAlert(AlertType.ERROR,
                    "Erreur", 
                    "Erreur lors de la réservation", 
                    "Une erreur est survenue : " + e.getMessage());
        }
    }
    
    /**
     * Met à jour l'affichage du prix total dans l'interface
     */
    private void updateTotalPriceUI(int quantity, Label totalLabel, double prixUnitaire) {
        double total = quantity * prixUnitaire;
        totalLabel.setText(String.format("%.2f €", total));
        
        // Animation de changement de prix
        ScaleTransition st = new ScaleTransition(Duration.millis(200), totalLabel);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(1.2);
        st.setToY(1.2);
        st.setCycleCount(2);
        st.setAutoReverse(true);
        st.play();
    }
    
    /**
     * Affiche un dialogue de confirmation stylisé après une réservation réussie
     */
    private void showSuccessReservationDialog(Evenement event, int places) {
        Dialog<Void> confirmationDialog = new Dialog<>();
        confirmationDialog.setTitle("Réservation confirmée");
        confirmationDialog.setHeaderText(null);
        
        // Contenu de confirmation
        VBox confirmContent = new VBox(15);
        confirmContent.setPadding(new Insets(20));
        confirmContent.setStyle("-fx-background-color: white;");
        
        // Icône de succès avec animation
        StackPane iconContainer = new StackPane();
        iconContainer.setMinHeight(100);
        iconContainer.setAlignment(Pos.CENTER);
        
        Circle successCircle = new Circle(40);
        successCircle.setFill(Color.valueOf("#2ecc71"));
        successCircle.setOpacity(0);
        
        Label checkmark = new Label("✓");
        checkmark.setStyle("-fx-font-size: 48px; -fx-text-fill: white; -fx-font-weight: bold;");
        checkmark.setOpacity(0);
        
        iconContainer.getChildren().addAll(successCircle, checkmark);
        
        // Message de succès
        Label successMessage = new Label("Réservation effectuée avec succès !");
        successMessage.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2ecc71;");
        
        // Détails
        VBox detailsContent = new VBox(8);
        detailsContent.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 5;");
        
        Label eventNameDetail = new Label("Événement : " + event.getTitre());
        eventNameDetail.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        Label placesDetail = new Label("Nombre de places : " + places);
        placesDetail.setStyle("-fx-font-size: 14px;");
        
        Label dateDetail = new Label("Date : " + event.getDateD().format(formatter));
        dateDetail.setStyle("-fx-font-size: 14px;");
        
        // Prix total
        Label prixDetail = new Label(String.format("Prix total : %.2f €", places * event.getPrix()));
        prixDetail.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        detailsContent.getChildren().addAll(eventNameDetail, placesDetail, dateDetail, prixDetail);
        
        // Info supplémentaire (optionnelle)
        Label infoLabel = new Label("Un e-mail de confirmation vous sera envoyé prochainement.");
        infoLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d; -fx-font-style: italic;");
        
        // Assembler le contenu
        confirmContent.getChildren().addAll(iconContainer, successMessage, detailsContent, infoLabel);
        
        // Configurer le dialogue
        confirmationDialog.getDialogPane().setContent(confirmContent);
        confirmationDialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        
        Button okButton = (Button) confirmationDialog.getDialogPane().lookupButton(ButtonType.OK);
        if (okButton != null) {
            okButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
        }
        
        // Lancer les animations quand le dialogue est affiché
        confirmationDialog.setOnShowing(e -> {
            // Animation du cercle
            FadeTransition fadeInCircle = new FadeTransition(Duration.millis(500), successCircle);
            fadeInCircle.setFromValue(0.0);
            fadeInCircle.setToValue(1.0);
            
            // Animation du checkmark
            FadeTransition fadeInCheck = new FadeTransition(Duration.millis(500), checkmark);
            fadeInCheck.setFromValue(0.0);
            fadeInCheck.setToValue(1.0);
            fadeInCheck.setDelay(Duration.millis(300));
            
            // Lancer les animations
            fadeInCircle.play();
            fadeInCheck.play();
        });
        
        confirmationDialog.showAndWait();
    }
    
    private void updateTotalPrice(int quantity, Label totalLabel, double prixUnitaire) {
        double total = quantity * prixUnitaire;
        totalLabel.setText(String.format("Total : %.2f €", total));
    }
    
    @FXML
    private void handleBack() {
        try {
            System.out.println("Retour à la page principale");
            
            // Créer un effet de transition pour la navigation
            if (btnBack.getScene() != null) {
                AnimationUtils.fadeOut(btnBack.getScene().getRoot(), 300);
            }
            
            // Planifier la navigation après la fin de l'animation
            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(Duration.millis(300));
            pause.setOnFinished(event -> {
                try {
                    // Retour à l'interface utilisateur
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface.fxml"));
                Parent root = loader.load();
                
                    // Animation d'entrée pour la nouvelle vue
                    AnimationUtils.fadeIn(root, 500);
                    
                    Scene scene = btnBack.getScene();
                    if (scene != null) {
                        scene.setRoot(root);
                        
                        // Appliquer le style professionnel à la nouvelle scène
                        MainStyleFixer.applyProfessionalStyle(scene);
                    } else {
                        System.err.println("La scène est null, impossible de naviguer");
                    }
        } catch (IOException e) {
                    System.err.println("Erreur lors de la navigation: " + e.getMessage());
                    e.printStackTrace();
            showAlert(AlertType.ERROR, "Erreur", "Erreur de navigation",
                     "Impossible de retourner à la page principale : " + e.getMessage());
                }
            });
            pause.play();
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la navigation: " + e.getMessage());
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Erreur", "Erreur de navigation",
                    "Impossible de retourner à la page principale : " + e.getMessage());
        }
    }
    
    private void showAlert(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        
        // Appliquer le style professionnel à l'alerte
        MainStyleFixer.styleProfessionalDialog(alert.getDialogPane());
        
        alert.showAndWait();
    }

    private void showWeatherDialog(String location) {
        try {
            System.out.println("Affichage des informations météo pour " + location);
            
            // Vérifier que la localisation n'est pas vide
            if (location == null || location.trim().isEmpty()) {
                showAlert(AlertType.WARNING,
                         "Emplacement non défini", 
                         "Impossible d'afficher la météo", 
                         "Aucun emplacement n'est défini pour cet événement.");
                return;
            }
            
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Météo pour " + location);
            dialog.setHeaderText("Informations météorologiques");
                
            // Augmenter la taille de la boîte de dialogue
            DialogPane dialogPane = dialog.getDialogPane();
            dialogPane.setPrefWidth(700);
            dialogPane.setPrefHeight(500);
            dialogPane.setStyle("-fx-background-color: white;");
        
            VBox content = new VBox(15);
            content.setPadding(new Insets(20));
            content.setStyle("-fx-background-color: linear-gradient(to bottom, #87CEFA, #1E90FF);");
        
            Label loadingLabel = new Label("Chargement des données météo...");
            loadingLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18px;");
            ProgressIndicator progressIndicator = new ProgressIndicator();
            progressIndicator.setStyle("-fx-progress-color: white;");
            progressIndicator.setPrefSize(80, 80);
                
            VBox loadingBox = new VBox(20, loadingLabel, progressIndicator);
            loadingBox.setAlignment(Pos.CENTER);
            loadingBox.setPrefHeight(300);
                
            content.getChildren().add(loadingBox);
        
            dialog.getDialogPane().setContent(content);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
                
            // Appliquer le style professionnel
            MainStyleFixer.styleProfessionalDialog(dialog.getDialogPane());
                
            // Afficher le dialogue immédiatement
            dialog.show();
        
            // Charger les données météo réelles via l'API OpenWeatherMap
            new Thread(() -> {
                try {
                    // Clé API valide d'OpenWeatherMap
                    String apiKey = "8d4fa32d77dab0bbaccba3ffa0135ef8";
                    String encodedLocation = java.net.URLEncoder.encode(location, "UTF-8");
                    String url = "https://api.openweathermap.org/data/2.5/weather?q=" + encodedLocation + "&units=metric&lang=fr&appid=" + apiKey;
                        
                    System.out.println("URL API météo: " + url);
                
                    java.net.HttpURLConnection connection = (java.net.HttpURLConnection) new URL(url).openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(15000); // Augmenter le délai à 15 secondes
                    connection.setReadTimeout(15000); // Augmenter le délai à 15 secondes
                
                    int responseCode = connection.getResponseCode();
                    System.out.println("Code de réponse API météo: " + responseCode);
                
                    if (responseCode == 200) {
                        java.io.BufferedReader reader = new java.io.BufferedReader(
                                new java.io.InputStreamReader(connection.getInputStream(), "UTF-8"));
                        StringBuilder response = new StringBuilder();
                        String line;
                        
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        reader.close();
                        
                        // Parser la réponse JSON
                        org.json.JSONObject jsonResponse = new org.json.JSONObject(response.toString());
                        System.out.println("Réponse JSON reçue: " + jsonResponse.toString().substring(0, Math.min(100, jsonResponse.toString().length())) + "...");
                        
                        // Extraire les données météo
                        double temperature = jsonResponse.getJSONObject("main").getDouble("temp");
                        int humidity = jsonResponse.getJSONObject("main").getInt("humidity");
                        double pressure = jsonResponse.getJSONObject("main").getDouble("pressure");
                        double windSpeed = jsonResponse.getJSONObject("wind").getDouble("speed");
                        String weatherDescription = jsonResponse.getJSONArray("weather").getJSONObject(0).getString("description");
                        String weatherIcon = jsonResponse.getJSONArray("weather").getJSONObject(0).getString("icon");
                        double feelsLike = jsonResponse.getJSONObject("main").getDouble("feels_like");
                        double minTemp = jsonResponse.getJSONObject("main").getDouble("temp_min");
                        double maxTemp = jsonResponse.getJSONObject("main").getDouble("temp_max");
                        long sunrise = jsonResponse.getJSONObject("sys").getLong("sunrise");
                        long sunset = jsonResponse.getJSONObject("sys").getLong("sunset");
                        
                        // Obtenir le nom de la ville à partir de la réponse
                        String cityName = jsonResponse.getString("name");
                        String country = jsonResponse.getJSONObject("sys").getString("country");
                        
                        // Mettre à jour l'interface utilisateur
                        javafx.application.Platform.runLater(() -> {
                            try {
                                content.getChildren().clear();
                                
                                // En-tête avec la ville et le pays
                                Label locationLabel = new Label(cityName + ", " + country);
                                locationLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 5, 0, 0, 1);");
                                
                                // Section principale: température et icône
                                HBox mainWeatherInfo = new HBox(30);
                                mainWeatherInfo.setAlignment(Pos.CENTER);
                                mainWeatherInfo.setPadding(new Insets(20));
                            
                                // Ajouter une icône météo
                                ImageView weatherIconView = new ImageView();
                                try {
                                    // Utiliser l'icône réelle d'OpenWeatherMap avec HTTPS pour éviter les problèmes de sécurité
                                    String iconUrl = "https://openweathermap.org/img/wn/" + weatherIcon + "@4x.png";
                                    System.out.println("URL icône météo: " + iconUrl);
                                    Image image = new Image(iconUrl, true); // true pour chargement en arrière-plan
                                    weatherIconView.setImage(image);
                                    weatherIconView.setFitWidth(150);
                                    weatherIconView.setFitHeight(150);
                                    mainWeatherInfo.getChildren().add(weatherIconView);
                                } catch (Exception e) {
                                    System.err.println("Erreur lors du chargement de l'icône météo: " + e.getMessage());
                                    e.printStackTrace();
                                    // Utiliser un texte à la place de l'icône en cas d'erreur
                                    Label iconPlaceholder = new Label("☁️");
                                    iconPlaceholder.setStyle("-fx-font-size: 80px;");
                                    mainWeatherInfo.getChildren().add(iconPlaceholder);
                                }
                                
                                // Zone de température principale
                                VBox tempBox = new VBox(10);
                                tempBox.setAlignment(Pos.CENTER);
                            
                                Label temperatureLabel = new Label(String.format("%.1f°C", temperature));
                                temperatureLabel.setStyle("-fx-font-size: 60px; -fx-font-weight: bold; -fx-text-fill: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 5, 0, 0, 1);");
                            
                                Label feelsLikeLabel = new Label("Ressenti: " + String.format("%.1f°C", feelsLike));
                                feelsLikeLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 3, 0, 0, 1);");
                            
                                Label conditionLabel = new Label(weatherDescription);
                                conditionLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 3, 0, 0, 1);");
                                
                                tempBox.getChildren().addAll(temperatureLabel, feelsLikeLabel, conditionLabel);
                                mainWeatherInfo.getChildren().add(tempBox);
                            
                                Separator separator = new Separator();
                                separator.setStyle("-fx-background-color: white; -fx-opacity: 0.7;");
                            
                                // Panneau pour les détails météo supplémentaires
                                GridPane weatherDetails = new GridPane();
                                weatherDetails.setHgap(50);
                                weatherDetails.setVgap(30);
                                weatherDetails.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-background-radius: 15; -fx-padding: 20;");
                                weatherDetails.setPadding(new Insets(20));
                                weatherDetails.setAlignment(Pos.CENTER);
                                
                                // Formatage de l'heure pour lever/coucher du soleil
                                java.util.Date sunriseDate = new java.util.Date(sunrise * 1000);
                                java.util.Date sunsetDate = new java.util.Date(sunset * 1000);
                                java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("HH:mm");
                                String sunriseTime = timeFormat.format(sunriseDate);
                                String sunsetTime = timeFormat.format(sunsetDate);
                                
                                // Créer tous les indicateurs météo
                                createWeatherDetailItem(weatherDetails, 0, 0, "Humidité", humidity + "%", "💧");
                                createWeatherDetailItem(weatherDetails, 1, 0, "Vent", String.format("%.1f km/h", windSpeed * 3.6), "💨");
                                createWeatherDetailItem(weatherDetails, 0, 1, "Pression", String.format("%.0f hPa", pressure), "🔄");
                                createWeatherDetailItem(weatherDetails, 1, 1, "Min/Max", String.format("%.1f°C / %.1f°C", minTemp, maxTemp), "🌡️");
                                createWeatherDetailItem(weatherDetails, 0, 2, "Lever du soleil", sunriseTime, "🌅");
                                createWeatherDetailItem(weatherDetails, 1, 2, "Coucher du soleil", sunsetTime, "🌇");
                                
                                // Assembler tous les éléments
                                content.getChildren().addAll(
                                    locationLabel,
                                    mainWeatherInfo,
                                    separator,
                                    weatherDetails
                                );
                            } catch (Exception ex) {
                                System.err.println("Erreur lors de la mise à jour de l'interface météo: " + ex.getMessage());
                                ex.printStackTrace();
                                content.getChildren().clear();
                                    
                                Label errorLabel = new Label("Erreur lors de l'affichage des données météo");
                                errorLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 20px;");
                                    
                                Label detailsLabel = new Label("Une erreur technique est survenue: " + ex.getMessage());
                                detailsLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
                                detailsLabel.setWrapText(true);
                                    
                                content.getChildren().addAll(errorLabel, detailsLabel);
                            }
                        });
                    } else {
                        System.err.println("Erreur API météo: code " + responseCode);
                        javafx.application.Platform.runLater(() -> {
                            content.getChildren().clear();
                            
                            Label errorLabel = new Label("Impossible de récupérer les données météo");
                            errorLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 20px;");
                            
                            Label detailsLabel = new Label("Erreur " + responseCode + " : Vérifiez le nom de la ville \"" + location + "\" ou votre connexion internet");
                            detailsLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
                            detailsLabel.setWrapText(true);
                            
                            // Ajouter un bouton pour réessayer
                            Button retryButton = new Button("Réessayer");
                            retryButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
                            retryButton.setOnAction(e -> {
                                dialog.close();
                                showWeatherDialog(location);
                            });
                            
                            VBox errorBox = new VBox(20, errorLabel, detailsLabel, retryButton);
                            errorBox.setAlignment(Pos.CENTER);
                            
                            content.getChildren().add(errorBox);
                        });
                    }
                } catch (Exception e) {
                    System.err.println("Erreur lors de la récupération des données météo: " + e.getMessage());
                    e.printStackTrace();
                    javafx.application.Platform.runLater(() -> {
                        content.getChildren().clear();
                        
                        Label errorLabel = new Label("Erreur de connexion");
                        errorLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 20px;");
                        
                        Label detailsLabel = new Label("Vérifiez votre connexion internet. Détail: " + e.getMessage());
                        detailsLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
                        detailsLabel.setWrapText(true);
                        
                        // Ajouter un bouton pour réessayer
                        Button retryButton = new Button("Réessayer");
                        retryButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20;");
                        retryButton.setOnAction(evt -> {
                            dialog.close();
                            showWeatherDialog(location);
                        });
                        
                        VBox errorBox = new VBox(20, errorLabel, detailsLabel, retryButton);
                        errorBox.setAlignment(Pos.CENTER);
                        
                        content.getChildren().add(errorBox);
                    });
                }
            }).start();
        
        } catch (Exception e) {
            System.err.println("Erreur lors de l'affichage du dialogue météo: " + e.getMessage());
            e.printStackTrace();
            showAlert(AlertType.ERROR,
                    "Erreur", 
                    "Erreur d'affichage", 
                    "Impossible d'afficher les informations météo: " + e.getMessage());
        }
    }
    
    // Méthode utilitaire pour créer un élément de détail météo
    private void createWeatherDetailItem(GridPane parent, int col, int row, String title, String value, String icon) {
        VBox item = new VBox(10);
        item.setAlignment(Pos.CENTER);
        
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 24px;");
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white;");
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        item.getChildren().addAll(iconLabel, titleLabel, valueLabel);
        parent.add(item, col, row);
    }
    
    private void showMapDialog(String location) {
        try {
            System.out.println("Affichage de la carte pour " + location);
            
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Localisation de " + location);
        dialog.setHeaderText("Localisation et Carte");
        dialog.getDialogPane().setPrefWidth(800);
        dialog.getDialogPane().setPrefHeight(650);
        
            // Appliquer le style professionnel
            MainStyleFixer.styleProfessionalDialog(dialog.getDialogPane());
        
        // Obtenir les coordonnées réelles du lieu
        LocationUtils.Coordinates coords = LocationUtils.getCoordinates(location);
            System.out.println("Coordonnées obtenues: " + coords);
        
        // Création d'un TabPane pour gérer les onglets
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        // Onglet 1: Informations de localisation
        Tab infoTab = new Tab("Informations");
        VBox infoContent = new VBox(15);
        infoContent.setPadding(new Insets(20));
        infoContent.setStyle("-fx-background-color: white;");
        
        // Titre
        Label titleLabel = new Label("Informations sur " + location);
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        // Panneau d'informations de localisation
        VBox locationInfoBox = new VBox(10);
        locationInfoBox.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 20; -fx-background-radius: 5; -fx-border-color: #e9ecef; -fx-border-radius: 5;");
        
        // En-tête avec icône de localisation
        HBox locationHeader = new HBox(10);
        locationHeader.setAlignment(Pos.CENTER_LEFT);
        
        Label locationIcon = new Label("📍");
        locationIcon.setStyle("-fx-font-size: 24px;");
        
        Label locationNameLabel = new Label(location);
        locationNameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
        
        locationHeader.getChildren().addAll(locationIcon, locationNameLabel);
        
        // Description du lieu
        Label locationDescLabel = new Label(coords.getDescription());
        locationDescLabel.setStyle("-fx-font-size: 14px;");
        locationDescLabel.setWrapText(true);
        
        // Coordonnées
        HBox coordBox = new HBox(10);
        coordBox.setAlignment(Pos.CENTER_LEFT);
        
        Label coordIcon = new Label("🌐");
        coordIcon.setStyle("-fx-font-size: 16px;");
        
        Label locationCoordLabel = new Label("Coordonnées: " + coords);
        locationCoordLabel.setStyle("-fx-font-size: 14px;");
        
        coordBox.getChildren().addAll(coordIcon, locationCoordLabel);
        
        // Adresse générée
        HBox addressBox = new HBox(10);
        addressBox.setAlignment(Pos.CENTER_LEFT);
        
        Label addressIcon = new Label("🏢");
        addressIcon.setStyle("-fx-font-size: 16px;");
        
        String address = LocationUtils.generateAddress(location);
        Label locationAddressLabel = new Label("Adresse: " + address);
        locationAddressLabel.setStyle("-fx-font-size: 14px;");
        locationAddressLabel.setWrapText(true);
        
        addressBox.getChildren().addAll(addressIcon, locationAddressLabel);
        
            // Créer le séparateur
        Separator separator = new Separator();
        separator.setPadding(new Insets(5, 0, 5, 0));
        
            // Informations de transport et parking
        GridPane detailsGrid = new GridPane();
        detailsGrid.setHgap(15);
        detailsGrid.setVgap(10);
            detailsGrid.setPadding(new Insets(10));
            detailsGrid.setStyle("-fx-background-color: #f0f8ff; -fx-background-radius: 5;");
        
            // Transport
        Label transportIcon = new Label("🚌");
        transportIcon.setStyle("-fx-font-size: 16px;");
        
        Label transportLabel = new Label("Transport:");
        transportLabel.setStyle("-fx-font-weight: bold;");
        
        String transportInfo = LocationUtils.generateTransportInfo();
        Label transportValue = new Label(transportInfo);
            transportValue.setWrapText(true);
        
            // Parking
        Label parkingIcon = new Label("🅿️");
        parkingIcon.setStyle("-fx-font-size: 16px;");
        
        Label parkingLabel = new Label("Parking:");
        parkingLabel.setStyle("-fx-font-weight: bold;");
        
        String parkingInfo = LocationUtils.generateParkingInfo();
        Label parkingValue = new Label(parkingInfo);
            parkingValue.setWrapText(true);
        
        // Ajouter à la grille
        detailsGrid.add(transportIcon, 0, 0);
        detailsGrid.add(transportLabel, 1, 0);
        detailsGrid.add(transportValue, 2, 0);
        
        detailsGrid.add(parkingIcon, 0, 1);
        detailsGrid.add(parkingLabel, 1, 1);
        detailsGrid.add(parkingValue, 2, 1);
        
        // Ajouter tous les éléments au panneau d'informations
        locationInfoBox.getChildren().addAll(
            locationHeader,
            locationDescLabel,
            coordBox,
            addressBox,
            separator,
            detailsGrid
        );
        
        // Bouton pour ouvrir Google Maps avec les coordonnées réelles
        Button openMapsButton = new Button("Ouvrir dans Google Maps");
        openMapsButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-font-weight: bold;");
        openMapsButton.setOnAction(e -> {
            String mapsUrl = String.format("https://www.google.com/maps/search/?api=1&query=%f,%f", 
                coords.getLatitude(), coords.getLongitude());
            
            try {
                java.awt.Desktop.getDesktop().browse(java.net.URI.create(mapsUrl));
            } catch (Exception ex) {
                    System.err.println("Erreur lors de l'ouverture du navigateur: " + ex.getMessage());
                    ex.printStackTrace();
                showAlert(AlertType.ERROR, "Erreur", "Impossible d'ouvrir le navigateur",
                         "Veuillez copier cette URL dans votre navigateur : " + mapsUrl);
            }
        });
        
        // Ajouter tous les éléments à l'onglet d'informations
        infoContent.getChildren().addAll(titleLabel, locationInfoBox, openMapsButton);
        infoTab.setContent(infoContent);
        
        // Onglet 2: Carte interactive
        Tab mapTab = new Tab("Carte");
        
            // Container principal pour la carte
            VBox mapContainer = new VBox(15);
            mapContainer.setPadding(new Insets(20));
            mapContainer.setStyle("-fx-background-color: white;");
            
            // Titre de la carte
            Label mapTitleLabel = new Label("Carte de " + location);
            mapTitleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
            
            // Créer un panneau pour la carte stylisée
            Pane mapPane = new Pane();
            mapPane.setPrefSize(750, 450);
            mapPane.setStyle("-fx-background-color: #f0f0f0; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);");
            
            // Créer une carte stylisée avec des formes JavaFX
            createStylishMap(mapPane, location, coords);
            
            // Légende de la carte
            VBox legendBox = new VBox(10);
            legendBox.setPadding(new Insets(15));
            legendBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.8); -fx-background-radius: 5;");
            legendBox.setMaxWidth(200);
            legendBox.setLayoutX(20);
            legendBox.setLayoutY(20);
            
            Label legendTitle = new Label("Légende");
            legendTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            
            // Éléments de légende
            HBox locationItem = new HBox(10);
            locationItem.setAlignment(Pos.CENTER_LEFT);
            Circle locationDot = new Circle(6, Color.RED);
            Label locationItemLabel = new Label("Emplacement");
            locationItem.getChildren().addAll(locationDot, locationItemLabel);
            
            HBox poiItem = new HBox(10);
            poiItem.setAlignment(Pos.CENTER_LEFT);
            Circle poiDot = new Circle(6, Color.BLUE);
            Label poiItemLabel = new Label("Point d'intérêt");
            poiItem.getChildren().addAll(poiDot, poiItemLabel);
            
            HBox roadItem = new HBox(10);
            roadItem.setAlignment(Pos.CENTER_LEFT);
            Rectangle roadRect = new Rectangle(12, 4);
            roadRect.setFill(Color.GRAY);
            Label roadItemLabel = new Label("Route");
            roadItem.getChildren().addAll(roadRect, roadItemLabel);
            
            legendBox.getChildren().addAll(legendTitle, locationItem, poiItem, roadItem);
            mapPane.getChildren().add(legendBox);
            
            // Bouton pour changer le thème de la carte (clair/sombre)
            Button themeButton = new Button("🌙 Mode sombre");
            themeButton.setStyle("-fx-background-color: #343a40; -fx-text-fill: white;");
            
            // Gestionnaire d'événements pour le changement de thème
            final boolean[] isDarkMode = {false};
            themeButton.setOnAction(e -> {
                isDarkMode[0] = !isDarkMode[0];
                if (isDarkMode[0]) {
                    // Appliquer le thème sombre
                    mapPane.setStyle("-fx-background-color: #343a40; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);");
                    legendBox.setStyle("-fx-background-color: rgba(52, 58, 64, 0.8); -fx-background-radius: 5;");
                    legendTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: white;");
                    locationItemLabel.setStyle("-fx-text-fill: white;");
                    poiItemLabel.setStyle("-fx-text-fill: white;");
                    roadItemLabel.setStyle("-fx-text-fill: white;");
                    themeButton.setText("☀️ Mode clair");
                    themeButton.setStyle("-fx-background-color: #f8f9fa; -fx-text-fill: black;");
                    
                    // Redessiner la carte en thème sombre
                    mapPane.getChildren().clear();
                    mapPane.getChildren().add(legendBox);
                    createStylishMap(mapPane, location, coords, true);
                } else {
                    // Appliquer le thème clair
                    mapPane.setStyle("-fx-background-color: #f0f0f0; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);");
                    legendBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.8); -fx-background-radius: 5;");
                    legendTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: black;");
                    locationItemLabel.setStyle("-fx-text-fill: black;");
                    poiItemLabel.setStyle("-fx-text-fill: black;");
                    roadItemLabel.setStyle("-fx-text-fill: black;");
                    themeButton.setText("🌙 Mode sombre");
                    themeButton.setStyle("-fx-background-color: #343a40; -fx-text-fill: white;");
                    
                    // Redessiner la carte en thème clair
                    mapPane.getChildren().clear();
                    mapPane.getChildren().add(legendBox);
                    createStylishMap(mapPane, location, coords, false);
                }
            });
            
            // Bouton pour simuler un zoom avant
            Button zoomInButton = new Button("🔍 Zoom avant");
            zoomInButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white;");
            
            // Bouton pour simuler un zoom arrière
            Button zoomOutButton = new Button("🔍 Zoom arrière");
            zoomOutButton.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white;");
            
            // Ajouter des comportements aux boutons de zoom (simulation uniquement)
            final double[] zoomLevel = {1.0};
            zoomInButton.setOnAction(e -> {
                if (zoomLevel[0] < 1.5) {
                    zoomLevel[0] += 0.1;
                    mapPane.setScaleX(zoomLevel[0]);
                    mapPane.setScaleY(zoomLevel[0]);
                }
            });
            
            zoomOutButton.setOnAction(e -> {
                if (zoomLevel[0] > 0.7) {
                    zoomLevel[0] -= 0.1;
                    mapPane.setScaleX(zoomLevel[0]);
                    mapPane.setScaleY(zoomLevel[0]);
                }
            });
            
            // Mettre les boutons dans une HBox
            HBox buttonBox = new HBox(10);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.getChildren().addAll(zoomOutButton, themeButton, zoomInButton);
            
            // Ajouter tous les éléments au conteneur principal
            mapContainer.getChildren().addAll(mapTitleLabel, mapPane, buttonBox);
            
            ScrollPane scrollPane = new ScrollPane(mapContainer);
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            scrollPane.setPannable(true);
            
            mapTab.setContent(scrollPane);
            
            // Ajouter les onglets au TabPane
            tabPane.getTabs().addAll(infoTab, mapTab);
            
            // Définir le contenu du dialogue
            dialog.getDialogPane().setContent(tabPane);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
            
            dialog.showAndWait();
        } catch (Exception e) {
            System.err.println("Erreur lors de l'affichage du dialogue carte: " + e.getMessage());
            e.printStackTrace();
            showAlert(AlertType.ERROR,
                    "Erreur", 
                    "Erreur d'affichage", 
                    "Impossible d'afficher la carte: " + e.getMessage());
        }
    }

    /**
     * Crée une carte stylisée personnalisée avec des formes JavaFX
     * @param parent Le panneau parent sur lequel dessiner la carte
     * @param location Le nom du lieu
     * @param coords Les coordonnées du lieu
     * @param isDarkMode Indique si le mode sombre est activé
     */
    private void createStylishMap(Pane parent, String location, LocationUtils.Coordinates coords, boolean isDarkMode) {
        // Couleurs du thème
        Color bgColor = isDarkMode ? Color.rgb(52, 58, 64) : Color.rgb(240, 240, 240);
        Color roadColor = isDarkMode ? Color.rgb(150, 150, 150) : Color.rgb(200, 200, 200);
        Color roadOutlineColor = isDarkMode ? Color.rgb(180, 180, 180) : Color.rgb(150, 150, 150);
        Color waterColor = isDarkMode ? Color.rgb(70, 130, 180) : Color.rgb(173, 216, 230);
        Color parkColor = isDarkMode ? Color.rgb(53, 94, 59) : Color.rgb(144, 238, 144);
        Color textColor = isDarkMode ? Color.WHITE : Color.BLACK;
        
        // Dimensions du parent
        double width = parent.getPrefWidth();
        double height = parent.getPrefHeight();
        double centerX = width / 2;
        double centerY = height / 2;
        
        // Fond de la carte
        Rectangle background = new Rectangle(0, 0, width, height);
        background.setFill(bgColor);
        
        // Créer un élément de fond décoratif (parc ou lac)
        if (Math.random() > 0.5) {
            // Parc
            Circle park = new Circle(width * 0.25, height * 0.3, Math.min(width, height) * 0.15);
            park.setFill(parkColor);
            park.setOpacity(0.7);
            
            // Étiquette du parc
            Label parkLabel = new Label("Parc municipal");
            parkLabel.setTextFill(textColor);
            parkLabel.setLayoutX(width * 0.25 - 40);
            parkLabel.setLayoutY(height * 0.3 - 10);
            
            parent.getChildren().addAll(park, parkLabel);
        } else {
            // Lac
            Ellipse lake = new Ellipse(width * 0.7, height * 0.7, width * 0.15, height * 0.1);
            lake.setFill(waterColor);
            lake.setOpacity(0.7);
            
            // Étiquette du lac
            Label lakeLabel = new Label("Lac");
            lakeLabel.setTextFill(textColor);
            lakeLabel.setLayoutX(width * 0.7 - 10);
            lakeLabel.setLayoutY(height * 0.7 - 10);
            
            parent.getChildren().addAll(lake, lakeLabel);
        }
        
        // Routes principales
        Line mainRoad1 = new Line(0, centerY, width, centerY);
        mainRoad1.setStroke(roadColor);
        mainRoad1.setStrokeWidth(20);
        mainRoad1.setStrokeLineCap(javafx.scene.shape.StrokeLineCap.BUTT);
        
        Line mainRoad2 = new Line(centerX, 0, centerX, height);
        mainRoad2.setStroke(roadColor);
        mainRoad2.setStrokeWidth(15);
        mainRoad2.setStrokeLineCap(javafx.scene.shape.StrokeLineCap.BUTT);
        
        // Routes secondaires
        Line road1 = new Line(centerX * 0.5, 0, centerX * 0.5, height);
        road1.setStroke(roadColor);
        road1.setStrokeWidth(10);
        
        Line road2 = new Line(centerX * 1.5, 0, centerX * 1.5, height);
        road2.setStroke(roadColor);
        road2.setStrokeWidth(10);
        
        Line road3 = new Line(0, centerY * 0.5, width, centerY * 0.5);
        road3.setStroke(roadColor);
        road3.setStrokeWidth(10);
        
        Line road4 = new Line(0, centerY * 1.5, width, centerY * 1.5);
        road4.setStroke(roadColor);
        road4.setStrokeWidth(10);
        
        // Diagonales
        Line diagonal1 = new Line(centerX - width * 0.3, centerY - height * 0.3, centerX + width * 0.3, centerY + height * 0.3);
        diagonal1.setStroke(roadColor);
        diagonal1.setStrokeWidth(8);
        
        Line diagonal2 = new Line(centerX - width * 0.3, centerY + height * 0.3, centerX + width * 0.3, centerY - height * 0.3);
        diagonal2.setStroke(roadColor);
        diagonal2.setStrokeWidth(8);
        
        // Marqueur principal pour l'emplacement (au centre)
        Circle locationMarker = new Circle(centerX, centerY, 12);
        locationMarker.setFill(Color.RED);
        locationMarker.setStroke(Color.WHITE);
        locationMarker.setStrokeWidth(3);
        
        // Étiquette d'emplacement
        Label locationLabel = new Label(location);
        locationLabel.setStyle("-fx-background-color: white; -fx-padding: 5 10; -fx-background-radius: 5; " +
                              "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 3, 0, 0, 1);");
        locationLabel.setLayoutX(centerX - (location.length() * 3));
        locationLabel.setLayoutY(centerY - 35);
        
        // Ajouter des points d'intérêt (POIs)
        VBox poi1 = createPOI("Hôtel", centerX - width * 0.2, centerY - height * 0.2, textColor);
        VBox poi2 = createPOI("Restaurant", centerX + width * 0.2, centerY - height * 0.2, textColor);
        VBox poi3 = createPOI("Musée", centerX - width * 0.3, centerY + height * 0.3, textColor);
        VBox poi4 = createPOI("Café", centerX + width * 0.3, centerY + height * 0.2, textColor);
        
        // Ajouter tous les éléments à la carte
        parent.getChildren().addAll(
            background,
            mainRoad1, mainRoad2,
            road1, road2, road3, road4,
            diagonal1, diagonal2,
            poi1, poi2, poi3, poi4,
            locationMarker, locationLabel
        );
    }

    /**
     * Crée un point d'intérêt pour la carte
     * @param name Nom du point d'intérêt
     * @param x Position X
     * @param y Position Y
     * @param textColor Couleur du texte
     * @return Un conteneur avec l'icône et le nom du POI
     */
    private VBox createPOI(String name, double x, double y, Color textColor) {
        VBox poi = new VBox(3);
        poi.setAlignment(Pos.CENTER);
        poi.setLayoutX(x - 15);
        poi.setLayoutY(y - 15);
        
        Circle poiCircle = new Circle(6);
        poiCircle.setFill(Color.BLUE);
        poiCircle.setStroke(Color.WHITE);
        poiCircle.setStrokeWidth(1.5);
        
        Label poiLabel = new Label(name);
        poiLabel.setTextFill(textColor);
        poiLabel.setStyle("-fx-font-size: 10px;");
        
        poi.getChildren().addAll(poiCircle, poiLabel);
        return poi;
    }

    /**
     * Surcharge de createStylishMap qui utilise le mode clair par défaut
     */
    private void createStylishMap(Pane parent, String location, LocationUtils.Coordinates coords) {
        createStylishMap(parent, location, coords, false);
    }

    // Afficher les détails d'un événement
    private void showEventDetails(Evenement event) {
        try {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Détails de l'événement");
            dialog.setHeaderText(null);
            
            // Créer un BorderPane pour organiser le contenu
            BorderPane contentPane = new BorderPane();
            contentPane.setPadding(new Insets(20));
            
            // Section supérieure (image, titre, etc.)
            VBox topSection = new VBox(15);
            
            // Image de l'événement
            ImageView eventImage = new ImageView();
            eventImage.setFitWidth(350);
            eventImage.setFitHeight(200);
            eventImage.setPreserveRatio(true);
            
            // Charger l'image
            try {
                String imagePath = event.getImage();
                if (imagePath != null && !imagePath.isEmpty()) {
                    // Essayer d'abord comme chemin absolu
                    File imageFile = new File(imagePath);
                    if (imageFile.exists()) {
                        eventImage.setImage(new Image(imageFile.toURI().toString()));
                    } else {
                        // Essayer dans le répertoire d'images XAMPP
                        String uploadDir = "C:\\xampp\\htdocs\\imageP\\";
                        File uploadedImage = new File(uploadDir + imagePath);
                        System.out.println("Tentative de chargement de l'image détaillée: " + uploadedImage.getAbsolutePath());
                        
                        if (uploadedImage.exists()) {
                            eventImage.setImage(new Image(uploadedImage.toURI().toString()));
                            System.out.println("✅ Image détaillée chargée avec succès depuis le répertoire XAMPP");
                        } else {
                            // Image par défaut
                            System.out.println("❌ Image détaillée non trouvée: " + uploadedImage.getAbsolutePath());
                            URL defaultImageUrl = getClass().getResource("/images/default-event.jpg");
                            if (defaultImageUrl != null) {
                                eventImage.setImage(new Image(defaultImageUrl.toExternalForm()));
                            }
                        }
                    }
                } else {
                    // Pas d'image spécifiée, essayer d'utiliser une image par défaut
                    URL defaultImageUrl = getClass().getResource("/images/default-event.jpg");
                    if (defaultImageUrl != null) {
                        eventImage.setImage(new Image(defaultImageUrl.toExternalForm()));
                    }
                }
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement de l'image: " + e.getMessage());
            }
            
            // Informations principales
            Label titleLabel = new Label(event.getTitre());
            titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
            titleLabel.setWrapText(true);
            
            // Étoiles pour la popularité
            HBox starsBox = new HBox(3);
            starsBox.setAlignment(Pos.CENTER_LEFT);
            
            int stars = calculerEtoiles(event);
            String starsText = genererChaineEtoiles(stars);
            
            Label starsLabel = new Label(starsText);
            starsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #f39c12;");
            
            Label ratingLabel = new Label(" (" + stars + "/5)");
            ratingLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
            
            starsBox.getChildren().addAll(starsLabel, ratingLabel);
            
            // Type d'événement
            Label typeLabel = new Label(event.getType() != null ? event.getType() : "Type non spécifié");
            typeLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: white; -fx-background-color: #3498db; -fx-padding: 5 10; -fx-background-radius: 3;");
            
            // Dates
            Label datesLabel = new Label(String.format("Du %s au %s", 
                                                 event.getDateD().format(formatter), 
                                                 event.getDateF().format(formatter)));
            datesLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
            
            // Lieu
            Label locationLabel = new Label(event.getLocation() != null ? event.getLocation() : "Lieu non spécifié");
            locationLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
            
            // Places disponibles
            Label placesLabel = new Label(String.format("Places disponibles: %d", event.getNbPlace()));
            if (event.getNbPlace() <= 0) {
                placesLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");
            } else if (event.getNbPlace() < 5) {
                placesLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #f39c12;");
            } else {
                placesLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2ecc71;");
            }
            
            // Prix
            Label prixLabel = new Label(String.format("Prix: %.2f €", event.getPrix()));
            prixLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
            
            // Ajouter les éléments au panneau supérieur
            topSection.getChildren().addAll(eventImage, titleLabel, starsBox, typeLabel, datesLabel, locationLabel, placesLabel, prixLabel);
            
            // Section inférieure (description)
            VBox bottomSection = new VBox(15);
            bottomSection.setPadding(new Insets(20, 0, 0, 0));
            
            // Séparateur
            Separator separator = new Separator();
            separator.setStyle("-fx-background-color: #ecf0f1;");
            
            // Titre de la section description
            HBox descriptionHeader = new HBox(10);
            descriptionHeader.setAlignment(Pos.CENTER_LEFT);
            
            Label descriptionTitle = new Label("Description");
            descriptionTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
            
            // Bouton de lecture vocale
            Button ttsButton = new Button();
            ttsButton.setGraphic(new Label("🔊"));
            ttsButton.setTooltip(new Tooltip("Lire la description à voix haute"));
            ttsButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-cursor: hand;");
            
            // Gestionnaire d'événement pour la lecture vocale
            ttsButton.setOnAction(e -> {
                String description = event.getDescription();
                if (description != null && !description.isEmpty()) {
                    try {
                        // Indique que la lecture va commencer
                        ttsButton.setGraphic(new Label("⏳"));
                        ttsButton.setDisable(true);
                        
                        // Utiliser le service TTS pour lire la description
                        TTSService ttsService = TTSService.getInstance();
                        
                        // Si la lecture est en cours, l'arrêter
                        if (ttsService.isSpeaking()) {
                            ttsService.stop();
                            ttsButton.setGraphic(new Label("🔊"));
                            ttsButton.setDisable(false);
                            return;
                        }
                        
                        // Limiter le texte pour éviter les problèmes
                        if (description.length() > 2000) {
                            description = description.substring(0, 2000) + "...";
                        }
                        
                        // Utiliser la méthode améliorée qui nettoie le texte et utilise un fichier temporaire
                        System.out.println("Tentative de lecture TTS via fichier temporaire");
                        ttsService.speakFromFile(description, true);
                        
                        // Réactiver le bouton après un court délai
                        new Thread(() -> {
                            try {
                                Thread.sleep(1000);
                                javafx.application.Platform.runLater(() -> {
                                    ttsButton.setGraphic(new Label("🔊"));
                                    ttsButton.setDisable(false);
                                });
                            } catch (InterruptedException ex) {
                                Thread.currentThread().interrupt();
                            }
                        }).start();
                } catch (Exception ex) {
                        showAlert(AlertType.ERROR, "Erreur", "Erreur de lecture vocale",
                                 "Impossible de lire la description: " + ex.getMessage());
                        ttsButton.setGraphic(new Label("🔊"));
                        ttsButton.setDisable(false);
                    }
                } else {
                    showAlert(AlertType.WARNING, "Pas de description",
                             "Aucun texte à lire", 
                             "Cet événement n'a pas de description.");
                }
            });
            
            descriptionHeader.getChildren().addAll(descriptionTitle, ttsButton);
            
            // Zone de texte pour la description
            TextArea descriptionText = new TextArea(event.getDescription());
            descriptionText.setWrapText(true);
            descriptionText.setEditable(false);
            descriptionText.setPrefHeight(150);
            descriptionText.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #ecf0f1;");
            
            // Ajouter les éléments à la section description
            bottomSection.getChildren().addAll(separator, descriptionHeader, descriptionText);
            
            // Style de base pour les boutons
            String buttonBaseStyle = "-fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5; -fx-cursor: hand; ";
            
            // Boutons d'action
            Button sessionsButton = new Button("Voir les sessions");
            sessionsButton.setStyle(buttonBaseStyle + "-fx-background-color: #3498db; -fx-text-fill: white;");
            sessionsButton.setOnAction(e -> {
                dialog.close();
                showSessionsModal(event);
            });
            
            Button reserveButton = new Button("Réserver");
            reserveButton.setStyle(buttonBaseStyle + "-fx-background-color: #2ecc71; -fx-text-fill: white;");
            reserveButton.setOnAction(e -> {
                dialog.close();
                showReservationDialog(event);
            });
            
            Button mapButton = new Button("Carte");
            mapButton.setStyle(buttonBaseStyle + "-fx-background-color: #e67e22; -fx-text-fill: white;");
            mapButton.setOnAction(e -> {
                showMapDialog(event.getLocation());
            });
            
            Button weatherButton = new Button("Météo");
            weatherButton.setStyle(buttonBaseStyle + "-fx-background-color: #9b59b6; -fx-text-fill: white;");
            weatherButton.setOnAction(e -> {
                try {
                    System.out.println("Affichage des informations météo pour " + event.getLocation());
                    showWeatherDialog(event.getLocation());
                } catch (Exception ex) {
                    System.err.println("Erreur lors de l'affichage des données météo: " + ex.getMessage());
                    ex.printStackTrace();
                    showAlert(AlertType.ERROR,
                             "Erreur", 
                             "Erreur de connexion météo", 
                             "Impossible d'afficher les informations météo: " + ex.getMessage());
                }
            });
            
            // Désactiver le bouton de réservation si l'événement est complet
            if (event.getNbPlace() <= 0) {
                reserveButton.setDisable(true);
                reserveButton.setStyle(buttonBaseStyle + "-fx-background-color: #bdc3c7; -fx-text-fill: white;");
            }
            
            // Aligner les boutons dans un FlowPane pour un meilleur affichage sur petits écrans
            FlowPane buttonPane = new FlowPane(15, 15);
            buttonPane.setAlignment(Pos.CENTER);
            buttonPane.getChildren().addAll(weatherButton, mapButton, sessionsButton, reserveButton);
            
            bottomSection.getChildren().add(buttonPane);
            
            // Conteneur principal en VBox pour un meilleur affichage
            VBox mainContainer = new VBox(20);
            mainContainer.getChildren().addAll(topSection, bottomSection);
            
            // Assembler le contenu
            contentPane.setCenter(mainContainer);
            
            // Configurer la boîte de dialogue
            dialog.getDialogPane().setContent(contentPane);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        
            // Appliquer le style professionnel
            MainStyleFixer.styleProfessionalDialog(dialog.getDialogPane());
            
            // Afficher la boîte de dialogue
        dialog.showAndWait();
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(
                AlertType.ERROR,
                "Erreur",
                "Impossible d'afficher les détails de l'événement",
                "Une erreur s'est produite: " + e.getMessage()
            );
        }
    }
    
    /**
     * Méthode pour modifier un événement (CRUD)
     */
    private void editEvent(Evenement event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterEvent.fxml"));
            Parent root = loader.load();
            
            AjouterEventController controller = loader.getController();
            controller.initializeForEdit(event);
            
            Stage stage = new Stage();
            stage.setTitle("Modifier l'événement");
                Scene scene = new Scene(root);
                
            // Appliquer le style professionnel
                MainStyleFixer.applyProfessionalStyle(scene);
                
            stage.setScene(scene);
            stage.show();
            
            // Recharger les événements après la fermeture de la fenêtre
            stage.setOnHidden(e -> loadEvents());
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(
                AlertType.ERROR,
                "Erreur",
                "Impossible de modifier l'événement",
                "Une erreur s'est produite: " + e.getMessage()
            );
        }
    }
    
    /**
     * Méthode pour supprimer un événement (CRUD)
     */
    private void deleteEvent(Evenement event) {
        // Demander confirmation avant de supprimer
        Alert confirmDialog = new Alert(AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmer la suppression");
        confirmDialog.setHeaderText("Êtes-vous sûr de vouloir supprimer cet événement ?");
        confirmDialog.setContentText("Cette action est irréversible.");
        
        // Appliquer le style professionnel
        MainStyleFixer.styleProfessionalDialog(confirmDialog.getDialogPane());
        
        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Supprimer l'événement
                evenementService.supprimer(event.getId());
                
                // Afficher un message de confirmation
                showAlert(
                    AlertType.INFORMATION,
                    "Succès",
                    "Événement supprimé",
                    "L'événement a été supprimé avec succès."
                );
                
                // Recharger la liste des événements
                loadEvents();
                
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(
                    AlertType.ERROR,
                    "Erreur",
                    "Impossible de supprimer l'événement",
                    "Une erreur s'est produite: " + e.getMessage()
                );
            }
        }
    }
    
    /**
     * Méthode utilitaire pour calculer le nombre d'étoiles basé sur le nombre de réservations
     * @param event L'événement pour lequel calculer les étoiles
     * @return Le nombre d'étoiles (de 1 à 5)
     */
    private int calculerEtoiles(Evenement event) {
        // Valeur par défaut pour le nombre total de places
        int placesTotal = 20; 
        
        // Places restantes
        int placesRestantes = event.getNbPlace();
        
        // Calculer les places réservées
        int placesReservees = placesTotal - placesRestantes;
        
        // Assurer que nous avons au moins 1 étoile et maximum 5 étoiles
        return Math.min(5, Math.max(1, (int)Math.ceil(placesReservees * 5.0 / placesTotal)));
    }
    
    /**
     * Méthode utilitaire pour générer la chaîne de caractères d'étoiles
     * @param nbEtoiles Le nombre d'étoiles à afficher (de 1 à 5)
     * @return Une chaîne de caractères représentant les étoiles (pleines et vides)
     */
    private String genererChaineEtoiles(int nbEtoiles) {
        StringBuilder stars = new StringBuilder();
        
        // Ajouter les étoiles pleines
        for (int i = 0; i < nbEtoiles; i++) {
            stars.append("★");
        }
        
        // Ajouter les étoiles vides
        for (int i = nbEtoiles; i < 5; i++) {
            stars.append("☆");
        }
        
        return stars.toString();
    }
    
    // Méthode pour accéder à l'interface d'administration
    @FXML
    private void handleAdminAccess() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageEvent.fxml"));
            Parent root = loader.load();
            
            // Changer la scène
            Stage stage = (Stage) btnAdmin.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Gestion des Événements - Administration");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR,
                     "Erreur", 
                     "Erreur de navigation", 
                     "Impossible d'accéder à l'interface d'administration: " + e.getMessage());
        }
    }
}

/**
 * Classe utilitaire pour afficher un dialogue de progression
 */
class ProgressDialog extends Dialog<Void> {
    private ProgressIndicator progressIndicator;
    
    public ProgressDialog() {
        initComponents();
    }
    
    private void initComponents() {
        setTitle("Traitement en cours");
        
        // Créer l'indicateur de progression
        progressIndicator = new ProgressIndicator();
        progressIndicator.setPrefSize(50, 50);
        
        // Créer la disposition
        VBox content = new VBox(15);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(20));
        content.getChildren().add(progressIndicator);
        
        // Configurer le dialogue
        getDialogPane().setContent(content);
        getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        getDialogPane().setStyle("-fx-background-color: white;");
        
        // Masquer le bouton d'annulation (optionnel)
        Button cancelButton = (Button) getDialogPane().lookupButton(ButtonType.CANCEL);
        if (cancelButton != null) {
            cancelButton.setVisible(false);
        }
    }
}