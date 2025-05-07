package tn.esprit.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.service.ServiceUser;
import tn.esprit.service.session.AuthDTO;

import java.io.IOException;

public class CodeConfirmationController {

    @FXML
    private TextField codeInput;

    @FXML
    private Label errorLabel;

    private int expectedCode;
    private AuthDTO user;

    public void setExpectedCode(int code) {
        this.expectedCode = code;
    }

    public void setUser(AuthDTO user) {
        this.user = user;
    }

    @FXML
    void validateCode(ActionEvent event) {
        try {
            int inputCode = Integer.parseInt(codeInput.getText().trim());
            System.out.println("Code saisi par l'utilisateur : " + inputCode);
            System.out.println("Code attendu : " + expectedCode);

            if (inputCode == expectedCode) {
                new ServiceUser().confirmAccount(user.getEmail());
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) codeInput.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } else {
                errorLabel.setText("Code incorrect.");
            }

        } catch (NumberFormatException e) {
            errorLabel.setText("Format de code invalide.");
        } catch (IOException e) {
            e.printStackTrace();
            errorLabel.setText("Erreur lors du chargement.");
        }
    }
}