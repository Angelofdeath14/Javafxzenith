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
import tn.esprit.entities.Reclamation;
import tn.esprit.service.ServiceReclamation;
import tn.esprit.service.session.UserSession;
import tn.esprit.utils.BadWordFilter;

import java.io.IOException;
import java.time.LocalDateTime;

public class AjouterModifierReclamationController {

    @FXML
    private TextField tftitre;

    @FXML
    private TextArea tadesc;
    Reclamation reclamation;
    ServiceReclamation serviceReclamation=new ServiceReclamation();

    public void setReclamation(Reclamation reclamation) {
        this.reclamation = reclamation;
        tftitre.setText(reclamation.getTitre());
        tadesc.setText(reclamation.getDescription());
    }

    @FXML
    void retour(ActionEvent event) {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficher-reclamation.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Afficher Reclamation");
            stage.setScene(new Scene(root));
            stage.show();
            Stage stage2 = (Stage) tftitre.getScene().getWindow();
            stage2.close();


        }catch (IOException e){
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de retourner à l'affichage.", e.getMessage());
        }
    }

    @FXML
    void submit(ActionEvent event) {
        String titre = tftitre.getText();
        String desc = tadesc.getText();
        if (titre == null || titre.isEmpty() || desc == null || desc.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Champs incomplets", "Veuillez remplir tous les champs.", null);
            return;
        }

        String originalText=tadesc.getText();
        String filteredText = BadWordFilter.filterBadWords(originalText);
        int badWordCount = BadWordFilter.countBadWords(filteredText);
        System.out.println(badWordCount);
        if (badWordCount > 3) {
            showAlert(Alert.AlertType.ERROR, "Réclamation refusée", "Votre réclamation contient trop de mauvais mots et ne peut être enregistrée.",null);

            return;
        }
        if(reclamation==null){
            Reclamation r=new Reclamation();


            r.setTitre(tftitre.getText());
            r.setDescription(filteredText);
            r.setDate_creation(LocalDateTime.now());
            r.setId_user(UserSession.CURRENT_USER.getUserLoggedIn().getId());
            serviceReclamation.ajouter(r);
            showAlert(Alert.AlertType.INFORMATION, "Ajout réussi", "La réclamation a été ajoutée.", null);
        }else{
            reclamation.setTitre(tftitre.getText());
            reclamation.setDescription(filteredText);
            reclamation.setDate_creation(LocalDateTime.now());
            serviceReclamation.modifier(reclamation);
            showAlert(Alert.AlertType.INFORMATION, "Modification réussie", "La réclamation a été modifiée.", null);
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
