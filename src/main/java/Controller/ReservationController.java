package Controller;

import Entity.Evenement;
import Entity.Reservation;
import Entity.Session;
import Utils.LogUtils;
import Utils.MainStyleFixer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import services.EvenementService;
import services.ReservationService;
import services.SessionService;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Contrôleur pour la gestion des réservations
 * Permet aux utilisateurs de créer, modifier et annuler des réservations
 */
public class ReservationController implements Initializable {

    @FXML private VBox mainContainer;
    @FXML private Label eventTitle;
    @FXML private Label eventDate;
    @FXML private Label eventLocation;
    @FXML private VBox sessionsContainer;
    @FXML private TextField nbPlacesField;
    @FXML private Button btnConfirm;
    @FXML private Button btnCancel;
    
    private Evenement event;
    private Session selectedSession;
    private int userId = 1; // Utilisateur simulé pour la démonstration
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy");
    
    private EvenementService evenementService;
    private SessionService sessionService;
    private ReservationService reservationService;
    
    // Callback pour notifier le contrôleur parent que la réservation est terminée
    private Runnable onReservationCompleteCallback;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Initialisation des services
            evenementService = new EvenementService();
            sessionService = new SessionService();
            reservationService = new ReservationService();
            
            // Configurer le champ de nombre de places
            nbPlacesField.textProperty().addListener((observable, oldValue, newValue) -> {
                if (!newValue.matches("\\d*")) {
                    nbPlacesField.setText(newValue.replaceAll("[^\\d]", ""));
                }
            });
            
            // Par défaut, nous commençons avec 1 place
            nbPlacesField.setText("1");
            
