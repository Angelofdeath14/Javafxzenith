package tn.esprit.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.entities.Produit;
import tn.esprit.service.ServiceProduit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ProduitAdminController {

    String path="C:\\xampp\\htdocs\\artyphoria - Copy - Copy\\public\\uploads";

    @FXML
    private ListView<Produit> lvproduit;
    ObservableList<Produit> produitList= FXCollections.observableArrayList();
    ServiceProduit serviceProduit=new ServiceProduit();
    public void initialize(){


        refreshTable();
        lvproduit.setCellFactory(lv -> new ListCell<Produit>() {
            @Override
            protected void updateItem(Produit produit, boolean empty) {
                super.updateItem(produit, empty);
                if (empty || produit == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Create ImageView objects for each image attribute.
                    ImageView ivFront = createImageView(produit.getFront_image());
                    ImageView ivBack = createImageView(produit.getBack_image());
                    ImageView ivTop = createImageView(produit.getTop_image());

                    // Create a VBox for product details.
                    Label lblName = new Label("Nom: " + produit.getNom());
                    Label lblDesc = new Label("Description: " + produit.getDescription());
                    Label lblCat = new Label("Catégorie: " + produit.getCategorie());
                    Label lblPrix = new Label("Prix: " + produit.getPrix());
                    Label lblEtat = new Label("État: " + produit.getEtat());
                    Label lblEtatProduit = new Label("État Produit: " + produit.getEtat_produit());

                    VBox detailsBox = new VBox(5, lblName, lblDesc, lblCat, lblPrix, lblEtat, lblEtatProduit);

                    // Create an HBox for images.
                    HBox imagesBox = new HBox(10, ivFront, ivBack, ivTop);

                    // Combine images and details in a main container.
                    HBox cellBox = new HBox(15, imagesBox, detailsBox);
                    setGraphic(cellBox);
                }
            }

            // Helper method to create an ImageView from a file name.
            private ImageView createImageView(String fileName) {
                ImageView imageView = new ImageView();
                try {
                    // Combine the base path with the file name.
                    File file = new File("C:\\xampp\\htdocs\\artyphoria - Copy - Copy\\public\\uploads\\"+ fileName);
                    if (file.exists()) {
                        FileInputStream fis = new FileInputStream(file);
                        Image image = new Image(fis);
                        imageView.setImage(image);
                        imageView.setFitWidth(50);
                        imageView.setFitHeight(50);
                        imageView.setPreserveRatio(true);
                    } else {
                        imageView.setImage(null);
                    }
                } catch (Exception ex) {
                    System.out.println("Error loading image: " + ex.getMessage());
                }
                return imageView;
            }
        });
    }
    private void refreshTable(){
        produitList.setAll(serviceProduit.afficher());
        lvproduit.setItems(produitList);
    }
    @FXML
    void accept(ActionEvent event) {
        Produit selectedProduit = lvproduit.getSelectionModel().getSelectedItem();
        if (selectedProduit == null && selectedProduit.getEtat().equals("Pending")) {
            return;
        }
        selectedProduit.setEtat("Accepted");
        serviceProduit.modifier(selectedProduit);
        refreshTable();
    }

    @FXML
    void reject(ActionEvent event) {
        Produit selectedProduit = lvproduit.getSelectionModel().getSelectedItem();
        if (selectedProduit == null && selectedProduit.getEtat().equals("Pending")) {
            return;
        }
        selectedProduit.setEtat("Rejected");
        serviceProduit.modifier(selectedProduit);
        refreshTable();
    }
    @FXML
    void command(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/command-admin.fxml"));
            AnchorPane root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Command Admin");
            stage.show();
            ((Stage) lvproduit.getScene().getWindow()).close();
        } catch (IOException e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Erreur");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Impossible de revenir à l'affichage utilisateur : " + e.getMessage());
            errorAlert.showAndWait();
        }
    }



    @FXML
    void stat(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/product-stat.fxml"));
            AnchorPane root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Produits Stat");
            stage.show();

        } catch (IOException e) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Erreur");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Impossible de revenir à l'affichage utilisateur : " + e.getMessage());
            errorAlert.showAndWait();
        }
    }

}

