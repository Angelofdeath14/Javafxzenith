package tn.esprit.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import tn.esprit.entities.Produit;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ProductCardViewController {

    @FXML private ImageView imageView;
    @FXML private Button btnPrev;
    @FXML private Button btnNext;
    @FXML private Button btnAddToCart;
    @FXML private Label lblName;
    @FXML private Label lblCategory;
    @FXML private Text txtDescription;
    @FXML private Label lblPrice;
    @FXML private Label lblEtat;

    private Produit produit;
    private List<String> imageFiles;
    private int currentIndex = 0;
    private Consumer<Produit> addToCartHandler;

    @FXML
    public void initialize() {
        btnPrev.setOnAction(e -> showPreviousImage());
        btnNext.setOnAction(e -> showNextImage());
    }

    /**
     * FXML handler for Add to Cart button click.
     */
    @FXML
    private void onAddToCart(ActionEvent event) {
        if (addToCartHandler != null && produit != null) {
            addToCartHandler.accept(produit);
            btnAddToCart.setText("Added");
            btnAddToCart.setDisable(true);
        }
    }

    /**
     * Populates the card with product data and images.
     */
    public void fillCard(Produit produit) {
        this.produit = produit;
        lblName.setText(produit.getNom());
        lblCategory.setText(produit.getCategorie());
        txtDescription.setText(produit.getDescription());
        lblPrice.setText(String.format("%.2f DT", produit.getPrix()));
        lblEtat.setText(produit.getEtat());

        imageFiles = new ArrayList<>();
        if (produit.getFront_image() != null && !produit.getFront_image().isBlank()) {
            imageFiles.add(produit.getFront_image());
        }
        if (produit.getBack_image() != null && !produit.getBack_image().isBlank()) {
            imageFiles.add(produit.getBack_image());
        }
        if (produit.getTop_image() != null && !produit.getTop_image().isBlank()) {
            imageFiles.add(produit.getTop_image());
        }

        if (!imageFiles.isEmpty()) {
            currentIndex = 0;
            loadImage(imageFiles.get(currentIndex));
        }
    }

    /**
     * Registers a callback to be invoked when the user clicks "Add to Cart".
     */
    public void setOnAddToCart(Consumer<Produit> handler) {
        this.addToCartHandler = handler;
    }

    private void loadImage(String fileName) {
        try {
            File file = new File("C:\\xampp\\htdocs\\artyphoria - Copy - Copy\\public\\uploads\\" + fileName);
            if (file.exists()) {
                try (FileInputStream fis = new FileInputStream(file)) {
                    Image img = new Image(fis);
                    imageView.setImage(img);
                }
            } else {
                imageView.setImage(null);
            }
        } catch (Exception ex) {
            System.err.println("Error loading image: " + ex.getMessage());
        }
    }

    private void showNextImage() {
        if (imageFiles == null || imageFiles.isEmpty()) return;
        currentIndex = (currentIndex + 1) % imageFiles.size();
        loadImage(imageFiles.get(currentIndex));
    }

    private void showPreviousImage() {
        if (imageFiles == null || imageFiles.isEmpty()) return;
        currentIndex = (currentIndex - 1 + imageFiles.size()) % imageFiles.size();
        loadImage(imageFiles.get(currentIndex));
    }
}
