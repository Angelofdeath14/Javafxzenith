package Controller;

import Entity.Evenement;
import Entity.Session;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Insets;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.stage.Stage;
import javafx.scene.Scene;
import services.EvenementService;
import services.SessionService;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;
import java.sql.SQLException;
import java.util.Optional;
import java.io.File;
import java.time.format.DateTimeFormatter;

public class EvenementsController implements Initializable {
    @FXML private FlowPane eventsContainer;
    @FXML private FlowPane featuredEventsContainer;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterCategory;
    @FXML private ComboBox<String> filterDate;
    
    @FXML private VBox eventCardTemplate;
    @FXML private VBox sessionModalTemplate;
    @FXML private HBox sessionCardTemplate;
    
    // Vues principales
    @FXML private ScrollPane userView;
    @FXML private VBox adminListView;
    @FXML private VBox adminSessionsView;
    @FXML private VBox adminAddView;
    
    // Tableaux pour admin
    @FXML private TableView<Evenement> eventsTable;
    @FXML private TableView<Session> sessionsTable;
    
    // Labels de statistiques
    @FXML private Label lblTotalEvents;
    @FXML private Label lblActiveSessions;
    
    private EvenementService evenementService;
    private SessionService sessionService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            evenementService = new EvenementService();
            sessionService = new SessionService();
            
            // Configuration des filtres
            setupFilters();
            
            // Chargement des événements
            loadEvents();
            
            // Chargement des événements à la une
            loadFeaturedEvents();
            
            // Configuration des tableaux admin
            setupEventsTable();
            setupSessionsTable();
            
