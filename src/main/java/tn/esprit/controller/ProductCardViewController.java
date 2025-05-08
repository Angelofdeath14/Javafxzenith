package tn.esprit.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import tn.esprit.entities.Produit;
import tn.esprit.utils.QRCodeGenerator;

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
    @FXML private ImageView qrcode;
    private int currentIndex = 0;
    private Consumer<Produit> addToCartHandler;

    private Produit produit;
    private List<String> imageFiles = new ArrayList<>();
    @FXML
    private void onAddToCart(ActionEvent event) {
        if (addToCartHandler != null && produit != null) {
            addToCartHandler.accept(produit);
            btnAddToCart.setText("Added");
            btnAddToCart.setDisable(true);
        }
    }

    @FXML
    public void initialize() {
        btnPrev.setOnAction(e -> showPreviousImage());
        btnNext.setOnAction(e -> showNextImage());
    }

    public void fillCard(Produit produit) {
        this.produit = produit;
        lblName.setText(produit.getNom());
        lblCategory.setText(produit.getCategorie());
        txtDescription.setText(produit.getDescription());
        lblPrice.setText(String.format("%.2f DT", produit.getPrix()));
        lblEtat.setText(produit.getEtat());
        try {
            qrcode.setImage(
                    QRCodeGenerator.generateQRCodeImage(
                            QRCodeGenerator.formatProductData(produit),
                            150, 150
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (produit.getFront_image() != null && !produit.getFront_image().isBlank())
            imageFiles.add(produit.getFront_image());
        if (produit.getBack_image() != null && !produit.getBack_image().isBlank())
            imageFiles.add(produit.getBack_image());
        if (produit.getTop_image() != null && !produit.getTop_image().isBlank())
            imageFiles.add(produit.getTop_image());
        if (!imageFiles.isEmpty()) {
            currentIndex = 0;
            loadImage(imageFiles.get(0));
        }
    }

    private void loadImage(String fileName) {
        try {
            File f = new File("C:\\Users\\Abir12\\Desktop\\artyphoria - Copy\\public\\uploads\\" + fileName);
            if (f.exists()) {
                try (FileInputStream fis = new FileInputStream(f)) {
                    imageView.setImage(new Image(fis));
                }
            } else {
                imageView.setImage(null);
            }
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }
    public void setOnAddToCart(Consumer<Produit> handler) {
        this.addToCartHandler = handler;
    }
    private void showNextImage() {
        if (imageFiles.isEmpty()) return;
        currentIndex = (currentIndex + 1) % imageFiles.size();
        loadImage(imageFiles.get(currentIndex));
    }

    private void showPreviousImage() {
        if (imageFiles.isEmpty()) return;
        currentIndex = (currentIndex - 1 + imageFiles.size()) % imageFiles.size();
        loadImage(imageFiles.get(currentIndex));
    }
    public Button getBtnAddToCart() {
        return btnAddToCart;
    }
}
