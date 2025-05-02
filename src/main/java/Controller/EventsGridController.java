package Controller;

import Entity.Evenement;
import Service.EvenementService;
import Utils.MainStyleFixer;
import Utils.AnimationUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Contrôleur pour afficher une grille d'événements stylisée
 * Avec fonctionnalités de filtre, recherche et pagination
 */
public class EventsGridController implements Initializable {
    @FXML private ScrollPane scrollPane;
    @FXML private FlowPane eventsContainer;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> typeFilter;
    @FXML private ComboBox<String> sortFilter;
    @FXML private Label totalEvents;
    @FXML private Pagination pagination;
    @FXML private VBox adminPanel;
    @FXML private ToggleButton btnAdminMode;
    @FXML private Button btnAjouterEvent;
    @FXML private Button btnGestionEvents;
    @FXML private Button btnAjouterSession;
    @FXML private Button btnGestionSessions;
    @FXML private Button btnStatsEvents;
    @FXML private Button btnStatsSessions;
    @FXML private Button btnDeconnexion;
    @FXML private Button btnFullScreen;
    
    private EvenementService eventService;
    private List<Evenement> allEvents = new ArrayList<>();
    private List<Evenement> filteredEvents = new ArrayList<>();
    
    private static final int EVENTS_PER_PAGE = 8;
    private boolean isAdminMode = false;
    private boolean isFullScreen = false;
    
    /**
     * Initialise le contrôleur
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            eventService = new EvenementService();
            setupUI();
            setupAdminPanel();
            loadEvents();
            setupFilters();
            setupSearch();
            setupPagination();
        } catch (SQLException e) {
            showError("Erreur d'initialisation", "Impossible de charger les événements: " + e.getMessage());
        }
    }
    
    /**
     * Configure l'interface utilisateur
     */
    private void setupUI() {
        // Masquer le panneau d'administration par défaut
        if (adminPanel != null) {
            adminPanel.setVisible(false);
            adminPanel.setManaged(false);
        }
        
        // Configurer le conteneur d'événements
        eventsContainer.setPadding(new Insets(20));
        eventsContainer.setHgap(20);
        eventsContainer.setVgap(20);
        eventsContainer.getStyleClass().add("events-grid");
        
        // Rendre le ScrollPane responsive
        scrollPane.setFitToWidth(true);
        eventsContainer.prefWidthProperty().bind(scrollPane.widthProperty().subtract(40));
        
        // Configurer les options de tri
        sortFilter.setItems(FXCollections.observableArrayList(
                "Date (récent - ancien)",
                "Date (ancien - récent)",
                "Alphabétique (A-Z)",
                "Alphabétique (Z-A)"
        ));
        sortFilter.getSelectionModel().selectFirst();
        
        // Configurer le bouton plein écran s'il existe
        if (btnFullScreen != null) {
            btnFullScreen.setOnAction(e -> toggleFullScreen());
            AnimationUtils.addClickEffect(btnFullScreen);
        }
        
        // Ajouter les listeners pour le redimensionnement
        scrollPane.widthProperty().addListener((obs, oldVal, newVal) -> {
            updateFlowPaneWidth();
        });
    }
    
    /**
     * Configure le panneau d'administration
     */
    private void setupAdminPanel() {
        if (btnAdminMode != null) {
            btnAdminMode.selectedProperty().addListener((obs, oldVal, newVal) -> {
                isAdminMode = newVal;
                toggleAdminMode(isAdminMode);
            });
        }
        
        // Configurer les boutons du panneau admin
        if (btnAjouterEvent != null) {
            btnAjouterEvent.setOnAction(e -> openAjouterEventForm());
        }
        
        if (btnGestionEvents != null) {
            btnGestionEvents.setOnAction(e -> openGestionEventsPanel());
        }
        
        if (btnAjouterSession != null) {
            btnAjouterSession.setOnAction(e -> openAjouterSessionForm());
        }
        
        if (btnGestionSessions != null) {
            btnGestionSessions.setOnAction(e -> openGestionSessionsPanel());
        }
        
        if (btnStatsEvents != null) {
            btnStatsEvents.setOnAction(e -> openStatsEvents());
        }
        
        if (btnStatsSessions != null) {
            btnStatsSessions.setOnAction(e -> openStatsSessions());
        }
        
        if (btnDeconnexion != null) {
            btnDeconnexion.setOnAction(e -> handleDeconnexion());
        }
    }
    
