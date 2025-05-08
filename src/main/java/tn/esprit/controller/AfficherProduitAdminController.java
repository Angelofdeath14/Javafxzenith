package tn.esprit.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import tn.esprit.entities.Produit;
import tn.esprit.service.ServiceProduit;
import tn.esprit.service.session.UserSession;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class AfficherProduitAdminController {
    @FXML
    private FlowPane cardsContainer;
    private Produit selectedProduit;
    private ServiceProduit serviceProduit = new ServiceProduit();

    public void initialize() {
        refreshCards();
    }

    private void refreshCards() {
        cardsContainer.getChildren().clear();
        List<Produit> produits = serviceProduit.getUserProducts(UserSession.CURRENT_USER.getUserLoggedIn().getId());
        for (Produit p : produits) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/product-card-view.fxml"));
                AnchorPane card = loader.load();
                ProductCardViewController ctrl = loader.getController();
                ctrl.fillCard(p);
                ctrl.getBtnAddToCart().setVisible(false);
                card.setOnMouseClicked(e -> selectedProduit = p);
                cardsContainer.getChildren().add(card);
            } catch (IOException e) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Erreur");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Impossible de charger la carte produit : " + e.getMessage());
                errorAlert.showAndWait();
            }
        }
    }

    @FXML
    private void ajouter(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouter-modifier-produit-admin.fxml"));
            AnchorPane root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter Produit");
            stage.showAndWait();
            refreshCards();
        } catch (IOException e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Erreur");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Impossible d'ouvrir l'interface d'ajout : " + e.getMessage());
            errorAlert.showAndWait();
        }
    }

    @FXML
    private void retour(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficher-produit-user.fxml"));
            AnchorPane root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Produits Utilisateur");
            stage.show();
        } catch (IOException e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Erreur");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Impossible de revenir à l'affichage utilisateur : " + e.getMessage());
            errorAlert.showAndWait();
        }
    }

    @FXML
    private void modifier(ActionEvent event) {
        if (selectedProduit == null) {
            Alert warningAlert = new Alert(Alert.AlertType.WARNING);
            warningAlert.setTitle("Modification");
            warningAlert.setHeaderText(null);
            warningAlert.setContentText("Veuillez sélectionner un produit à modifier.");
            warningAlert.showAndWait();
            return;
        }
        if ("Accepted".equals(selectedProduit.getEtat()) || "Rejected".equals(selectedProduit.getEtat())) {
            Alert warningAlert = new Alert(Alert.AlertType.WARNING);
            warningAlert.setTitle("Modification");
            warningAlert.setHeaderText(null);
            warningAlert.setContentText("Un produit accepté ou rejeté ne peut pas être modifié.");
            warningAlert.showAndWait();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouter-modifier-produit-admin.fxml"));
            AnchorPane root = loader.load();
            AjouterModifierProduitAdminController ctrl = loader.getController();
            ctrl.setProduit(selectedProduit);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier Produit");
            stage.showAndWait();
            refreshCards();
        } catch (IOException e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Erreur");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Impossible d'ouvrir l'interface de modification : " + e.getMessage());
            errorAlert.showAndWait();
        }
    }

    @FXML
    private void supprimer(ActionEvent event) {
        if (selectedProduit == null) {
            Alert warningAlert = new Alert(Alert.AlertType.WARNING);
            warningAlert.setTitle("Suppression");
            warningAlert.setHeaderText(null);
            warningAlert.setContentText("Veuillez sélectionner un produit à supprimer.");
            warningAlert.showAndWait();
            return;
        }
        if ("Accepted".equals(selectedProduit.getEtat())) {
            Alert warningAlert = new Alert(Alert.AlertType.WARNING);
            warningAlert.setTitle("Suppression");
            warningAlert.setHeaderText(null);
            warningAlert.setContentText("Un produit accepté ne peut pas être supprimé.");
            warningAlert.showAndWait();
            return;
        }
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation");
        confirmAlert.setHeaderText("Suppression de produit");
        confirmAlert.setContentText("Voulez-vous vraiment supprimer le produit \"" + selectedProduit.getNom() + "\" ?");
        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            serviceProduit.supprimer(selectedProduit.getId());
            refreshCards();
            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
            infoAlert.setTitle("Suppression");
            infoAlert.setHeaderText(null);
            infoAlert.setContentText("Produit supprimé avec succès.");
            infoAlert.showAndWait();
        }
    }
}
