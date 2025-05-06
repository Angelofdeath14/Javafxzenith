package com.artphoria;

import Utils.DatabaseUpdater;
import Utils.MainStyleFixer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Mettre à jour la structure de la base de données
        DatabaseUpdater.updateDatabase();
        
        // Initialiser les correctifs de style
        MainStyleFixer.initialize();
        
        // Charger le fichier FXML principal
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserInterface.fxml"));
        Parent root = loader.load();
        
        primaryStage.setTitle("ArtPhoria - Gestion des Événements");
        Scene scene = new Scene(root);
        
        // Appliquer le style clair par défaut
        MainStyleFixer.applyLightModeToScene(scene);
        
        primaryStage.setScene(scene);
        
        // Appliquer le mode plein écran
        MainStyleFixer.applyFullScreenMode(primaryStage);
        
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
} 