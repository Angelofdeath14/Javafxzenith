package tn.esprit.controller;


import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import tn.esprit.entities.Evenement;
import tn.esprit.entities.Session;
import tn.esprit.service.EvenementService;
import tn.esprit.service.SessionService;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class AjouterSessionController implements Initializable {
    @FXML private TextArea descriptionField;
    @FXML private DatePicker startTimePicker;
    @FXML private DatePicker endTimePicker;
    @FXML private ComboBox<Evenement> evenementComboBox;

    private Session sessionToEdit = null;
    private final SessionService sessionService = new SessionService();
    private final EvenementService evenementService;

    {
        try {
            evenementService = new EvenementService();
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement", 
                     "Impossible de charger la liste des événements: " + e.getMessage());
        }
    }

    public void initializeForEdit(Session session) {
        this.sessionToEdit = session;
        descriptionField.setText(session.getDescription());
        startTimePicker.setValue(session.getStartTime().toLocalDate());
        endTimePicker.setValue(session.getEndTime().toLocalDate());
        
        // Sélectionner l'événement correspondant
        evenementComboBox.getItems().stream()
            .filter(e -> e.getId() == session.getEvenementId())
            .findFirst()
            .ifPresent(evenementComboBox::setValue);
    }

    @FXML
    private void handleEnregistrer() {
        if (validateFields()) {

                Session session = new Session();
                if (sessionToEdit != null) {
                    session.setId(sessionToEdit.getId());
                    session.setImage(sessionToEdit.getImage());
                }
                session.setDescription(descriptionField.getText().trim());
                
                // Utiliser setStartTime et setEndTime au lieu de setDateDebut et setDateFin
                // pour correspondre aux colonnes start_time et end_time de la table session
                session.setStartTime(LocalDateTime.of(startTimePicker.getValue(), LocalTime.MIDNIGHT));
                session.setEndTime(LocalDateTime.of(endTimePicker.getValue(), LocalTime.MIDNIGHT));
                session.setEvenementId(evenementComboBox.getValue().getId());

                if (sessionToEdit != null) {
                    sessionService.modifierSession(session);
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Modification réussie", 
                            "La session a été modifiée avec succès !");
                } else {
                    sessionService.ajouterSession(session);
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Ajout réussi", 
                            "La session a été ajoutée avec succès !");
                }

                closeWindow();

        }
    }

    private boolean validateFields() {
        StringBuilder errors = new StringBuilder();

        if (evenementComboBox.getValue() == null) {
            errors.append("Veuillez sélectionner un événement.\n");
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
        }
    }
} 