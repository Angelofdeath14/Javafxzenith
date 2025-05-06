package com.artphoria;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Application JavaFX minimale pour tester si JavaFX fonctionne correctement
 */
public class MinimalApp extends Application {
    
    @Override
    public void start(Stage stage) {
        try {
            // Créer une interface utilisateur simple sans FXML
            Label label = new Label("Test de l'application Artphoria");
            label.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
            
            Button btnTest = new Button("Cliquez ici pour tester");
            btnTest.setOnAction(e -> label.setText("JavaFX fonctionne correctement!"));
            
            Button btnClose = new Button("Fermer l'application");
            btnClose.setOnAction(e -> stage.close());
            
            // Créer un layout
            VBox root = new VBox(20);
            root.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 30;");
            root.getChildren().addAll(label, btnTest, btnClose);
            
            // Configurer et afficher la scène
            Scene scene = new Scene(root, 400, 300);
            stage.setTitle("Test Artphoria");
            stage.setScene(scene);
            stage.show();
            
            System.out.println("Application minimale lancée avec succès!");
            
        } catch (Exception e) {
            System.err.println("Erreur dans l'application minimale: " + e);
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) {
        try {
            launch(args);
        } catch (Exception e) {
            System.err.println("Erreur au lancement de l'application minimale: " + e);
            e.printStackTrace();
        }
    }
} 