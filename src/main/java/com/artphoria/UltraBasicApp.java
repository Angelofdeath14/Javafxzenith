package com.artphoria;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class UltraBasicApp extends Application {

    @Override
    public void start(Stage stage) {
        Label label = new Label("Artphoria - Test de base");
        Button button = new Button("Test");
        button.setOnAction(e -> System.out.println("Button clicked"));
        
        VBox root = new VBox(10, label, button);
        root.setStyle("-fx-padding: 20px;");
        
        Scene scene = new Scene(root, 300, 200);
        stage.setScene(scene);
        stage.setTitle("Artphoria");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
} 