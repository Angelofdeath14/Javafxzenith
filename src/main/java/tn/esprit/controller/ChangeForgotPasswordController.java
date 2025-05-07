package tn.esprit.controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.mindrot.jbcrypt.BCrypt;
import tn.esprit.entities.User;
import tn.esprit.service.OTPService;
import tn.esprit.service.UserRepository;

import java.sql.SQLException;

public class ChangeForgotPasswordController {

    @FXML
    private PasswordField fpNew;

    @FXML
    private PasswordField fpConfirm;

    @FXML
    private Text newError;

    @FXML
    private Text confirmError;

    private final UserRepository userRepository = new UserRepository();

    /**
     * Méthode principale pour changer le mot de passe.
     * Elle est appelée lors de la soumission du formulaire.
     *
     * @param event Action de l'utilisateur sur le bouton ResetPassword
     */
    @FXML
    void changePassword(ActionEvent event) {
        // Nettoyer les messages d'erreur précédents
        clearErrors();

        // Vérification de la validité des champs
        if (!isValidPassword()) {
            return; // Si une erreur est trouvée, on arrête l'exécution
        }

        String email = OTPService.getEmail(); // Récupération de l'email de l'utilisateur

        // Tentative de mise à jour du mot de passe
        try {
            User user = userRepository.findByEmail(email); // Recherche de l'utilisateur par email
            if (user != null) {
                String hashedPassword = BCrypt.hashpw(fpNew.getText(), BCrypt.gensalt()); // Hachage du mot de passe
                user.setPassword(hashedPassword); // Mise à jour du mot de passe de l'utilisateur
                userRepository.changePassword(user); // Enregistrement dans la base de données

                // Redirection vers la page de connexion
                goToLogin();
            } else {
                // Si l'utilisateur n'est pas trouvé
                newError.setText("User not found. Please check your email.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            newError.setText("Error updating password. Please try again later.");
        }
    }

    /**
     * Méthode pour rediriger l'utilisateur vers la page de connexion.
     */
    @FXML
    private void goToLogin() {
        try {
            Stage stage = (Stage) fpNew.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            newError.setText("Error loading login page.");
        }
    }

    /**
     * Méthode pour nettoyer les messages d'erreur.
     */
    private void clearErrors() {
        newError.setText("");
        confirmError.setText("");
        fpNew.getStyleClass().remove("error");
        fpConfirm.getStyleClass().remove("error");
    }

    /**
     * Méthode pour valider les mots de passe et afficher des erreurs appropriées.
     *
     * @return true si les mots de passe sont valides, sinon false
     */
    private boolean isValidPassword() {
        boolean isValid = true;

        // Vérification de la longueur du nouveau mot de passe
        if (fpNew.getText().isEmpty()) {
            newError.setText("New password is required.");
            fpNew.getStyleClass().add("error");
            isValid = false;
        } else if (fpNew.getText().length() < 8) {
            newError.setText("Password must be at least 8 characters.");
            fpNew.getStyleClass().add("error");
            isValid = false;
        }

        // Vérification si les mots de passe correspondent
        if (!fpConfirm.getText().equals(fpNew.getText())) {
            confirmError.setText("Passwords do not match.");
            fpConfirm.getStyleClass().add("error");
            isValid = false;
        }

        return isValid;
    }

    @FXML
    void initialize() {
        assert newError != null : "fx:id=\"newError\" not injected: check FXML.";
        assert confirmError != null : "fx:id=\"confirmError\" not injected: check FXML.";
        assert fpConfirm != null : "fx:id=\"fpConfirm\" not injected: check FXML.";
        assert fpNew != null : "fx:id=\"fpNew\" not injected: check FXML.";
    }
}
