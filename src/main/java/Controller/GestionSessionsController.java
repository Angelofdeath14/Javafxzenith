package Controller;

import Entity.Evenement;
import Entity.Session;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.SessionService;
import services.EvenementService;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.geometry.Insets;
import java.util.List;
import java.util.Optional;
import javafx.scene.layout.GridPane;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.scene.layout.FlowPane;

public class GestionSessionsController implements Initializable {
    @FXML private Label eventTitleLabel;
    @FXML private Label eventDetailsLabel;
    @FXML private TableView<Session> sessionsTableView;
    @FXML private TableColumn<Session, String> colTitre;
    @FXML private TableColumn<Session, String> colDescription;
    @FXML private TableColumn<Session, String> colDateDebut;
    @FXML private TableColumn<Session, String> colDateFin;
    @FXML private TableColumn<Session, ImageView> colImage;
    @FXML private TableColumn<Session, HBox> colActions;
    @FXML private TextField titreField;
    @FXML private TextArea descriptionArea;
    @FXML private DatePicker dateDebutPicker;
    @FXML private DatePicker dateFinPicker;
    @FXML private TextField capaciteField;
    @FXML private ComboBox<Evenement> evenementComboBox;
    @FXML private Button btnRetour;
    @FXML private VBox sessionsContainer;
    
    // Composants pour les statistiques
    @FXML private Label lblTotalSessions;
    @FXML private Label lblTotalCapacity;
    @FXML private Label lblReservedPlaces;
    @FXML private Label lblReservationRate;
    @FXML private ProgressBar progressReservationRate;

