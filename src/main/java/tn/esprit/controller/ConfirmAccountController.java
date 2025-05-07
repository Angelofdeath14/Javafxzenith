package tn.esprit.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import tn.esprit.service.ServiceUser;

public class ConfirmAccountController {

    @FXML
    private TextField tokenField;  // Champs pour le token au lieu de l'email
    @FXML
    private Label messageLabel;

    private ServiceUser userService = new ServiceUser();

    @FXML
    private void handleConfirm() {
        String token = tokenField.getText();  // Obtenir le token entré par l'utilisateur

        try {
            if (userService.confirmAccount(token)) {  // Utiliser le token pour confirmer le compte
                messageLabel.setText("✅ Compte confirmé avec succès !");
            } else {
                messageLabel.setText("❌ Token invalide ou compte déjà confirmé !");
            }
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("❌ Erreur serveur.");
        }
    }
}
