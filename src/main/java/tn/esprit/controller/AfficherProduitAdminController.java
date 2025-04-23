package tn.esprit.controller;

import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.esprit.entities.Produit;
import tn.esprit.service.ServiceProduit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;

public class AfficherProduitAdminController {






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

    @FXML
    void ajouter(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouter-modifier-produit-admin.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter Produit");
            stage.show();
            Stage stage2 = (Stage) lvproduit.getScene().getWindow();
            stage2.close();

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }

    @FXML
    void modifier(ActionEvent event) {
        Produit selectedProduit = lvproduit.getSelectionModel().getSelectedItem();
        if (selectedProduit == null) {
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ajouter-modifier-produit-admin.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter Produit");
            stage.show();
            Stage stage2 = (Stage) lvproduit.getScene().getWindow();
            stage2.close();
            AjouterModifierProduitAdminController controller = loader.getController();
            controller.setProduit(selectedProduit);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }
    private void refreshTable(){
        produitList.setAll(serviceProduit.afficher());
        lvproduit.setItems(produitList);
    }
    @FXML
    void supprimer(ActionEvent event) {
        Produit p=lvproduit.getSelectionModel().getSelectedItem();
        if(p!=null){
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirmation de suppression");
            confirmAlert.setHeaderText("Suppression de produit");
            confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer le produit \"" + p.getNom() + "\" ?");
            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                serviceProduit.supprimer(p.getId());
                refreshTable();
            }

        }else{
            Alert warningAlert = new Alert(Alert.AlertType.WARNING);
            warningAlert.setTitle("Aucun produit sélectionné");
            warningAlert.setHeaderText(null);
            warningAlert.setContentText("Veuillez sélectionner un produit à supprimer.");
            warningAlert.showAndWait();
        }

    }

}