    /**
     * Active/désactive le mode administrateur
     */
    private void toggleAdminMode(boolean active) {
        if (adminPanel != null) {
            adminPanel.setVisible(active);
            adminPanel.setManaged(active);
        }
        
        // Mettre à jour l'affichage des événements
        updateEventsDisplay();
    }
    
    /**
     * Ouvre le formulaire d'ajout d'événement
     */
    private void openAjouterEventForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterEvent.fxml"));
            Parent root = loader.load();
            
            AjouterEventController controller = loader.getController();
            controller.setOnEventAdded(() -> {
                try {
                    loadEvents();
                } catch (SQLException e) {
                    showError("Erreur", "Impossible de rafraîchir la liste des événements: " + e.getMessage());
                }
            });
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Ajouter un événement");
            
            Scene scene = new Scene(root);
            MainStyleFixer.applyProfessionalStyle(scene);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showError("Erreur", "Impossible d'ouvrir le formulaire d'ajout d'événement: " + e.getMessage());
        }
    }
    
    /**
     * Ouvre le panneau de gestion des événements
     */
    private void openGestionEventsPanel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionEvenements.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Gestion des événements");
            
            Scene scene = new Scene(root);
            MainStyleFixer.applyProfessionalStyle(scene);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showError("Erreur", "Impossible d'ouvrir le panneau de gestion des événements: " + e.getMessage());
        }
    }
    
    /**
     * Ouvre le formulaire d'ajout de session
     */
    private void openAjouterSessionForm() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterSession.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Ajouter une session");
            
            Scene scene = new Scene(root);
            MainStyleFixer.applyProfessionalStyle(scene);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showError("Erreur", "Impossible d'ouvrir le formulaire d'ajout de session: " + e.getMessage());
        }
    }
    
    /**
     * Ouvre le panneau de gestion des sessions
     */
    private void openGestionSessionsPanel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionSessions.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Gestion des sessions");
            
            Scene scene = new Scene(root);
            MainStyleFixer.applyProfessionalStyle(scene);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showError("Erreur", "Impossible d'ouvrir le panneau de gestion des sessions: " + e.getMessage());
        }
    }
    
    /**
     * Ouvre les statistiques des événements
     */
    private void openStatsEvents() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EventStats.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Statistiques des événements");
            
            Scene scene = new Scene(root);
            MainStyleFixer.applyProfessionalStyle(scene);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showError("Erreur", "Impossible d'ouvrir les statistiques des événements: " + e.getMessage());
        }
    }
    
    /**
     * Ouvre les statistiques des sessions
     */
    private void openStatsSessions() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SessionStats.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Statistiques des sessions");
            
            Scene scene = new Scene(root);
            MainStyleFixer.applyProfessionalStyle(scene);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showError("Erreur", "Impossible d'ouvrir les statistiques des sessions: " + e.getMessage());
        }
    }
    
    /**
     * Gère la déconnexion de l'administrateur
     */
    private void handleDeconnexion() {
        // Désactiver le mode admin
        isAdminMode = false;
        if (btnAdminMode != null) {
            btnAdminMode.setSelected(false);
        }
        toggleAdminMode(false);
    }
    
    /**
     * Met à jour la largeur du FlowPane lors du redimensionnement
     */
    private void updateFlowPaneWidth() {
        double width = scrollPane.getWidth() - 40; // Soustraire le padding
        eventsContainer.setPrefWidth(width);
        
        // Calculer combien de cartes peuvent tenir dans la largeur (environ 280px par carte + 20px de gap)
        int cardsPerRow = Math.max(1, (int) (width / 300));
        double cardWidth = (width - ((cardsPerRow - 1) * 20)) / cardsPerRow;
        
        // Mettre à jour la largeur des cartes d'événements
        eventsContainer.getChildren().forEach(node -> {
            if (node instanceof Region) {
                ((Region) node).setPrefWidth(cardWidth);
                ((Region) node).setMaxWidth(cardWidth);
            }
        });
    }
    
    /**
     * Charge tous les événements
     */
    private void loadEvents() throws SQLException {
        allEvents = eventService.getAllEvenements();
        filteredEvents = new ArrayList<>(allEvents);
        updateEventsDisplay();
    }
    
    /**
     * Configure les filtres de type d'événement
     */
    private void setupFilters() throws SQLException {
        // Récupérer tous les types d'événements uniques
        List<String> eventTypes = allEvents.stream()
                .map(Evenement::getType)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        
        // Ajouter l'option "Tous les types"
        eventTypes.add(0, "Tous les types");
        
        // Remplir le ComboBox
        typeFilter.setItems(FXCollections.observableArrayList(eventTypes));
        typeFilter.getSelectionModel().selectFirst();
        
        // Ajouter les écouteurs de changement
        typeFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
        sortFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());
    }
    
    /**
     * Configure la recherche
     */
    private void setupSearch() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            applyFilters();
        });
    }
    
    /**
     * Configure la pagination
     */
    private void setupPagination() {
        pagination.setPageCount(calculatePageCount());
        pagination.currentPageIndexProperty().addListener((obs, oldVal, newVal) -> {
            updateEventsDisplay();
        });
    }
    
    /**
     * Calcule le nombre de pages nécessaires
     */
    private int calculatePageCount() {
        return (int) Math.ceil((double) filteredEvents.size() / EVENTS_PER_PAGE);
    }
    
    /**
     * Applique les filtres et la recherche
     */
    private void applyFilters() {
        String searchText = searchField.getText().toLowerCase();
        String selectedType = typeFilter.getValue();
        String sortType = sortFilter.getValue();
        
        // Filtrer par type et texte de recherche
        filteredEvents = allEvents.stream()
                .filter(event -> {
                    // Filtrer par type si nécessaire
                    if (!"Tous les types".equals(selectedType)) {
                        if (!selectedType.equals(event.getType())) {
                            return false;
                        }
                    }
                    
                    // Filtrer par texte de recherche
                    if (!searchText.isEmpty()) {
                        return event.getTitre().toLowerCase().contains(searchText) ||
                               (event.getDescription() != null && event.getDescription().toLowerCase().contains(searchText)) ||
                               (event.getLocation() != null && event.getLocation().toLowerCase().contains(searchText)) ||
                               (event.getType() != null && event.getType().toLowerCase().contains(searchText));
                    }
                    
                    return true;
                })
                .collect(Collectors.toList());
        
        // Trier les résultats
        sortFilteredEvents(sortType);
        
        // Mettre à jour la pagination
        pagination.setPageCount(Math.max(1, calculatePageCount()));
        pagination.setCurrentPageIndex(0);
        
        // Mettre à jour l'affichage
        updateEventsDisplay();
    }
    
    /**
     * Trie les événements filtrés selon le critère sélectionné
     */
    private void sortFilteredEvents(String sortType) {
        switch (sortType) {
            case "Date (récent - ancien)":
                filteredEvents.sort((e1, e2) -> {
                    if (e1.getDateD() == null) return 1;
                    if (e2.getDateD() == null) return -1;
                    return e2.getDateD().compareTo(e1.getDateD());
                });
                break;
            case "Date (ancien - récent)":
                filteredEvents.sort((e1, e2) -> {
                    if (e1.getDateD() == null) return 1;
                    if (e2.getDateD() == null) return -1;
                    return e1.getDateD().compareTo(e2.getDateD());
                });
                break;
            case "Alphabétique (A-Z)":
                filteredEvents.sort((e1, e2) -> {
                    if (e1.getTitre() == null) return 1;
                    if (e2.getTitre() == null) return -1;
                    return e1.getTitre().compareToIgnoreCase(e2.getTitre());
                });
                break;
            case "Alphabétique (Z-A)":
                filteredEvents.sort((e1, e2) -> {
                    if (e1.getTitre() == null) return 1;
                    if (e2.getTitre() == null) return -1;
                    return e2.getTitre().compareToIgnoreCase(e1.getTitre());
                });
                break;
        }
    }
    
    /**
     * Met à jour l'affichage des événements
     */
    private void updateEventsDisplay() {
        eventsContainer.getChildren().clear();
        
        // Calculer les indices pour la pagination
        int startIndex = pagination.getCurrentPageIndex() * EVENTS_PER_PAGE;
        int endIndex = Math.min(startIndex + EVENTS_PER_PAGE, filteredEvents.size());
        
        // Obtenir les événements pour la page actuelle
        List<Evenement> eventsToDisplay = filteredEvents.subList(startIndex, endIndex);
        
        // Créer les cartes d'événements
        for (Evenement event : eventsToDisplay) {
            VBox eventCard = EventCardController.createEventCard(
                    event, 
                    this::showEventDetails,
                    this::handleReservation
            );
            eventsContainer.getChildren().add(eventCard);
        }
        
        // Mettre à jour le compteur d'événements
        totalEvents.setText(filteredEvents.size() + " événement(s) trouvé(s)");
        
        // Mettre à jour la largeur des cartes
        updateFlowPaneWidth();
    }
    
    /**
     * Affiche les détails d'un événement
     * @param event L'événement à afficher
     */
    private void showEventDetails(Evenement event) {
        try {
            // Fermer toutes les fenêtres de détails existantes d'abord
            for (javafx.stage.Window window : javafx.stage.Window.getWindows()) {
                if (window instanceof Stage) {
                    Stage stage = (Stage) window;
                    if (stage.isShowing() && stage.getTitle() != null && 
                        stage.getTitle().equals("Détails de l'événement")) {
                        stage.close();
                    }
                }
            }
            
            // Charger la vue des détails de l'événement
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EventDetails.fxml"));
            Parent root = loader.load();
            
            // Initialiser le contrôleur avec l'événement sélectionné
            EventDetailsController controller = loader.getController();
            controller.setEvent(event, isAdminMode);
            controller.setOnReservationComplete(() -> onReservationComplete());
            
            // Appliquer les styles compacts et masquer les barres de défilement
            root.getStyleClass().add("compact-window");
            hideScrollBarsInRoot(root);
            
            // Créer une nouvelle scène avec des dimensions réduites
            Scene scene = new Scene(root);
            
            // Configurer la fenêtre de détails
            Stage detailStage = new Stage();
            detailStage.initModality(Modality.APPLICATION_MODAL);
            detailStage.initStyle(StageStyle.UNDECORATED);
            detailStage.setTitle("Détails de l'événement");
            
            // Appliquer le style professionnel à la scène
            MainStyleFixer.applyProfessionalStyle(scene);
            
            // Définir la taille réduite et les limites de la fenêtre
            detailStage.setScene(scene);
            detailStage.setWidth(400);
            detailStage.setHeight(320);
            detailStage.setMinWidth(350);
            detailStage.setMinHeight(300);
            detailStage.setMaxWidth(450);
            detailStage.setMaxHeight(350);
            
            // Centrer la fenêtre par rapport à la fenêtre principale
            detailStage.centerOnScreen();
            
            // Afficher la fenêtre avec animation d'apparition
            detailStage.setOpacity(0);
            detailStage.show();
            
            // Animation de fondu pour l'apparition
            javafx.animation.FadeTransition fadeIn = new javafx.animation.FadeTransition(
                    javafx.util.Duration.millis(200), root);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
            
            // Animation de la fenêtre
            javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                    new javafx.animation.KeyFrame(javafx.util.Duration.ZERO, 
                            new javafx.animation.KeyValue(detailStage.opacityProperty(), 0)),
                    new javafx.animation.KeyFrame(javafx.util.Duration.millis(200), 
                            new javafx.animation.KeyValue(detailStage.opacityProperty(), 1))
            );
            timeline.play();
        } catch (IOException e) {
            showError("Erreur", "Impossible d'afficher les détails de l'événement: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Masque récursivement toutes les barres de défilement dans une hiérarchie de nœuds
     */
    private void hideScrollBarsInRoot(Parent root) {
        if (root == null) return;
        
        for (Node node : root.getChildrenUnmodifiable()) {
            if (node instanceof ScrollPane scrollPane) {
                scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
                scrollPane.setPadding(new Insets(0));
            }
            
            if (node instanceof Parent parent) {
                hideScrollBarsInRoot(parent);
            }
        }
    }
    
    /**
     * Gère la réservation d'un événement
     */
    private void handleReservation(Evenement event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Reservation.fxml"));
            Parent root = loader.load();
            
            // Récupérer le contrôleur et configurer l'événement
            Object controller = loader.getController();
            
            // Appeler les méthodes par réflexion pour être flexible avec différents contrôleurs
            try {
                controller.getClass().getMethod("setEvent", Evenement.class).invoke(controller, event);
                controller.getClass().getMethod("setOnReservationComplete", Runnable.class)
                    .invoke(controller, (Runnable)this::onReservationComplete);
            } catch (Exception e) {
                showError("Erreur", "Le contrôleur de réservation n'est pas correctement configuré");
                return;
            }
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.DECORATED);
            stage.setTitle("Réserver - " + event.getTitre());
            
            Scene scene = new Scene(root);
            MainStyleFixer.applyProfessionalStyle(scene);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showError("Erreur", "Impossible d'ouvrir la fenêtre de réservation: " + e.getMessage());
        }
    }
    
    /**
     * Callback après une réservation réussie
     */
    private void onReservationComplete() {
        try {
            loadEvents(); // Recharger les événements pour mettre à jour les disponibilités
        } catch (SQLException e) {
            showError("Erreur", "Impossible de mettre à jour les événements: " + e.getMessage());
        }
    }
    
    /**
     * Affiche une boîte de dialogue d'erreur
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        // Appliquer le style professionnel à la boîte de dialogue
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/professional_style.css").toExternalForm());
        
        alert.showAndWait();
    }
    
    /**
     * Bascule entre le mode plein écran et le mode normal
     */
    private void toggleFullScreen() {
        Stage stage = (Stage) scrollPane.getScene().getWindow();
        if (stage != null) {
            isFullScreen = !isFullScreen;
            
            if (isFullScreen) {
                // Passer en plein écran en utilisant le mode plein écran de JavaFX
                stage.setFullScreen(true);
                stage.setFullScreenExitHint("");  // Supprimer le message de sortie du plein écran
                
                // Mettre à jour l'icône du bouton
                if (btnFullScreen != null) {
                    btnFullScreen.setText("⬇");
                }
            } else {
                // Quitter le mode plein écran
                stage.setFullScreen(false);
                
                // Mettre à jour l'icône du bouton
                if (btnFullScreen != null) {
                    btnFullScreen.setText("🔍");
                }
            }
            
            // Animer la transition
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), scrollPane.getScene().getRoot());
            scaleTransition.setFromX(0.95);
            scaleTransition.setFromY(0.95);
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
            scaleTransition.play();
        }
    }
} 