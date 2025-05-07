package tn.esprit.controller;


import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import tn.esprit.entities.Evenement;
import tn.esprit.entities.Session;
import tn.esprit.service.EvenementService;
import tn.esprit.service.SessionService;
import tn.esprit.utils.DescriptionGenerator;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class AjouterSessionController2 implements Initializable {
    @FXML private TextField titreField;
    @FXML private TextArea descriptionField;
    @FXML private DatePicker startTimePicker;
    @FXML private DatePicker endTimePicker;
    @FXML private TextField capaciteField;
    @FXML private TextField placesDisponiblesField;
    @FXML private TextField lieuField;
    @FXML private TextField imageField;
    @FXML private ComboBox<Evenement> evenementComboBox;
    @FXML private Button genererDescriptionBtn;

    private Session sessionToEdit = null;
    private final SessionService sessionService;
    private final EvenementService evenementService;

    {
        try {
            evenementService = new EvenementService();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public AjouterSessionController2() {
        try {
            this.sessionService = new SessionService();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'initialisation du service de session", e);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Charger les événements dans le ComboBox
            evenementComboBox.setItems(FXCollections.observableArrayList(evenementService.getAllEvenements()));
            evenementComboBox.setConverter(new javafx.util.StringConverter<Evenement>() {
                @Override
                public String toString(Evenement evenement) {
                    return evenement != null ? evenement.getTitre() : "";
                }

                @Override
                public Evenement fromString(String string) {
                    return null;
                }
            });
            
            // Initialiser les champs numériques avec des validateurs
            capaciteField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal.matches("\\d*")) {
                    capaciteField.setText(newVal.replaceAll("[^\\d]", ""));
                }
            });
            
            placesDisponiblesField.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal.matches("\\d*")) {
                    placesDisponiblesField.setText(newVal.replaceAll("[^\\d]", ""));
                }
            });
            
            // Définir des valeurs par défaut
            capaciteField.setText("100");
            placesDisponiblesField.setText("100");
            imageField.setText("default_session.png");
            
            // Configuration du bouton de génération de description
            if (genererDescriptionBtn != null) {
                genererDescriptionBtn.setOnAction(e -> genererDescription());
            }
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement", 
                     "Impossible de charger la liste des événements: " + e.getMessage());
        }
    }

    public void initializeForEdit(Session session) {
        this.sessionToEdit = session;
        titreField.setText(session.getTitre());
        descriptionField.setText(session.getDescription());
        startTimePicker.setValue(session.getStartTime().toLocalDate());
        endTimePicker.setValue(session.getEndTime().toLocalDate());
        capaciteField.setText(String.valueOf(session.getCapacity()));
        placesDisponiblesField.setText(String.valueOf(session.getAvailableSeats()));
        lieuField.setText(session.getLocation());
        imageField.setText(session.getImage());
        
        // Sélectionner l'événement correspondant
        evenementComboBox.getItems().stream()
            .filter(e -> e.getId() == session.getEvenementId())
            .findFirst()
            .ifPresent(evenementComboBox::setValue);
    }

    @FXML
    private void handleEnregistrer() {
        if (validateFields()) {
            try {
                Session session = new Session();
                if (sessionToEdit != null) {
                    session.setId(sessionToEdit.getId());
                }
                
                // Récupérer les valeurs des champs
                session.setTitre(titreField.getText().trim());
                session.setDescription(descriptionField.getText().trim());
                session.setStartTime(LocalDateTime.of(startTimePicker.getValue(), LocalTime.MIDNIGHT));
                session.setEndTime(LocalDateTime.of(endTimePicker.getValue(), LocalTime.MIDNIGHT));
                session.setEvenementId(evenementComboBox.getValue().getId());
                session.setCapacity(Integer.parseInt(capaciteField.getText().trim()));
                session.setAvailableSeats(Integer.parseInt(placesDisponiblesField.getText().trim()));
                session.setLocation(lieuField.getText().trim());
                session.setImage(imageField.getText().trim());

                if (sessionToEdit != null) {
                    try {
                        sessionService.modifierSession(session);
                        showAlert(Alert.AlertType.INFORMATION, "Succès", "Modification réussie", 
                                "La session a été modifiée avec succès !");
                        closeWindow();
                    } catch (SQLException e) {
                        showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la modification", 
                                "La session n'a pas pu être modifiée: " + e.getMessage());
                    }
                } else {
                    sessionService.ajouterSession(session);

                        showAlert(Alert.AlertType.INFORMATION, "Succès", "Ajout réussi", 
                                "La session a été ajoutée avec succès !");
                        System.out.println("Session ajoutée avec succès - ID: " + session.getId() + ", Titre: " + session.getTitre());
                        closeWindow();

                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'opération", e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private boolean validateFields() {
        StringBuilder errors = new StringBuilder();

        if (evenementComboBox.getValue() == null) {
            errors.append("Veuillez sélectionner un événement.\n");
        }
        if (titreField.getText().trim().isEmpty()) {
            errors.append("Le titre est requis.\n");
        }
        if (descriptionField.getText().trim().isEmpty()) {
            errors.append("La description est requise.\n");
        }
        if (startTimePicker.getValue() == null) {
            errors.append("La date de début est requise.\n");
        }
        if (endTimePicker.getValue() == null) {
            errors.append("La date de fin est requise.\n");
        }
        if (startTimePicker.getValue() != null && endTimePicker.getValue() != null &&
            startTimePicker.getValue().isAfter(endTimePicker.getValue())) {
            errors.append("La date de début doit être antérieure à la date de fin.\n");
        }
        if (capaciteField.getText().trim().isEmpty() || Integer.parseInt(capaciteField.getText().trim()) <= 0) {
            errors.append("La capacité doit être un nombre positif.\n");
        }
        if (placesDisponiblesField.getText().trim().isEmpty() || Integer.parseInt(placesDisponiblesField.getText().trim()) < 0) {
            errors.append("Le nombre de places disponibles doit être un nombre positif ou zéro.\n");
        }
        if (!placesDisponiblesField.getText().trim().isEmpty() && !capaciteField.getText().trim().isEmpty() &&
            Integer.parseInt(placesDisponiblesField.getText().trim()) > Integer.parseInt(capaciteField.getText().trim())) {
            errors.append("Le nombre de places disponibles ne peut pas dépasser la capacité de la session.\n");
        }

        if (errors.length() > 0) {
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", 
                     "Veuillez corriger les erreurs suivantes:", errors.toString());
            return false;
        }
        return true;
    }

    @FXML
    private void handleAnnuler() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) descriptionField.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void preSelectEvent(Evenement evenement) {
        if (evenement != null) {
            // Attendre que le ComboBox soit initialisé avec les événements
            if (evenementComboBox.getItems().isEmpty()) {

                    evenementComboBox.setItems(FXCollections.observableArrayList(evenementService.getAllEvenements()));

            }
            
            // Sélectionner l'événement
            evenementComboBox.getItems().stream()
                .filter(e -> e.getId() == evenement.getId())
                .findFirst()
                .ifPresent(evenementComboBox::setValue);
            
            // Préremplir le lieu avec celui de l'événement
            if (evenement.getLocation() != null && !evenement.getLocation().isEmpty()) {
                lieuField.setText(evenement.getLocation());
            }
            
            // Préremplir le titre avec un nom basé sur l'événement
            titreField.setText(evenement.getTitre() + " - Session");
        }
    }

    @FXML
    private void handleUploadImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg"));

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            try {
                // Créer un nom de fichier unique avec timestamp
                String fileName = System.currentTimeMillis() + "_" + file.getName();
                
                // Définir le répertoire d'upload
                String uploadDir = "C:\\xampp\\htdocs\\imageP";
                Path destDir = Paths.get(uploadDir);

                // Créer le répertoire s'il n'existe pas
                if (!Files.exists(destDir)) {
                    Files.createDirectories(destDir);
                }

                // Copier le fichier vers la destination
                Path destPath = destDir.resolve(fileName);
                Files.copy(file.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
                
                // Mettre à jour le champ avec le nom du fichier
                imageField.setText(fileName);
                
                // Afficher un message de succès
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Upload réussi", 
                         "L'image a été uploadée avec succès.");
                
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'upload de l'image", 
                         "Impossible d'uploader l'image: " + e.getMessage());
            }
        }
    }

    /**
     * Génère automatiquement une description pour la session
     * en utilisant le titre, le lieu et la date
     */
    @FXML
    private void genererDescription() {
        try {
            // Vérifier que les champs nécessaires sont remplis
            if (titreField.getText().isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Champs manquants", 
                        "Titre manquant", "Veuillez d'abord saisir un titre pour la session.");
                return;
            }
            
            if (startTimePicker.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Champs manquants", 
                        "Date de début manquante", "Veuillez d'abord sélectionner une date de début.");
                return;
            }
            
            String titre = titreField.getText();
            String lieu = lieuField.getText();
            
            // Si le lieu n'est pas spécifié, utiliser celui de l'événement
            if (lieu.isEmpty() && evenementComboBox.getValue() != null) {
                lieu = evenementComboBox.getValue().getLocation();
            }
            
            // Si le lieu est toujours vide, utiliser une valeur par défaut
            if (lieu.isEmpty()) {
                lieu = "notre espace culturel";
            }
            
            // Créer une date avec l'heure (midi par défaut)
            LocalDateTime dateDebut = LocalDateTime.of(startTimePicker.getValue(), LocalTime.of(12, 0));
            
            // Demander si l'utilisateur veut une description courte ou détaillée
            Alert formatAlert = new Alert(Alert.AlertType.CONFIRMATION);
            formatAlert.setTitle("Format de description");
            formatAlert.setHeaderText("Quel format de description souhaitez-vous ?");
            
            ButtonType btnCourt = new ButtonType("Format court");
            ButtonType btnDetaille = new ButtonType("Format détaillé");
            formatAlert.getButtonTypes().setAll(btnCourt, btnDetaille);
            
            // Générer la description selon le choix
            String description;
            if (formatAlert.showAndWait().get() == btnCourt) {
                // Format court par défaut
                description = DescriptionGenerator.generateShortSessionDescription(titre, lieu, dateDebut);
            } else {
                // Format détaillé
                description = DescriptionGenerator.generateSessionDescription(titre, lieu, dateDebut);
            }
            
            // Demander confirmation avant de remplacer la description existante
            if (!descriptionField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Remplacer la description");
                alert.setHeaderText("Voulez-vous remplacer la description actuelle ?");
                alert.setContentText("La description existante sera remplacée par celle générée automatiquement.");
                
                ButtonType buttonTypeOui = new ButtonType("Oui");
                ButtonType buttonTypeNon = new ButtonType("Non");
                
                alert.getButtonTypes().setAll(buttonTypeOui, buttonTypeNon);
                
                if (alert.showAndWait().get() == buttonTypeOui) {
                    descriptionField.setText(description);
                }
            } else {
                descriptionField.setText(description);
            }
            
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de génération", 
                    "Une erreur est survenue lors de la génération de la description: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 