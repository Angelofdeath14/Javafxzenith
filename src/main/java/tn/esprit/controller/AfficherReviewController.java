package tn.esprit.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import tn.esprit.entity.Review;
import tn.esprit.service.ServiceReview;

import java.io.IOException;
import java.util.Optional;

public class AfficherReviewController {

    @FXML private ListView<Review> lvReview;
    private ServiceReview service = new ServiceReview();
    private ObservableList<Review> data = FXCollections.observableArrayList();

    public void initialize() {
        refresh();
    }

    private void refresh() {
        data.clear();
        data.addAll(service.afficher());
        lvReview.setItems(data);
    }

    @FXML
    void ajouter(ActionEvent e) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/ajouter-modifier-review.fxml"));
            Stage st = new Stage();
            st.setTitle("Ajouter Review");
            st.setScene(new Scene(root));
            st.show();
            ((Stage) lvReview.getScene().getWindow()).close();

        } catch (IOException ex) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir l'interface d'ajout.", ex.getMessage());
        }
    }
    @FXML
    void gotoReclamation(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/afficher-reclamation.fxml"));
            Stage st = new Stage();
            st.setTitle("Ajouter Review");
            st.setScene(new Scene(root));
            st.show();
            ((Stage) lvReview.getScene().getWindow()).close();

        } catch (IOException ex) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir l'interface d'ajout.", ex.getMessage());
        }
    }

    @FXML
    void modifier(ActionEvent e) {
        Review sel = lvReview.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Sélectionnez une review à modifier.", null);
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouter-modifier-review.fxml"));
            Parent root = loader.load();
            AjouterModifierReviewController ctrl = loader.getController();
            ctrl.setReview(sel);

            Stage st = new Stage();
            st.setTitle("Modifier Review");
            st.setScene(new Scene(root));
            st.show();
            ((Stage) lvReview.getScene().getWindow()).close();

        } catch (IOException ex) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir l'interface de modification.", ex.getMessage());
        }
    }

    @FXML
    void supprimer(ActionEvent e) {
        Review sel = lvReview.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Sélectionnez une review à supprimer.", null);
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Voulez‑vous vraiment supprimer cette review ?",
                ButtonType.OK, ButtonType.CANCEL);
        Optional<ButtonType> res = confirm.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            service.supprimer(sel.getId());
            showAlert(Alert.AlertType.INFORMATION, "Suppression", "Review supprimée avec succès.", null);
            refresh();
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
