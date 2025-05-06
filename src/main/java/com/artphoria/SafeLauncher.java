package com.artphoria;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Lanceur ultra-sécurisé pour Artphoria
 */
public class SafeLauncher extends Application {
    
    private static CountDownLatch latch = new CountDownLatch(1);
    private static volatile boolean initialized = false;
    
    static {
        // Configurations critiques de JavaFX
        System.setProperty("javafx.preloader", "false");
        System.setProperty("prism.order", "sw");
        System.setProperty("javafx.autoproxy", "true");
        System.setProperty("javafx.verbose", "true");
    }
    
    @Override
    public void init() {
        // Initialisation avant l'affichage de l'UI
        try {
            initialized = true;
            System.out.println("Artphoria: Initialisation réussie");
        } catch (Exception e) {
            System.err.println("Erreur d'initialisation: " + e.getMessage());
        } finally {
            latch.countDown();
        }
    }
    
    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // Attendre l'initialisation
            if (!initialized) {
                latch.await(10, TimeUnit.SECONDS);
            }
            
            System.out.println("Chargement de l'interface utilisateur...");
            
            // Charger l'interface de façon explicite
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/UserInterface.fxml"));
            Parent root = loader.load();
            
            // Configurer la scène et l'afficher
            Scene scene = new Scene(root);
            primaryStage.setTitle("ArtPhoria");
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.show();
            
            System.out.println("Interface chargée avec succès!");
            
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
            // Lancer l'application JavaFX
            launch(args);
            
        } catch (Throwable e) {
            System.err.println("Erreur fatale au lancement: " + e.getMessage());
            e.printStackTrace();
            
            // Essayer les autres classes de lancement
            try {
                System.out.println("Tentative avec SimpleLauncher...");
                SimpleLauncher.main(args);
            } catch (Throwable ex) {
                try {
                    System.out.println("Tentative avec FxMain...");
                    test.FxMain.main(args);
                } catch (Throwable ex2) {
                    System.err.println("Échec de tous les lanceurs!");
                }
            }
        }
    }
} 