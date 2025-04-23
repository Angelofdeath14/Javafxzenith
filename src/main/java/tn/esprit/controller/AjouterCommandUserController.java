package tn.esprit.controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.entities.Command;
import tn.esprit.entities.Produit;
import tn.esprit.service.ServiceCommand;
import tn.esprit.service.ServiceProduit;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

public class AjouterCommandUserController {

    @FXML private ScrollPane cartScrollPane;
    @FXML private VBox cartContainer;
    @FXML private TextArea deliveryAddressField;
    @FXML private TextArea notesArea;
    @FXML private Label productInfoLabel;

    private Command currentCommand;
    private List<Produit> cart;
    private final ServiceProduit serviceProduit = new ServiceProduit();
    private final ServiceCommand serviceCommand = new ServiceCommand();

    /**
     * Called by previous screen to pass in the cart products.
     */
    public void setCart(List<Produit> cart) {
        this.cart = cart;
        loadCartItems();
    }

    private void loadCartItems() {
        cartContainer.getChildren().clear();
        for (Produit p : cart) {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/product-cart-card-view.fxml"));
                Node itemNode = loader.load();
                CartItemViewController ctrl = loader.getController();
                ctrl.fillItem(p, removed -> {
                    cart.remove(removed);
                    cartContainer.getChildren().remove(itemNode);
                });
                cartContainer.getChildren().add(itemNode);
            } catch (IOException e) {
                System.err.println("Error loading cart item: " + e.getMessage());
            }
        }
    }

    public void setCommand(Command command) {
        this.currentCommand = command;
        populateFields();
    }

    private void populateFields() {
        if (currentCommand != null) {
            deliveryAddressField.setText(currentCommand.getDelivery_address());
            notesArea.setText(currentCommand.getNotes());
        }
    }

    @FXML
    void submit(ActionEvent event) {
        if (cart == null || cart.isEmpty()) {
            new Alert(Alert.AlertType.WARNING,
                    "Le panier est vide.").showAndWait();
            return;
        }
        if (currentCommand == null) {
            Command cmd = new Command();
            cmd.setId_user(1);
            cmd.setCreate_at(LocalDateTime.now());
            cmd.setStatus("Pending");
            cmd.setTotal_amount(
                    cart.stream().mapToDouble(Produit::getPrix).sum());
            cmd.setDelivery_address(deliveryAddressField.getText());
            cmd.setNotes(notesArea.getText());

            int cmdId = serviceCommand.ajouterWithReturningId(cmd);
            if (cmdId != -1) {
                // update each product
                for (Produit p : cart) {
                    p.setEtat("Non disponible");
                    p.setCommand_id(cmdId);
                    serviceProduit.modifier(p);
                }
                new Alert(Alert.AlertType.INFORMATION,
                        "Commande ajoutée avec succès !").showAndWait();
            }
        } else {
            currentCommand.setDelivery_address(deliveryAddressField.getText());
            currentCommand.setNotes(notesArea.getText());
            serviceCommand.modifier(currentCommand);
            new Alert(Alert.AlertType.INFORMATION,
                    "Commande modifiée avec succès !").showAndWait();
        }
    }

    @FXML
    void retour(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/afficher-produit-user.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Produits");
            stage.show();

            Stage old = (Stage) cartScrollPane.getScene().getWindow();
            old.close();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
