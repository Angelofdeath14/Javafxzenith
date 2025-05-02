package Controller;

import Entity.Evenement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.FlowPane;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import services.EvenementService;
import Utils.FXMLUtils;

import java.time.format.DateTimeFormatter;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AffichageEventController implements Initializable {
    @FXML private ScrollPane scrollPane;
    @FXML private VBox mainContainer;
    @FXML private Button btnAddEvent;
    @FXML private Button btnDashboard;
    @FXML private Button btnUsers;
    @FXML private Button btnLogout;
    @FXML private Button btnFront;
    
    private TextField searchField;
    private ComboBox<String> filterType;
    private EvenementService evenementService;
    private ObservableList<Evenement> eventList;
    private FilteredList<Evenement> filteredEvents;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // Configuration du ScrollPane
            scrollPane.setFitToWidth(true);
            scrollPane.setFitToHeight(true);
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

            evenementService = new EvenementService();

            // Configuration des boutons
            btnAddEvent.setOnAction(event -> addEvent());
            btnDashboard.setOnAction(event -> goToDash());
            btnUsers.setOnAction(event -> goToUsers());
            btnLogout.setOnAction(event -> Logout());
            btnFront.setOnAction(event -> goToFrontView());

            // Initialisation de la liste des événements
            loadEvents();

        } catch (SQLException e) {
            showError("Erreur de connexion", "Impossible de se connecter à la base de données : " + e.getMessage());
        }
    }

    public void loadEvents() {
        try {
            // Vider le conteneur
            mainContainer.getChildren().clear();
            
            // Ajouter le titre
            Label titleLabel = new Label("Liste des Événements");
            titleLabel.getStyleClass().add("header-title");
            mainContainer.getChildren().add(titleLabel);
            
            // Ajouter la barre de recherche
            HBox searchBox = new HBox(10);
            searchBox.getStyleClass().add("search-container");
            searchField = new TextField();
            searchField.setPromptText("Rechercher un événement...");
            searchField.getStyleClass().add("search-field");
            HBox.setHgrow(searchField, javafx.scene.layout.Priority.ALWAYS);
            
            filterType = new ComboBox<>();
            filterType.setPromptText("Filtrer par type");
            filterType.getStyleClass().add("filter-combo");
            filterType.getItems().addAll("Tous", "Concert", "Théâtre", "Exposition", "Sport");
            filterType.setValue("Tous");
            
            searchBox.getChildren().addAll(searchField, filterType);
            mainContainer.getChildren().add(searchBox);
            
            // Configurer les filtres
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                filterEvents(newValue);
            });
            
            filterType.setOnAction(event -> {
                filterEvents(searchField.getText());
            });
            
            // Récupérer tous les événements
            eventList = FXCollections.observableArrayList(evenementService.getAllEvents());
            filteredEvents = new FilteredList<>(eventList, p -> true);
            
            // Création du tableau d'événements
            TableView<Evenement> eventsTable = new TableView<>();
            eventsTable.setItems(eventList);
            VBox.setVgrow(eventsTable, javafx.scene.layout.Priority.ALWAYS);
            
            // Configuration des colonnes
            TableColumn<Evenement, Integer> colId = new TableColumn<>("ID");
            colId.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
            colId.setPrefWidth(50);
            
            TableColumn<Evenement, String> colTitle = new TableColumn<>("Titre");
            colTitle.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("titre"));
            colTitle.setPrefWidth(200);
            
            TableColumn<Evenement, String> colType = new TableColumn<>("Type");
            colType.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("type"));
            colType.setPrefWidth(100);
            
            TableColumn<Evenement, String> colDates = new TableColumn<>("Dates");
            colDates.setCellValueFactory(cellData -> {
                Evenement event = cellData.getValue();
                return new javafx.beans.property.SimpleStringProperty(
                    event.getDateD().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " au " +
                    event.getDateF().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                );
            });
            colDates.setPrefWidth(150);
            
            TableColumn<Evenement, String> colLocation = new TableColumn<>("Lieu");
            colLocation.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("location"));
            colLocation.setPrefWidth(120);
            
            TableColumn<Evenement, Integer> colCapacity = new TableColumn<>("Places");
            colCapacity.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("nbPlace"));
            colCapacity.setPrefWidth(80);
            
            // Colonne d'actions
            TableColumn<Evenement, Void> colActions = new TableColumn<>("Actions");
            colActions.setPrefWidth(200);
            colActions.setCellFactory(param -> {
                return new TableCell<>() {
                    private final Button viewButton = new Button("Voir");
            private final Button editButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");
                    private final HBox pane = new HBox(5, viewButton, editButton, deleteButton);
                    
                    {
                        viewButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                        editButton.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
                        deleteButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                        
                        viewButton.setOnAction(event -> {
                            Evenement evenement = getTableView().getItems().get(getIndex());
                            goToSessions(evenement);
                        });

                editButton.setOnAction(event -> {
                    Evenement evenement = getTableView().getItems().get(getIndex());
                    handleEdit(evenement);
                });

                deleteButton.setOnAction(event -> {
                    Evenement evenement = getTableView().getItems().get(getIndex());
                    handleDelete(evenement);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                        setGraphic(empty ? null : pane);
                    }
                };
            });
            
            eventsTable.getColumns().addAll(colId, colTitle, colType, colDates, colLocation, colCapacity, colActions);
            mainContainer.getChildren().add(eventsTable);
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, 
                     "Erreur", 
                     "Erreur de chargement", 
                     "Erreur lors du chargement des événements : " + e.getMessage());
        }
    }

    private void filterEvents(String searchText) {
        try {
            if (mainContainer.getChildren().size() < 3) {
                return; // Pas encore initialisé
            }
            
            // Récupérer le tableau dans le conteneur principal
            TableView<Evenement> eventsTable = (TableView<Evenement>) mainContainer.getChildren().get(2);
            
            String lowerCaseFilter = searchText != null ? searchText.toLowerCase() : "";
            String selectedType = filterType.getValue();
            
            filteredEvents.setPredicate(event -> {
                boolean matchesSearch = lowerCaseFilter.isEmpty() || 
                    event.getTitre().toLowerCase().contains(lowerCaseFilter) ||
                    event.getDescription().toLowerCase().contains(lowerCaseFilter) ||
                    event.getType().toLowerCase().contains(lowerCaseFilter) ||
                    event.getLocation().toLowerCase().contains(lowerCaseFilter);
                    
                boolean matchesType = selectedType.equals("Tous") || event.getType().equals(selectedType);
                
                return matchesSearch && matchesType;
            });
            
            eventsTable.setItems(filteredEvents);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void addEvent() {
        try {
            Parent root = FXMLUtils.loadFXML("AjouterEvent.fxml", getClass());
            
            Stage stage = new Stage();
            stage.setTitle("Ajouter un événement");
            Scene scene = new Scene(root);
            stage.setScene(scene);
            
            stage.showAndWait();
            loadEvents();
        } catch (IOException e) {
            e.printStackTrace(); // Imprimer la stack trace pour le débogage
            showAlert(Alert.AlertType.ERROR, 
                     "Erreur", 
                     "Erreur d'ouverture", 
                     "Impossible d'ouvrir la fenêtre d'ajout : " + e.getMessage());
        }
    }

    @FXML
    private void goToDash() {
        try {
            // Charger la vue du tableau de bord avec statistiques
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
            Parent root = loader.load();
            
            // Changer la scène
            Stage stage = (Stage) btnDashboard.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Tableau de bord - Statistiques");
            stage.show();
        } catch (IOException e) {
            showError("Erreur de navigation", "Impossible d'accéder au tableau de bord : " + e.getMessage());
        }
    }

    @FXML
    private void goToFrontView() {
        try {
            // Charger la vue frontend des événements en style cartes
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FrontEvents.fxml"));
            Parent root = loader.load();
            
            // Si vous avez besoin de passer des données au contrôleur
            // FrontEventsController controller = loader.getController();
            // controller.initData();
            
            // Changer la scène
            Stage stage = (Stage) btnDashboard.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Événements - Vue Utilisateur");
            stage.show();
        } catch (IOException e) {
            showError("Erreur de navigation", "Impossible d'accéder à la vue utilisateur : " + e.getMessage());
        }
    }

    @FXML
    private void goToUsers() {
        try {
            Parent root = FXMLUtils.loadFXML("GestionUsers.fxml", getClass());
            Stage stage = (Stage) btnUsers.getScene().getWindow();
            stage.setTitle("Gestion des utilisateurs");
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, 
                     "Erreur", 
                     "Erreur de navigation", 
                     "Impossible de naviguer vers la gestion des utilisateurs : " + e.getMessage());
        }
    }

    @FXML
    private void Logout() {
        try {
            Stage stage = (Stage) btnLogout.getScene().getWindow();
            stage.close();
            
            Parent root = FXMLUtils.loadFXML("Login.fxml", getClass());
            Stage loginStage = new Stage();
            loginStage.setTitle("Connexion");
            Scene scene = new Scene(root);
            loginStage.setScene(scene);
            loginStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, 
                     "Erreur", 
                     "Erreur de déconnexion", 
                     "Impossible de se déconnecter : " + e.getMessage());
        }
    }

    private void navigateTo(String fxml, String title) {
        try {
            Parent root = FXMLUtils.loadFXML(fxml, getClass());
            
            Stage stage = (Stage) btnDashboard.getScene().getWindow();
            stage.setTitle(title);
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, 
                     "Erreur", 
                     "Erreur de navigation", 
                     "Impossible de naviguer vers " + title + " : " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String headerText, String contentText) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(contentText);
        alert.showAndWait();
    }

    private void showError(String title, String message) {
        showAlert(Alert.AlertType.ERROR, title, "Erreur", message);
    }

    private void handleEdit(Evenement evenement) {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL url = getClass().getResource("/AjouterEvent.fxml");
            if (url == null) {
                url = getClass().getClassLoader().getResource("AjouterEvent.fxml");
            }
            if (url == null) {
                String resourcePath = "file:" + System.getProperty("user.dir") + "/target/classes/AjouterEvent.fxml";
                url = new URL(resourcePath);
            }
            
            loader.setLocation(url);
            Parent root = loader.load();
            
            AjouterEventController controller = loader.getController();
            controller.initializeForEdit(evenement);
            
            Stage stage = new Stage();
            stage.setTitle("Modifier l'événement");
            Scene scene = new Scene(root);
            stage.setScene(scene);
            
            stage.showAndWait();
            loadEvents();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, 
                     "Erreur", 
                     "Erreur de modification", 
                     "Impossible de modifier l'événement : " + e.getMessage());
        }
    }

    private void handleDelete(Evenement evenement) {
        try {
            evenementService.supprimer(evenement.getId());
            loadEvents();
            showAlert(Alert.AlertType.INFORMATION, 
                     "Succès", 
                     "L'événement a été supprimé avec succès !",
                     "");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, 
                     "Erreur", 
                     "Erreur lors de la suppression : " + e.getMessage(),
                     "");
        }
    }

    private void goToSessions(Evenement evenement) {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL url = getClass().getResource("/GestionSessions.fxml");
            if (url == null) {
                url = getClass().getClassLoader().getResource("GestionSessions.fxml");
            }
            if (url == null) {
                String resourcePath = "file:" + System.getProperty("user.dir") + "/target/classes/GestionSessions.fxml";
                url = new URL(resourcePath);
            }
            
            loader.setLocation(url);
            Parent root = loader.load();
            
            GestionSessionsController controller = loader.getController();
            controller.setEvenement(evenement);
            
            Stage stage = new Stage();
            stage.setTitle("Gestion des sessions");
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, 
                     "Erreur", 
                     "Erreur d'ouverture", 
                     "Impossible d'ouvrir la fenêtre des sessions : " + e.getMessage());
        }
    }
}
