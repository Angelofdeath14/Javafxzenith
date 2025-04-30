package Controller;

import Entity.Evenement;
import Entity.Session;
import Utils.DataSource;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import Service.SessionService;
import services.EvenementService;
import javafx.beans.property.SimpleStringProperty;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.sql.SQLException;
import java.util.List;
import java.io.IOException;
import javafx.util.StringConverter;

public class AffichageSessionsController {
    @FXML private TableView<Session> sessionsTableView;
    @FXML private TableColumn<Session, String> colDescription;
    @FXML private TableColumn<Session, String> colEvenement;
    @FXML private TableColumn<Session, String> colStartTime;
    @FXML private TableColumn<Session, String> colEndTime;
    @FXML private TableColumn<Session, Void> colActions;
    @FXML private ComboBox<Evenement> eventFilter;
    @FXML private TextField searchField;

    private SessionService sessionService;
    private EvenementService evenementService;
    private ObservableList<Session> sessionsList;
    private Evenement currentEvenement;

    @FXML
    public void initialize() {
        try {
            sessionService = new SessionService();
            evenementService = new EvenementService();
            sessionsList = FXCollections.observableArrayList();

            setupTableColumns();
            loadSessions();
            setupEventFilter();
            setupSearch();
        } catch (SQLException e) {
            showError("Erreur de connexion", "Impossible de se connecter à la base de données : " + e.getMessage());
        }
    }

    @FXML
    private void setupTableColumns() {
        colDescription.setCellValueFactory(cellData -> cellData.getValue().descriptionProperty());
        
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
        
        setupActionColumn();
    }

    private void setupActionColumn() {
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button editButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");
            private final HBox buttonsBox = new HBox(5, editButton, deleteButton);

            {
                // Style pour le bouton Modifier
                editButton.setStyle(
                    "-fx-background-color: #FFD700; " +
                    "-fx-text-fill: black; " +
                    "-fx-font-weight: bold; " +
                    "-fx-min-width: 80px; " +
                    "-fx-min-height: 30px; " +
                    "-fx-background-radius: 5px; " +
                    "-fx-cursor: hand;"
                );

                // Style pour le bouton Supprimer
                deleteButton.setStyle(
                    "-fx-background-color: #FF4444; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-weight: bold; " +
                    "-fx-min-width: 80px; " +
                    "-fx-min-height: 30px; " +
                    "-fx-background-radius: 5px; " +
                    "-fx-cursor: hand;"
                );

                // Effets hover
                editButton.setOnMouseEntered(e -> editButton.setStyle(
                    editButton.getStyle() + "-fx-background-color: derive(#FFD700, -10%);"
                ));
                editButton.setOnMouseExited(e -> editButton.setStyle(
                    "-fx-background-color: #FFD700; " +
                    "-fx-text-fill: black; " +
                    "-fx-font-weight: bold; " +
                    "-fx-min-width: 80px; " +
                    "-fx-min-height: 30px; " +
                    "-fx-background-radius: 5px; " +
                    "-fx-cursor: hand;"
                ));

                deleteButton.setOnMouseEntered(e -> deleteButton.setStyle(
                    deleteButton.getStyle() + "-fx-background-color: derive(#FF4444, -10%);"
                ));
                deleteButton.setOnMouseExited(e -> deleteButton.setStyle(
                    "-fx-background-color: #FF4444; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-weight: bold; " +
                    "-fx-min-width: 80px; " +
                    "-fx-min-height: 30px; " +
                    "-fx-background-radius: 5px; " +
                    "-fx-cursor: hand;"
                ));

                editButton.setOnAction(event -> {
                    Session session = getTableView().getItems().get(getIndex());
                    handleModifierSession(session);
                });

                deleteButton.setOnAction(event -> {
                    Session session = getTableView().getItems().get(getIndex());
                    handleSupprimerSession(session);
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
            sessionsTableView.setItems(sessionsList);
        } catch (SQLException e) {
            showError("Erreur lors du chargement des sessions", e.getMessage());
        }
    }

    private void setupEventFilter() {
        ObservableList<Evenement> evenements = FXCollections.observableArrayList(evenementService.getAllEvents());
        eventFilter.setItems(evenements);
        
        eventFilter.setConverter(new StringConverter<Evenement>() {
            @Override
            public String toString(Evenement evenement) {
                return evenement != null ? evenement.getTitre() : "";
            }

            @Override
            public Evenement fromString(String string) {
                return null;
            }
        });

        eventFilter.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                loadSessionsByEvent(newVal.getId());
            } else {
                loadSessions();
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

    private void filterSessionsByEvent(Evenement evenement) {
        try {
            List<Session> filteredSessions = sessionService.getSessionsByEvenement(evenement);
            sessionsList.setAll(filteredSessions);
        } catch (SQLException e) {
            showError("Erreur lors du filtrage des sessions", e.getMessage());
        }
    }

    private void filterSessionsBySearch(String searchText) {
        try {
            List<Session> filteredSessions = sessionService.searchSessions(searchText);
            sessionsList.setAll(filteredSessions);
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
            stage.setScene(new Scene(root));
            stage.show();
            
            stage.setOnHidden(e -> loadSessions());
        } catch (IOException e) {
            showError("Erreur", "Impossible d'ouvrir la fenêtre de modification");
        }
    }

    private void handleSupprimerSession(Session session) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer la session");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette session ?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    sessionService.supprimerSession(session.getId());
                    loadSessions();
                } catch (SQLException e) {
                    showError("Erreur", "Impossible de supprimer la session");
                }
            }
        });
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showInfo(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void goToUsers() throws IOException {
        loadView("/AffichageUser.fxml");
    }

    @FXML
    private void goToAffichageEvent() throws IOException {
        loadView("/AffichageEvent.fxml");
    }

    @FXML
    private void goToSessions() throws IOException {
        loadView("/AffichageSessions.fxml");
    }

    @FXML
    private void addEvent() throws IOException {
        loadView("/AjoutEvent.fxml");
    }

    private void loadView(String fxml) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxml));
        Scene scene = sessionsTableView.getScene();
        scene.setRoot(root);
    }

    public void setEvenement(Evenement evenement) {
        this.currentEvenement = evenement;
        if (evenement != null) {
            try {
                List<Session> sessions = sessionService.getSessionsByEvenement(evenement);
                sessionsList.setAll(sessions);
                eventFilter.setValue(evenement);
            } catch (SQLException e) {
                showError("Erreur", "Impossible de charger les sessions de l'événement");
            }
        }
    }

    private void loadSessionsByEvent(int eventId) {
        try {
            List<Session> sessions = sessionService.getSessionsByEvent(eventId);
            sessionsTableView.setItems(FXCollections.observableArrayList(sessions));
        } catch (SQLException e) {
            showError("Erreur lors du chargement des sessions", e.getMessage());
        }
    }
} 