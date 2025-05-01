package tn.esprit.controller;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import tn.esprit.entities.Command;
import tn.esprit.service.ServiceCommand;
import tn.esprit.service.ServiceProduit;
import tn.esprit.utils.EmailSender;

import javax.mail.MessagingException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
public class CommandAdminController {
    @FXML
    private ListView<Command> lvcommand;

    private final ServiceCommand service = new ServiceCommand();
    private final ServiceProduit serviceProduit=new ServiceProduit();
    private ObservableList<Command> commandList;
    public void initialize(){
        loadCommands();
    }
    private void loadCommands() {
        List<Command> commands = service.afficher();
        commandList = FXCollections.observableArrayList(commands);
        lvcommand.setItems(commandList);
    }
    @FXML
    private void accept(ActionEvent event) {
        Command selected = lvcommand.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an order to accept.");
            return;
        }
        if ("Pending".equalsIgnoreCase(selected.getStatus())) {
            selected.setStatus("Accepted");
            service.modifier(selected);
            try {
                EmailSender.sendInvoiceEmail("lisa.fx370c@gmail.com",selected,serviceProduit.getByCommandId(selected.getId()));
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }
            showAlert(Alert.AlertType.INFORMATION, "Order Accepted",
                    "Order #" + selected.getId() + " has been accepted.");
            loadCommands();
        } else {
            showAlert(Alert.AlertType.WARNING, "Invalid Operation",
                    "Only orders with status \"Pending\" can be accepted.");
        }
    }
    @FXML
    private void reject(ActionEvent event) {
        Command selected = lvcommand.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an order to reject.");
            return;
        }
        if ("Pending".equalsIgnoreCase(selected.getStatus())) {
            selected.setStatus("Rejected");
            service.modifier(selected);
            showAlert(Alert.AlertType.INFORMATION, "Order Rejected",
                    "Order #" + selected.getId() + " has been rejected.");
            loadCommands();
        } else {
            showAlert(Alert.AlertType.WARNING, "Invalid Operation",
                    "Only orders with status \"Pending\" can be rejected.");
        }
    }
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML
    void retour(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/produit-admin.fxml"));
            AnchorPane root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Produit Admin");
            stage.show();
            ((Stage) lvcommand.getScene().getWindow()).close();
        } catch (IOException e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Erreur");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Impossible de revenir à l'affichage utilisateur : " + e.getMessage());
            errorAlert.showAndWait();
        }
    }

}
