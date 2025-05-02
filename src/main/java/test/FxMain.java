package test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Screen;
import Utils.MainStyleFixer;
import Utils.DatabaseInitializer;
import java.util.logging.Logger;
import java.util.logging.Level;

public class FxMain extends Application {
    private static final Logger logger = Logger.getLogger(FxMain.class.getName());

    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialiser la base de données
            logger.info("Initialisation de la base de données...");
            DatabaseInitializer.initialize();
            
            // Charger la vue principale de l'application
            logger.info("Chargement de l'interface utilisateur...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FrontEvents.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            
            // Appliquer le style professionnel
            MainStyleFixer.applyProfessionalStyle(scene);
            
            // Configurer pour plein écran
            primaryStage.setTitle("Artphoria - Plateforme de Gestion d'Événements Artistiques");
            primaryStage.setScene(scene);
            
            // Définir la taille pour occuper tout l'écran
            javafx.geometry.Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            primaryStage.setX(screenBounds.getMinX());
            primaryStage.setY(screenBounds.getMinY());
            primaryStage.setWidth(screenBounds.getWidth());
            primaryStage.setHeight(screenBounds.getHeight());
            
            // Alternative au mode plein écran, qui parfois peut causer des problèmes d'interface
            primaryStage.setMaximized(true);
            
            // Activer le mode plein écran
            primaryStage.setFullScreen(true);
            primaryStage.setFullScreenExitHint("");  // Supprimer le message de sortie du plein écran
            
            primaryStage.show();
            
            logger.info("Application démarrée en plein écran!");
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Erreur lors du démarrage de l'application", e);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}