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
import tn.esprit.service.ServiceUser;

import java.net.URL;
import java.util.ResourceBundle;

public class ForgotPasswordController extends NavigationController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Text emailError;

    @FXML
    private TextField ftEmail;

    private final ServiceUser serviceUser = new ServiceUser();
    private final OTPService otpService = new OTPService();

    @FXML
    void findAccount(ActionEvent event) {
        String email = ftEmail.getText().trim();

        if (email.isEmpty()) {
            emailError.setText("Please enter an email address.");
            return;
        }

        if (serviceUser.emailExists(email)) {
            String otp = otpService.generateOTP(email, 8);
            boolean otpSent = otpService.sendOTP(email, otp);

            if (otpSent) {
                try {
                    Stage stage = (Stage) ftEmail.getScene().getWindow();
                    stage.setTitle("Reset Password");

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/ResetPassword.fxml"));
                    Parent root = loader.load();
                    Scene scene = new Scene(root);

                    stage.setScene(scene);
                    stage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                    emailError.setText("Failed to load the Reset Password screen.");
                }
            } else {
                emailError.setText("Failed to send OTP. Try again later.");
            }
        } else {
            emailError.setText("The email address does not exist.");
        }
    }

    @FXML
    void goToLogin(ActionEvent event) {
        try {
            Stage stage = (Stage) ftEmail.getScene().getWindow();
            stage.setTitle("Login");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            emailError.setText("Failed to load the Login screen.");
        }
    }

    @FXML
    void initialize() {
        assert emailError != null : "fx:id=\"emailError\" was not injected: check your FXML file 'ForgotPassword.fxml'.";
        assert ftEmail != null : "fx:id=\"ftEmail\" was not injected: check your FXML file 'ForgotPassword.fxml'.";
    }
}