            // Désactiver le bouton de confirmation jusqu'à ce qu'une session soit sélectionnée
            btnConfirm.setDisable(true);
            
        } catch (SQLException e) {
            showError("Erreur d'initialisation", "Impossible de charger les services de réservation: " + e.getMessage());
        }
    }
    
    /**
     * Définit l'événement pour cette réservation
     */
    public void setEvent(Evenement event) {
        this.event = event;
        
        // Mettre à jour l'interface avec les détails de l'événement
        eventTitle.setText(event.getTitre());
        eventDate.setText("Date: " + event.getDateD().format(formatter));
        eventLocation.setText("Lieu: " + event.getLocation());
        
        // Charger les sessions disponibles
        loadSessions();
    }
    
    /**
     * Charge les sessions disponibles pour cet événement
     */
    private void loadSessions() {
        LogUtils.info("ReservationController", "Chargement des sessions pour l'événement: " + 
                    (event != null ? event.getId() + " - " + event.getTitre() : "null"));
        
        try {
            if (event == null) {
                LogUtils.error("ReservationController", "L'événement est null, impossible de charger les sessions", null);
                showNoSessionsMessage();
                return;
            }

            // Récupérer toutes les sessions pour cet événement
            List<Session> sessions = sessionService.getSessionsByEvent(event.getId());
            if (sessions == null) {
                LogUtils.error("ReservationController", "La liste des sessions est null", null);
                sessions = new ArrayList<>();
            }
            
            LogUtils.info("ReservationController", "Nombre de sessions récupérées: " + sessions.size());
            
            if (sessions.isEmpty()) {
                LogUtils.info("ReservationController", "Aucune session disponible pour cet événement");
                showNoSessionsMessage();
                return;
            }
            
            // Vider le conteneur de sessions
            if (sessionsContainer != null) {
                sessionsContainer.getChildren().clear();
            } else {
                LogUtils.error("ReservationController", "Le conteneur de sessions est null", null);
                return;
            }
            
            // Nombre de sessions avec des places disponibles
            int availableSessions = 0;
            
            // Ajouter une carte pour chaque session disponible
            for (Session session : sessions) {
                if (session == null) {
                    LogUtils.error("ReservationController", "Session null trouvée dans la liste", null);
                    continue;
                }
                
                LogUtils.info("ReservationController", "Traitement de la session: " + session.getId() + 
                             ", Titre: " + (session.getTitre() != null ? session.getTitre() : "Sans titre") + 
                             ", Capacité: " + session.getCapacity());
                
                // Ne montrer que les sessions qui ont encore des places disponibles
                if (session.getCapacity() > 0) {
                    VBox sessionCard = createSessionCard(session);
                    if (sessionCard != null) {
                        sessionsContainer.getChildren().add(sessionCard);
                        availableSessions++;
                    }
                } else {
                    LogUtils.info("ReservationController", "Session " + session.getId() + " ignorée (aucune place disponible)");
                }
            }
            
            LogUtils.info("ReservationController", "Nombre de sessions affichées: " + availableSessions);
            
            // Si aucune session n'a de places disponibles, afficher un message
            if (availableSessions == 0) {
                LogUtils.info("ReservationController", "Aucune session avec des places disponibles");
                showNoSessionsMessage();
            }
            
        } catch (SQLException e) {
            LogUtils.error("ReservationController", "Erreur SQL lors du chargement des sessions", e);
            showError("Erreur de chargement", "Impossible de charger les sessions: " + e.getMessage());
        } catch (Exception e) {
            LogUtils.error("ReservationController", "Erreur inattendue lors du chargement des sessions", e);
            showError("Erreur", "Une erreur inattendue est survenue: " + e.getMessage());
        }
    }
    
    /**
     * Crée une carte visuelle pour afficher une session
     */
    private VBox createSessionCard(Session session) {
        try {
            LogUtils.info("ReservationController", "Création d'une carte pour la session: " + session.getId() + " - " + 
                        (session.getTitre() != null ? session.getTitre() : "Sans titre"));
            
            VBox card = new VBox(10);
            card.getStyleClass().add("reservation-session-card");
            card.setPadding(new Insets(15));
            card.setUserData(session);
            
            // Titre de la session
            Label title = new Label(session.getTitre() != null ? session.getTitre() : "Sans titre");
            title.setFont(Font.font("System", FontWeight.BOLD, 16));
            
            // Date et heure
            String formattedDateTime = "Date non définie";
            if (session.getStartTime() != null) {
                formattedDateTime = session.getStartTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
            }
            Label datetime = new Label("Date: " + formattedDateTime);
            
            // Lieu
            Label location = new Label("Lieu: " + (session.getLocation() != null ? session.getLocation() : "Non défini"));
            
            // Capacité
            int capacityValue = session.getCapacity();
            Label capacityLabel = new Label("Places disponibles: " + capacityValue);
            
            // Ajouter tous les éléments à la carte
            card.getChildren().addAll(title, datetime, location, capacityLabel);
            
            // Gestionnaire d'événements pour la sélection
            card.setOnMouseClicked(e -> {
                try {
                    // Déselectionner toutes les autres cartes
                    sessionsContainer.getChildren().forEach(node -> {
                        if (node instanceof VBox) {
                            node.getStyleClass().remove("selected");
                        }
                    });
                    
                    // Sélectionner cette carte
                    card.getStyleClass().add("selected");
                    
                    // Mémoriser la session sélectionnée
                    selectedSession = (Session) card.getUserData();
                    
                    // Activer le bouton de confirmation
                    btnConfirm.setDisable(false);
                    
                    LogUtils.info("ReservationController", "Session sélectionnée: " + selectedSession.getId() + " - " + 
                            (selectedSession.getTitre() != null ? selectedSession.getTitre() : "Sans titre"));
                } catch (Exception ex) {
                    LogUtils.error("ReservationController", "Erreur lors de la sélection de la session", ex);
                }
            });
            
            return card;
        } catch (Exception e) {
            LogUtils.error("ReservationController", "Erreur lors de la création de la carte pour la session " + 
                         (session != null ? session.getId() : "null"), e);
            
            // Renvoyer une carte par défaut en cas d'erreur
            VBox errorCard = new VBox(10);
            errorCard.setPadding(new Insets(15));
            errorCard.setStyle("-fx-background-color: #ffeeee; -fx-border-color: #ffcccc; -fx-border-width: 1;");
            
            Label errorLabel = new Label("Erreur: Impossible d'afficher cette session");
            errorLabel.setTextFill(Color.RED);
            
            errorCard.getChildren().add(errorLabel);
            return errorCard;
        }
    }
    
    /**
     * Affiche un message quand aucune session n'est disponible
     */
    private void showNoSessionsMessage() {
        sessionsContainer.getChildren().clear();
        
        Label message = new Label("Aucune session disponible pour cet événement.");
        message.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d;");
        
        sessionsContainer.getChildren().add(message);
        btnConfirm.setDisable(true);
    }
    
    /**
     * Gère la confirmation de la réservation
     */
    @FXML
    private void handleConfirmation() {
        LogUtils.info("ReservationController", "Début du processus de confirmation de réservation");
        
        if (selectedSession == null) {
            LogUtils.error("ReservationController", "Aucune session sélectionnée", null);
            showError("Sélection requise", "Veuillez sélectionner une session");
            return;
        }
        
        try {
            int nbPlaces = Integer.parseInt(nbPlacesField.getText());
            LogUtils.info("ReservationController", "Nombre de places demandées: " + nbPlaces);
            
            if (nbPlaces <= 0) {
                LogUtils.error("ReservationController", "Nombre de places invalide: " + nbPlaces, null);
                showError("Nombre de places invalide", "Le nombre de places doit être supérieur à 0");
                return;
            }
            
            if (nbPlaces > selectedSession.getCapacity()) {
                LogUtils.error("ReservationController", "Capacité insuffisante. Demandé: " + nbPlaces + 
                              ", Disponible: " + selectedSession.getCapacity(), null);
                showError("Nombre de places insuffisant", 
                         "Il n'y a pas assez de places disponibles pour cette session");
                return;
            }
            
            // Calculer le prix total
            double prixUnitaire = 0.0;
            if (event != null && event.getPrix() != null) {
                prixUnitaire = event.getPrix();
            }
            double prixTotal = prixUnitaire * nbPlaces;
            LogUtils.info("ReservationController", "Prix unitaire: " + prixUnitaire + ", Prix total: " + prixTotal);
            
            // Créer l'objet réservation
            Reservation reservation = new Reservation(
                userId,
                event != null ? event.getId() : 0,
                selectedSession.getId(),
                nbPlaces
            );
            
            // Définir le prix total et le statut
            reservation.setPrixTotal(prixTotal);
            reservation.setStatut("Confirmée");
            
            LogUtils.info("ReservationController", "Tentative d'ajout de réservation: " + reservation);
            
            try {
                // Enregistrer la réservation
                reservationService.add(reservation);
                LogUtils.info("ReservationController", "Réservation ajoutée avec succès. ID: " + reservation.getId());
                
                // Mettre à jour la capacité de la session
                selectedSession.setCapacity(selectedSession.getCapacity() - nbPlaces);
                sessionService.updateSession(selectedSession);
                LogUtils.info("ReservationController", "Capacité de la session mise à jour. Nouvelle capacité: " + 
                             selectedSession.getCapacity());
                
                // Afficher une confirmation
                showSuccess("Réservation confirmée", 
                          "Votre réservation a été enregistrée avec succès!\n" +
                          "Nombre de places: " + nbPlaces + "\n" +
                          "Prix total: " + prixTotal + " €");
                
                // Fermer la fenêtre
                closeWindow();
                
                // Appeler le callback si défini
                if (onReservationCompleteCallback != null) {
                    LogUtils.info("ReservationController", "Exécution du callback de fin de réservation");
                    onReservationCompleteCallback.run();
                }
            } catch (SQLException ex) {
                LogUtils.error("ReservationController", "Erreur SQL lors de la réservation", ex);
                showError("Erreur de base de données", 
                         "Impossible de finaliser la réservation.\nDétails: " + ex.getMessage());
            }
            
        } catch (NumberFormatException e) {
            LogUtils.error("ReservationController", "Format de nombre invalide", e);
            showError("Entrée invalide", "Veuillez entrer un nombre valide de places");
        } catch (Exception e) {
            LogUtils.error("ReservationController", "Erreur inattendue lors de la réservation", e);
            showError("Erreur inattendue", "Une erreur s'est produite: " + e.getMessage());
        }
    }
    
    /**
     * Gère l'annulation de la réservation
     */
    @FXML
    private void handleCancel() {
        closeWindow();
    }
    
    /**
     * Définit le callback à exécuter lorsque la réservation est terminée
     */
    public void setOnReservationComplete(Runnable callback) {
        this.onReservationCompleteCallback = callback;
    }
    
    /**
     * Ferme la fenêtre actuelle
     */
    private void closeWindow() {
        Stage stage = (Stage) btnCancel.getScene().getWindow();
        stage.close();
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
        dialogPane.getStylesheets().add(getClass().getResource("/professional_style.css").toExternalForm());
        
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
        dialogPane.getStylesheets().add(getClass().getResource("/professional_style.css").toExternalForm());
        
        alert.showAndWait();
    }
} 