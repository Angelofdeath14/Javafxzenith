package tn.esprit.controller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import tn.esprit.service.session.UserSession;

import java.io.IOException;
public class DashboardFrontController {
    @FXML private StackPane contentPane;
    @FXML private Label lblUsername;
    @FXML private ImageView userIcon;

    @FXML
    public void initialize() {
        String currentUser = UserSession.CURRENT_USER.getUserLoggedIn().getfirst_name();
        lblUsername.setText(currentUser);

        // Load default view
        navigateTo("FrontEvents.fxml");
    }

    private void navigateTo(String fxmlFile) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource("/" + fxmlFile));
            contentPane.getChildren().setAll(view);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Navigation handlers
    @FXML private void handleNavEvenement() {
        navigateTo("FrontEvents.fxml");
    }
    @FXML
    private void handleNavSellProduit() {
        navigateTo("afficher-produit-admin.fxml");
    }

    @FXML
    private void handleMesCommands() {
        navigateTo("afficher-command-user.fxml");
    }

    @FXML private void handleNavProduit() {
        navigateTo("afficher-produit-user.fxml");
    }
    @FXML private void handleNavCart() {
        navigateTo("ajouter-command-user.fxml");
    }
    @FXML private void handleNavReclamation() {
        navigateTo("afficher-reclamation.fxml");
    }
    @FXML private void handleNavReview() {
        navigateTo("afficher-review.fxml");
    }

    // Redirect to profile on icon click
    @FXML private void handleProfile(MouseEvent event) {
        navigateTo("ShowProfile.fxml");
    }
}
