package tn.esprit.controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.text.Text;
import tn.esprit.entities.User;
import tn.esprit.service.UserRepository;
import tn.esprit.service.session.UserSession;

import java.sql.SQLException;

public class ChangePasswordController extends NavigationController {
    private final UserRepository userRepository = new UserRepository();
    @FXML
    private Text currentUserName;

    @FXML
    private Text confirmError, newError;
    @FXML
    private PasswordField fpNew, fpConfirm;

    // Clears error messages and styles
    private void clearError() {
        confirmError.setText("");
        newError.setText("");
        removeErrorStyles();
    }

    private void removeErrorStyles() {
        fpConfirm.getStyleClass().remove("error");
        fpNew.getStyleClass().remove("error");
    }

    private boolean validateFields() {
        boolean isValid = true;

        if (fpNew.getText().isEmpty() || fpNew.getText().length() < 8) {
            newError.setText("New Password must contain at least 8 characters.");
            fpNew.getStyleClass().add("error");
            isValid = false;
        }

        if (!fpNew.getText().equals(fpConfirm.getText())) {
            confirmError.setText("Passwords do not match.");
            fpConfirm.getStyleClass().add("error");
            isValid = false;
        }

        return isValid;
    }

    @FXML
    void saveChanges(ActionEvent event) {
        clearError();

        if (!validateFields()) {
            return;
        }

        String newPassword = fpNew.getText();

        User user = new User();
        user.setId(UserSession.CURRENT_USER.getUserLoggedIn().getId());
        user.setPassword(newPassword);  // mot de passe en clair ou tu peux encoder si besoin

        try {
            userRepository.changePassword(user);
            UserSession.CURRENT_USER.getUserLoggedIn().setPassword(newPassword);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Password Changed");
            alert.setContentText("Your password has been updated successfully.");
            alert.show();

            goToOverview();
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Error", "There was an error while updating your password.");
        }
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.show();
    }

    @FXML
    void initialize() throws SQLException {
        assert confirmError != null : "fx:id=\"confirmError\" was not injected.";
        assert fpConfirm != null : "fx:id=\"fpConfirm\" was not injected.";
        assert fpNew != null : "fx:id=\"fpNew\" was not injected.";
        assert newError != null : "fx:id=\"newError\" was not injected.";
    }


}
