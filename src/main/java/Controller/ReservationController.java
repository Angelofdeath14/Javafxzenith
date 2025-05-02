package Controller;

import Entity.Evenement;
import Entity.Session;
import Service.EvenementService;
import Service.SessionService;
import Utils.MainStyleFixer;
import Utils.AnimationUtils;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Contrôleur pour la fenêtre de réservation améliorée
 */
public class ReservationController implements Initializable {
    @FXML private ImageView eventImage;
    @FXML private Label eventName;
    @FXML private Label eventDate;
    @FXML private VBox sessionsContainer;
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField emailField;
    @FXML private TextField telephoneField;
    @FXML private Spinner<Integer> placesSpinner;
    @FXML private Label placesDisponibles;
    @FXML private Label prixTotal;
    @FXML private Button btnCancel;
    @FXML private Button btnConfirm;
    
    private Evenement event;
    private Session selectedSession;
    private SessionService sessionService;
    private EvenementService eventService;
    private Runnable onReservationComplete;
    private SimpleObjectProperty<Session> selectedSessionProperty = new SimpleObjectProperty<>();
    private double prix = 0.0;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialiser les services
        try {
            sessionService = new SessionService();
            eventService = new EvenementService();
            
            // Configurer l'interface utilisateur de base
            setupUI();
            setupListeners();
            
            // Attendre que tous les composants soient chargés puis configurer les boutons du spinner
            Platform.runLater(() -> {
                try {
                    setupCustomSpinnerButtons();
                } catch (Exception e) {
                    System.err.println("Erreur lors de la configuration des boutons du spinner: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            showError("Erreur d'initialisation", "Impossible d'initialiser les services: " + e.getMessage());
        }
    }
    
    /**
     * Configure l'événement à réserver
     * @param event L'événement à réserver
     */
    public void setEvent(Evenement event) {
        this.event = event;
        if (event != null) {
            updateUI();
            loadSessions();
        }
    }
    
    /**
     * Configure le callback à exécuter après une réservation réussie
     * @param onComplete Action à exécuter
     */
    public void setOnReservationComplete(Runnable onComplete) {
        this.onReservationComplete = onComplete;
    }
    
    /**
     * Configure l'interface utilisateur de base
     */
    private void setupUI() {
        // Configuration du Spinner
        SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1);
        placesSpinner.setValueFactory(valueFactory);
        
        // Ajouter des écouteurs pour les actions des boutons
        btnCancel.setOnAction(e -> closeWindow());
        btnConfirm.setOnAction(e -> handleReservation());
        
        // Appliquer des effets d'animation
        AnimationUtils.addHoverEffect(btnConfirm);
        AnimationUtils.addHoverEffect(btnCancel);
    }
    
    /**
     * Configure les boutons personnalisés pour le spinner
     */
    private void setupCustomSpinnerButtons() {
        // Créer les boutons + et -
        Button plusButton = new Button("+");
        plusButton.setPrefWidth(40);
        plusButton.setPrefHeight(30);
        plusButton.getStyleClass().add("spinner-button");
        
        Button minusButton = new Button("-");
        minusButton.setPrefWidth(40);
        minusButton.setPrefHeight(30);
        minusButton.getStyleClass().add("spinner-button");
        
        // Configurer les actions des boutons
        plusButton.setOnAction(e -> {
            int currentValue = placesSpinner.getValue();
            if (currentValue < 10) {
                placesSpinner.getValueFactory().setValue(currentValue + 1);
            }
        });
        
        minusButton.setOnAction(e -> {
            int currentValue = placesSpinner.getValue();
            if (currentValue > 1) {
                placesSpinner.getValueFactory().setValue(currentValue - 1);
            }
        });
        
        // Appliquer des effets d'animation
        AnimationUtils.addHoverEffect(plusButton);
        AnimationUtils.addHoverEffect(minusButton);
        
        // Créer un conteneur pour les boutons et le spinner
        HBox spinnerControls = new HBox(10, minusButton, placesSpinner, plusButton);
        spinnerControls.setAlignment(Pos.CENTER_LEFT);
        
        // Remplacer le spinner par notre conteneur personnalisé
        VBox parent = (VBox) placesSpinner.getParent();
        if (parent != null) {
            int index = parent.getChildren().indexOf(placesSpinner);
            if (index >= 0) {
                parent.getChildren().remove(placesSpinner);
                parent.getChildren().add(index, spinnerControls);
                System.out.println("Spinner remplacé avec succès par les contrôles personnalisés");
            } else {
                System.err.println("Impossible de trouver le spinner dans son parent");
            }
        } else {
            System.err.println("Le parent du spinner est null");
        }
    }
    
    /**
     * Configure les écouteurs pour les changements
     */
    private void setupListeners() {
        // Écouteur pour le changement de session sélectionnée
        selectedSessionProperty.addListener((obs, oldVal, newVal) -> {
            selectedSession = newVal;
            updatePlacesInfo();
            updateTotalPrice();
        });
        
        // Écouteur pour le changement du nombre de places
        placesSpinner.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateTotalPrice();
        });
    }
    
