package tn.esprit.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try{
            //FXMLLoader loader=new FXMLLoader(getClass().getResource("/afficher-produit-admin.fxml"));
            FXMLLoader loader=new FXMLLoader(getClass().getResource("/FrontEvents.fxml"));
            //FXMLLoader loader=new FXMLLoader(getClass().getResource("/afficher-produit-user.fxml"));
            Parent root=loader.load();
            Scene scene=new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Gestion produits");
            primaryStage.show();
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }
}
