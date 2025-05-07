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
import tn.esprit.entity.Reclamation;
import tn.esprit.service.ServiceReclamation;

import java.io.IOException;
import java.util.Optional;

public class AfficherReclamationController {

    @FXML
    private ListView<Reclamation> lvreclamation;
    ServiceReclamation serviceReclamation = new ServiceReclamation();
    ObservableList<Reclamation> reclamationObservableList = FXCollections.observableArrayList();
    public void initialize() {
        refresh();
    }
    private void refresh(){
        lvreclamation.getItems().clear();
        reclamationObservableList.addAll(serviceReclamation.afficher());
        lvreclamation.setItems(reclamationObservableList);
    }
    @FXML
    void gotoReview(ActionEvent event) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficher-review.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter Reclamation");
            stage.setScene(new Scene(root));
            stage.show();
            Stage stage2 = (Stage) lvreclamation.getScene().getWindow();
            stage2.close();


        }catch (IOException e){
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le formulaire d'ajout.", e.getMessage());
        }
    }
    @FXML
    void ajouter(ActionEvent event) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouter-modifier-reclamation.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter Reclamation");
            stage.setScene(new Scene(root));
            stage.show();
            Stage stage2 = (Stage) lvreclamation.getScene().getWindow();
            stage2.close();


        }catch (IOException e){
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le formulaire d'ajout.", e.getMessage());
        }
    }

    @FXML
    void modifier(ActionEvent event) {
        Reclamation reclamation = lvreclamation.getSelectionModel().getSelectedItem();
        if (reclamation != null) {
            try{
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouter-modifier-reclamation.fxml"));
                Parent root = loader.load();
                AjouterModifierReclamationController controller = loader.getController();
                controller.setReclamation(reclamation);
                Stage stage = new Stage();
                stage.setTitle("Modifier Reclamation");
                stage.setScene(new Scene(root));
                stage.show();
                Stage stage2 = (Stage) lvreclamation.getScene().getWindow();
                stage2.close();


            }catch (IOException e){
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le formulaire de modification.", e.getMessage());
            }
        }
    }

    @FXML
    void supprimer(ActionEvent event) {
        Reclamation reclamation = lvreclamation.getSelectionModel().getSelectedItem();
        if (reclamation == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner une réclamation à supprimer.", null);
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Supprimer la réclamation");
        confirm.setContentText("Êtes-vous sûr de vouloir supprimer cette réclamation ?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                serviceReclamation.supprimer(reclamation.getId());
                showAlert(Alert.AlertType.INFORMATION, "Suppression réussie", "La réclamation a été supprimée avec succès.", null);
                refresh();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression.", e.getMessage());
            }
        }
    }
    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        if (header != null) alert.setHeaderText(header);
        if (content != null) alert.setContentText(content);
        alert.showAndWait();
    }

}
