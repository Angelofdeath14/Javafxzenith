package tn.esprit.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import tn.esprit.entities.Produit;
import tn.esprit.service.ServiceProduit;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class AfficherProduitUserController {

    @FXML private ScrollPane scrollPane;
    @FXML private GridPane gridProducts;
    @FXML private Button btnCommand;
    @FXML private Button btnMesCommandes;
    @FXML private TextField searchField;

    private final ServiceProduit serviceProduit = new ServiceProduit();
    private final List<Produit> allProducts = serviceProduit.getOtherProducts(1).stream()
            .filter(p -> "Accepted".equals(p.getEtat()))
            .collect(Collectors.toList());
    private final java.util.List<Produit> cart = new java.util.ArrayList<>();

    @FXML
    public void initialize() {
        gridProducts.setHgap(20);
        gridProducts.setVgap(20);
        gridProducts.setPadding(new Insets(15));
        renderGrid(allProducts);
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String lower = newVal.toLowerCase();
            List<Produit> filtered = allProducts.stream()
                    .filter(p -> p.getNom().toLowerCase().contains(lower)
                            || p.getDescription().toLowerCase().contains(lower)
                            || p.getCategorie().toLowerCase().contains(lower)
                            || String.valueOf(p.getPrix()).contains(lower)
                            || p.getEtat().toLowerCase().contains(lower))
                    .collect(Collectors.toList());
            renderGrid(filtered);
        });
    }

    private void renderGrid(List<Produit> produits) {
        gridProducts.getChildren().clear();
        int columns = 3;
        int col = 0, row = 0;
        for (Produit produit : produits) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/product-card-view.fxml"));
                Node card = loader.load();
                ProductCardViewController controller = loader.getController();
                controller.fillCard(produit);
                controller.setOnAddToCart(p -> {
                    if (!cart.contains(p)) cart.add(p);
                });
                gridProducts.add(card, col, row);
                GridPane.setMargin(card, new Insets(10));
                col++;
                if (col >= columns) {
                    col = 0;
                    row++;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void command(ActionEvent event) {
        if (cart.isEmpty()) return;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouter-command-user.fxml"));
            Parent root = loader.load();
            AjouterCommandUserController controller = loader.getController();
            controller.setCart(cart);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter Commande");
            stage.show();
            ((Stage) gridProducts.getScene().getWindow()).close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    void gotomescommande(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficher-command-user.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Mes commandes");
            stage.show();
            ((Stage) gridProducts.getScene().getWindow()).close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    void gotoSellProduct(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficher-produit-admin.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Sell Product");
            stage.show();
            ((Stage) gridProducts.getScene().getWindow()).close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
