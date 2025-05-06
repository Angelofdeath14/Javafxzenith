package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.text.TextAlignment;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.stage.Screen;
import java.io.IOException;
import java.util.Optional;
import java.lang.reflect.Method;

public class UserInterfaceController {
    @FXML
    private StackPane contentArea;

    @FXML
    public void showEvents() {
        try {
            System.out.println("Tentative de chargement de FrontEvents.fxml");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FrontEvents.fxml"));
            if (loader.getLocation() == null) {
                System.err.println("ERREUR: Impossible de trouver le fichier FrontEvents.fxml");
                showErrorAlert("Fichier introuvable", "Le fichier FrontEvents.fxml est introuvable.");
                return;
            }
            Parent eventsView = loader.load();
            
            // Appliquer le design moderne
            Scene scene = contentArea.getScene();
            if (scene != null) {
                Utils.MainStyleFixer.applyModernDesign(scene);
                Utils.MainStyleFixer.styleButtonsByText(eventsView);
            }
            
            contentArea.getChildren().setAll(eventsView);
            System.out.println("FrontEvents.fxml chargé avec succès et style moderne appliqué");
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

    /**
     * Méthode pour afficher les informations météo
     */
    @FXML
    private void showWeather() {
        try {
            // Afficher une boîte de dialogue pour saisir le nom de la ville
            TextInputDialog dialog = new TextInputDialog("Paris");
            dialog.setTitle("Service météo");
            dialog.setHeaderText("Entrez le nom d'une ville");
            dialog.setContentText("Ville:");
            
            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                String city = result.get();
                
                // Charger le contrôleur FrontEvents et utiliser sa méthode showWeatherDialog
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/FrontEvents.fxml"));
                try {
                    Parent eventsView = loader.load();
                    FrontEventsController controller = loader.getController();
                    
                    // Appeler la méthode showWeatherDialog par réflexion car elle est privée
                    Method showWeatherDialogMethod = FrontEventsController.class.getDeclaredMethod("showWeatherDialog", String.class);
                    showWeatherDialogMethod.setAccessible(true);
                    showWeatherDialogMethod.invoke(controller, city);
                } catch (Exception e) {
                    System.err.println("Erreur lors de l'accès à la méthode showWeatherDialog: " + e.getMessage());
                    e.printStackTrace();
                    showErrorAlert("Erreur", "Impossible d'afficher les informations météo: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'affichage de la météo: " + e.getMessage());
            e.printStackTrace();
            showErrorAlert("Erreur", "Impossible d'afficher les informations météo: " + e.getMessage());
        }
    }
    
    /**
     * Navigue vers le tableau de bord d'administration
     */
    @FXML
    private void showAdmin() {
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