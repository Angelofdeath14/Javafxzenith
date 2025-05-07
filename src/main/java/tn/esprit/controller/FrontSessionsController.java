package tn.esprit.controller;


import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import tn.esprit.entities.Evenement;
import tn.esprit.entities.Session;
import tn.esprit.service.EvenementService;
import tn.esprit.service.SessionService;
import tn.esprit.utils.MyDataBase;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class FrontSessionsController implements Initializable {
    @FXML private Label eventTitleLabel;
    @FXML private Label eventDetailsLabel;
    @FXML private Button btnRetour;
    @FXML private Button btnViewEvents;
    @FXML private VBox sessionsContainer;

    private Evenement evenement;
    private final SessionService sessionService = new SessionService();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private EvenementService evenementService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            evenementService = new EvenementService();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void setEvenement(Evenement evenement) {
        this.evenement = evenement;
        eventTitleLabel.setText("Sessions de l'événement : " + evenement.getTitre());
        updateEventDetails();
        loadSessions();
    }

    private void updateEventDetails() {
        try {
            // Création du texte pour les informations de base de l'événement
            String details = String.format(
                "Type: %s\n" +
                "Lieu: %s\n" +
                "Date de début: %s\n" +
                "Date de fin: %s\n" +
                "Places disponibles: %d",
                evenement.getType(),
                evenement.getLocation(),
                evenement.getDateD().format(formatter),
                evenement.getDateF().format(formatter),
                evenement.getNbPlace()
            );
            
            eventDetailsLabel.setText(details);
            
        } catch(Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur d'affichage", 
                    "Impossible de charger les détails de l'événement.");
        }
    }

    private void loadSessions() {
        try {
            // Vider le conteneur de sessions
            sessionsContainer.getChildren().clear();
            
            // Récupérer les sessions de l'événement
            List<Session> sessions = sessionService.getSessionsByEvent(evenement.getId());
            
            // Conteneur principal pour les sessions
            VBox mainSessionsContainer = new VBox(15);
            mainSessionsContainer.setPadding(new Insets(20));
            mainSessionsContainer.setStyle("-fx-background-color: white;");
            
            // Créer le FlowPane pour les cartes
            FlowPane cardsContainer = new FlowPane();
            cardsContainer.setHgap(20);
            cardsContainer.setVgap(20);
            cardsContainer.setPadding(new Insets(10));
            cardsContainer.setPrefWidth(Region.USE_COMPUTED_SIZE);
            cardsContainer.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 10;");
            
            if (sessions.isEmpty()) {
                // Message d'information simple sans bouton d'ajout
                VBox emptyStateBox = new VBox(15);
                emptyStateBox.setAlignment(Pos.CENTER);
                emptyStateBox.setPadding(new Insets(50, 20, 50, 20));
                emptyStateBox.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 10;");
                
                Label iconLabel = new Label("📅");
                iconLabel.setStyle("-fx-font-size: 48px;");
                
                Label emptyLabel = new Label("Aucune session disponible pour cet événement");
                emptyLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
                emptyLabel.setTextFill(Color.web("#2c3e50"));
                
                Label infoLabel = new Label("Veuillez revenir ultérieurement");
                infoLabel.setFont(Font.font("System", 14));
                infoLabel.setTextFill(Color.web("#7f8c8d"));
                
                emptyStateBox.getChildren().addAll(iconLabel, emptyLabel, infoLabel);
                mainSessionsContainer.getChildren().add(emptyStateBox);
            } else {
                // Créer une carte pour chaque session
                for (Session session : sessions) {
                    VBox sessionCard = createSessionCard(session);
                    cardsContainer.getChildren().add(sessionCard);
                }
                mainSessionsContainer.getChildren().add(cardsContainer);
            }
            
            sessionsContainer.getChildren().add(mainSessionsContainer);
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement", 
                     "Une erreur est survenue lors du chargement des sessions: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FrontEvents.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) btnRetour.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de navigation", 
                     "Impossible de retourner à la liste des événements: " + e.getMessage());
        }
    }

    @FXML
    private void handleViewEvents() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FrontEvents.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) btnViewEvents.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de navigation", 
                     "Impossible d'afficher la vue des événements: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private VBox createSessionCard(Session session) {
        // Carte principale
        VBox card = new VBox(12);
        card.setPrefWidth(280);
        card.setPrefHeight(330);
        card.setPadding(new Insets(15));
        card.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 10px; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);"
        );
        
        // Conteneur pour l'image avec bord arrondi
        StackPane imageContainer = new StackPane();
        imageContainer.setMinHeight(150);
        imageContainer.setStyle("-fx-background-radius: 5px; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 0);");
        
        // Image
        ImageView imageView = new ImageView();
        imageView.setFitWidth(250);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);
        
        // Appliquer un effet de bord arrondi à l'image
        Rectangle clip = new Rectangle(imageView.getFitWidth(), imageView.getFitHeight());
        clip.setArcWidth(10);
        clip.setArcHeight(10);
        imageView.setClip(clip);
        
        // Charger l'image
        try {
            String imagePath = session.getImage();
            if (imagePath != null && !imagePath.isEmpty()) {
                // Essayer d'abord comme URL complète
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    imageView.setImage(new Image(imageFile.toURI().toString()));
                } else {
                    // Essayer dans le dossier d'images configuré
                    String uploadDir = "C:\\xampp\\htdocs\\imageP\\";
                    File uploadedImage = new File(uploadDir + imagePath);
                    if (uploadedImage.exists()) {
                        imageView.setImage(new Image(uploadedImage.toURI().toString()));
                    } else {
                        // Utiliser une image par défaut
                        URL defaultImageUrl = getClass().getResource("/images/default-session.png");
                        if (defaultImageUrl != null) {
                            imageView.setImage(new Image(defaultImageUrl.toExternalForm()));
                        }
                    }
                }
            } else {
                // Image par défaut
                URL defaultImageUrl = getClass().getResource("/images/default-session.png");
                if (defaultImageUrl != null) {
                    imageView.setImage(new Image(defaultImageUrl.toExternalForm()));
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image: " + e.getMessage());
        }
        
        imageContainer.getChildren().add(imageView);
        
        // Titre de la session
        Label titleLabel = new Label(session.getTitre());
        titleLabel.setWrapText(true);
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
        
        // Badge pour le type de session (on utilise un type par défaut si non disponible)
        String sessionType = "SESSION";
        if (session.getEvenement() != null && session.getEvenement().getType() != null) {
            sessionType = session.getEvenement().getType().toUpperCase();
        }
        Label typeLabel = new Label(sessionType);
        typeLabel.setStyle(
            "-fx-background-color: #3498db; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 5 10; " +
            "-fx-background-radius: 10;"
        );
        
        // Système d'étoiles pour la popularité
        HBox starsContainer = new HBox(2);
        starsContainer.setAlignment(Pos.CENTER_LEFT);
        
        // Calcul du nombre d'étoiles basé sur le taux de réservation
        int totalSeats = session.getAvailableSeats();
        int reservedSeats = totalSeats - session.getCapacity();
        int stars = 0;
        
        if (totalSeats > 0) {
            double reservationRate = (double) reservedSeats / totalSeats;
            // Attribution d'étoiles : 0-20% => 1 étoile, 20-40% => 2 étoiles, etc.
            stars = Math.min(5, Math.max(1, (int) Math.ceil(reservationRate * 5)));
        } else {
            stars = 1; // Par défaut, 1 étoile
        }
        
        // Ajouter les étoiles
        for (int i = 0; i < 5; i++) {
            Label starLabel = new Label(i < stars ? "★" : "☆");
            starLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: " + (i < stars ? "#f39c12" : "#bdc3c7") + ";");
            starsContainer.getChildren().add(starLabel);
        }
        
        // Label pour le taux de popularité
        Label popularityLabel = new Label(getPopularityText(stars));
        popularityLabel.setStyle("-fx-font-size: 12px; -fx-font-style: italic; -fx-text-fill: #7f8c8d;");
        
        // Informations principales: date, lieu, places disponibles, animateur
        VBox infoBox = new VBox(8);
        infoBox.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 5; -fx-padding: 10;");
        
        // Date
        HBox dateBox = new HBox(8);
        dateBox.setAlignment(Pos.CENTER_LEFT);
        Label dateIcon = new Label("📅");
        Label dateText = new Label(session.getStartTime().format(formatter));
        dateText.setStyle("-fx-font-size: 12px; -fx-text-fill: #34495e;");
        dateBox.getChildren().addAll(dateIcon, dateText);
        
        // Lieu (on utilise le lieu de la session ou de l'événement parent)
        HBox locationBox = new HBox(8);
        locationBox.setAlignment(Pos.CENTER_LEFT);
        Label locationIcon = new Label("📍");
        String lieu = "Non spécifié";
        if (session.getLocation() != null && !session.getLocation().isEmpty()) {
            lieu = session.getLocation();
        } else if (session.getEvenement() != null && session.getEvenement().getLocation() != null) {
            lieu = session.getEvenement().getLocation();
        }
        Label locationText = new Label(lieu);
        locationText.setStyle("-fx-font-size: 12px; -fx-text-fill: #34495e;");
        locationBox.getChildren().addAll(locationIcon, locationText);
        
        // Places disponibles
        HBox capacityBox = new HBox(8);
        capacityBox.setAlignment(Pos.CENTER_LEFT);
        Label capacityIcon = new Label("🪑");
        Label capacityText = new Label(session.getCapacity() + " places disponibles");
        
        // Changer la couleur selon la disponibilité
        if (session.getCapacity() <= 0) {
            capacityText.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;"); // Rouge
        } else if (session.getCapacity() < 5) {
            capacityText.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #f39c12;"); // Orange
        } else {
            capacityText.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #2ecc71;"); // Vert
        }
        
        capacityBox.getChildren().addAll(capacityIcon, capacityText);
        
        // Regrouper les informations
        infoBox.getChildren().addAll(dateBox, locationBox, capacityBox);
        
        // Bouton de réservation
        Button reserveButton = new Button("Réserver");
        reserveButton.setPrefWidth(Double.MAX_VALUE);
        reserveButton.setStyle(
            "-fx-background-color: #2ecc71; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-padding: 10; " +
            "-fx-background-radius: 5;"
        );
        
        // Désactiver le bouton si la session est complète
        if (session.getCapacity() <= 0) {
            reserveButton.setDisable(true);
            reserveButton.setStyle(
                "-fx-background-color: #bdc3c7; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-padding: 10; " +
                "-fx-background-radius: 5;"
            );
        }
        
        reserveButton.setOnAction(e -> handleReservation(session));
        
        // Animation au survol
        card.setOnMouseEntered(e -> {
            card.setStyle(
                "-fx-background-color: #f9f9f9; " +
                "-fx-background-radius: 10px; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 15, 0, 0, 0);"
            );
        });
        
        card.setOnMouseExited(e -> {
            card.setStyle(
                "-fx-background-color: white; " +
                "-fx-background-radius: 10px; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);"
            );
        });
        
        // Ajouter tous les éléments à la carte
        card.getChildren().addAll(imageContainer, titleLabel, typeLabel, starsContainer, popularityLabel, infoBox, reserveButton);
        
        return card;
    }
    
    // Méthode pour obtenir le texte de popularité en fonction du nombre d'étoiles
    private String getPopularityText(int stars) {
        switch (stars) {
            case 1: return "Peu populaire";
            case 2: return "Assez populaire";
            case 3: return "Populaire";
            case 4: return "Très populaire";
            case 5: return "Extrêmement populaire";
            default: return "Popularité inconnue";
        }
    }
    
    private void handleReservation(Session session) {
        try {
            if (session.getCapacity() <= 0) {
                showAlert(Alert.AlertType.WARNING, "Session complète", "Aucune place disponible", 
                          "Toutes les places pour cette session ont été réservées.");
                return;
            }
            
            // Créer un dialogue personnalisé pour la réservation
            Dialog<Integer> dialog = new Dialog<>();
            dialog.setTitle("Réservation");
            dialog.setHeaderText("Réserver pour: " + session.getTitre());
            
            // Configuration du dialogue
            ButtonType reserverType = new ButtonType("Réserver", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(reserverType, ButtonType.CANCEL);
            
            // Créer la mise en page
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));
            
            // Champ pour le nombre de places
            Spinner<Integer> placesSpinner = new Spinner<>(1, session.getCapacity(), 1);
            placesSpinner.setEditable(true);
            placesSpinner.setPrefWidth(100);
            
            grid.add(new Label("Nombre de places:"), 0, 0);
            grid.add(placesSpinner, 1, 0);
            
            dialog.getDialogPane().setContent(grid);
            
            // Convertir le résultat en nombre de places
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == reserverType) {
                    return placesSpinner.getValue();
                }
                return null;
            });
            
            // Afficher le dialogue et traiter le résultat
            Optional<Integer> result = dialog.showAndWait();
            
            result.ifPresent(nbPlaces -> {
                try {
                    // Mise à jour des places directement dans la session
                    int newCapacity = session.getCapacity() - nbPlaces;
                    
                    // Créer une requête directe pour mettre à jour capacity spécifiquement
                    String query = "UPDATE sessions SET capacity = ? WHERE id = ?";
                    
                    try (Connection conn = MyDataBase.getInstance().getCon();
                         PreparedStatement pstmt = conn.prepareStatement(query)) {
                        
                        pstmt.setInt(1, newCapacity);
                        pstmt.setInt(2, session.getId());
                        
                        int updatedRows = pstmt.executeUpdate();
                        
                        if (updatedRows > 0) {
                            // Mise à jour réussie en base de données
                            // Mettre également à jour l'objet local
                            session.setCapacity(newCapacity);
                            
                            showAlert(Alert.AlertType.INFORMATION, "Succès", "Réservation confirmée", 
                                    "Vous avez réservé " + nbPlaces + " place(s) pour cette session.");
                            
                            // Recharger les sessions pour rafraîchir l'affichage
                            loadSessions();
                        } else {
                            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de réservation", 
                                    "Impossible de réserver les places demandées.");
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de réservation", 
                            "Une erreur est survenue: " + e.getMessage());
                }
            });
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de navigation", 
                    "Impossible de traiter la réservation: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 