            // Mise à jour des statistiques
            updateStatistics();
            
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, 
                     "Erreur de connexion", 
                     "Impossible de se connecter à la base de données", 
                     e.getMessage());
        }
    }

    // Méthodes pour changer de vue
    @FXML
    public void showUserView() {
        userView.setVisible(true);
        adminListView.setVisible(false);
        adminSessionsView.setVisible(false);
        adminAddView.setVisible(false);
    }
    
    @FXML
    public void showEventsListView() {
        userView.setVisible(false);
        adminListView.setVisible(true);
        adminSessionsView.setVisible(false);
        adminAddView.setVisible(false);
        
        // Actualisation de la liste
        loadEventsTable();
    }
    
    @FXML
    public void showSessionsListView() {
        userView.setVisible(false);
        adminListView.setVisible(false);
        adminSessionsView.setVisible(true);
        adminAddView.setVisible(false);
        
        // Actualisation de la liste
        loadSessionsTable();
    }
    
    @FXML
    public void showAddEventView() {
        userView.setVisible(false);
        adminListView.setVisible(false);
        adminSessionsView.setVisible(false);
        adminAddView.setVisible(true);
    }
    
    // Méthodes pour gérer les tableaux admin
    private void setupEventsTable() {
        // Configuration des colonnes du tableau d'événements
        // Cette méthode sera implémentée plus tard
    }
    
    private void setupSessionsTable() {
        // Configuration des colonnes du tableau de sessions
        // Cette méthode sera implémentée plus tard
    }
    
    private void loadEventsTable() {
        try {
            eventsTable.getItems().clear();
            List<Evenement> events = evenementService.getAllEvents();
            eventsTable.getItems().addAll(events);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement", 
                     "Une erreur est survenue lors du chargement des événements.");
        }
    }
    
    private void loadSessionsTable() {
        try {
            sessionsTable.getItems().clear();
            List<Session> sessions = sessionService.getAllSessions();
            sessionsTable.getItems().addAll(sessions);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement", 
                     "Une erreur est survenue lors du chargement des sessions.");
        }
    }
    
    private void updateStatistics() {
        try {
            // Mise à jour du nombre total d'événements
            int totalEvents = evenementService.countEvents();
            lblTotalEvents.setText(String.valueOf(totalEvents));
            
            // Mise à jour du nombre de sessions actives
            int activeSessions = sessionService.countActiveSessions();
            lblActiveSessions.setText(String.valueOf(activeSessions));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setupFilters() {
        // Configuration des catégories
        filterCategory.getItems().addAll("Tous", "Concert", "Théâtre", "Exposition", "Sport", "Conférence");
        filterCategory.setValue("Tous");
        
        // Configuration des dates
        filterDate.getItems().addAll("Tous", "Aujourd'hui", "Cette semaine", "Ce mois-ci");
        filterDate.setValue("Tous");
        
        // Configurez les actions de filtrage
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterEvents();
        });
        
        filterCategory.setOnAction(event -> filterEvents());
        filterDate.setOnAction(event -> filterEvents());
    }

    private void loadEvents() {
        try {
            // Vider le conteneur
            eventsContainer.getChildren().clear();
            
            List<Evenement> events = evenementService.getAllEvents();
            
            // Ajouter chaque événement
            for (Evenement event : events) {
                VBox eventCard = createEventCard(event);
                eventsContainer.getChildren().add(eventCard);
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement", 
                     "Une erreur est survenue lors du chargement des événements.");
        }
    }
    
    private void loadFeaturedEvents() {
        try {
            // Vider le conteneur
            featuredEventsContainer.getChildren().clear();
            
            // Dans un cas réel, vous pourriez avoir une méthode pour obtenir seulement les événements à la une
            // Ici nous prenons simplement les 3 premiers événements
            List<Evenement> events = evenementService.getAllEvents();
            int count = 0;
            
            for (Evenement event : events) {
                if (count < 3) {
                    VBox eventCard = createEventCard(event);
                    featuredEventsContainer.getChildren().add(eventCard);
                    count++;
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement", 
                     "Une erreur est survenue lors du chargement des événements à la une.");
        }
    }

    private VBox createEventCard(Evenement event) {
        try {
            // Créer une copie du template
            VBox card = new VBox();
            card.setPrefWidth(350);
            card.setPrefHeight(450);
            card.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                         "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 10);");
            
            // Bannière avec image
            StackPane banner = new StackPane();
            
            // Rectangle pour l'effet d'assombrissement
            Rectangle overlay = new Rectangle(350, 200);
            overlay.setFill(Color.BLACK);
            overlay.setOpacity(0.1);
            
            // Image de l'événement
            ImageView imageView = new ImageView();
            if (event.getImage() != null && !event.getImage().isEmpty()) {
                try {
                    String fullPath = "C:\\xampp\\htdocs\\imageP\\" + event.getImage();
                    File file = new File(fullPath);
                    if (file.exists()) {
                        Image image = new Image(file.toURI().toString());
                        imageView.setImage(image);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            imageView.setFitWidth(350);
            imageView.setFitHeight(200);
            imageView.setPreserveRatio(true);
            
            // Étiquette de catégorie
            HBox categoryBox = new HBox();
            categoryBox.setAlignment(Pos.TOP_LEFT);
            StackPane.setAlignment(categoryBox, Pos.TOP_LEFT);
            
            Label categoryLabel = new Label(event.getType());
            categoryLabel.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; " +
                                 "-fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 0 0 5 0;");
            categoryBox.getChildren().add(categoryLabel);
            
            banner.getChildren().addAll(imageView, overlay, categoryBox);
            
            // Contenu de la carte
            VBox content = new VBox(10);
            content.setPadding(new Insets(15));
            content.setStyle("-fx-padding: 15;");
            
            // Titre
            Label titleLabel = new Label(event.getTitre());
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
            titleLabel.setWrapText(true);
            
            // Date
            HBox dateBox = new HBox(8);
            dateBox.setAlignment(Pos.CENTER_LEFT);
            
            ImageView calendarIcon = new ImageView();
            try {
                calendarIcon.setImage(new Image(getClass().getResourceAsStream("/images/calendar-icon.png")));
            } catch (Exception e) {
                // Si l'icône n'est pas trouvée, on continue sans elle
            }
            calendarIcon.setFitWidth(16);
            calendarIcon.setFitHeight(16);
            
            Label dateLabel = new Label(
                event.getDateD().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + 
                " - " + 
                event.getDateF().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            );
            dateLabel.setStyle("-fx-text-fill: #7f8c8d;");
            
            dateBox.getChildren().addAll(calendarIcon, dateLabel);
            
            // Lieu
            HBox locationBox = new HBox(8);
            locationBox.setAlignment(Pos.CENTER_LEFT);
            
            ImageView locationIcon = new ImageView();
            try {
                locationIcon.setImage(new Image(getClass().getResourceAsStream("/images/location-icon.png")));
            } catch (Exception e) {
                // Si l'icône n'est pas trouvée, on continue sans elle
            }
            locationIcon.setFitWidth(16);
            locationIcon.setFitHeight(16);
            
            Label locationLabel = new Label(event.getLocation());
            locationLabel.setStyle("-fx-text-fill: #7f8c8d;");
            
            locationBox.getChildren().addAll(locationIcon, locationLabel);
            
            // Description
            String shortDesc = event.getDescription();
            if (shortDesc != null && shortDesc.length() > 100) {
                shortDesc = shortDesc.substring(0, 97) + "...";
            }
            Label descLabel = new Label(shortDesc);
            descLabel.setStyle("-fx-text-fill: #7f8c8d;");
            descLabel.setWrapText(true);
            descLabel.setMaxHeight(60);
            
            // Places disponibles
            HBox capacityBox = new HBox(5);
            capacityBox.setAlignment(Pos.CENTER_LEFT);
            
            Label capacityTitleLabel = new Label("Places disponibles:");
            capacityTitleLabel.setStyle("-fx-text-fill: #7f8c8d;");
            
            Label capacityValueLabel = new Label(String.valueOf(event.getNbPlace()));
            capacityValueLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
            
            capacityBox.getChildren().addAll(capacityTitleLabel, capacityValueLabel);
            
            // Espace flexible
            Region spacer = new Region();
            VBox.setVgrow(spacer, Priority.ALWAYS);
            
            // Boutons d'action
            HBox buttonBox = new HBox(10);
            buttonBox.setAlignment(Pos.CENTER);
            
            Button viewSessionsButton = new Button("Voir les sessions");
            viewSessionsButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; " +
                                     "-fx-font-weight: bold; -fx-min-width: 150px; -fx-background-radius: 5;");
            viewSessionsButton.setOnAction(e -> showSessionsModal(event));
            
            Button reserveButton = new Button("Réserver");
            reserveButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; " +
                                "-fx-font-weight: bold; -fx-min-width: 110px; -fx-background-radius: 5;");
            reserveButton.setOnAction(e -> showReservationDialog(event));
            
            buttonBox.getChildren().addAll(viewSessionsButton, reserveButton);
            
            // Assembler tous les éléments
            content.getChildren().addAll(
                titleLabel, dateBox, locationBox, descLabel, capacityBox, spacer, buttonBox
            );
            
            // Assembler la carte complète
            card.getChildren().addAll(banner, content);
            
            return card;
        } catch (Exception e) {
            e.printStackTrace();
            return new VBox(); // Retourner une carte vide en cas d'erreur
        }
    }

    private void showSessionsModal(Evenement event) {
        try {
            // Créer une copie du template de modal
            VBox modal = new VBox();
            modal.setPrefWidth(600);
            modal.setPrefHeight(500);
            modal.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                         "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 20, 0, 0, 20);");
            
            // En-tête du modal
            VBox header = new VBox();
            header.setStyle("-fx-background-color: #3498db; -fx-padding: 15; -fx-background-radius: 10 10 0 0;");
            
            Label titleLabel = new Label("Sessions de " + event.getTitre());
            titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");
            
            header.getChildren().add(titleLabel);
            
            // Contenu avec les sessions
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: transparent;");
            VBox.setVgrow(scrollPane, Priority.ALWAYS);
            
            VBox sessionsContainer = new VBox(15);
            sessionsContainer.setPadding(new Insets(20));
            
            // Récupérer les sessions de l'événement
            List<Session> sessions = sessionService.getSessionsByEvent(event.getId());
            
            if (sessions.isEmpty()) {
                Label emptyLabel = new Label("Aucune session disponible pour cet événement");
                emptyLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
                sessionsContainer.getChildren().add(emptyLabel);
            } else {
                // Ajouter chaque session
                for (Session session : sessions) {
                    HBox sessionCard = createSessionCard(session);
                    sessionsContainer.getChildren().add(sessionCard);
                }
            }
            
            scrollPane.setContent(sessionsContainer);
            
            // Pied de page avec bouton de fermeture
            HBox footer = new HBox();
            footer.setAlignment(Pos.CENTER_RIGHT);
            footer.setPadding(new Insets(15));
            footer.setStyle("-fx-padding: 15; -fx-background-color: #f5f5f5; -fx-background-radius: 0 0 10 10;");
            
            Button closeButton = new Button("Fermer");
            closeButton.setStyle("-fx-background-color: #7f8c8d; -fx-text-fill: white;");
            
            footer.getChildren().add(closeButton);
            
            // Assembler le modal
            modal.getChildren().addAll(header, scrollPane, footer);
            
            // Créer et afficher la fenêtre
            Stage modalStage = new Stage();
            modalStage.initModality(Modality.APPLICATION_MODAL);
            modalStage.initStyle(StageStyle.TRANSPARENT);
            
            Scene scene = new Scene(modal);
            scene.setFill(Color.TRANSPARENT);
            
            modalStage.setScene(scene);
            
            // Configurer le bouton de fermeture
            closeButton.setOnAction(e -> modalStage.close());
            
            modalStage.showAndWait();
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur d'affichage", 
                     "Impossible d'afficher les sessions de l'événement.");
        }
    }

    private HBox createSessionCard(Session session) {
        try {
            // Créer une copie du template de carte de session
            HBox card = new HBox(15);
            card.setPadding(new Insets(15));
            card.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 5; " +
                        "-fx-border-color: #e0e0e0; -fx-border-radius: 5;");
            
            // Image de la session
            ImageView imageView = new ImageView();
            if (session.getImage() != null && !session.getImage().isEmpty()) {
                try {
                    String fullPath = "C:\\xampp\\htdocs\\imageP\\" + session.getImage();
                    File file = new File(fullPath);
                    if (file.exists()) {
                        Image image = new Image(file.toURI().toString());
                        imageView.setImage(image);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            imageView.setFitWidth(80);
            imageView.setFitHeight(80);
            imageView.setPreserveRatio(true);
            
            // Informations de la session
            VBox info = new VBox(5);
            HBox.setHgrow(info, Priority.ALWAYS);
            
            // Titre
            Label titleLabel = new Label(session.getTitre());
            titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
            
            // Date et heure
            String dateTime = "";
            if (session.getStartTime() != null && session.getEndTime() != null) {
                dateTime = "Le " + session.getStartTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                          " de " + session.getStartTime().format(DateTimeFormatter.ofPattern("HH:mm")) +
                          " à " + session.getEndTime().format(DateTimeFormatter.ofPattern("HH:mm"));
            }
            Label dateTimeLabel = new Label(dateTime);
            dateTimeLabel.setStyle("-fx-text-fill: #3498db;");
            
            // Description
            Label descLabel = new Label(session.getDescription());
            descLabel.setStyle("-fx-text-fill: #7f8c8d;");
            descLabel.setWrapText(true);
            
            // Places disponibles
            HBox capacityBox = new HBox(10);
            capacityBox.setAlignment(Pos.CENTER_LEFT);
            
            Label placesLabel = new Label("Places:");
            placesLabel.setStyle("-fx-text-fill: #7f8c8d;");
            
            Label capacityLabel = new Label(String.valueOf(session.getCapacity()));
            capacityLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
            
            capacityBox.getChildren().addAll(placesLabel, capacityLabel);
            
            info.getChildren().addAll(titleLabel, dateTimeLabel, descLabel, capacityBox);
            
            // Bouton de réservation
            Button reserveButton = new Button("Réserver");
            reserveButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; " +
                               "-fx-font-weight: bold; -fx-min-width: 100px;");
            reserveButton.setOnAction(e -> handleSessionReservation(session));
            
            // Assembler la carte
            card.getChildren().addAll(imageView, info, reserveButton);
            
            return card;
        } catch (Exception e) {
            e.printStackTrace();
            return new HBox(); // Retourner une carte vide en cas d'erreur
        }
    }

    private void showReservationDialog(Evenement event) {
        try {
            // Afficher la liste des sessions pour cet événement
            showSessionsModal(event);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de réservation", 
                     "Une erreur est survenue lors de la réservation.");
        }
    }

    private void handleSessionReservation(Session session) {
        try {
            TextInputDialog dialog = new TextInputDialog("1");
            dialog.setTitle("Réserver une place");
            dialog.setHeaderText("Réservation pour la session : " + session.getTitre());
            dialog.setContentText("Nombre de places à réserver :");

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                try {
                    int places = Integer.parseInt(result.get());
                    if (places <= 0) {
                        showAlert(Alert.AlertType.ERROR, "Erreur", "Nombre invalide", 
                                 "Le nombre de places doit être supérieur à 0.");
                        return;
                    }
                    
                    if (places > session.getCapacity()) {
                        showAlert(Alert.AlertType.ERROR, "Erreur", "Places insuffisantes", 
                                 "Il n'y a pas assez de places disponibles.");
                        return;
                    }
                    
                    // Réduire le nombre de places disponibles
                    session.setCapacity(session.getCapacity() - places);
                    sessionService.updateSession(session);
                    
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Réservation effectuée", 
                             "Vous avez réservé " + places + " place(s) pour cette session.");
                    
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Format invalide", 
                             "Veuillez entrer un nombre valide.");
                }
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de réservation", 
                     "Une erreur est survenue lors de la réservation.");
        }
    }

    private void filterEvents() {
        try {
            // Récupérer les valeurs de filtre
            String searchText = searchField.getText().toLowerCase();
            String category = filterCategory.getValue();
            String date = filterDate.getValue();
            
            // Vider le conteneur
            eventsContainer.getChildren().clear();
            
            // Obtenir tous les événements
            List<Evenement> allEvents = evenementService.getAllEvents();
            
            // Filtrer et ajouter les événements
            for (Evenement event : allEvents) {
                boolean matchesSearch = searchText == null || searchText.isEmpty() ||
                    event.getTitre().toLowerCase().contains(searchText) ||
                    event.getDescription().toLowerCase().contains(searchText) ||
                    event.getType().toLowerCase().contains(searchText) ||
                    event.getLocation().toLowerCase().contains(searchText);
                
                boolean matchesCategory = "Tous".equals(category) || event.getType().equals(category);
                
                boolean matchesDate = true; // Todo: implémenter le filtrage par date
                
                if (matchesSearch && matchesCategory && matchesDate) {
                    VBox eventCard = createEventCard(event);
                    eventsContainer.getChildren().add(eventCard);
                }
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de filtrage", 
                     "Une erreur est survenue lors du filtrage des événements.");
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