    private Evenement evenement;
    private final SessionService sessionService = new SessionService();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private Session sessionToEdit;
    private EvenementService evenementService;
    private String eventTitle;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            setupTableColumns();
            evenementService = new EvenementService();
        } catch (SQLException e) {
            e.printStackTrace();
            // Vous pouvez ajouter ici une meilleure gestion des erreurs si nécessaire
        }
        
        // Style du bouton retour
        btnRetour.setStyle(
            "-fx-background-color: #555555; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-min-width: 100px; " +
            "-fx-min-height: 30px; " +
            "-fx-background-radius: 5px; " +
            "-fx-cursor: hand;"
        );
    }

    public void setEvenement(Evenement evenement) {
        this.evenement = evenement;
        eventTitleLabel.setText("Sessions de l'événement : " + evenement.getNom());
        updateEventDetails();
        loadSessions();
    }

    private void updateEventDetails() {
        try {
            // Nombre total de sessions pour cet événement
            List<Session> sessions = sessionService.getSessionsByEvent(evenement.getId());
            int totalSessions = sessions.size();
            
            // Nombre de places réservées pour l'événement
            int totalCapacity = evenement.getNbPlace();
            int reservedPlaces = 0;
            
            // Calcul des places réservées si on a des sessions
            for (Session session : sessions) {
                // Supposons que session.getCapacity() donne le nombre de places restantes
                // et que session.getAvailableSeats() donne la capacité totale de la session
                int sessionTotal = session.getAvailableSeats();
                int sessionRemaining = session.getCapacity();
                reservedPlaces += (sessionTotal - sessionRemaining);
            }
            
            // Taux de réservation
            double reservationRate = totalCapacity > 0 ? (reservedPlaces * 100.0 / totalCapacity) : 0;
            
            // Création du texte pour les informations de base de l'événement
            String details = String.format(
                "Type: %s\n" +
                "Lieu: %s\n" +
                "Date de début: %s\n" +
                "Date de fin: %s",
                evenement.getType(),
                evenement.getLocation(),
                evenement.getDateD().format(formatter),
                evenement.getDateF().format(formatter)
            );
            
            eventDetailsLabel.setText(details);
            
            // Mettre à jour les composants de statistiques
            lblTotalSessions.setText(String.valueOf(totalSessions));
            lblTotalCapacity.setText(String.valueOf(totalCapacity));
            lblReservedPlaces.setText(String.valueOf(reservedPlaces));
            lblReservationRate.setText(String.format("%.1f%%", reservationRate));
            progressReservationRate.setProgress(reservationRate / 100);
            
        } catch(Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur d'affichage", 
                    "Impossible de charger les statistiques de l'événement.");
        }
    }

    private void setupTableColumns() {
        colTitre.setCellValueFactory(cellData -> {
            if (cellData.getValue().getTitre() != null) {
                return new SimpleStringProperty(cellData.getValue().getTitre());
            }
            return new SimpleStringProperty("");
        });
        
        colDescription.setCellValueFactory(cellData -> {
            if (cellData.getValue().getDescription() != null) {
                return new SimpleStringProperty(cellData.getValue().getDescription());
            }
            return new SimpleStringProperty("");
        });
        
        // Configuration de la colonne image
        colImage.setCellFactory(column -> new TableCell<Session, ImageView>() {
            private final ImageView imageView = new ImageView();
            
            {
                imageView.setFitHeight(50);
                imageView.setFitWidth(50);
                imageView.setPreserveRatio(true);
            }
            
            @Override
            protected void updateItem(ImageView item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Session session = getTableView().getItems().get(getIndex());
                    String imagePath = session.getImage();
                    if (imagePath != null && !imagePath.isEmpty()) {
                        try {
                            String fullPath = "C:\\xampp\\htdocs\\imageP\\" + imagePath;
                            File file = new File(fullPath);
                            if (file.exists()) {
                                Image image = new Image(file.toURI().toString());
                                imageView.setImage(image);
                                setGraphic(imageView);
                            } else {
                                setGraphic(null);
                            }
                        } catch (Exception e) {
                            setGraphic(null);
                        }
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
        
        colDateDebut.setCellValueFactory(cellData -> {
            if (cellData.getValue().getStartTime() != null) {
                String dateStr = cellData.getValue().getStartTime().format(formatter);
                return new javafx.beans.property.SimpleStringProperty(dateStr);
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });
        
        colDateFin.setCellValueFactory(cellData -> {
            if (cellData.getValue().getEndTime() != null) {
                String dateStr = cellData.getValue().getEndTime().format(formatter);
                return new javafx.beans.property.SimpleStringProperty(dateStr);
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });

        colActions.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");
            private final Button imageButton = new Button("Upload Image");
            private final HBox buttons = new HBox(5, editButton, imageButton, deleteButton);

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

                // Style pour le bouton Image
                imageButton.setStyle(
                    "-fx-background-color: #2196F3; " +
                    "-fx-text-fill: white; " +
                    "-fx-font-weight: bold; " +
                    "-fx-min-width: 100px; " +
                    "-fx-min-height: 30px; " +
                    "-fx-background-radius: 5px; " +
                    "-fx-cursor: hand;"
                );

                buttons.setAlignment(javafx.geometry.Pos.CENTER);

                editButton.setOnAction(e -> {
                    Session session = getTableView().getItems().get(getIndex());
                    handleEdit(session);
                });

                imageButton.setOnAction(e -> {
                    Session session = getTableView().getItems().get(getIndex());
                    handleUploadImage(session);
                });

                deleteButton.setOnAction(e -> {
                    Session session = getTableView().getItems().get(getIndex());
                    handleDelete(session);
                });
            }

            @Override
            protected void updateItem(HBox item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttons);
                }
            }
        });

        // Appliquer les styles d'alignement
        colTitre.setStyle("-fx-alignment: CENTER-LEFT;");
        colDescription.setStyle("-fx-alignment: CENTER-LEFT;");
        colDateDebut.setStyle("-fx-alignment: CENTER;");
        colDateFin.setStyle("-fx-alignment: CENTER;");
        colActions.setStyle("-fx-alignment: CENTER;");
    }

    private void loadSessions() {
        try {
            // Vider le conteneur de sessions
            sessionsContainer.getChildren().clear();
            
            // Récupérer les sessions de l'événement
            List<Session> sessions = sessionService.getSessionsByEvent(evenement.getId());
            
            // Mettre à jour le tableau
            sessionsTableView.getItems().clear();
            sessionsTableView.getItems().addAll(sessions);
            
            // Titre de la section
            Label titleLabel = new Label("Sessions disponibles");
            titleLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
            titleLabel.setTextFill(Color.web("#2c3e50"));
            titleLabel.setPadding(new Insets(10, 0, 20, 0));
            sessionsContainer.getChildren().add(titleLabel);
            
            // Créer le FlowPane pour les cartes
            FlowPane cardsContainer = new FlowPane();
            cardsContainer.setHgap(20);
            cardsContainer.setVgap(20);
            cardsContainer.setPadding(new Insets(10));
            cardsContainer.setStyle("-fx-background-color: #f8f9fa; -fx-background-radius: 10;");
            
            if (sessions.isEmpty()) {
                Label emptyLabel = new Label("Aucune session disponible pour cet événement");
                emptyLabel.setFont(Font.font("System", 14));
                emptyLabel.setTextFill(Color.web("#7f8c8d"));
                emptyLabel.setPadding(new Insets(20));
                cardsContainer.getChildren().add(emptyLabel);
            } else {
                // Créer une carte pour chaque session
                for (Session session : sessions) {
                    VBox sessionCard = createSessionCard(session);
                    cardsContainer.getChildren().add(sessionCard);
                }
            }
            
            sessionsContainer.getChildren().add(cardsContainer);
            
            // Bouton pour ajouter une nouvelle session
            Button addButton = new Button("Ajouter une session");
            addButton.setStyle(
                "-fx-background-color: #2ecc71; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-min-width: 200px; " +
                "-fx-min-height: 40px; " +
                "-fx-background-radius: 5px; " +
                "-fx-cursor: hand;"
            );
            addButton.setOnAction(event -> handleAjouterSession());
            
            HBox buttonBox = new HBox(addButton);
            buttonBox.setAlignment(Pos.CENTER);
            buttonBox.setPadding(new Insets(20, 0, 10, 0));
            
            sessionsContainer.getChildren().add(buttonBox);
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement", 
                     "Une erreur est survenue lors du chargement des sessions: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAjouterSession() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterSession.fxml"));
            Parent root = loader.load();
            
            AjouterSessionController controller = loader.getController();
            
            // Pré-sélectionner l'événement actuel
            controller.preSelectEvent(evenement);
            
            Stage stage = new Stage();
            stage.setTitle("Ajouter une session");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            loadSessions();
        } catch (IOException e) {
            e.printStackTrace(); // Pour le débogage
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture", e.getMessage());
        }
    }

    private void handleEdit(Session session) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierSession.fxml"));
            Parent root = loader.load();
            
            ModifierSessionController controller = loader.getController();
            controller.setSession(session);
            
            Stage stage = new Stage();
            stage.setTitle("Modifier Session");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            
            // Rafraîchir la liste des sessions après la modification
            loadSessions();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de modification", "Une erreur est survenue lors de la modification de la session.");
        }
    }

    private void handleDelete(Session session) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer la session");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette session ?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                sessionService.deleteSession(session.getId());
                loadSessions();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Session supprimée", "La session a été supprimée avec succès.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de suppression", "Une erreur est survenue lors de la suppression de la session.");
            }
        }
    }

    @FXML
    private void handleRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageEvent.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ViewEvents.fxml"));
            Parent root = loader.load();
            ViewEventsController controller = loader.getController();
            
            // Initialiser le contrôleur si nécessaire
            // controller.init();
            
            Scene scene = new Scene(root);
            Stage stage = (Stage) btnRetour.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de navigation", 
                     "Impossible d'afficher la vue des événements: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleShowStats() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/EventStats.fxml"));
            Parent root = loader.load();
            
            // Vous pouvez passer des données au contrôleur si nécessaire
            EventStatsController controller = loader.getController();
            // controller.initData();
            
            Stage statsStage = new Stage();
            statsStage.setTitle("Statistiques des événements");
            statsStage.setScene(new Scene(root));
            statsStage.initModality(Modality.APPLICATION_MODAL);
            statsStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur d'affichage", 
                     "Impossible d'afficher les statistiques: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void setSession(Session session) {
        this.sessionToEdit = session;
        if (session != null) {
            descriptionArea.setText(session.getDescription());
            dateDebutPicker.setValue(session.getStartTime().toLocalDate());
            dateFinPicker.setValue(session.getEndTime().toLocalDate());
        }
    }

    private void handleUploadImage(Session session) {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().add(
            new javafx.stage.FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File selectedFile = fileChooser.showOpenDialog(sessionsTableView.getScene().getWindow());
        if (selectedFile != null) {
            try {
                String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                String uploadDir = "C:\\xampp\\htdocs\\imageP";
                java.nio.file.Path destDir = java.nio.file.Paths.get(uploadDir);

                if (!java.nio.file.Files.exists(destDir)) {
                    java.nio.file.Files.createDirectories(destDir);
                }

                java.nio.file.Path destPath = destDir.resolve(fileName);
                java.nio.file.Files.copy(selectedFile.toPath(), destPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                session.setImage(fileName);
                sessionService.updateSession(session);
                loadSessions();

                showAlert(Alert.AlertType.INFORMATION, "Succès", "Upload réussi", "L'image a été uploadée avec succès");
            } catch (Exception e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'upload", e.getMessage());
            }
        }
    }

    public void setEventTitle(String title) {
        this.eventTitle = title;
        loadSessions();
    }

    private VBox createSessionCard(Session session) {
        // Création de la carte
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; " +
                     "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 5); " +
                     "-fx-background-radius: 10;");
        card.setPrefWidth(300);
        card.setPrefHeight(400);
        card.setPadding(new Insets(15));
        
        // Image de la session
        ImageView imageView = new ImageView();
        if (session.getImage() != null && !session.getImage().isEmpty()) {
            try {
                String fullPath = "C:\\xampp\\htdocs\\imageP\\" + session.getImage();
                File file = new File(fullPath);
                if (file.exists()) {
                    Image image = new Image(file.toURI().toString());
                    imageView.setImage(image);
                } else {
                    // Image par défaut si l'image n'existe pas
                    imageView.setImage(new Image(getClass().getResourceAsStream("/images/default-session.png")));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // Essayer de charger une image par défaut
            try {
                imageView.setImage(new Image(getClass().getResourceAsStream("/images/default-session.png")));
            } catch (Exception e) {
                // Si pas d'image par défaut disponible
                imageView.setStyle("-fx-background-color: #eeeeee;");
            }
        }
        
        imageView.setFitWidth(270);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);
        
        // Titre de la session
        Label titleLabel = new Label(session.getTitre());
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        titleLabel.setTextFill(Color.web("#2c3e50"));
        titleLabel.setWrapText(true);
        
        // Description
        Label descLabel = new Label(session.getDescription());
        descLabel.setWrapText(true);
        descLabel.setTextFill(Color.web("#7f8c8d"));
        descLabel.setMaxHeight(60);
        
        // Dates
        Label dateStartLabel = new Label("Début: " + session.getStartTime().format(formatter));
        dateStartLabel.setTextFill(Color.web("#3498db"));
        
        Label dateEndLabel = new Label("Fin: " + session.getEndTime().format(formatter));
        dateEndLabel.setTextFill(Color.web("#3498db"));
        
        // Lieu (on prend celui de l'événement parent)
        Label locationLabel = new Label("Lieu: " + evenement.getLocation());
        locationLabel.setTextFill(Color.web("#7f8c8d"));
        
        // Places disponibles
        Label placesLabel = new Label("Places disponibles: " + session.getCapacity());
        placesLabel.setTextFill(Color.web("#27ae60"));
        placesLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        // Boutons d'action
        Button reserveButton = new Button("Réserver");
        reserveButton.setStyle(
            "-fx-background-color: #3498db; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-min-width: 80px; " +
            "-fx-cursor: hand;"
        );
        reserveButton.setOnAction(e -> handleReservation(session));
        
        Button editButton = new Button("Modifier");
        editButton.setStyle(
            "-fx-background-color: #f39c12; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-min-width: 80px; " +
            "-fx-cursor: hand;"
        );
        editButton.setOnAction(e -> handleEdit(session));
        
        Button deleteButton = new Button("Supprimer");
        deleteButton.setStyle(
            "-fx-background-color: #e74c3c; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-min-width: 80px; " +
            "-fx-cursor: hand;"
        );
        deleteButton.setOnAction(e -> handleDelete(session));
        
        Button imageButton = new Button("Image");
        imageButton.setStyle(
            "-fx-background-color: #9b59b6; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-min-width: 80px; " +
            "-fx-cursor: hand;"
        );
        imageButton.setOnAction(e -> handleUploadImage(session));
        
        // Organiser les boutons
        HBox topButtons = new HBox(10, reserveButton, editButton);
        topButtons.setAlignment(Pos.CENTER);
        
        HBox bottomButtons = new HBox(10, deleteButton, imageButton);
        bottomButtons.setAlignment(Pos.CENTER);
        
        VBox buttonsContainer = new VBox(10, topButtons, bottomButtons);
        buttonsContainer.setAlignment(Pos.CENTER);
        
        // Ajouter tous les éléments à la carte
        card.getChildren().addAll(
            imageView, titleLabel, descLabel,
            dateStartLabel, dateEndLabel,
            locationLabel, placesLabel,
            buttonsContainer
        );
        
        return card;
    }

    private void handleReservation(Session session) {
        // Afficher une boîte de dialogue pour la réservation
        try {
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
                                 "Il n'y a pas assez de places disponibles.");
                        return;
                    }
                    
                    // Réduire le nombre de places disponibles
                    session.setCapacity(session.getCapacity() - places);
                    sessionService.updateSession(session);
                    
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Réservation effectuée", 
                             "Vous avez réservé " + places + " place(s) pour cette session.");
                    
                    // Recharger les sessions pour mettre à jour l'affichage
                    loadSessions();
                    
                } catch (NumberFormatException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Format invalide", 
                             "Veuillez entrer un nombre valide.");
                }
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de réservation", 
                     "Une erreur est survenue lors de la réservation : " + e.getMessage());
        }
    }
} 