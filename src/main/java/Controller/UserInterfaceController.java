package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.stage.Screen;
import java.io.IOException;

public class UserInterfaceController {
    @FXML
    private StackPane contentArea;

    @FXML
    private void showEvents() {
        try {
            System.out.println("Tentative de chargement de FrontEvents.fxml");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FrontEvents.fxml"));
            if (loader.getLocation() == null) {
                System.err.println("ERREUR: Impossible de trouver le fichier FrontEvents.fxml");
                showErrorAlert("Fichier introuvable", "Le fichier FrontEvents.fxml est introuvable.");
                return;
            }
            Parent eventsView = loader.load();
            contentArea.getChildren().setAll(eventsView);
            System.out.println("FrontEvents.fxml chargé avec succès");
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de FrontEvents.fxml: " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Erreur de chargement", "Impossible de charger la vue des événements: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Exception inattendue: " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Erreur inattendue", "Une erreur inattendue est survenue: " + e.getMessage());
        }
    }

    @FXML
    private void showReservations() {
        try {
            // Vérifier si le fichier MyReservations.fxml existe réellement
            System.out.println("Tentative de chargement de MyReservations.fxml");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MyReservations.fxml"));
            if (loader.getLocation() == null) {
                System.err.println("ERREUR: Impossible de trouver le fichier MyReservations.fxml");
                showErrorAlert("Fichier introuvable", "Le fichier MyReservations.fxml est introuvable.");
                return;
            }
            Parent reservationsView = loader.load();
            contentArea.getChildren().setAll(reservationsView);
            System.out.println("MyReservations.fxml chargé avec succès");
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement: " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Erreur de chargement", "Impossible de charger la vue des réservations: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Exception inattendue: " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Erreur inattendue", "Une erreur inattendue est survenue: " + e.getMessage());
        }
    }
    
    /**
     * Navigue vers le tableau de bord d'administration
     */
    @FXML
    private void goToAdministration() {
        try {
            System.out.println("Chargement du tableau de bord d'administration...");
            
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
            if (loader.getLocation() == null) {
                System.err.println("ERREUR: Impossible de trouver le fichier Dashboard.fxml");
                showErrorAlert("Fichier introuvable", "Le fichier Dashboard.fxml est introuvable.");
                return;
            }
            
            Parent dashboardView = loader.load();
            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setTitle("Artphoria - Tableau de bord d'administration");
            Scene scene = new Scene(dashboardView);
            stage.setScene(scene);
            
            // Utiliser la méthode du StyleFixer pour appliquer le mode plein écran
            Utils.MainStyleFixer.applyFullScreenMode(stage);
            
            stage.show();
            
            System.out.println("Tableau de bord d'administration chargé avec succès");
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement du Dashboard: " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Erreur de chargement", "Impossible de charger le tableau de bord: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Exception inattendue: " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Erreur inattendue", "Une erreur inattendue est survenue: " + e.getMessage());
        }
    }
    
    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 