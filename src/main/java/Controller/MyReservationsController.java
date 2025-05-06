package Controller;

import Entity.Evenement;
import Entity.Reservation;
import Entity.Session;
import Utils.MainStyleFixer;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import services.EvenementService;
import services.ReservationService;
import services.SessionService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Contrôleur pour la gestion des réservations d'un utilisateur
 * Permet de visualiser, modifier et annuler ses réservations
 */
public class MyReservationsController implements Initializable {

    @FXML private Button btnBack;
    @FXML private VBox reservationsContainer;
    
    private int userId = 1; // Utilisateur simulé pour la démonstration
    
    private ReservationService reservationService;
    private EvenementService evenementService;
    private SessionService sessionService;
    
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Initialisation des services
            reservationService = new ReservationService();
            evenementService = new EvenementService();
            sessionService = new SessionService();
            
            // Appliquer les styles modernes
            setupSceneListener();
            
            // Chargement des réservations
            loadReservations();
            
        } catch (SQLException e) {
            showError("Erreur d'initialisation", "Impossible de charger les services: " + e.getMessage());
        }
    }
    
    /**
     * Configure un listener pour la scène pour appliquer les styles modernes
     */
    private void setupSceneListener() {
        if (reservationsContainer != null) {
            reservationsContainer.sceneProperty().addListener((obs, oldScene, newScene) -> {
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
    
    /**
     * Charge les réservations de l'utilisateur
     */
    private void loadReservations() {
        try {
            // Récupérer les réservations de l'utilisateur
            List<Reservation> reservations = reservationService.getReservationsByUser(userId);
            
            if (reservations.isEmpty()) {
                showNoReservationsMessage();
                return;
            }
            
            // Vider le conteneur
            reservationsContainer.getChildren().clear();
            
            // Ajouter chaque réservation
            for (Reservation reservation : reservations) {
                try {
                    // Récupérer les informations associées
                    Evenement event = evenementService.getOne(reservation.getEventId());
                    Session session = sessionService.getOneById(reservation.getSessionId());
                    
                    if (event != null && session != null) {
                        VBox reservationCard = createReservationCard(reservation, event, session);
                        reservationsContainer.getChildren().add(reservationCard);
                    }
                } catch (Exception e) {
                    System.err.println("Erreur lors du chargement de la réservation " + 
                                      reservation.getId() + ": " + e.getMessage());
                }
            }
            
        } catch (SQLException e) {
            showError("Erreur de chargement", "Impossible de charger vos réservations: " + e.getMessage());
        }
    }
    
    /**
     * Affiche un message quand l'utilisateur n'a pas de réservations
     */
    private void showNoReservationsMessage() {
        reservationsContainer.getChildren().clear();
        
        VBox messageBox = new VBox(15);
        messageBox.setAlignment(Pos.CENTER);
        messageBox.setPadding(new Insets(50, 0, 0, 0));
        
        Label message = new Label("Vous n'avez pas encore de réservations.");
        message.setStyle("-fx-font-size: 18px; -fx-text-fill: #7f8c8d;");
        
        Button btnExplore = new Button("Découvrir les événements");
        btnExplore.getStyleClass().add("primary-button");
        btnExplore.setOnAction(e -> navigateToEvents());
        
        messageBox.getChildren().addAll(message, btnExplore);
        reservationsContainer.getChildren().add(messageBox);
    }
    
    /**
     * Crée une carte visuelle pour afficher une réservation
     */
    private VBox createReservationCard(Reservation reservation, Evenement event, Session session) {
        VBox card = new VBox(15);
        card.getStyleClass().add("card");
        card.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 10); "
                     + "-fx-background-radius: 10; -fx-padding: 15;");
        card.setPadding(new Insets(20));
        
        // En-tête avec le titre de l'événement
        Label eventTitle = new Label(event.getTitre());
        eventTitle.getStyleClass().add("card-title");
        eventTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Ajouter un effet de survol
        card.setOnMouseEntered(e -> {
            card.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 12, 0, 0, 12); "
                        + "-fx-background-radius: 10; -fx-padding: 15;");
        });
        card.setOnMouseExited(e -> {
            card.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 10); "
                        + "-fx-background-radius: 10; -fx-padding: 15;");
        });
        
        // Informations sur la session
        Label sessionInfo = new Label("Session: " + session.getTitre());
        sessionInfo.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #3498db;");
        
        // Date et heure
        Label datetime = new Label("Date: " + session.getStartTime().format(dateFormatter));
        datetime.setStyle("-fx-text-fill: #7f8c8d;");
        
        // Lieu
        Label location = new Label("Lieu: " + session.getLocation());
        location.setStyle("-fx-text-fill: #7f8c8d;");
        
        // Nombre de places
        HBox detailsBox = new HBox(30);
        detailsBox.setAlignment(Pos.CENTER_LEFT);
        
        Label places = new Label("Places: " + reservation.getNombrePlaces());
        places.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        detailsBox.getChildren().addAll(places);
        
        // Statut de la réservation avec badge
        HBox statusBox = new HBox(10);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        
        Label statusLabel = new Label("Statut:");
        statusLabel.setStyle("-fx-text-fill: #7f8c8d;");
        
        Label statusValue = new Label(reservation.getStatut());
        statusValue.setStyle("-fx-padding: 3 10; -fx-background-radius: 10; -fx-font-weight: bold;");
        
        if ("Confirmée".equals(reservation.getStatut())) {
            statusValue.getStyleClass().add("badge-success");
            statusValue.setStyle(statusValue.getStyle() + "-fx-background-color: #2ecc71; -fx-text-fill: white;");
        } else if ("Annulée".equals(reservation.getStatut())) {
            statusValue.getStyleClass().add("badge-danger");
            statusValue.setStyle(statusValue.getStyle() + "-fx-background-color: #e74c3c; -fx-text-fill: white;");
        } else {
            statusValue.getStyleClass().add("badge-warning");
            statusValue.setStyle(statusValue.getStyle() + "-fx-background-color: #f39c12; -fx-text-fill: white;");
        }
        
        statusBox.getChildren().addAll(statusLabel, statusValue);
        
        // Date de réservation
        Label reservationDate = new Label("Réservé le: " + 
                                        reservation.getDateReservation().format(dateFormatter));
        reservationDate.setStyle("-fx-font-size: 12px; -fx-text-fill: #95a5a6;");
        
        // Séparateur
        Separator separator = new Separator();
        separator.setStyle("-fx-background-color: #ecf0f1;");
        
        // Boutons d'action
        HBox actionsBox = new HBox(10);
        actionsBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button btnDetails = new Button("Détails");
        btnDetails.getStyleClass().add("button-info");
        btnDetails.setOnAction(e -> showEventDetails(event.getId()));
        
        Button btnModify = new Button("Modifier");
        btnModify.getStyleClass().add("button-warning");
        btnModify.setOnAction(e -> modifyReservation(reservation, event, session));
        
        Button btnCancel = new Button("Annuler");
        btnCancel.getStyleClass().add("button-danger");
        btnCancel.setOnAction(e -> cancelReservation(reservation));
        
        // Désactiver les boutons si la réservation est déjà annulée
        if ("Annulée".equals(reservation.getStatut())) {
            btnModify.setDisable(true);
            btnCancel.setDisable(true);
        }
        
        actionsBox.getChildren().addAll(btnDetails, btnModify, btnCancel);
        
        // Ajouter tous les éléments à la carte
        card.getChildren().addAll(
            eventTitle, sessionInfo, datetime, location, 
            detailsBox, statusBox, reservationDate, separator, actionsBox
        );
        
        return card;
    }
    
    /**
     * Ouvre la fenêtre de détails d'un événement
     */
    private void showEventDetails(int eventId) {
        try {
            // Récupérer l'événement pour le passer au contrôleur
            Evenement event = evenementService.getOne(eventId);
            if (event == null) {
                showError("Erreur", "Événement non trouvé");
                return;
            }
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EventDetails.fxml"));
            Parent root = loader.load();
            
            EventDetailsController controller = loader.getController();
            controller.setEvent(event);
            
            Stage stage = new Stage();
            stage.setTitle("Détails de l'événement");
            
            Scene scene = new Scene(root);
            MainStyleFixer.applyProfessionalStyle(scene);
            
            stage.setScene(scene);
            stage.show();
            
        } catch (IOException e) {
            showError("Erreur", "Impossible d'afficher les détails de l'événement: " + e.getMessage());
        }
    }
    
    /**
     * Permet à l'utilisateur de modifier une réservation
     */
    private void modifyReservation(Reservation reservation, Evenement event, Session session) {
        // Créer une boîte de dialogue
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Modifier la réservation");
        dialog.setHeaderText("Modifier le nombre de places");
        
        // Boutons
        ButtonType confirmButtonType = new ButtonType("Modifier", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);
        
        // Créer le contenu
        VBox content = new VBox(15);
        content.setPadding(new Insets(20, 10, 10, 10));
        
        Label infoLabel = new Label("Réservation pour: " + event.getTitre());
        infoLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        Label sessionLabel = new Label("Session: " + session.getTitre());
        
        // Nombre de places
        HBox placesBox = new HBox(10);
        placesBox.setAlignment(Pos.CENTER_LEFT);
        
        Label placesLabel = new Label("Nombre de places:");
        Spinner<Integer> placesSpinner = new Spinner<>(1, 
                                                      session.getCapacity() + reservation.getNombrePlaces(), 
                                                      reservation.getNombrePlaces());
        placesSpinner.setEditable(true);
        placesSpinner.setPrefWidth(100);
        
        placesBox.getChildren().addAll(placesLabel, placesSpinner);
        
        placesSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            // Suppression de la mise à jour du prix total
        });
        
        content.getChildren().addAll(infoLabel, sessionLabel, placesBox);
        dialog.getDialogPane().setContent(content);
        
        // Convertir le résultat
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == confirmButtonType) {
                return placesSpinner.getValue();
            }
            return null;
        });
        
        // Appliquer le style
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(
            getClass().getResource("/professional_style.css").toExternalForm()
        );
        
        // Afficher la boîte de dialogue
        Optional<Integer> result = dialog.showAndWait();
        
        result.ifPresent(newPlacesCount -> {
            try {
                // Si le nombre a changé
                if (newPlacesCount != reservation.getNombrePlaces()) {
                    int difference = newPlacesCount - reservation.getNombrePlaces();
                    
                    // Mettre à jour la réservation
                    reservation.setNombrePlaces(newPlacesCount);
                    reservationService.update(reservation);
                    
                    // Mettre à jour la capacité de la session
                    session.setCapacity(session.getCapacity() - difference);
                    sessionService.updateSession(session);
                    
                    // Recharger les réservations
                    loadReservations();
                    
                    showSuccess("Réservation modifiée", 
                              "Votre réservation a été modifiée avec succès!");
                }
            } catch (SQLException e) {
                showError("Erreur", "Impossible de modifier la réservation: " + e.getMessage());
            }
        });
    }
    
    /**
     * Permet à l'utilisateur d'annuler une réservation
     */
    private void cancelReservation(Reservation reservation) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Annuler la réservation");
        alert.setHeaderText("Êtes-vous sûr de vouloir annuler cette réservation?");
        alert.setContentText("Cette action ne peut pas être annulée.");
        
        // Appliquer le style
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
            getClass().getResource("/professional_style.css").toExternalForm()
        );
        
        Optional<ButtonType> result = alert.showAndWait();
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Récupérer la session
                Session session = sessionService.getOneById(reservation.getSessionId());
                
                // Mettre à jour le statut de la réservation
                reservation.setStatut("Annulée");
                reservationService.update(reservation);
                
                // Remettre les places dans la capacité de la session
                if (session != null) {
                    session.setCapacity(session.getCapacity() + reservation.getNombrePlaces());
                    sessionService.updateSession(session);
                }
                
                // Recharger les réservations
                loadReservations();
                
                showSuccess("Réservation annulée", 
                          "Votre réservation a été annulée avec succès!");
                
            } catch (SQLException e) {
                showError("Erreur", "Impossible d'annuler la réservation: " + e.getMessage());
            }
        }
    }
    
    /**
     * Retourne à la vue des événements
     */
    @FXML
    private void handleBack() {
        try {
            // Charger l'interface utilisateur principale
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface.fxml"));
            Parent root = loader.load();
            
            UserInterfaceController controller = loader.getController();
            
            // Obtenir la scène actuelle
            Scene scene = reservationsContainer.getScene();
            if (scene != null) {
                // Appliquer le style moderne
                Utils.MainStyleFixer.applyModernDesign(scene);
                
                // Mettre à jour la scène avec la nouvelle vue
                scene.setRoot(root);
                
                // Charger automatiquement la vue des événements
                controller.showEvents();
                
                System.out.println("Retour à l'interface principale réussi");
            } else {
                System.err.println("Erreur: Impossible de récupérer la scène");
            }
            
        } catch (Exception e) {
            System.err.println("Erreur lors du retour à l'interface principale: " + e.getMessage());
            e.printStackTrace();
            showError("Erreur de navigation", "Impossible de revenir à l'écran d'accueil: " + e.getMessage());
        }
    }
    
    /**
     * Navigue vers la vue des événements
     */
    private void navigateToEvents() {
        try {
            // Charger l'interface utilisateur principale
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface.fxml"));
            Parent root = loader.load();
            
            UserInterfaceController controller = loader.getController();
            
            // Obtenir la scène actuelle
            Scene scene = reservationsContainer.getScene();
            if (scene != null) {
                // Appliquer le style moderne
                Utils.MainStyleFixer.applyModernDesign(scene);
                
                // Mettre à jour la scène avec la nouvelle vue
                scene.setRoot(root);
                
                // Charger automatiquement la vue des événements
                controller.showEvents();
                
                System.out.println("Navigation vers les événements réussie");
            } else {
                System.err.println("Erreur: Impossible de récupérer la scène");
            }
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la navigation vers les événements: " + e.getMessage());
            e.printStackTrace();
            showError("Erreur de navigation", "Impossible de naviguer vers les événements: " + e.getMessage());
        }
    }
    
    /**
     * Affiche une alerte d'erreur
     */
    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(title);
        alert.setContentText(message);
        
        // Appliquer le style
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
            getClass().getResource("/professional_style.css").toExternalForm()
        );
        
        alert.showAndWait();
    }
    
    /**
     * Affiche une alerte de succès
     */
    private void showSuccess(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(title);
        alert.setContentText(message);
        
        // Appliquer le style
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
            getClass().getResource("/professional_style.css").toExternalForm()
        );
        
        alert.showAndWait();
    }
} 