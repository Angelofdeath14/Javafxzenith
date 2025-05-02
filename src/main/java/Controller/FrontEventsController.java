package Controller;

import Entity.Evenement;
import Entity.Session;
import Utils.LocationUtils;
import Utils.MapStyleUtils;
import Utils.WeatherUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Ellipse;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.EvenementService;
import services.SessionService;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import Utils.MainStyleFixer;
import Utils.AnimationUtils;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.layout.BorderPane;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.ArrayList;

public class FrontEventsController implements Initializable {
    
    @FXML private FlowPane eventsContainer;
    @FXML private FlowPane thisWeekEventsContainer;
    @FXML private FlowPane upcomingEventsContainer;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterTypeComboBox;
    @FXML private ComboBox<String> filterDate;
    @FXML private Button btnBack;
    @FXML private Button btnMyReservations;
    
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
                showAlert(Alert.AlertType.ERROR, 
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
                btnBack.getStyleClass().add("neutral");
                // Ajouter un effet de clic
                AnimationUtils.addClickEffect(btnBack);
            }
            
            // Appliquer le style professionnel à la scène après son chargement
            setupSceneListener();
            
            System.out.println("FrontEventsController initialisé avec succès");
            
        } catch (Exception e) {
            System.err.println("Erreur d'initialisation globale: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, 
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
                    MainStyleFixer.applyProfessionalStyle(newScene);
                }
            });
        } else if (thisWeekEventsContainer != null) {
            thisWeekEventsContainer.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    MainStyleFixer.applyProfessionalStyle(newScene);
                }
            });
        } else if (upcomingEventsContainer != null) {
            upcomingEventsContainer.sceneProperty().addListener((obs, oldScene, newScene) -> {
                if (newScene != null) {
                    MainStyleFixer.applyProfessionalStyle(newScene);
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
                showAlert(Alert.AlertType.ERROR, "Erreur", "Service non initialisé", 
                         "Le service d'événements n'est pas initialisé");
                return;
            }
            
            // Récupérer les événements
            List<Evenement> events = evenementService.getAllEvents();
            
            // Vider et remplir les conteneurs qui existent
            populateEventsContainer(events);
            populateThisWeekEventsContainer(events);
            populateUpcomingEventsContainer(events);
            
            System.out.println("Événements chargés avec succès: " + events.size() + " événements");
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des événements: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement", 
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
        
        // Ajouter un titre de section
        VBox headerBox = new VBox(5);
        headerBox.setPrefWidth(thisWeekEventsContainer.getPrefWidth());
        headerBox.setStyle("-fx-padding: 10 0 20 10; -fx-background-color: #f5f6fa; -fx-background-radius: 10 10 0 0;");
        
        Label sectionTitle = new Label("Cette semaine");
        sectionTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Label sectionSubtitle = new Label("Les événements qui ont lieu cette semaine");
        sectionSubtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
        
        headerBox.getChildren().addAll(sectionTitle, sectionSubtitle);
        thisWeekEventsContainer.getChildren().add(headerBox);
        
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
        VBox card = new VBox(10);
        card.getStyleClass().add("event-card");
        card.setPadding(new Insets(15));
        card.setPrefWidth(270);
        card.setCursor(javafx.scene.Cursor.HAND);
        card.setStyle("-fx-background-color: white; -fx-border-radius: 15; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);");
        
        // Ajouter un effet de survol amélioré
        card.setOnMouseEntered(e -> {
            card.setStyle("-fx-background-color: #f8f9fa; -fx-border-radius: 15; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.4), 15, 0, 0, 0); -fx-border-color: #dce1e6; -fx-border-width: 1;");
        });
        
        card.setOnMouseExited(e -> {
            card.setStyle("-fx-background-color: white; -fx-border-radius: 15; -fx-background-radius: 15; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);");
        });
        
        // Ajouter une action de clic
        card.setOnMouseClicked(e -> {
            showEventDetails(event);
        });
        
        // Conteneur pour l'image avec badge de statut
        StackPane imageContainer = new StackPane();
        imageContainer.setMinHeight(150);
        
        // Rectangle coloré comme fond avec bords arrondis
        Rectangle background = new Rectangle(270, 150);
        background.setArcWidth(15);
        background.setArcHeight(15);
        
        // Choisir une couleur en fonction du type d'événement pour le fond
        String eventType = event.getType() != null ? event.getType().toLowerCase() : "";
        switch (eventType) {
            case "concert":
                background.setFill(Color.web("#3498db", 0.2));
                break;
            case "théâtre":
                background.setFill(Color.web("#e74c3c", 0.2));
                break;
            case "exposition":
                background.setFill(Color.web("#2ecc71", 0.2));
                break;
            case "sport":
                background.setFill(Color.web("#f39c12", 0.2));
                break;
            default:
                background.setFill(Color.web("#9b59b6", 0.2));
                break;
        }
        
        // Création de l'ImageView pour afficher l'image de l'événement
        ImageView imageView = new ImageView();
        imageView.setFitWidth(270);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);
        imageView.getStyleClass().add("event-image");
        
        // Vérification et chargement de l'image de l'événement
        String imagePath = event.getImage();
        try {
            if (imagePath != null && !imagePath.isEmpty()) {
                // Vérifier si l'image est une URL ou un chemin local
                if (imagePath.toLowerCase().startsWith("http")) {
                    // Image à partir d'une URL
                    Image image = new Image(imagePath, true);
                    imageView.setImage(image);
                } else {
                    // Image locale - vérifier d'abord si c'est un chemin complet
                    File imageFile = new File(imagePath);
                    if (imageFile.exists()) {
                        Image image = new Image(imageFile.toURI().toString());
                        imageView.setImage(image);
                    } else {
                        // Essayer avec le chemin relatif dans le dossier de l'application
                        File relativeFile = new File("src/main/resources/images/" + imagePath);
                        if (relativeFile.exists()) {
                            Image image = new Image(relativeFile.toURI().toString());
                            imageView.setImage(image);
                        } else {
                            // Essayer de charger à partir du dossier d'images configuré
                            String uploadDir = "C:\\xampp\\htdocs\\imageP\\";
                            File uploadedImage = new File(uploadDir + imagePath);
                            if (uploadedImage.exists()) {
                                Image image = new Image(uploadedImage.toURI().toString());
                                imageView.setImage(image);
                            } else {
                                // Essayer le chemin en ressource directe
                                String resourcePath = "/images/" + imagePath;
                                URL resourceUrl = getClass().getResource(resourcePath);
                                if (resourceUrl != null) {
                                    Image image = new Image(resourceUrl.toExternalForm());
                                    imageView.setImage(image);
                                } else {
                                    // Utiliser l'image par défaut si le fichier n'existe pas
                                    URL defaultUrl = getClass().getResource("/images/default-session.png");
                                    if (defaultUrl != null) {
                                        Image defaultImage = new Image(defaultUrl.toExternalForm());
                                        imageView.setImage(defaultImage);
                                    } else {
                                        System.err.println("Image par défaut introuvable: /images/default-session.png");
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Utiliser l'image par défaut si aucune image n'est spécifiée
                URL defaultUrl = getClass().getResource("/images/default-session.png");
                if (defaultUrl != null) {
                    Image defaultImage = new Image(defaultUrl.toExternalForm());
                    imageView.setImage(defaultImage);
                } else {
                    System.err.println("Image par défaut introuvable: /images/default-session.png");
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image: " + e.getMessage());
            e.printStackTrace();
            // Fallback à l'emoji en cas d'erreur avec l'image
            String emoji;
            switch (eventType) {
                case "concert":
                    emoji = "🎵";
                    break;
                case "théâtre":
                    emoji = "🎭";
                    break;
                case "exposition":
                    emoji = "🖼️";
                    break;
                case "sport":
                    emoji = "⚽";
                    break;
                default:
                    emoji = "🎪";
                    break;
            }
            
            Label emojiLabel = new Label(emoji);
            emojiLabel.setStyle("-fx-font-size: 48px;");
            imageContainer.getChildren().add(emojiLabel);
        }
        
        // Ajouter un effet d'arrondissement aux images
        Rectangle clip = new Rectangle(imageView.getFitWidth(), imageView.getFitHeight());
        clip.setArcWidth(15);
        clip.setArcHeight(15);
        imageView.setClip(clip);
        
        // Badge de statut avec couleurs plus vives et bords arrondis
        Label statusBadge = new Label();
        statusBadge.setTextFill(Color.WHITE);
        statusBadge.setPadding(new Insets(5, 10, 5, 10));
        statusBadge.setFont(Font.font("System", FontWeight.BOLD, 12));
        statusBadge.setStyle("-fx-background-radius: 20; -fx-padding: 5 10;");
        
        if (event.getNbPlace() <= 0) {
            statusBadge.setText("COMPLET");
            statusBadge.setStyle("-fx-background-color: #e74c3c; -fx-background-radius: 20; -fx-padding: 5 10;");
        } else if (event.getNbPlace() < 5) {
            statusBadge.setText("DERNIÈRES PLACES");
            statusBadge.setStyle("-fx-background-color: #f39c12; -fx-background-radius: 20; -fx-padding: 5 10;");
        } else {
            statusBadge.setText(event.getNbPlace() + " PLACES");
            statusBadge.setStyle("-fx-background-color: #2ecc71; -fx-background-radius: 20; -fx-padding: 5 10;");
        }
        
        // Positionnement du badge
        StackPane.setAlignment(statusBadge, Pos.TOP_RIGHT);
        StackPane.setMargin(statusBadge, new Insets(10));
        
        imageContainer.getChildren().addAll(background, imageView, statusBadge);
        
        // Titre de l'événement
        Label titleLabel = new Label(event.getTitre() != null ? event.getTitre() : "Événement sans titre");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        titleLabel.setWrapText(true);
        
        // Lieu de l'événement avec icône
        HBox locationBox = new HBox(5);
        locationBox.setAlignment(Pos.CENTER_LEFT);
        
        Label locationIcon = new Label("📍");
        Label locationText = new Label(event.getLocation() != null ? event.getLocation() : "Lieu non spécifié");
        locationText.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
        
        locationBox.getChildren().addAll(locationIcon, locationText);
        
        // Date de l'événement avec icône
        HBox dateBox = new HBox(5);
        dateBox.setAlignment(Pos.CENTER_LEFT);
        
        Label dateIcon = new Label("📅");
        
        String dateText = "Date non spécifiée";
        if (event.getDateD() != null) {
            if (event.getDateF() != null && !event.getDateD().equals(event.getDateF())) {
                dateText = event.getDateD().format(formatter) + " au " + event.getDateF().format(formatter);
            } else {
                dateText = event.getDateD().format(formatter);
            }
        }
        
        Label dateLabel = new Label(dateText);
        dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");
        
        dateBox.getChildren().addAll(dateIcon, dateLabel);
        
        // Affichage du prix avec icône
        HBox priceBox = new HBox(5);
        priceBox.setAlignment(Pos.CENTER_LEFT);
        
        Label priceIcon = new Label("💰");
        double prix = event.getPrix() != null ? event.getPrix() : 0.0;
        Label priceLabel = new Label(String.format("%.2f DT", prix));
        priceLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");
        
        priceBox.getChildren().addAll(priceIcon, priceLabel);
        
        // Système d'étoiles basé sur le nombre de réservations
        HBox starsBox = new HBox(15);
        starsBox.setAlignment(Pos.CENTER_LEFT);
        
        // Icône d'étoile
        Label starsIcon = new Label("⭐");
        starsIcon.setStyle("-fx-font-size: 18px;");
        
        // Calculer le nombre d'étoiles et générer la chaîne
        int nbEtoiles = calculerEtoiles(event);
        String chaineEtoiles = genererChaineEtoiles(nbEtoiles);
        
        // Libellé pour les étoiles
        Label starsLabel = new Label(chaineEtoiles);
        starsLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #f39c12;");
        
        // Note en texte
        Label ratingLabel = new Label(nbEtoiles + "/5");
        ratingLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d; -fx-padding: 0 0 0 10;");
        
        starsBox.getChildren().addAll(starsIcon, starsLabel, ratingLabel);
        
        // Séparateur
        Separator separator = new Separator();
        separator.setOpacity(0.3);
        
        // Boutons d'action
        HBox actionButtons = new HBox(10);
        actionButtons.setAlignment(Pos.CENTER);
        
        Button reserveButton = new Button("Réserver");
        reserveButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        reserveButton.setPrefWidth(120);
        
        Button detailsButton = new Button("Détails");
        detailsButton.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        detailsButton.setPrefWidth(120);
        
        // Ajouter les actions aux boutons
        reserveButton.setOnAction(e -> {
            showReservationDialog(event);
            e.consume();
        });
        
        detailsButton.setOnAction(e -> {
            showEventDetails(event);
            e.consume();
        });
        
        // Désactiver le bouton de réservation si l'événement est complet
        if (event.getNbPlace() <= 0) {
            reserveButton.setDisable(true);
            reserveButton.setStyle("-fx-background-color: #bdc3c7; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        }
        
        actionButtons.getChildren().addAll(detailsButton, reserveButton);
        
        // Ajouter tous les éléments à la carte
        card.getChildren().addAll(imageContainer, titleLabel, locationBox, dateBox, priceBox, starsBox, separator, actionButtons);
        
        return card;
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
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de filtrage", 
                     "Une erreur est survenue lors du filtrage des événements: " + e.getMessage());
        }
    }
    
    private void showSessionsModal(Evenement event) {
        try {
            System.out.println("Affichage des sessions pour l'événement " + event.getId());
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionSessions.fxml"));
            Parent root = loader.load();
            
            GestionSessionsController controller = loader.getController();
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
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur d'affichage", 
                     "Impossible d'afficher les sessions de l'événement : " + e.getMessage());
        }
    }
    
    private void showReservationDialog(Evenement event) {
        try {
            System.out.println("Affichage du dialogue de réservation pour l'événement " + event.getId());
            
            // Vérifier si l'événement est complet
            if (event.getNbPlace() <= 0) {
                showAlert(Alert.AlertType.INFORMATION, 
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
            dialogPane.setStyle("-fx-background-color: white;");
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
            
            // Image de l'événement
            ImageView eventImage = new ImageView();
            eventImage.setFitWidth(220);
            eventImage.setFitHeight(130);
            eventImage.setPreserveRatio(true);
            
            // Charger l'image
            try {
                String imagePath = event.getImage();
                if (imagePath != null && !imagePath.isEmpty()) {
                    File imageFile = new File(imagePath);
                    if (imageFile.exists()) {
                        eventImage.setImage(new Image(imageFile.toURI().toString()));
                    } else {
                        // Essayer dans le dossier d'images
                        String uploadDir = "C:\\xampp\\htdocs\\imageP\\";
                        File uploadedImage = new File(uploadDir + imagePath);
                        if (uploadedImage.exists()) {
                            eventImage.setImage(new Image(uploadedImage.toURI().toString()));
                        } else {
                            // Image par défaut
                            URL defaultImageUrl = getClass().getResource("/images/default-session.png");
                            if (defaultImageUrl != null) {
                                eventImage.setImage(new Image(defaultImageUrl.toExternalForm()));
                            }
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Erreur de chargement d'image: " + e.getMessage());
            }
            
            // Clip pour arrondir les coins de l'image
            Rectangle clip = new Rectangle(eventImage.getFitWidth(), eventImage.getFitHeight());
            clip.setArcWidth(20);
            clip.setArcHeight(20);
            eventImage.setClip(clip);
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
            
            detailsBox.getChildren().addAll(dateBox, locationBox);
            
            // Badge de disponibilité
            Label availabilityLabel = new Label(event.getNbPlace() + " places disponibles");
            availabilityLabel.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 5 15; -fx-background-radius: 20;");
            
            // Assembler la partie info événement
            eventInfoBox.getChildren().addAll(eventImage, eventTitleLabel, detailsBox, availabilityLabel);
            
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
            
            // Ajout des écouteurs d'événements
            decreaseButton.setOnAction(e -> {
                int currentValue = placesSpinner.getValue();
                if (currentValue > 1) {
                    placesSpinner.getValueFactory().setValue(currentValue - 1);
                }
            });
            
            increaseButton.setOnAction(e -> {
                int currentValue = placesSpinner.getValue();
                if (currentValue < event.getNbPlace()) {
                    placesSpinner.getValueFactory().setValue(currentValue + 1);
                }
            });
            
            spinnerBox.getChildren().addAll(decreaseButton, placesSpinner, increaseButton);
            
            // Assemblage de la partie formulaire
            formBox.getChildren().addAll(formTitle, placesLabel, spinnerBox);
            
            // Disposition horizontale des deux parties
            HBox contentLayout = new HBox(20, eventInfoBox, formBox);
            contentLayout.setAlignment(Pos.CENTER);
            
            mainContent.setCenter(contentLayout);
            
            // Définir le contenu du dialogue
            dialog.getDialogPane().setContent(mainContent);
            
            // Styler les boutons du dialogue
            Button reserveButton = (Button) dialog.getDialogPane().lookupButton(reserveButtonType);
            if (reserveButton != null) {
                reserveButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
            }
            
            Button cancelButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
            if (cancelButton != null) {
                cancelButton.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white;");
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
                        // Effectuer la réservation
                        boolean success = evenementService.reserverPlace(event.getId(), places);
                        
                        if (success) {
                            // Mettre à jour l'affichage
                            event.setNbPlace(event.getNbPlace() - places);
                            
                            // Rafraîchir l'affichage
                            filterEvents();
                            
                            // Confirmation colorée et détaillée
                            Dialog<Void> confirmationDialog = new Dialog<>();
                            confirmationDialog.setTitle("Réservation confirmée");
                            confirmationDialog.setHeaderText(null);
                            
                            // Contenu de confirmation
                            VBox confirmContent = new VBox(15);
                            confirmContent.setPadding(new Insets(20));
                            confirmContent.setStyle("-fx-background-color: white;");
                            
                            // Icône de succès
                            Label successIcon = new Label("✅");
                            successIcon.setStyle("-fx-font-size: 48px; -fx-alignment: center;");
                            
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
                            
                            detailsContent.getChildren().addAll(eventNameDetail, placesDetail, dateDetail);
                            
                            // Assembler le contenu
                            HBox iconBox = new HBox(successIcon);
                            iconBox.setAlignment(Pos.CENTER);
                            
                            confirmContent.getChildren().addAll(iconBox, successMessage, detailsContent);
                            
                            // Configurer le dialogue
                            confirmationDialog.getDialogPane().setContent(confirmContent);
                            confirmationDialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
                            
                            Button okButton = (Button) confirmationDialog.getDialogPane().lookupButton(ButtonType.OK);
                            if (okButton != null) {
                                okButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;");
                            }
                            
                            confirmationDialog.showAndWait();
                        } else {
                            showAlert(Alert.AlertType.ERROR, 
                                     "Erreur de réservation", 
                                     "La réservation a échoué", 
                                     "Une erreur est survenue lors de la réservation.");
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Erreur lors de la réservation: " + e.getMessage());
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, 
                             "Erreur", 
                             "Erreur lors de la réservation", 
                             "Une erreur est survenue : " + e.getMessage());
                }
            });
        } catch (Exception e) {
            System.err.println("Erreur lors de l'affichage du dialogue de réservation: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, 
                     "Erreur", 
                     "Erreur lors de la réservation", 
                     "Une erreur est survenue : " + e.getMessage());
        }
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
            javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.millis(300));
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
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de navigation", 
                     "Impossible de retourner à la page principale : " + e.getMessage());
                }
            });
            pause.play();
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la navigation: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de navigation", 
                    "Impossible de retourner à la page principale : " + e.getMessage());
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String header, String content) {
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
            
            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Météo pour " + location);
            dialog.setHeaderText("Informations météorologiques");
            
            // Augmenter la taille de la boîte de dialogue
            DialogPane dialogPane = dialog.getDialogPane();
            dialogPane.setPrefWidth(700);
            dialogPane.setPrefHeight(500);
            dialogPane.setStyle("-fx-background-color: white;");
            
            VBox content = new VBox(15);
            content.setPadding(new javafx.geometry.Insets(20));
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
                    // Nouvelle clé API valide (API gratuite d'OpenWeatherMap)
                    String apiKey = "4d8fb5b93d4af21d66a2948710284366";
                    String encodedLocation = java.net.URLEncoder.encode(location, "UTF-8");
                    String url = "https://api.openweathermap.org/data/2.5/weather?q=" + encodedLocation + "&units=metric&lang=fr&appid=" + apiKey;
                    
                    System.out.println("URL API météo: " + url);
                
                    java.net.HttpURLConnection connection = (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
                    connection.setRequestMethod("GET");
                    connection.setConnectTimeout(10000); // Augmenter le délai à 10 secondes
                    connection.setReadTimeout(10000); // Augmenter le délai à 10 secondes
                
                    int responseCode = connection.getResponseCode();
                    System.out.println("Code de réponse API météo: " + responseCode);
                
                    if (responseCode == 200) {
                        java.io.BufferedReader reader = new java.io.BufferedReader(
                                new java.io.InputStreamReader(connection.getInputStream()));
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
                            content.getChildren().clear();
                            
                            // En-tête avec la ville et le pays
                            Label locationLabel = new Label(cityName + ", " + country);
                            locationLabel.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 5, 0, 0, 1);");
                            
                            // Section principale: température et icône
                            HBox mainWeatherInfo = new HBox(30);
                            mainWeatherInfo.setAlignment(javafx.geometry.Pos.CENTER);
                            mainWeatherInfo.setPadding(new Insets(20));
                            
                            // Ajouter une icône météo
                            ImageView weatherIconView = new ImageView();
                            try {
                                // Utiliser l'icône réelle d'OpenWeatherMap
                                String iconUrl = "http://openweathermap.org/img/wn/" + weatherIcon + "@4x.png";
                                System.out.println("URL icône météo: " + iconUrl);
                                Image image = new Image(iconUrl, true); // true pour chargement en arrière-plan
                                weatherIconView.setImage(image);
                                weatherIconView.setFitWidth(150);
                                weatherIconView.setFitHeight(150);
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
                            
                            mainWeatherInfo.getChildren().addAll(weatherIconView, tempBox);
                            
                            Separator separator = new Separator();
                            separator.setStyle("-fx-background-color: white; -fx-opacity: 0.7;");
                            
                            // Panneau pour les détails météo supplémentaires
                            GridPane weatherDetails = new GridPane();
                            weatherDetails.setHgap(50);
                            weatherDetails.setVgap(30);
                            weatherDetails.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-background-radius: 15; -fx-padding: 20;");
                            weatherDetails.setPadding(new javafx.geometry.Insets(20));
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
                        });
                    } else {
                        System.err.println("Erreur API météo: code " + responseCode);
                        javafx.application.Platform.runLater(() -> {
                            content.getChildren().clear();
                            
                            Label errorLabel = new Label("Impossible de récupérer les données météo");
                            errorLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 20px;");
                            
                            Label detailsLabel = new Label("Erreur " + responseCode + " : Vérifiez le nom de la ville ou votre connexion internet");
                            detailsLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
                            detailsLabel.setWrapText(true);
                            
                            content.getChildren().addAll(errorLabel, detailsLabel);
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
                        
                        content.getChildren().addAll(errorLabel, detailsLabel);
                    });
                }
            }).start();
        
        } catch (Exception e) {
            System.err.println("Erreur lors de l'affichage du dialogue météo: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, 
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
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le navigateur", 
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
            Circle locationDot = new Circle(6, javafx.scene.paint.Color.RED);
            Label locationItemLabel = new Label("Emplacement");
            locationItem.getChildren().addAll(locationDot, locationItemLabel);
            
            HBox poiItem = new HBox(10);
            poiItem.setAlignment(Pos.CENTER_LEFT);
            Circle poiDot = new Circle(6, javafx.scene.paint.Color.BLUE);
            Label poiItemLabel = new Label("Point d'intérêt");
            poiItem.getChildren().addAll(poiDot, poiItemLabel);
            
            HBox roadItem = new HBox(10);
            roadItem.setAlignment(Pos.CENTER_LEFT);
            Rectangle roadRect = new Rectangle(12, 4);
            roadRect.setFill(javafx.scene.paint.Color.GRAY);
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
            showAlert(Alert.AlertType.ERROR, 
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
        javafx.scene.paint.Color bgColor = isDarkMode ? javafx.scene.paint.Color.rgb(52, 58, 64) : javafx.scene.paint.Color.rgb(240, 240, 240);
        javafx.scene.paint.Color roadColor = isDarkMode ? javafx.scene.paint.Color.rgb(150, 150, 150) : javafx.scene.paint.Color.rgb(200, 200, 200);
        javafx.scene.paint.Color roadOutlineColor = isDarkMode ? javafx.scene.paint.Color.rgb(180, 180, 180) : javafx.scene.paint.Color.rgb(150, 150, 150);
        javafx.scene.paint.Color waterColor = isDarkMode ? javafx.scene.paint.Color.rgb(70, 130, 180) : javafx.scene.paint.Color.rgb(173, 216, 230);
        javafx.scene.paint.Color parkColor = isDarkMode ? javafx.scene.paint.Color.rgb(53, 94, 59) : javafx.scene.paint.Color.rgb(144, 238, 144);
        javafx.scene.paint.Color textColor = isDarkMode ? javafx.scene.paint.Color.WHITE : javafx.scene.paint.Color.BLACK;
        
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
        locationMarker.setFill(javafx.scene.paint.Color.RED);
        locationMarker.setStroke(javafx.scene.paint.Color.WHITE);
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
    private VBox createPOI(String name, double x, double y, javafx.scene.paint.Color textColor) {
        VBox poi = new VBox(3);
        poi.setAlignment(Pos.CENTER);
        poi.setLayoutX(x - 15);
        poi.setLayoutY(y - 15);
        
        Circle poiCircle = new Circle(6);
        poiCircle.setFill(javafx.scene.paint.Color.BLUE);
        poiCircle.setStroke(javafx.scene.paint.Color.WHITE);
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
                    File imageFile = new File(imagePath);
                    if (imageFile.exists()) {
                        eventImage.setImage(new Image(imageFile.toURI().toString()));
                    } else {
                        // Image par défaut
                        URL defaultImageUrl = getClass().getResource("/images/default-event.jpg");
                        if (defaultImageUrl != null) {
                            eventImage.setImage(new Image(defaultImageUrl.toExternalForm()));
                        }
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
                        Utils.TTSService ttsService = Utils.TTSService.getInstance();
                        
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
                        showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de lecture vocale", 
                                 "Impossible de lire la description: " + ex.getMessage());
                        ttsButton.setGraphic(new Label("🔊"));
                        ttsButton.setDisable(false);
                    }
                } else {
                    showAlert(Alert.AlertType.WARNING, "Pas de description", 
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
                showWeatherDialog(event.getLocation());
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
                Alert.AlertType.ERROR,
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
                Alert.AlertType.ERROR,
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
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
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
                    Alert.AlertType.INFORMATION,
                    "Succès",
                    "Événement supprimé",
                    "L'événement a été supprimé avec succès."
                );
                
                // Recharger la liste des événements
                loadEvents();
                
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(
                    Alert.AlertType.ERROR,
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
    
    /**
     * Méthode pour afficher la vue des réservations de l'utilisateur
     */
    @FXML
    private void handleShowReservations() {
        try {
            System.out.println("Chargement de la vue des réservations...");
            
            // Charger la vue des réservations
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MyReservations.fxml"));
            Parent reservationsView = loader.load();
            
            // Créer la scène
            Scene scene = new Scene(reservationsView);
            
            // Récupérer la fenêtre actuelle
            Stage stage = (Stage) btnMyReservations.getScene().getWindow();
            
            // Appliquer le style professionnel à la nouvelle scène
            MainStyleFixer.applyProfessionalStyle(scene);
            
            // Changer la scène
            stage.setScene(scene);
            stage.show();
            
            System.out.println("Vue des réservations chargée avec succès");
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la vue des réservations: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, 
                    "Erreur de navigation", 
                    "Impossible d'afficher la vue des réservations", 
                    e.getMessage());
        }
    }
}