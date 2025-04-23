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
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import tn.esprit.entities.Produit;
import tn.esprit.service.ServiceProduit;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for displaying all available products in a scrollable grid of cards.
 */
public class AfficherProduitUserController {

    @FXML private ScrollPane scrollPane;
    @FXML private GridPane gridProducts;
    @FXML private Button btnCommand;
    @FXML private Button btnMesCommandes;

    private final ServiceProduit serviceProduit = new ServiceProduit();
    // Simple in-memory cart to collect added products
    private final java.util.List<Produit> cart = new java.util.ArrayList<>();

    @FXML
    public void initialize() {
        gridProducts.setHgap(20);
        gridProducts.setVgap(20);
        gridProducts.setPadding(new Insets(15));

        // Load available products
        List<Produit> produits = serviceProduit.afficher().stream()
                .filter(p -> "Disponible".equals(p.getEtat()))
                .collect(Collectors.toList());

        final int columns = 3; // number of columns in grid
        int col = 0, row = 0;

        for (Produit produit : produits) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/product-card-view.fxml"));
                Node card = loader.load();
                // Configure the card
                tn.esprit.controller.ProductCardViewController controller = loader.getController();
                controller.fillCard(produit);
                controller.setOnAddToCart(p -> {
                    if (!cart.contains(p)) {
                        cart.add(p);
                        System.out.println("Added to cart: " + p.getNom());
                    }
                });

                // Add to grid with margin
                gridProducts.add(card, col, row);
                GridPane.setMargin(card, new Insets(10));

                col++;
                if (col >= columns) {
                    col = 0;
                    row++;
                }
            } catch (IOException e) {
                System.err.println("Error loading product card: " + e.getMessage());
            }
        }
    }

    /**
     * Handler for the "Command" button: proceed to create an order for the cart contents.
     */
    @FXML
    void command(ActionEvent event) {
        if (cart.isEmpty()) {
            System.out.println("Cart is empty. Add products before commanding.");
            return;
        }
        // Open the order creation UI
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouter-command-user.fxml"));
            Parent root = loader.load();
            // You may pass the cart to the next controller if needed
            AjouterCommandUserController controller = loader.getController();
            controller.setCart(cart);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter Commande");
            stage.show();
            Stage old = (Stage) gridProducts.getScene().getWindow();
            old.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            System.out.println(ex.getMessage());
        }
    }

    /**
     * Handler for "Mes commandes": view existing orders.
     */
    @FXML
    void gotomescommande(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficher-command-user.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Mes commandes");
            stage.show();
            Stage old = (Stage) gridProducts.getScene().getWindow();
            old.close();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
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
            Stage old = (Stage) gridProducts.getScene().getWindow();
            old.close();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