    /**
     * Met à jour l'interface utilisateur avec les détails de l'événement
     */
    private void updateUI() {
        // Mettre à jour le nom et la date de l'événement
        eventName.setText(event.getTitre());
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        String dateStr = event.getDateD() != null ? event.getDateD().format(formatter) : "";
        eventDate.setText(dateStr);
        
        // Charger l'image de l'événement
        loadEventImage();
        
        // Mettre à jour le prix
        prix = event.getPrix() != null ? event.getPrix() : 0.0;
    }
    
    /**
     * Charge l'image de l'événement
     */
    private void loadEventImage() {
        try {
            String imagePath = event.getImage();
            
            if (imagePath != null && !imagePath.isEmpty()) {
                Image image;
                
                // Vérifier si c'est une URL ou un chemin local
                if (imagePath.startsWith("http") || imagePath.startsWith("www")) {
                    image = new Image(imagePath, true);
                } else {
                    // Image locale
                    File file = new File(imagePath);
                    if (file.exists()) {
                        image = new Image(file.toURI().toString());
                    } else {
                        // Image par défaut si le fichier n'existe pas
                        image = new Image(getClass().getResourceAsStream("/images/default_event.jpg"));
                    }
                }
                
                eventImage.setImage(image);
            } else {
                // Image par défaut
                eventImage.setImage(new Image(getClass().getResourceAsStream("/images/default_event.jpg")));
            }
        } catch (Exception e) {
            // En cas d'erreur, utiliser une image par défaut
            try {
                eventImage.setImage(new Image(getClass().getResourceAsStream("/images/default_event.jpg")));
            } catch (Exception ex) {
                // Ignorer
            }
        }
    }
    
    /**
     * Charge les sessions disponibles pour l'événement
     */
    private void loadSessions() {
        try {
            // Vider le conteneur de sessions
            sessionsContainer.getChildren().clear();
            
            // Récupérer les sessions pour cet événement
            List<Session> sessions = sessionService.getSessionsByEvent(event.getId());
            
            if (sessions.isEmpty()) {
                Label noSessionsLabel = new Label("Aucune session disponible pour cet événement.");
                noSessionsLabel.getStyleClass().add("empty-message");
                sessionsContainer.getChildren().add(noSessionsLabel);
                return;
            }
            
            // Créer des cartes pour chaque session
            ToggleGroup sessionGroup = new ToggleGroup();
            
            for (Session session : sessions) {
                HBox sessionCard = createSessionCard(session);
                RadioButton radioButton = new RadioButton();
                radioButton.setToggleGroup(sessionGroup);
                radioButton.setUserData(session);
                
                HBox container = new HBox(10, radioButton, sessionCard);
                container.setAlignment(Pos.CENTER_LEFT);
                container.getStyleClass().add("reservation-session-container");
                
                // Ajouter un effet de clic sur toute la boîte
                container.setOnMouseClicked(e -> {
                    radioButton.setSelected(true);
                });
                
                sessionsContainer.getChildren().add(container);
            }
            
            // Écouter les changements de sélection
            sessionGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
                if (newVal != null) {
                    selectedSessionProperty.set((Session) newVal.getUserData());
                } else {
                    selectedSessionProperty.set(null);
                }
            });
            
