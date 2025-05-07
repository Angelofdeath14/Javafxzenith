package tn.esprit.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.entity.Review;
import tn.esprit.service.ServiceReview;

import java.io.IOException;
import java.time.LocalDateTime;

public class AjouterModifierReviewController {


    @FXML private TextArea taComment;
    @FXML private TextField tfNote;

    private Review review;
    private ServiceReview service = new ServiceReview();

    public void setReview(Review r) {
        this.review = r;

        taComment.setText(r.getComment());
        tfNote.setText(String.valueOf(r.getNote()));
    }

    @FXML
    void retour(ActionEvent e) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/afficher-review.fxml"));
            Stage st = new Stage();
            st.setTitle("Liste des Reviews");
            st.setScene(new Scene(root));
            st.show();
            ((Stage) taComment.getScene().getWindow()).close();

        } catch (IOException ex) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de revenir à la liste.", ex.getMessage());
        }
    }

    @FXML
    void submit(ActionEvent e) {
        String comment = taComment.getText().trim();
        String noteStr = tfNote.getText().trim();
        if (comment.isEmpty() || noteStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champs manquants", "Commentaire et note sont requis.", null);
            return;
        }

        try {

            int note = Integer.parseInt(noteStr);
            if (note < 0 || note > 5) {
                showAlert(Alert.AlertType.WARNING,
                        "Note invalide",
                        "La note doit être comprise entre 0 et 5.",
                        null);
                return;
            }
            if (review == null) {
                Review r = new Review();
                r.setComment(comment);
                r.setNote(note);
                r.setCreated_at(LocalDateTime.now());
                service.ajouter(r);
                showAlert(Alert.AlertType.INFORMATION, "Ajout", "Review ajoutée avec succès.", null);
            } else {
                review.setComment(comment);
                review.setNote(note);
                review.setCreated_at(LocalDateTime.now());
                service.modifier(review);
                showAlert(Alert.AlertType.INFORMATION, "Modification", "Review modifiée avec succès.", null);
            }

            retour(e);

        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Format invalide", "Les IDs et la note doivent être des nombres.", ex.getMessage());
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Opération échouée.", ex.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert a = new Alert(type);
        a.setTitle(title);
        if (header != null) a.setHeaderText(header);
        if (content != null) a.setContentText(content);
        a.showAndWait();
    }
}
