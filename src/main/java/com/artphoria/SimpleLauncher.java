package com.artphoria;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Lanceur ultra simplifié pour Artphoria
 */
public class SimpleLauncher extends Application {
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // Charger l'interface utilisateur
            Parent root = FXMLLoader.load(getClass().getResource("/UserInterface.fxml"));
            
            // Configurer la scène
            Scene scene = new Scene(root);
            primaryStage.setTitle("ArtPhoria - Gestion des Événements");
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.show();
            
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'interface: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }
    
    /**
     * Méthode principale qui lance l'application
     */
    public static void main(String[] args) {
        try {
            // Configurer les propriétés système
            System.setProperty("javafx.preloader", "false");
            System.setProperty("prism.order", "sw");
            
            // Lancer l'application
            launch(args);
            
        } catch (Exception e) {
            System.err.println("Erreur au lancement: " + e.getMessage());
            e.printStackTrace();
            
            // Dernier recours
            try {
                test.FxMain.main(args);
            } catch (Exception ex) {
                System.err.println("Échec total du lancement: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
} 