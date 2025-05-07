package tn.esprit.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.esprit.service.OTPService;

import java.net.URL;
import java.util.ResourceBundle;

public class ResetPasswordController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Text emailError;

    @FXML
    private TextField ftCode;

    @FXML
    private TextField ftEmail;

    @FXML
    void confirmeOTP(ActionEvent event) {
        String email = ftEmail.getText();
        String code = ftCode.getText();

        if (email.isEmpty() || code.isEmpty()) {
            emailError.setText("Please fill in all fields.");
            return;
        }

        if (OTPService.validateOTP(email, code)) {
            try {
                Stage stage = (Stage) ftEmail.getScene().getWindow();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ChangeForgotPassword.fxml"));
                Parent root = loader.load();

                stage.setTitle("Change Password");
                stage.setScene(new Scene(root));
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            emailError.setText("Incorrect or expired code.");
        }
    }

    @FXML
    void goToLogin(ActionEvent event) {
        try {
            Stage stage = (Stage) ftCode.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();

            stage.setTitle("Login");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void initialize() {
        assert emailError != null : "fx:id=\"emailError\" was not injected: check your FXML file 'ResetPassword.fxml'.";
        assert ftCode != null : "fx:id=\"ftCode\" was not injected: check your FXML file 'ResetPassword.fxml'.";
        assert ftEmail != null : "fx:id=\"ftEmail\" was not injected: check your FXML file 'ResetPassword.fxml'.";
    }
}
