package tn.esprit.controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.esprit.service.UserDao;
import tn.esprit.service.session.AuthDTO;
import tn.esprit.service.session.UserSession;

import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginController {

    private final UserDao userDao = new UserDao();

    @FXML
    private TextField tfEmail;

    @FXML
    private PasswordField pfPassword;

    @FXML
    private Label invalidLogin;

    @FXML
    private CheckBox rememberMeCheckbox;

    @FXML
    void handleLogin(ActionEvent event) {
        String email = tfEmail.getText().trim();
        String password = pfPassword.getText().trim();

        // Vérification de la validité de l'email
        if (!isValidEmail(email)) {
            invalidLogin.setText("Email invalide.");
            return;
        }

        try {
            boolean isLogged = userDao.login(email, password);

            if (!isLogged) {
                invalidLogin.setText("Email ou mot de passe incorrect.");
                return;
            }

            AuthDTO user = UserSession.CURRENT_USER.getUserLoggedIn();

            // Vérifier si le compte est activé
            if (!user.isEnabled()) {
                // === Compte non activé : demander le code ===
                String resetToken = userDao.getConfirmationTokenByEmail(email);
                System.out.println("Token reçu depuis la BD : " + resetToken);

                TextInputDialog codeDialog = new TextInputDialog();
                codeDialog.setTitle("Confirmation de compte");
                codeDialog.setHeaderText("Entrez le code envoyé par email");
                codeDialog.setContentText("Code de confirmation :");

                codeDialog.showAndWait().ifPresent(codeSaisi -> {
                    System.out.println("Code saisi par l'utilisateur : " + codeSaisi);
                    if (codeSaisi.trim().equals(resetToken.trim())) {
                        try {
                            userDao.enableUser(email);
                            user.setEnabled(true); // Mettre à jour l'objet utilisateur
                            invalidLogin.setText("Compte activé ! Vous pouvez maintenant vous connecter.");
                            loadScene("/Dashboard.fxml");
                        } catch (SQLException e) {
                            e.printStackTrace();
                            invalidLogin.setText("Erreur lors de l'activation.");
                        }
                    } else {
                        invalidLogin.setText("Code incorrect !");
                    }
                });
                return; // On arrête ici, car on a déjà redirigé ou affiché une erreur
            }

            // Si le compte est activé, redirection selon le rôle
            String role = String.valueOf(user.getRoles()); // Attention : à adapter si getRoles() retourne autre chose
            System.out.println("*******LOGIN*******\n"+role);
            if (role.contains("ROLE_ADMIN")) {
                loadScene("/Dashboard.fxml");
            } else if (role.contains("ROLE_USER")) {
                loadScene("/ShowProfile.fxml");
            } else {
                invalidLogin.setText("Rôle non reconnu.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            invalidLogin.setText("Erreur interne. Veuillez réessayer.");
        }
    }

    @FXML
    void navigateToRegister(ActionEvent event) {
        try {
            Stage stage = (Stage) tfEmail.getScene().getWindow();
            stage.setTitle("Register");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Register.fxml"));
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            invalidLogin.setText("Erreur lors du chargement de la page d'inscription.");
        }
    }

    @FXML
    void goToForgotPass(ActionEvent event) {
        loadSceneWithErrorHandling("/ForgotPassword.fxml", "Erreur lors du chargement de la page de réinitialisation.");
    }
    private void loadSceneWithErrorHandling(String fxmlPath, String errorMsg) {
        try {
            Stage stage = (Stage) tfEmail.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            invalidLogin.setText(errorMsg);
        }
    }
    private void loadScene(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) tfEmail.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            invalidLogin.setText("Erreur de chargement de l'interface.");
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
