package Controller;

import Entity.Evenement;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.stage.Stage;
import services.EvenementService;
import services.SessionService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ViewEventsController implements Initializable {
    
    @FXML private FlowPane eventContainer; // Non utilisé dans la vue liste
    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryFilter;
    @FXML private DatePicker dateFilter;
    
    @FXML private TableView<Evenement> eventsTable;
    @FXML private TableColumn<Evenement, Integer> colId;
    @FXML private TableColumn<Evenement, String> colTitle;
    @FXML private TableColumn<Evenement, String> colCategory;
    @FXML private TableColumn<Evenement, String> colDate;
    @FXML private TableColumn<Evenement, String> colLocation;
    @FXML private TableColumn<Evenement, Integer> colCapacity;
    @FXML private TableColumn<Evenement, Void> colActions;
    
    private EvenementService evenementService;
    private SessionService sessionService;
    private List<Evenement> allEvents;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            evenementService = new EvenementService();
            sessionService = new SessionService();
            
            // Initialisation des filtres
            setupFilters();
            
            // Configuration des colonnes du tableau
            setupTableColumns();
            
            // Chargement des événements
            loadEvents();
            
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, 
                    "Erreur de connexion", 
                    "Impossible de se connecter à la base de données", 
                    e.getMessage());
        }
    }
    
    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("titre"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("type"));
        
        // Formater la date
        colDate.setCellValueFactory(cellData -> {
            Evenement event = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                event.getDateD().format(formatter) + " au " + event.getDateF().format(formatter)
            );
        });
        
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colCapacity.setCellValueFactory(new PropertyValueFactory<>("nbPlace"));
        
        // Colonne d'actions
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button viewButton = new Button("Voir");
            private final Button sessionsButton = new Button("Sessions");
            private final HBox pane = new HBox(5, viewButton, sessionsButton);
            
            {
                viewButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
                sessionsButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                
                viewButton.setOnAction(event -> {
                    Evenement evenement = getTableView().getItems().get(getIndex());
                    handleViewDetails(evenement);
                });
                
                sessionsButton.setOnAction(event -> {
                    Evenement evenement = getTableView().getItems().get(getIndex());
                    handleViewSessions(evenement);
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : pane);
            }
        });
    }
    
    private void setupFilters() {
        // Configuration des catégories
        categoryFilter.getItems().addAll("Tous", "Concert", "Théâtre", "Exposition", "Sport", "Conférence");
        categoryFilter.setValue("Tous");
        
        // Configuration des actions de filtrage
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterEvents();
        });
        
        categoryFilter.setOnAction(event -> filterEvents());
        dateFilter.setOnAction(event -> filterEvents());
    }
    
    @FXML
    private void handleSearch() {
        filterEvents();
    }
    
    @FXML
    private void handleReset() {
        searchField.clear();
        categoryFilter.setValue("Tous");
        dateFilter.setValue(null);
        loadEvents();
    }
    
    private void loadEvents() {
        try {
            // Charger tous les événements
            allEvents = evenementService.getAllEvents();
            
            // Mettre à jour le tableau
            eventsTable.getItems().setAll(allEvents);
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement", 
                    "Une erreur est survenue lors du chargement des événements: " + e.getMessage());
        }
    }
    
    private void filterEvents() {
        if (allEvents == null) return;
        
        String searchTerm = searchField.getText().toLowerCase();
        String category = categoryFilter.getValue();
        LocalDate date = dateFilter.getValue();
        
        List<Evenement> filteredEvents = allEvents.stream()
                .filter(event -> {
                    // Filtre par texte de recherche
                    boolean matchesSearch = searchTerm.isEmpty() || 
                            event.getTitre().toLowerCase().contains(searchTerm) || 
                            event.getDescription().toLowerCase().contains(searchTerm);
                    
                    // Filtre par catégorie
                    boolean matchesCategory = "Tous".equals(category) || 
                            event.getType().equals(category);
                    
                    // Filtre par date
                    boolean matchesDate = date == null || 
                            (event.getDateD().toLocalDate().isBefore(date) && 
                             event.getDateF().toLocalDate().isAfter(date)) ||
                            event.getDateD().toLocalDate().isEqual(date) ||
                            event.getDateF().toLocalDate().isEqual(date);
                    
                    return matchesSearch && matchesCategory && matchesDate;
                })
                .collect(Collectors.toList());
        
        // Mettre à jour le tableau avec les résultats filtrés
        eventsTable.getItems().setAll(filteredEvents);
    }
    
    @FXML
    private void handleViewSessions(Evenement event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionSessions.fxml"));
            Parent root = loader.load();
            
            GestionSessionsController controller = loader.getController();
            controller.setEvenement(event);
            
            Scene scene = new Scene(root);
            Stage stage = (Stage) eventsTable.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de navigation", 
                    "Impossible d'afficher les sessions: " + e.getMessage());
        }
    }
    
    private void handleViewDetails(Evenement event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageEvent.fxml"));
            Parent root = loader.load();
            
            // Au lieu d'utiliser setEvenement, nous devons recharger la liste complète
            // car AffichageEventController n'a pas de méthode setEvenement
            
            Scene scene = new Scene(root);
            Stage stage = (Stage) eventsTable.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de navigation", 
                    "Impossible d'afficher les détails: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionSessions.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            Stage stage = (Stage) eventsTable.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de navigation", 
                    "Impossible de retourner à la gestion des sessions: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleViewSessions() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionSessions.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            Stage stage = (Stage) eventsTable.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de navigation", 
                    "Impossible de retourner à la gestion des sessions: " + e.getMessage());
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