package tn.esprit.controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.esprit.entities.Command;
import tn.esprit.entities.Produit;
import tn.esprit.service.ServiceCommand;
import tn.esprit.service.ServiceProduit;
import tn.esprit.service.session.UserSession;
import tn.esprit.utils.EmailSender;

import javax.mail.MessagingException;
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
    @FXML private RadioButton rbCash, rbCard;
    @FXML
    private Label ltotal;

    /**
     * Called by previous screen to pass in the cart products.
     */
    public void setCart(List<Produit> cart) {
        this.cart = cart;
        loadCartItems();
    }

    private void loadCartItems() {
        double total=0.0;
        cartContainer.getChildren().clear();
        for (Produit p : cart) {
            try {
                total+=p.getPrix();
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
        ltotal.setText("Total: "+total);
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
        boolean payByCard = rbCard.isSelected();
        boolean success = true;
        String status = "Pending";

        if (payByCard) {
            success = showPaymentDialog();
            status = success ? "Accepted" : null;

        }

        if (!success) {
            return;
        }
        if (currentCommand == null) {
            Command cmd = new Command();
            cmd.setId_user(UserSession.CURRENT_USER.getUserLoggedIn().getId());
            cmd.setCreate_at(LocalDateTime.now());
            cmd.setStatus(status);
            cmd.setTotal_amount(
                    cart.stream().mapToDouble(Produit::getPrix).sum());
            cmd.setDelivery_address(deliveryAddressField.getText());
            cmd.setNotes(notesArea.getText());

            int cmdId = serviceCommand.ajouterWithReturningId(cmd);
            if (cmdId != -1) {
                if(status.equals("Accepted")){
                    try {
                        EmailSender.sendInvoiceEmail("lisa.fx370c@gmail.com",cmd,serviceProduit.getByCommandId(cmdId));
                    } catch (MessagingException e) {
                        throw new RuntimeException(e);
                    }
                }
                // update each product
                for (Produit p : cart) {
                    p.setEtat("Vendus");
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
    private boolean showPaymentDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/payment-form.fxml"));
            Parent form = loader.load();
            PaymentFormController ctrl = loader.getController();


            double total = cart.stream().mapToDouble(Produit::getPrix).sum();
            ctrl.setPaymentDetails((long)(total * 100), "usd");

            Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.setTitle("Paiement par carte");
            dialog.setScene(new Scene(form));
            dialog.showAndWait();

            return ctrl.isPaymentSuccessful();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            new Alert(Alert.AlertType.ERROR, "Impossible d'ouvrir le formulaire de paiement.")
                    .showAndWait();
            return false;
        }
    }

}
