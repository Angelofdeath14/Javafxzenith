package Controller;

import Entity.Session;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.SessionService;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

public class ModifierSessionController implements Initializable {
    @FXML private TextField titreField;
    @FXML private TextArea descriptionArea;
    @FXML private DatePicker dateDebutPicker;
    @FXML private DatePicker dateFinPicker;
    @FXML private TextField capaciteField;
    @FXML private TextField locationField;
    @FXML private Button btnValider;
    @FXML private Button btnAnnuler;

    private Session session;
    private final SessionService sessionService = new SessionService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupButtons();
    }

    public void setSession(Session session) {
        this.session = session;
        populateFields();
    }

    private void populateFields() {
        if (session != null) {
            titreField.setText(session.getTitre());
            descriptionArea.setText(session.getDescription());
            capaciteField.setText(String.valueOf(session.getCapacity()));
            locationField.setText(session.getLocation());
            
            if (session.getStartTime() != null) {
                dateDebutPicker.setValue(session.getStartTime().toLocalDate());
            }
            if (session.getEndTime() != null) {
                dateFinPicker.setValue(session.getEndTime().toLocalDate());
            }
        }
    }

    private void setupButtons() {
        btnValider.setOnAction(e -> handleValider());
        btnAnnuler.setOnAction(e -> handleAnnuler());
    }

    private void handleValider() {
        try {
            session.setTitre(titreField.getText());
            session.setDescription(descriptionArea.getText());
            session.setCapacity(Integer.parseInt(capaciteField.getText()));
            session.setLocation(locationField.getText());
            
            if (dateDebutPicker.getValue() != null) {
                session.setStartTime(LocalDateTime.of(dateDebutPicker.getValue(), session.getStartTime().toLocalTime()));
            }
            if (dateFinPicker.getValue() != null) {
                session.setEndTime(LocalDateTime.of(dateFinPicker.getValue(), session.getEndTime().toLocalTime()));
            }

            if (sessionService.updateSession(session)) {
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Session modifiée", "La session a été modifiée avec succès.");
                closeWindow();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de modification", "Une erreur est survenue lors de la modification de la session.");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Format invalide", "Veuillez entrer un nombre valide pour la capacité.");
        }
    }

    private void handleAnnuler() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) btnValider.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 