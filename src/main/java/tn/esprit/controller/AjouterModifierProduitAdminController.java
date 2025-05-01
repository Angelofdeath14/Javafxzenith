package tn.esprit.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.esprit.entities.Produit;
import tn.esprit.service.ServiceProduit;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class AjouterModifierProduitAdminController {
    String destinationFolder="C:\\xampp\\htdocs\\artyphoria - Copy - Copy\\public\\uploads\\";
    private Produit currentProduit;
    @FXML private Button btnCancel;
    @FXML private Button btnSave;
    @FXML private TextField txtNom;
    @FXML private TextArea txtDescription;
    @FXML private ComboBox<String> cmbCategorie;
    @FXML private TextField txtPrix;
    @FXML private TextField txtEtatProduit;
    @FXML private TextField txtFrontImage;
    @FXML private TextField txtBackImage;
    @FXML private TextField txtTopImage;

    private ServiceProduit serviceProduit = new ServiceProduit();

    public void initialize(){
        txtBackImage.setDisable(true);
        txtFrontImage.setDisable(true);
        txtTopImage.setDisable(true);
        cmbCategorie.getItems().addAll(
                "Seasonal Collections",
                "Small Artworks",
                "Kitchen & Dining",
                "Stationery & Books",
                "Fashion & Accessories",
                "Jewelry",
                "Home Decor",
                "Crafts",
                "Fine Art"
        );
    }

    public void setProduit(Produit produit) {
        this.currentProduit = produit;
        if (produit != null) {
            txtNom.setText(produit.getNom());
            txtDescription.setText(produit.getDescription());
            cmbCategorie.setValue(produit.getCategorie());
            txtPrix.setText(String.valueOf(produit.getPrix()));
            txtEtatProduit.setText(produit.getEtat_produit());
            txtFrontImage.setText(produit.getFront_image());
            txtBackImage.setText(produit.getBack_image());
            txtTopImage.setText(produit.getTop_image());
        }
    }

    @FXML
    void handleCancel(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficher-produit-admin.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Afficher Produits");
            stage.show();
            ((Stage)btnCancel.getScene().getWindow()).close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    void handleSave(ActionEvent event) {
        if (!controleSaisie()) return;
        if (currentProduit == null) {
            Produit newProduit = new Produit();
            newProduit.setNom(txtNom.getText());
            newProduit.setDescription(txtDescription.getText());
            newProduit.setCategorie(cmbCategorie.getValue());
            newProduit.setPrix(Double.parseDouble(txtPrix.getText()));
            newProduit.setEtat("Pending");
            newProduit.setEtat_produit(txtEtatProduit.getText());
            newProduit.setFront_image(txtFrontImage.getText());
            newProduit.setBack_image(txtBackImage.getText());
            newProduit.setTop_image(txtTopImage.getText());
            newProduit.setCommand_id(0);
            newProduit.setUser_id(1);
            serviceProduit.ajouter(newProduit);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Produit ajouté");
            alert.setHeaderText(null);
            alert.setContentText("Le produit a été ajouté avec succès.");
            alert.showAndWait();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficher-produit-admin.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Afficher Produits");
                stage.show();
                ((Stage)btnCancel.getScene().getWindow()).close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            currentProduit.setNom(txtNom.getText());
            currentProduit.setDescription(txtDescription.getText());
            currentProduit.setCategorie(cmbCategorie.getValue());
            currentProduit.setPrix(Double.parseDouble(txtPrix.getText()));
            currentProduit.setEtat_produit(txtEtatProduit.getText());
            currentProduit.setFront_image(txtFrontImage.getText());
            currentProduit.setBack_image(txtBackImage.getText());
            currentProduit.setTop_image(txtTopImage.getText());
            serviceProduit.modifier(currentProduit);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Produit modifié");
            alert.setHeaderText(null);
            alert.setContentText("Le produit a été modifié avec succès.");
            alert.showAndWait();
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficher-produit-admin.fxml"));
                Parent root = loader.load();
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Afficher Produits");
                stage.show();
                ((Stage)btnCancel.getScene().getWindow()).close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    void upload1(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir l'image front");
        File file = fileChooser.showOpenDialog(btnSave.getScene().getWindow());
        if (file != null) {
            try {
                Path source = file.toPath();
                Path destination = Paths.get(destinationFolder + file.getName());
                Files.copy(source, destination, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                txtFrontImage.setText(file.getName());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    void upload2(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir l'image back");
        File file = fileChooser.showOpenDialog(btnSave.getScene().getWindow());
        if (file != null) {
            try {
                Path source = file.toPath();
                Path destination = Paths.get(destinationFolder + file.getName());
                Files.copy(source, destination, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                txtBackImage.setText(file.getName());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    @FXML
    void upload3(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir l'image top");
        File file = fileChooser.showOpenDialog(btnSave.getScene().getWindow());
        if (file != null) {
            try {
                Path source = file.toPath();
                Path destination = Paths.get(destinationFolder + file.getName());
                Files.copy(source, destination, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                txtTopImage.setText(file.getName());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private boolean controleSaisie() {
        String errors = "";
        if (txtNom.getText().isEmpty()) errors += "Nom vide\n";
        if (txtDescription.getText().length() < 10) errors += "Description doit contenir au moins 10 caractères\n";
        if (cmbCategorie.getValue() == null) errors += "Catégorie vide\n";
        try {
            double price = Double.parseDouble(txtPrix.getText());
            if (price <= 0) errors += "Prix doit être positif\n";
        } catch (Exception e) {
            errors += "Prix invalide\n";
        }
        if (txtEtatProduit.getText().isEmpty()) errors += "État Produit vide\n";
        if (txtFrontImage.getText().isEmpty()) errors += "Image front vide\n";
        if (txtBackImage.getText().isEmpty()) errors += "Image back vide\n";
        if (txtTopImage.getText().isEmpty()) errors += "Image top vide\n";
        if (!errors.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setHeaderText("Veuillez corriger les erreurs");
            alert.setContentText(errors);
            alert.showAndWait();
            return false;
        }
        return true;
    }
}
