package Controller;

import Entity.Evenement;
import Entity.Reservation;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import services.ReservationService;
import services.EvenementService;
import Utils.MainStyleFixer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MyReservationsController implements Initializable {
    
    @FXML private VBox reservationsContainer;
    @FXML private Button btnBack;
    
    private ReservationService reservationService;
    private EvenementService evenementService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Initialisation des services
            reservationService = new ReservationService();
            evenementService = new EvenementService();
            
            // Chargement des réservations
            loadReservations();
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur de connexion", 
                     "Impossible de se connecter à la base de données", 
                     e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Une erreur est survenue lors de l'initialisation", 
                     e.getMessage());
        }
    }
    
    private void loadReservations() {
        try {
            // Vider le conteneur
            reservationsContainer.getChildren().clear();
            
            // Récupérer les réservations de l'utilisateur (à adapter selon votre système d'authentification)
            int userId = 1; // ID utilisateur fictif, à remplacer par l'ID réel
            List<Reservation> reservations = reservationService.getReservationsByUser(userId);
            
            if (reservations.isEmpty()) {
                // Message si aucune réservation
                Label emptyLabel = new Label("Vous n'avez aucune réservation.");
                emptyLabel.setStyle("-fx-font-size: 18px; -fx-text-fill: #7f8c8d;");
                
                VBox emptyBox = new VBox(20, emptyLabel);
                emptyBox.setAlignment(Pos.CENTER);
                emptyBox.setPadding(new Insets(50));
                emptyBox.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 10;");
                
                reservationsContainer.getChildren().add(emptyBox);
            } else {
                // Titre des sections
                Label upcomingTitle = new Label("Réservations à venir");
                upcomingTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
                reservationsContainer.getChildren().add(upcomingTitle);
                
                // Parcourir les réservations et créer une carte pour chacune
                for (Reservation reservation : reservations) {
                    // Récupérer l'événement associé
                    Evenement event = evenementService.getOne(reservation.getEventId());
                    if (event != null) {
                        VBox reservationCard = createReservationCard(reservation, event);
                        reservationsContainer.getChildren().add(reservationCard);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Impossible de charger les réservations", 
                     e.getMessage());
        }
    }
    
    private VBox createReservationCard(Reservation reservation, Evenement event) {
        // Création de la carte
        VBox card = new VBox(15);
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0); -fx-background-radius: 10;");
        
        // Conteneur pour l'en-tête : titre et date
        HBox headerBox = new HBox(20);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        
        // Image de l'événement
        ImageView eventImage = new ImageView();
        eventImage.setFitWidth(80);
        eventImage.setFitHeight(80);
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
                    URL defaultImageUrl = getClass().getResource("/images/default-session.png");
                    if (defaultImageUrl != null) {
                        eventImage.setImage(new Image(defaultImageUrl.toExternalForm()));
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image: " + e.getMessage());
        }
        
        // Appliquer un clip arrondi à l'image
        Rectangle clip = new Rectangle(eventImage.getFitWidth(), eventImage.getFitHeight());
        clip.setArcWidth(15);
        clip.setArcHeight(15);
        eventImage.setClip(clip);
        
        // Informations de l'événement
        VBox eventInfoBox = new VBox(5);
        eventInfoBox.setAlignment(Pos.CENTER_LEFT);
        
        Label eventTitle = new Label(event.getTitre());
        eventTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        Label eventDate = new Label(event.getDateD() != null ? event.getDateD().format(formatter) : "Date non spécifiée");
        eventDate.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
        
        Label eventLocation = new Label(event.getLocation() != null ? event.getLocation() : "Lieu non spécifié");
        eventLocation.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
        
        eventInfoBox.getChildren().addAll(eventTitle, eventDate, eventLocation);
        
        headerBox.getChildren().addAll(eventImage, eventInfoBox);
        
        // Détails de la réservation
        VBox detailsBox = new VBox(10);
        detailsBox.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 15; -fx-background-radius: 5;");
        
        Label placesLabel = new Label("Nombre de places : " + reservation.getNombrePlaces());
        placesLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e;");
        
        Label reservationDateLabel = new Label("Réservé le : " + reservation.getDateReservation().format(formatter));
        reservationDateLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e;");
        
        detailsBox.getChildren().addAll(placesLabel, reservationDateLabel);
        
        // Boutons d'action
        HBox actionsBox = new HBox(10);
        actionsBox.setAlignment(Pos.CENTER_RIGHT);
        
        Button modifyButton = new Button("Modifier");
        modifyButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        modifyButton.setOnAction(e -> modifyReservation(reservation, event));
        
        Button cancelButton = new Button("Annuler");
        cancelButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
        cancelButton.setOnAction(e -> cancelReservation(reservation, event));
        
        actionsBox.getChildren().addAll(modifyButton, cancelButton);
        
        // Assembler tous les éléments
        card.getChildren().addAll(headerBox, detailsBox, actionsBox);
        
        return card;
    }
    
    private void modifyReservation(Reservation reservation, Evenement event) {
        try {
            // Créer une boîte de dialogue pour modifier le nombre de places
            Dialog<Integer> dialog = new Dialog<>();
            dialog.setTitle("Modifier la réservation");
            dialog.setHeaderText("Modifier le nombre de places pour : " + event.getTitre());
            
            // Configurer les boutons
            ButtonType saveButtonType = new ButtonType("Enregistrer", ButtonBar.ButtonData.OK_DONE);
            ButtonType cancelButtonType = new ButtonType("Annuler", ButtonBar.ButtonData.CANCEL_CLOSE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, cancelButtonType);
            
            // Créer le contenu
            VBox content = new VBox(15);
            content.setPadding(new Insets(20));
            
            Label placesLabel = new Label("Nombre de places :");
            
            Spinner<Integer> placesSpinner = new Spinner<>(1, event.getNbPlace() + reservation.getNombrePlaces(), reservation.getNombrePlaces());
            placesSpinner.setEditable(true);
            
            content.getChildren().addAll(placesLabel, placesSpinner);
            
            dialog.getDialogPane().setContent(content);
            
            // Convertir le résultat
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == saveButtonType) {
                    return placesSpinner.getValue();
                }
                return null;
            });
            
            // Appliquer des styles
            MainStyleFixer.styleProfessionalDialog(dialog.getDialogPane());
            
            // Afficher la boîte de dialogue et traiter le résultat
            Optional<Integer> result = dialog.showAndWait();
            result.ifPresent(places -> {
                try {
                    // Mettre à jour la réservation
                    int difference = places - reservation.getNombrePlaces();
                    if (difference != 0) {
                        reservationService.updateReservation(reservation.getId(), places);
                        
                        // Mettre à jour le nombre de places disponibles dans l'événement
                        event.setNbPlace(event.getNbPlace() - difference);
                        evenementService.modifier(event);
                        
                        // Recharger les réservations
                        loadReservations();
                        
                        // Afficher un message de confirmation
                        showAlert(Alert.AlertType.INFORMATION, "Modification réussie", 
                                 "Réservation modifiée", 
                                 "Votre réservation a été modifiée avec succès.");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Erreur", 
                             "Impossible de modifier la réservation", 
                             e.getMessage());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Impossible de modifier la réservation", 
                     e.getMessage());
        }
    }
    
    private void cancelReservation(Reservation reservation, Evenement event) {
        try {
            // Demander confirmation
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("Confirmer l'annulation");
            confirmDialog.setHeaderText("Êtes-vous sûr de vouloir annuler cette réservation ?");
            confirmDialog.setContentText("Cette action est irréversible.");
            
            // Appliquer des styles
            MainStyleFixer.styleProfessionalDialog(confirmDialog.getDialogPane());
            
            Optional<ButtonType> result = confirmDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Annuler la réservation
                reservationService.deleteReservation(reservation.getId());
                
                // Mettre à jour le nombre de places disponibles dans l'événement
                event.setNbPlace(event.getNbPlace() + reservation.getNombrePlaces());
                evenementService.modifier(event);
                
                // Recharger les réservations
                loadReservations();
                
                // Afficher un message de confirmation
                showAlert(Alert.AlertType.INFORMATION, "Annulation réussie", 
                         "Réservation annulée", 
                         "Votre réservation a été annulée avec succès.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Impossible d'annuler la réservation", 
                     e.getMessage());
        }
    }
    
    @FXML
    private void handleBack() {
        try {
            // Recharger l'interface utilisateur principale
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) btnBack.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            
            // Appliquer le style professionnel
            MainStyleFixer.applyProfessionalStyle(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Impossible de retourner à l'écran principal", 
                     e.getMessage());
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
} 