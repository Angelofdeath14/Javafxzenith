package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnLogin;
    @FXML private CheckBox chkRemember;
    @FXML private Label lblError;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Effacer le message d'erreur au démarrage
        lblError.setText("");
        
        // Ajouter un écouteur pour permettre la connexion en appuyant sur Entrée
        txtPassword.setOnAction(event -> handleLogin());
    }

    @FXML
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Veuillez saisir un nom d'utilisateur et un mot de passe.");
            return;
        }

        // Pour la démo, on accepte n'importe quel utilisateur/mot de passe
        // Dans une vraie application, vous devriez vérifier les identifiants dans une base de données
        try {
            navigateToMainPage();
        } catch (Exception e) {
            lblError.setText("Erreur lors de la connexion: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleForgotPassword() {
        // Afficher une alerte d'information pour simuler la fonctionnalité
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText("Réinitialisation du mot de passe");
        alert.setContentText("Cette fonctionnalité n'est pas encore implémentée.");
        alert.showAndWait();
    }

    @FXML
    private void handleRegister() {
        // Afficher une alerte d'information pour simuler la fonctionnalité
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText("Inscription");
        alert.setContentText("Cette fonctionnalité n'est pas encore implémentée.");
        alert.showAndWait();
    }

    private void navigateToMainPage() {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL url = getClass().getResource("/Dashboard.fxml");
            if (url == null) {
                url = getClass().getClassLoader().getResource("Dashboard.fxml");
            }
            if (url == null) {
                String resourcePath = "file:" + System.getProperty("user.dir") + "/target/classes/Dashboard.fxml";
                url = new URL(resourcePath);
            }
            
            loader.setLocation(url);
            Parent root = loader.load();
            
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.setTitle("Tableau de bord");
            Scene scene = new Scene(root);
            stage.setScene(scene);
        } catch (IOException e) {
            lblError.setText("Erreur de navigation: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 