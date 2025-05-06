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
import javafx.scene.input.KeyCode;
import services.EvenementService;
import services.SessionService;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;
import java.sql.*;
import java.util.Optional;
import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import Utils.MyDatabase;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

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
            
            // Ajouter un diagnostic visuel
            addDiagnosticMessage("Initialisation du contrôleur...");
            
            // Configuration des filtres
            setupFilters();
            
            // Vérification de la connexion à la base de données
            testDatabaseConnection();
            
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
            addDiagnosticMessage("Erreur de connexion: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, 
                     "Erreur de connexion", 
                     "Impossible de se connecter à la base de données", 
                     e.getMessage());
        } catch (Exception e) {
            addDiagnosticMessage("Erreur d'initialisation: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Nouvelle méthode de test de connexion
    private void testDatabaseConnection() {
        try {
            Connection conn = MyDatabase.getInstance().getConnection();
            boolean isValid = conn != null && !conn.isClosed();
            addDiagnosticMessage("Connexion à la base de données: " + (isValid ? "OK" : "ÉCHEC"));
            
            // Vérification de la table evenement
            if (isValid) {
                try (Statement stmt = conn.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM evenement")) {
                    
                    if (rs.next()) {
                        int count = rs.getInt(1);
                        addDiagnosticMessage("Nombre d'événements en base: " + count);
                    }
                } catch (SQLException e) {
                    addDiagnosticMessage("Erreur lors du comptage des événements: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            addDiagnosticMessage("Erreur de test de connexion: " + e.getMessage());
        }
    }
    
    // Méthode pour ajouter un message de diagnostic visible à l'interface
    private void addDiagnosticMessage(String message) {
        System.out.println("[DIAGNOSTIC] " + message);
        
        // Créer un conteneur si nécessaire pour les messages de diagnostic
        if (eventsContainer.lookup("#diagnosticContainer") == null) {
            VBox diagnosticContainer = new VBox(5);
            diagnosticContainer.setId("diagnosticContainer");
            diagnosticContainer.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 10; -fx-border-color: #e9ecef; -fx-border-width: 1; -fx-border-radius: 5;");
            
            Label titleLabel = new Label("Diagnostic");
            titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            
            diagnosticContainer.getChildren().add(titleLabel);
            eventsContainer.getChildren().add(0, diagnosticContainer);
        }
        
        // Ajouter le message au conteneur
        VBox diagnosticContainer = (VBox) eventsContainer.lookup("#diagnosticContainer");
        Label messageLabel = new Label("- " + message);
        messageLabel.setStyle("-fx-text-fill: #495057;");
        diagnosticContainer.getChildren().add(messageLabel);
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
        try {
            // S'assurer que le tableau est vide
            eventsTable.getColumns().clear();
            
            // Définir les colonnes du tableau d'événements
            TableColumn<Evenement, Number> idColumn = new TableColumn<>("ID");
            idColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()));
            idColumn.setPrefWidth(50);
            
            TableColumn<Evenement, String> titreColumn = new TableColumn<>("Titre");
            titreColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitre()));
            titreColumn.setPrefWidth(150);
            
            TableColumn<Evenement, String> typeColumn = new TableColumn<>("Type");
            typeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getType()));
            typeColumn.setPrefWidth(100);
            
            TableColumn<Evenement, String> lieuColumn = new TableColumn<>("Lieu");
            lieuColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getLocation()));
            lieuColumn.setPrefWidth(120);
            
            TableColumn<Evenement, String> dateDebutColumn = new TableColumn<>("Date début");
            dateDebutColumn.setCellValueFactory(data -> {
                if (data.getValue().getDateD() != null) {
                    return new SimpleStringProperty(data.getValue().getDateD().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                } else {
                    return new SimpleStringProperty("Non définie");
                }
            });
            dateDebutColumn.setPrefWidth(100);
            
            TableColumn<Evenement, String> dateFinColumn = new TableColumn<>("Date fin");
            dateFinColumn.setCellValueFactory(data -> {
                if (data.getValue().getDateF() != null) {
                    return new SimpleStringProperty(data.getValue().getDateF().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                } else {
                    return new SimpleStringProperty("Non définie");
                }
            });
            dateFinColumn.setPrefWidth(100);
            
            TableColumn<Evenement, Number> nbPlaceColumn = new TableColumn<>("Places");
            nbPlaceColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getNbPlace()));
            nbPlaceColumn.setPrefWidth(70);
            
            // Colonne d'actions
            TableColumn<Evenement, Void> actionsColumn = new TableColumn<>("Actions");
            actionsColumn.setCellFactory(param -> new TableCell<>() {
                private final Button editBtn = new Button("Modifier");
                private final Button deleteBtn = new Button("Supprimer");
                private final HBox pane = new HBox(5, editBtn, deleteBtn);
                
                {
                    editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                    deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                    pane.setAlignment(Pos.CENTER);
                    
                    // Configuration des actions des boutons
                    editBtn.setOnAction(eventAction -> {
                        try {
                            Evenement event = getTableView().getItems().get(getIndex());
                            if (event != null) {
                                System.out.println("Éditer l'événement: " + event.getId());
                                // Code pour éditer l'événement - à implémenter plus tard
                                // Pour l'instant, juste afficher un message
                                showAlert(Alert.AlertType.INFORMATION, "Information", "Édition", 
                                         "Édition de l'événement \"" + event.getTitre() + "\" non implémentée.");
                            }
                        } catch (Exception e) {
                            System.err.println("Erreur lors de l'édition de l'événement: " + e.getMessage());
                            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur d'édition", 
                                     "Une erreur est survenue lors de l'édition de l'événement.");
                        }
                    });
                    
                    deleteBtn.setOnAction(eventAction -> {
                        try {
                            if (getIndex() >= 0 && getIndex() < getTableView().getItems().size()) {
                                Evenement eventToDelete = getTableView().getItems().get(getIndex());
                                if (eventToDelete != null) {
                                    // Confirmation avant suppression
                                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                    alert.setTitle("Confirmation de suppression");
                                    alert.setHeaderText("Supprimer l'événement");
                                    alert.setContentText("Êtes-vous sûr de vouloir supprimer l'événement \"" + eventToDelete.getTitre() + "\" ?");
                                    
                                    Optional<ButtonType> result = alert.showAndWait();
                                    if (result.isPresent() && result.get() == ButtonType.OK) {
                                        try {
                                            evenementService.supprimer(eventToDelete.getId());
                                            getTableView().getItems().remove(eventToDelete);
                                            showAlert(Alert.AlertType.INFORMATION, "Succès", "Suppression effectuée", 
                                                     "L'événement a été supprimé avec succès.");
                                        } catch (Exception e) {
                                            System.err.println("Erreur lors de la suppression: " + e.getMessage());
                                            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de suppression", 
                                                    "Impossible de supprimer l'événement: " + e.getMessage());
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            System.err.println("Erreur lors de la suppression: " + e.getMessage());
                            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de suppression", 
                                     "Une erreur est survenue lors de la suppression de l'événement.");
                        }
                    });
                }
                
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : pane);
                }
            });
            actionsColumn.setPrefWidth(150);
            
            // Ajouter les colonnes au tableau
            eventsTable.getColumns().addAll(
                idColumn, 
                titreColumn, 
                typeColumn, 
                lieuColumn, 
                dateDebutColumn, 
                dateFinColumn, 
                nbPlaceColumn, 
                actionsColumn
            );
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la configuration du tableau d'événements: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void setupSessionsTable() {
        try {
            // S'assurer que le tableau est vide
            sessionsTable.getColumns().clear();
            
            // Définir les colonnes du tableau de sessions
            TableColumn<Session, Number> idColumn = new TableColumn<>("ID");
            idColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()));
            idColumn.setPrefWidth(50);
            
            TableColumn<Session, String> titreColumn = new TableColumn<>("Titre");
            titreColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitre()));
            titreColumn.setPrefWidth(150);
            
            TableColumn<Session, String> evenementColumn = new TableColumn<>("Événement");
            evenementColumn.setCellValueFactory(data -> {
                try {
                    // Utiliser la méthode correcte getEvenementId()
                    int eventId = data.getValue().getEvenementId();
                    Evenement evt = evenementService.getOne(eventId);
                    return new SimpleStringProperty(evt != null ? evt.getTitre() : "N/A");
                } catch (Exception e) {
                    return new SimpleStringProperty("Erreur");
                }
            });
            evenementColumn.setPrefWidth(150);
            
            TableColumn<Session, String> dateStartColumn = new TableColumn<>("Début");
            dateStartColumn.setCellValueFactory(data -> {
                if (data.getValue().getStartTime() != null) {
                    return new SimpleStringProperty(data.getValue().getStartTime().format(formatter));
                } else {
                    return new SimpleStringProperty("Non définie");
                }
            });
            dateStartColumn.setPrefWidth(120);
            
            TableColumn<Session, String> dateEndColumn = new TableColumn<>("Fin");
            dateEndColumn.setCellValueFactory(data -> {
                if (data.getValue().getEndTime() != null) {
                    return new SimpleStringProperty(data.getValue().getEndTime().format(formatter));
                } else {
                    return new SimpleStringProperty("Non définie");
                }
            });
            dateEndColumn.setPrefWidth(120);
            
            TableColumn<Session, Number> capacityColumn = new TableColumn<>("Places");
            capacityColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getCapacity()));
            capacityColumn.setPrefWidth(70);
            
            // Colonne d'actions
            TableColumn<Session, Void> actionsColumn = new TableColumn<>("Actions");
            actionsColumn.setCellFactory(param -> new TableCell<>() {
                private final Button editBtn = new Button("Modifier");
                private final Button deleteBtn = new Button("Supprimer");
                private final HBox pane = new HBox(5, editBtn, deleteBtn);
                
                {
                    editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                    deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                    pane.setAlignment(Pos.CENTER);
                    
                    // Configuration des actions des boutons
                    editBtn.setOnAction(eventAction -> {
                        try {
                            if (getIndex() >= 0 && getIndex() < getTableView().getItems().size()) {
                                Session session = getTableView().getItems().get(getIndex());
                                if (session != null) {
                                    System.out.println("Éditer la session: " + session.getId());
                                    // Code pour éditer la session - à implémenter plus tard
                                    // Pour l'instant, juste afficher un message
                                    showAlert(Alert.AlertType.INFORMATION, "Information", "Édition", 
                                             "Édition de la session \"" + session.getTitre() + "\" non implémentée.");
                                }
                            }
                        } catch (Exception e) {
                            System.err.println("Erreur lors de l'édition de la session: " + e.getMessage());
                            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur d'édition", 
                                     "Une erreur est survenue lors de l'édition de la session.");
                        }
                    });
                    
                    deleteBtn.setOnAction(eventAction -> {
                        try {
                            if (getIndex() >= 0 && getIndex() < getTableView().getItems().size()) {
                                Session sessionToDelete = getTableView().getItems().get(getIndex());
                                if (sessionToDelete != null) {
                                    // Confirmation avant suppression
                                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                                    alert.setTitle("Confirmation de suppression");
                                    alert.setHeaderText("Supprimer la session");
                                    alert.setContentText("Êtes-vous sûr de vouloir supprimer la session \"" + sessionToDelete.getTitre() + "\" ?");
                                    
                                    Optional<ButtonType> result = alert.showAndWait();
                                    if (result.isPresent() && result.get() == ButtonType.OK) {
                                        try {
                                            sessionService.deleteSession(sessionToDelete.getId());
                                            getTableView().getItems().remove(sessionToDelete);
                                            showAlert(Alert.AlertType.INFORMATION, "Succès", "Suppression effectuée", 
                                                     "La session a été supprimée avec succès.");
                                        } catch (Exception e) {
                                            System.err.println("Erreur lors de la suppression: " + e.getMessage());
                                            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de suppression", 
                                                    "Impossible de supprimer la session: " + e.getMessage());
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            System.err.println("Erreur lors de la suppression: " + e.getMessage());
                            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de suppression", 
                                     "Une erreur est survenue lors de la suppression de la session.");
                        }
                    });
                }
                
                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : pane);
                }
            });
            actionsColumn.setPrefWidth(150);
            
            // Ajouter les colonnes au tableau
            sessionsTable.getColumns().addAll(
                idColumn, 
                titreColumn, 
                evenementColumn, 
                dateStartColumn, 
                dateEndColumn, 
                capacityColumn, 
                actionsColumn
            );
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la configuration du tableau de sessions: " + e.getMessage());
            e.printStackTrace();
        }
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
            
            System.out.println("Chargement des événements...");
            
            // Essayons d'abord d'accéder directement à la base de données
            Connection conn = MyDatabase.getInstance().getConnection();
            List<Evenement> events = new ArrayList<>();
            
            String query = "SELECT * FROM evenement";
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                
                System.out.println("Exécution directe de la requête SQL: " + query);
                int count = 0;
                
                while (rs.next()) {
                    count++;
                    Evenement evt = new Evenement();
                    evt.setId(rs.getInt("id"));
                    evt.setTitre(rs.getString("titre"));
                    evt.setDescription(rs.getString("description"));
                    
                    // Les champs optionnels avec gestion des nulls
                    String type = rs.getString("type");
                    evt.setType(type != null ? type : "Non catégorisé");
                    
                    String lieu = rs.getString("lieu");
                    evt.setLocation(lieu != null ? lieu : "Lieu non spécifié");
                    
                    try {
                        Timestamp dateD = rs.getTimestamp("date_debut");
                        if (dateD != null) {
                            evt.setDateD(dateD.toLocalDateTime());
                        }
                    } catch (SQLException e) {
                        System.out.println("Pas de date_debut trouvée: " + e.getMessage());
                    }
                    
                    try {
                        Timestamp dateF = rs.getTimestamp("date_fin");
                        if (dateF != null) {
                            evt.setDateF(dateF.toLocalDateTime());
                        }
                    } catch (SQLException e) {
                        System.out.println("Pas de date_fin trouvée: " + e.getMessage());
                    }
                    
                    String image = rs.getString("image");
                    evt.setImage(image != null ? image : "");
                    
                    try {
                        evt.setNbPlace(rs.getInt("nbPlace"));
                    } catch (SQLException e) {
                        evt.setNbPlace(0);
                        System.out.println("Pas de nbPlace trouvé: " + e.getMessage());
                    }
                    
                    try {
                        evt.setPrix(rs.getDouble("prix"));
                    } catch (SQLException e) {
                        evt.setPrix(0.0);
                        System.out.println("Pas de prix trouvé: " + e.getMessage());
                    }
                    
                    events.add(evt);
                    System.out.println("Événement #" + count + " récupéré: ID=" + evt.getId() + ", Titre=" + evt.getTitre());
                }
                
                System.out.println("Nombre total d'événements dans la BD: " + count);
            }
            
            if (events.isEmpty()) {
                System.out.println("Aucun événement trouvé en BD - essai avec le service...");
                // Si aucun événement trouvé en accès direct, essayons avec le service
                events = evenementService.getAllEvents();
                System.out.println("Nombre d'événements via service: " + (events != null ? events.size() : 0));
            }
            
            if (events == null || events.isEmpty()) {
                Label noEventsLabel = new Label("Aucun événement disponible");
                noEventsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d; -fx-padding: 20;");
                eventsContainer.getChildren().add(noEventsLabel);
                return;
            }
            
            // Ajouter chaque événement
            for (Evenement event : events) {
                try {
                    VBox eventCard = createEventCard(event);
                    eventsContainer.getChildren().add(eventCard);
                    System.out.println("Événement ajouté à l'UI: " + event.getId() + " - " + event.getTitre());
                } catch (Exception e) {
                    System.err.println("Erreur lors de la création de la carte pour l'événement " + event.getId() + ": " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des événements: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement", 
                     "Une erreur est survenue lors du chargement des événements: " + e.getMessage());
            
            // Afficher un message dans l'interface même en cas d'erreur
            Label errorLabel = new Label("Impossible de charger les événements: " + e.getMessage());
            errorLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #e74c3c; -fx-padding: 20;");
            eventsContainer.getChildren().add(errorLabel);
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
            System.out.println("Création de la carte pour l'événement ID: " + event.getId() + ", Titre: " + event.getTitre());
            // Créer une carte
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
            boolean imageLoaded = false;
            
            // Essayer de charger l'image de l'événement
            if (event.getImage() != null && !event.getImage().isEmpty()) {
                try {
                    String fullPath = "C:\\xampp\\htdocs\\imageP\\" + event.getImage();
                    System.out.println("Chemin de l'image: " + fullPath);
                    File file = new File(fullPath);
                    if (file.exists()) {
                        System.out.println("Fichier d'image trouvé");
                        Image image = new Image(file.toURI().toString(), true);
                        if (!image.isError()) {
                            imageView.setImage(image);
                            imageLoaded = true;
                            System.out.println("Image chargée avec succès");
                        } else {
                            System.out.println("Erreur lors du chargement de l'image: " + image.getException());
                        }
                    } else {
                        System.out.println("Fichier image introuvable: " + fullPath);
                    }
                } catch (Exception e) {
                    System.err.println("Erreur lors du chargement de l'image: " + e.getMessage());
                }
            }
            
            // Si l'image n'a pas pu être chargée, utiliser une image par défaut
            if (!imageLoaded) {
                try {
                    // Essayer d'abord de charger depuis les ressources
                    Image defaultImage = new Image(getClass().getResourceAsStream("/images/event-default.jpg"));
                    if (defaultImage != null && !defaultImage.isError()) {
                        imageView.setImage(defaultImage);
                        System.out.println("Image par défaut chargée depuis les ressources");
                    } else {
                        // Créer une image de remplacement simple
                        Rectangle placeholder = new Rectangle(350, 200, Color.LIGHTGRAY);
                        StackPane.setAlignment(placeholder, Pos.CENTER);
                        banner.getChildren().add(placeholder);
                        System.out.println("Utilisation d'un placeholder gris");
                    }
                } catch (Exception e) {
                    System.err.println("Erreur lors du chargement de l'image par défaut: " + e.getMessage());
                    // Créer une image de remplacement simple en cas d'erreur
                    Rectangle placeholder = new Rectangle(350, 200, Color.LIGHTGRAY);
                    StackPane.setAlignment(placeholder, Pos.CENTER);
                    banner.getChildren().add(placeholder);
                }
            }
            
            imageView.setFitWidth(350);
            imageView.setFitHeight(200);
            imageView.setPreserveRatio(true);
            
            // Assurer que l'image est toujours centrée
            if (imageView.getImage() != null) {
                StackPane.setAlignment(imageView, Pos.CENTER);
                banner.getChildren().add(imageView);
            }
            
            // Étiquette de catégorie
            HBox categoryBox = new HBox();
            categoryBox.setAlignment(Pos.TOP_LEFT);
            StackPane.setAlignment(categoryBox, Pos.TOP_LEFT);
            
            Label categoryLabel = new Label(event.getType() != null ? event.getType() : "Non catégorisé");
            categoryLabel.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; " +
                                 "-fx-font-weight: bold; -fx-padding: 5 10; -fx-background-radius: 0 0 5 0;");
            categoryBox.getChildren().add(categoryLabel);
            
            banner.getChildren().add(overlay);
            banner.getChildren().add(categoryBox);
            
            // Contenu de la carte
            VBox content = new VBox(10);
            content.setPadding(new Insets(15));
            content.setStyle("-fx-padding: 15;");
            
            // Titre
            Label titleLabel = new Label(event.getTitre() != null ? event.getTitre() : "Sans titre");
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
            
            String dateText = "Dates non définies";
            if (event.getDateD() != null && event.getDateF() != null) {
                dateText = event.getDateD().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + 
                           " - " + 
                           event.getDateF().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            }
            
            Label dateLabel = new Label(dateText);
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
            
            Label locationLabel = new Label(event.getLocation() != null ? event.getLocation() : "Lieu non défini");
            locationLabel.setStyle("-fx-text-fill: #7f8c8d;");
            
            locationBox.getChildren().addAll(locationIcon, locationLabel);
            
            // Description
            String shortDesc = event.getDescription();
            if (shortDesc == null) {
                shortDesc = "Aucune description disponible";
            } else if (shortDesc.length() > 100) {
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
            System.err.println("Erreur lors de la création de la carte d'événement: " + e.getMessage());
            return new VBox(); // Retourner une carte vide en cas d'erreur
        }
    }

    private void showSessionsModal(Evenement event) {
        try {
            // S'assurer que event n'est pas null
            if (event == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Événement invalide", 
                        "Impossible d'afficher les sessions pour un événement non défini.");
                return;
            }
            
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
            
            try {
                // Récupérer les sessions de l'événement
                List<Session> sessions = sessionService.getSessionsByEvent(event.getId());
                
                if (sessions.isEmpty()) {
                    Label emptyLabel = new Label("Aucune session disponible pour cet événement");
                    emptyLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
                    sessionsContainer.getChildren().add(emptyLabel);
                } else {
                    // Ajouter chaque session
                    for (Session session : sessions) {
                        try {
                            HBox sessionCard = createSessionCard(session);
                            sessionsContainer.getChildren().add(sessionCard);
                        } catch (Exception e) {
                            System.err.println("Erreur lors de la création de la carte pour la session: " + e.getMessage());
                        }
                    }
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de la récupération des sessions: " + e.getMessage());
                Label errorLabel = new Label("Erreur lors du chargement des sessions: " + e.getMessage());
                errorLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #e74c3c;");
                sessionsContainer.getChildren().add(errorLabel);
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
            
            try {
                // Créer et afficher la fenêtre
                Stage modalStage = new Stage();
                modalStage.initModality(Modality.APPLICATION_MODAL);
                
                // Utiliser un style standard plutôt que transparent qui peut causer des problèmes
                // modalStage.initStyle(StageStyle.TRANSPARENT);
                modalStage.initStyle(StageStyle.DECORATED);
                modalStage.setTitle("Sessions de " + event.getTitre());
                
                Scene scene = new Scene(modal);
                
                // Ne pas utiliser de fond transparent qui peut causer des problèmes
                // scene.setFill(Color.TRANSPARENT);
                
                modalStage.setScene(scene);
                
                // Configurer le bouton de fermeture
                closeButton.setOnAction(e -> modalStage.close());
                
                // Ajouter un gestionnaire pour fermer la fenêtre avec la touche ESC
                scene.setOnKeyPressed(e -> {
                    if (e.getCode() == KeyCode.ESCAPE) {
                        modalStage.close();
                    }
                });
                
                // Montrer la fenêtre de manière non bloquante
                modalStage.show();
                
                // Centrer la fenêtre sur l'écran
                modalStage.centerOnScreen();
            } catch (Exception e) {
                System.err.println("Erreur lors de l'affichage de la fenêtre: " + e.getMessage());
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur d'affichage", 
                        "Impossible d'afficher la fenêtre des sessions: " + e.getMessage());
            }
            
        } catch (Exception e) {
            System.err.println("Erreur générale lors de l'affichage des sessions: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur d'affichage", 
                     "Impossible d'afficher les sessions de l'événement: " + e.getMessage());
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
            // Vérifier si la session existe et a des places disponibles
            if (session == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Session invalide", 
                        "Impossible de trouver la session spécifiée.");
                return;
            }
            
            if (session.getCapacity() <= 0) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Places épuisées", 
                        "Il n'y a plus de places disponibles pour cette session.");
                return;
            }
            
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
                                "Il n'y a pas assez de places disponibles. Places restantes: " + session.getCapacity());
                        return;
                    }
                    
                    // Réduire le nombre de places disponibles
                    boolean success = sessionService.reserveSeats(session.getId(), places);
                    
                    if (success) {
                        // Mettre à jour la session locale pour l'affichage
                        session.setCapacity(session.getCapacity() - places);
                        
                        showAlert(Alert.AlertType.INFORMATION, "Succès", "Réservation effectuée", 
                                "Vous avez réservé " + places + " place(s) pour la session \"" + session.getTitre() + "\".\n\n" +
                                "Places restantes: " + session.getCapacity());
                        
                        // Rafraîchir la liste des événements pour refléter les changements
                        loadEvents();
                        loadFeaturedEvents();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de réservation", 
                                "La réservation n'a pas pu être effectuée. Veuillez réessayer.");
                    }
                    
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Format invalide", 
                            "Veuillez entrer un nombre valide.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la réservation: " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de réservation", 
                    "Une erreur est survenue lors de la réservation: " + e.getMessage());
        }
    }

    private void filterEvents() {
        try {
            // Récupérer les valeurs de filtre
            String searchText = searchField.getText().toLowerCase();
            String category = filterCategory.getValue();
            String date = filterDate.getValue();
            
            System.out.println("Filtrage des événements - Recherche: " + searchText + ", Catégorie: " + category + ", Date: " + date);
            
            // Vider le conteneur
            eventsContainer.getChildren().clear();
            
            // Obtenir tous les événements
            List<Evenement> allEvents = evenementService.getAllEvents();
            System.out.println("Nombre total d'événements à filtrer: " + (allEvents != null ? allEvents.size() : 0));
            
            if (allEvents == null || allEvents.isEmpty()) {
                Label noEventsLabel = new Label("Aucun événement disponible");
                noEventsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d; -fx-padding: 20;");
                eventsContainer.getChildren().add(noEventsLabel);
                return;
            }
            
            int matchCount = 0;
            
            // Filtrer et ajouter les événements
            for (Evenement event : allEvents) {
                try {
                    boolean matchesSearch = searchText == null || searchText.isEmpty() ||
                        (event.getTitre() != null && event.getTitre().toLowerCase().contains(searchText)) ||
                        (event.getDescription() != null && event.getDescription().toLowerCase().contains(searchText)) ||
                        (event.getType() != null && event.getType().toLowerCase().contains(searchText)) ||
                        (event.getLocation() != null && event.getLocation().toLowerCase().contains(searchText));
                    
                    boolean matchesCategory = "Tous".equals(category) || 
                        (event.getType() != null && event.getType().equals(category));
                    
                    boolean matchesDate = true; // Todo: implémenter le filtrage par date
                    
                    System.out.println("Évaluation de l'événement #" + event.getId() + 
                                      " - Recherche: " + matchesSearch + 
                                      ", Catégorie: " + matchesCategory + 
                                      ", Date: " + matchesDate);
                    
                    if (matchesSearch && matchesCategory && matchesDate) {
                        matchCount++;
                        VBox eventCard = createEventCard(event);
                        eventsContainer.getChildren().add(eventCard);
                        System.out.println("Événement #" + event.getId() + " correspond aux critères et est ajouté");
                    }
                } catch (Exception e) {
                    System.err.println("Erreur lors du traitement d'un événement pendant le filtrage: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            
            System.out.println("Nombre d'événements après filtrage: " + matchCount);
            
            // Afficher un message si aucun événement ne correspond aux critères
            if (eventsContainer.getChildren().isEmpty()) {
                Label noEventsLabel = new Label("Aucun événement ne correspond aux critères de recherche");
                noEventsLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d; -fx-padding: 20;");
                eventsContainer.getChildren().add(noEventsLabel);
            }
            
        } catch (Exception e) {
            System.err.println("Erreur de filtrage: " + e.getMessage());
            e.printStackTrace();
            
            // Afficher un message d'erreur dans l'interface
            Label errorLabel = new Label("Erreur de filtrage: " + e.getMessage());
            errorLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #e74c3c; -fx-padding: 20;");
            eventsContainer.getChildren().add(errorLabel);
            
            // Afficher également une alerte
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de filtrage", 
                     "Une erreur est survenue lors du filtrage des événements: " + e.getMessage());
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