package tn.esprit.controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.esprit.entities.User;
import tn.esprit.service.ServiceUser;
import tn.esprit.service.session.AuthDTO;
import tn.esprit.service.session.UserSession;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ProfileSettingController extends NavigationController implements Initializable {

    private ServiceUser serviceUser;

    @FXML
    private Label navigate;

    @FXML
    private Text emailError;

    @FXML
    private Text fnError;

    @FXML
    private TextField ftEmail;

    @FXML
    private TextField ftlast_name;

    @FXML
    private TextField ftfirst_name;

    @FXML
    private Text lnError;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        serviceUser = new ServiceUser(); // Initialisation ici
        chargeData();
    }

    void clearError() {
        lnError.setText("");
        fnError.setText("");
        emailError.setText("");

        ftfirst_name.getStyleClass().remove("error");
        ftEmail.getStyleClass().remove("error");
        ftlast_name.getStyleClass().remove("error");
    }

    void chargeData() {
        AuthDTO curr = UserSession.CURRENT_USER.getUserLoggedIn();
        ftEmail.setText(curr.getEmail());
        ftfirst_name.setText(curr.getfirst_name());
        ftlast_name.setText(curr.getlast_name());
    }

    @FXML
    void saveChanges(ActionEvent event) {
        clearError();
        boolean isValid = true;

        if (ftfirst_name.getText().isEmpty()) {
            isValid = false;
            ftfirst_name.getStyleClass().add("error");
            fnError.setText("First name is required");
        } else if (ftfirst_name.getText().length() < 4) {
            fnError.setText("First name must contain at least 4 characters.");
            isValid = false;
            ftfirst_name.getStyleClass().add("error");
        }

        if (ftlast_name.getText().isEmpty()) {
            isValid = false;
            lnError.setText("Last name is required");
            ftlast_name.getStyleClass().add("error");
        } else if (ftlast_name.getText().length() < 4) {
            lnError.setText("Last name must contain at least 4 characters.");
            ftlast_name.getStyleClass().add("error");
            isValid = false;
        }

        if (ftEmail.getText().isEmpty()) {
            isValid = false;
            ftEmail.getStyleClass().add("error");
            emailError.setText("Email address is required.");
        } else if (!ftEmail.getText().matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")) {
            emailError.setText("Invalid email address.");
            ftEmail.getStyleClass().add("error");
            isValid = false;
        }

        if (isValid) {
            try {
                User user = new User();
                user.setId(UserSession.CURRENT_USER.getUserLoggedIn().getId());
                user.setfirst_name(ftfirst_name.getText());
                user.setlast_name(ftlast_name.getText());
                user.setEmail(ftEmail.getText());
                user.setPassword(UserSession.CURRENT_USER.getUserLoggedIn().getPassword());
                user.setRoles(UserSession.CURRENT_USER.getUserLoggedIn().getRoles().toString());
                user.setBanned(false); // ou true si nécessaire

                serviceUser.updateOne(user);

                // Mettre à jour la session utilisateur
                UserSession.CURRENT_USER.getUserLoggedIn().setfirst_name(user.getfirst_name());
                UserSession.CURRENT_USER.getUserLoggedIn().setlast_name(user.getlast_name());
                UserSession.CURRENT_USER.getUserLoggedIn().setEmail(user.getEmail());

                // Afficher message succès
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("User updated");
                alert.setContentText("Your account has been updated.");
                alert.show();

                // Redirection vers Dashboard
                Stage stage = (Stage) this.ftEmail.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListUsers.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root, 822, 495);
                stage.setTitle("Dashboard");
                stage.setScene(scene);
                stage.show();

            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("SQL Error");
                alert.setContentText(e.getMessage());
                alert.show();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    public void getBack(MouseEvent mouseEvent) {
        Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
        stage.close();
    }
}
