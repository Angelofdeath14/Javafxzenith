package tn.esprit.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.AnchorPane;
import tn.esprit.entities.Produit;

import java.io.File;
import java.io.FileInputStream;
import java.util.function.Consumer;

public class CartItemViewController {

    @FXML private AnchorPane rootPane;
    @FXML private HBox container;
    @FXML private ImageView itemImage;
    @FXML private Label itemName;
    @FXML private Label itemPrice;
    @FXML private Button btnDelete;

    private Produit produit;
    private Consumer<Produit> removeHandler;

    /** Called by the FXMLLoader after fields are injected */
    @FXML
    public void initialize() {
        // wire delete button
        btnDelete.setOnAction(evt -> {
            if (removeHandler != null && produit != null) {
                removeHandler.accept(produit);
            }
        });
    }

    /**
     * Populate this view with the given product’s data,
     * and register a callback for when the user clicks delete.
     *
     * @param produit the product to display
     * @param removeHandler invoked when the delete button is clicked
     */
    public void fillItem(Produit produit, Consumer<Produit> removeHandler) {
        this.produit = produit;
        this.removeHandler = removeHandler;

        itemName.setText(produit.getNom());
        itemPrice.setText(String.format("%.2f DT", produit.getPrix()));

                // load front image
                String imageFile = produit.getFront_image();
        if (imageFile != null && !imageFile.isBlank()) {
            try {
                File file = new File("C:\\Users\\Abir12\\Desktop\\artyphoria - Copy\\public\\uploads\\" + imageFile);
                if (file.exists()) {
                    try (FileInputStream fis = new FileInputStream(file)) {
                        Image img = new Image(fis);
                        itemImage.setImage(img);
                    }
                }
            } catch (Exception ex) {
                System.err.println("Error loading cart image: " + ex.getMessage());
            }
        }
    }
}