            // Sélectionner la première session par défaut
            if (!sessions.isEmpty() && sessionGroup.getToggles().size() > 0) {
                sessionGroup.selectToggle(sessionGroup.getToggles().get(0));
            }
            
        } catch (SQLException e) {
            showError("Erreur", "Impossible de charger les sessions: " + e.getMessage());
        }
    }
    
    /**
     * Crée une carte pour une session
     * @param session La session à afficher
     * @return Le composant de la carte de session
     */
    private HBox createSessionCard(Session session) {
        VBox infoContainer = new VBox(5);
        
        // Titre et date
        Label titleLabel = new Label(session.getTitre());
        titleLabel.getStyleClass().add("reservation-session-title");
        
        // Date et heure
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy à HH:mm");
        String dateStr = session.getDateDebut() != null ? session.getDateDebut().format(formatter) : "";
        Label dateLabel = new Label(dateStr);
        dateLabel.getStyleClass().add("reservation-session-date");
        
        // Places disponibles
        int placesDisponibles = session.getAvailableSeats();
        String placesText = placesDisponibles + " places disponibles";
        Label placesLabel = new Label(placesText);
        placesLabel.getStyleClass().add("reservation-session-places");
        
        // Ajouter au conteneur
        infoContainer.getChildren().addAll(titleLabel, dateLabel, placesLabel);
        
        // Créer la carte complète
        HBox sessionCard = new HBox(15);
        sessionCard.getStyleClass().add("reservation-session-card");
        sessionCard.setPadding(new Insets(10));
        sessionCard.getChildren().add(infoContainer);
        
        return sessionCard;
    }
    
    /**
     * Met à jour les informations sur les places disponibles
     */
    private void updatePlacesInfo() {
        if (selectedSession != null) {
            placesDisponibles.setText(selectedSession.getAvailableSeats() + " places");
            
            // Mettre à jour le spinner
            SpinnerValueFactory.IntegerSpinnerValueFactory valueFactory = 
                    (SpinnerValueFactory.IntegerSpinnerValueFactory) placesSpinner.getValueFactory();
            valueFactory.setMax(Math.min(10, selectedSession.getAvailableSeats()));
            
            // Désactiver le bouton si aucune place n'est disponible
            btnConfirm.setDisable(selectedSession.getAvailableSeats() <= 0);
        } else {
            placesDisponibles.setText("0 place");
            btnConfirm.setDisable(true);
        }
    }
    
    /**
     * Met à jour le prix total
     */
    private void updateTotalPrice() {
        if (selectedSession != null && placesSpinner.getValue() != null) {
            double totalPrice = prix * placesSpinner.getValue();
            prixTotal.setText(String.format("%.2f €", totalPrice));
        } else {
            prixTotal.setText("0.00 €");
        }
    }
    
    /**
     * Gère la validation du formulaire
     * @return true si le formulaire est valide
     */
    private boolean validateForm() {
        // Vérifier que tous les champs sont remplis
        if (nomField.getText().isEmpty() || 
            prenomField.getText().isEmpty() ||
            emailField.getText().isEmpty() ||
            telephoneField.getText().isEmpty()) {
            showError("Formulaire incomplet", "Veuillez remplir tous les champs d'information.");
            return false;
        }
        
        // Vérifier que l'email est valide
        if (!emailField.getText().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            showError("Email invalide", "Veuillez entrer une adresse email valide.");
            return false;
        }
        
        // Vérifier qu'une session est sélectionnée
        if (selectedSession == null) {
            showError("Aucune session sélectionnée", "Veuillez sélectionner une session.");
            return false;
        }
        
        // Vérifier que le nombre de places est valide
        if (placesSpinner.getValue() <= 0 || placesSpinner.getValue() > selectedSession.getAvailableSeats()) {
            showError("Nombre de places invalide", "Le nombre de places demandé n'est pas valide.");
            return false;
        }
        
        return true;
    }
    
    /**
     * Gère la réservation
     */
    private void handleReservation() {
        if (!validateForm()) {
            return;
        }
        
        try {
            // Simuler une réservation réussie
            Alert confirmAlert = new Alert(Alert.AlertType.INFORMATION);
            confirmAlert.setTitle("Réservation confirmée");
            confirmAlert.setHeaderText(null);
            confirmAlert.setContentText("Votre réservation a été confirmée avec succès!\n\n" +
                    "Un email de confirmation a été envoyé à " + emailField.getText());
            
            // Appliquer le style professionnel
            DialogPane dialogPane = confirmAlert.getDialogPane();
            dialogPane.getStylesheets().add(getClass().getResource("/professional_style.css").toExternalForm());
            
            confirmAlert.showAndWait();
            
            // Appeler le callback si nécessaire
            if (onReservationComplete != null) {
                onReservationComplete.run();
            }
            
            // Fermer la fenêtre
            closeWindow();
        } catch (Exception e) {
            showError("Erreur de réservation", "Une erreur est survenue lors de la réservation: " + e.getMessage());
        }
    }
    
    /**
     * Ferme la fenêtre
     */
    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
    }
    
    /**
     * Affiche une boîte de dialogue d'erreur
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        // Appliquer le style professionnel
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/professional_style.css").toExternalForm());
        
        alert.showAndWait();
    }
} 