package tn.esprit.controller;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import tn.esprit.entities.Command;
import tn.esprit.entities.Produit;
import tn.esprit.service.ServiceCommand;

import java.io.IOException;

public class AfficherCommandUserController {

    @FXML
    private ListView<Command> lvcommand;
    ServiceCommand serviceCommand=new ServiceCommand();
    ObservableList<Command> data= FXCollections.observableArrayList();
    public void initialize(){
        data.setAll(serviceCommand.afficher());
        lvcommand.setItems(data);

    }
    @FXML
    void annuler(ActionEvent event) {
        Command command=lvcommand.getSelectionModel().getSelectedItem();
        if(command!=null){
            command.setStatus("Annuler");
            serviceCommand.modifier(command);
            data.clear();
            data.setAll(serviceCommand.afficher());
            lvcommand.setItems(data);
        }

    }

    @FXML
    void modifier(ActionEvent event) {
        Command selectedCommand = lvcommand.getSelectionModel().getSelectedItem();
        if (selectedCommand == null) {
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouter-command-user.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier command");
            stage.show();
            Stage stage2 = (Stage) lvcommand.getScene().getWindow();
            stage2.close();
            AjouterCommandUserController controller = loader.getController();
            controller.setCommand(selectedCommand);

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }

    @FXML
    void retour(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficher-produit-user.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Afficher Produit");
            stage.show();
            Stage stage2 = (Stage) lvcommand.getScene().getWindow();
            stage2.close();

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

}
