package Controller;

import Entity.Evenement;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.EvenementService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.ResourceBundle;

public class AjouterEventController implements Initializable {
    @FXML private TextField nomEventField;
    @FXML private TextArea descriptionField;
    @FXML private TextField typeField;
    @FXML private TextField lieuField;
    @FXML private DatePicker dateDebutPicker;
    @FXML private DatePicker dateFinPicker;
    @FXML private ImageView imageView;
    @FXML private Button ajouterEvents;
    @FXML private TextField nbPlaceField;
    @FXML private AnchorPane root;

    private File selectedImageFile;
    private EvenementService evenementService;
    private Evenement eventToEdit = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            evenementService = new EvenementService();
        Platform.runLater(() -> {
            Stage stage = (Stage) root.getScene().getWindow();
            stage.setResizable(true);  // Permettre le redimensionnement
        });

        // Configuration de l'ImageView
        imageView.setFitWidth(150);
        imageView.setFitHeight(100);
        imageView.setPreserveRatio(true);
        imageView.setStyle("-fx-border-color: #cccccc; -fx-border-width: 1; -fx-border-style: dashed;");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, 
                     "Erreur de connexion", 
                     "Impossible de se connecter à la base de données", 
                     e.getMessage());
        }
    }

    @FXML
    private void handleAjoutEvent() {
        if (validateFields()) {
            try {
                // Récupérer les valeurs des champs
                String nom = nomEventField.getText().trim();
                String description = descriptionField.getText().trim();
                String type = typeField.getText().trim();
                String lieu = lieuField.getText().trim();
                LocalDate dateD = dateDebutPicker.getValue();
                LocalDate dateF = dateFinPicker.getValue();
                int nbPlaceInt = Integer.parseInt(nbPlaceField.getText().trim());

                // Création de l'objet Evenement
                Evenement evenement = new Evenement();
                evenement.setNom(nom);
                evenement.setDescription(description);
                evenement.setType(type);
                evenement.setLocation(lieu);
                evenement.setDateD(LocalDateTime.of(dateD, LocalTime.MIDNIGHT));
                evenement.setDateF(LocalDateTime.of(dateF, LocalTime.MIDNIGHT));
                evenement.setNbPlace(nbPlaceInt);

                // Gestion de l'image
                if (selectedImageFile != null) {
                    String imagePath = saveImage(selectedImageFile);
                    if (imagePath == null) {
                        showAlert(Alert.AlertType.ERROR,
                                "Erreur",
                                "Erreur d'image",
                                "Échec lors de l'enregistrement de l'image.");
                        return;
                    }
                    evenement.setImage(imagePath);
                }

                if (eventToEdit != null) {
                    // Mode modification
                    evenement.setId(eventToEdit.getId());
                    if (eventToEdit.getImage() != null && selectedImageFile == null) {
                        evenement.setImage(eventToEdit.getImage());
                    }
                    evenementService.modifier(evenement);
                    showAlert(Alert.AlertType.INFORMATION,
                             "Succès",
                             "Modification réussie",
                             "L'événement a été modifié avec succès !");

                    // Fermer la fenêtre actuelle et retourner à la liste
                    goToAffichageEvent();
                } else {
                    // Mode ajout
                    evenementService.ajouter(evenement);
                    showAlert(Alert.AlertType.INFORMATION,
                             "Succès",
                             "Ajout réussi",
                             "L'événement a été ajouté avec succès !");

                    // Demander à l'utilisateur s'il veut ajouter un autre événement
                    if (askForAnotherEvent()) {
                        resetFields();
                    } else {
                        goToAffichageEvent();
                    }
                }

            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR,
                         "Erreur",
                         "Erreur lors de l'opération",
                         e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleChooseImage() {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choisir une image");

        FileChooser.ExtensionFilter imageFilter =
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif");
        chooser.getExtensionFilters().add(imageFilter);

        selectedImageFile = chooser.showOpenDialog(imageView.getScene().getWindow());

        if (selectedImageFile != null) {
            try {
                Image image = new Image(selectedImageFile.toURI().toString());
                imageView.setImage(image);
                System.out.println("✅ Image chargée : " + selectedImageFile.getAbsolutePath());
            } catch (Exception e) {
                System.err.println("❌ Erreur lors du chargement de l'image : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private String saveImage(File file) {
        if (file == null) return null;

        try {
            String fileName = System.currentTimeMillis() + "_" + file.getName();
            String uploadDir = "C:\\xampp\\htdocs\\imageP";
            Path destDir = Paths.get(uploadDir);

            if (!Files.exists(destDir)) {
                Files.createDirectories(destDir);
            }

            Path destPath = destDir.resolve(fileName);
            Files.copy(file.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void resetFields() {
        nomEventField.clear();
        descriptionField.clear();
        typeField.clear();
        lieuField.clear();
        dateDebutPicker.setValue(null);
        dateFinPicker.setValue(null);
        nbPlaceField.clear();
        imageView.setImage(null);
        selectedImageFile = null;
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void goToAffichageEvent() {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL url = getClass().getResource("/AffichageEvent.fxml");
            if (url == null) {
                url = getClass().getClassLoader().getResource("AffichageEvent.fxml");
            }
            if (url == null) {
                String resourcePath = "file:" + System.getProperty("user.dir") + "/target/classes/AffichageEvent.fxml";
                url = new URL(resourcePath);
            }
            
            loader.setLocation(url);
            Parent root = loader.load();

            // Configurer la nouvelle scène
            Scene scene = new Scene(root);
            Stage stage = (Stage) nomEventField.getScene().getWindow();
            stage.setTitle("Liste des événements");
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR,
                     "Erreur",
                     "Erreur de navigation",
                     "Erreur lors de la redirection vers la liste des événements : " + e.getMessage());
        }
    }

    @FXML
    private void goToDash() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) nomEventField.getScene().getWindow();
            stage.setTitle("Dashboard");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR,
                     "Erreur",
                     "Erreur de navigation",
                     "Erreur lors de la redirection : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void Logout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) nomEventField.getScene().getWindow();
            stage.setTitle("Connexion");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR,
                     "Erreur",
                     "Erreur de déconnexion",
                     "Erreur lors de la déconnexion : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void initializeForEdit(Evenement event) {
        this.eventToEdit = event;

        // Mettre à jour le texte du bouton
        if (ajouterEvents != null) {
            ajouterEvents.setText("Modifier");
        }

        nomEventField.setText(event.getNom());
        descriptionField.setText(event.getDescription());
        typeField.setText(event.getType());
        lieuField.setText(event.getLocation());
        nbPlaceField.setText(String.valueOf(event.getNbPlace()));

        if (event.getDateD() != null) {
            dateDebutPicker.setValue(event.getDateD().toLocalDate());
        }

        if (event.getDateF() != null) {
            dateFinPicker.setValue(event.getDateF().toLocalDate());
        }

        if (event.getImage() != null && !event.getImage().isEmpty()) {
            try {
                String fullPath = "C:\\xampp\\htdocs\\imageP\\" + event.getImage();
                File imageFile = new File(fullPath);
                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString());
                    imageView.setImage(image);
                    selectedImageFile = imageFile;
                }
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement de l'image: " + e.getMessage());
            }
        }

        ajouterEvents.setText("Modifier l'événement");
    }

    private boolean validateFields() {
        String titre = nomEventField.getText();
        String description = descriptionField.getText();
        LocalDate dateDebut = dateDebutPicker.getValue();
        LocalDate dateFin = dateFinPicker.getValue();
        LocalDate dateSysteme = LocalDate.now();

        if (titre == null || titre.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champ manquant", null, "Le champ titre est obligatoire.");
            return false;
        }

        if (description == null || description.trim().isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champ manquant", null, "Le champ description est obligatoire.");
            return false;
        }

        if (dateDebut == null) {
            showAlert(Alert.AlertType.WARNING, "Champ manquant", null, "Veuillez sélectionner une date de début.");
            return false;
        }

        if (!dateDebut.isAfter(dateSysteme)) {
            showAlert(Alert.AlertType.WARNING, "Date invalide", null, "La date de début doit être après aujourd'hui.");
            return false;
        }

        if (dateFin == null) {
            showAlert(Alert.AlertType.WARNING, "Champ manquant", null, "Veuillez sélectionner une date de fin.");
            return false;
        }

        if (!dateFin.isAfter(dateDebut)) {
            showAlert(Alert.AlertType.WARNING, "Date invalide", null, "La date de fin doit être après la date de début.");
            return false;
        }

        return true;
    }


    private boolean askForAnotherEvent() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Que souhaitez-vous faire ?");
        alert.setContentText("Voulez-vous ajouter un autre événement ?");

        ButtonType buttonTypeOui = new ButtonType("Oui");
        ButtonType buttonTypeNon = new ButtonType("Non, retourner à la liste");

        alert.getButtonTypes().setAll(buttonTypeOui, buttonTypeNon);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == buttonTypeOui;
    }
}
