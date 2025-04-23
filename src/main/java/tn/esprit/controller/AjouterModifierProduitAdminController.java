package tn.esprit.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
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
    public void setProduit(Produit produit) {
        this.currentProduit = produit;
        populateFields();
    }
    private void populateFields() {
        if (currentProduit != null) {
            txtNom.setText(currentProduit.getNom());
            txtDescription.setText(currentProduit.getDescription());
            txtCategorie.setText(currentProduit.getCategorie());
            txtPrix.setText(String.valueOf(currentProduit.getPrix()));

            txtEtatProduit.setText(currentProduit.getEtat_produit());
            txtFrontImage.setText(currentProduit.getFront_image());
            txtBackImage.setText(currentProduit.getBack_image());
            txtTopImage.setText(currentProduit.getTop_image());
        }
    }
    private ServiceProduit serviceProduit = new ServiceProduit();

    @FXML
    private Button btnCancel;

    @FXML
    private Button btnSave;

    @FXML
    private TextField txtBackImage;

    @FXML
    private TextField txtCategorie;

    @FXML
    private TextArea txtDescription;


    @FXML
    private TextField txtEtatProduit;

    @FXML
    private TextField txtFrontImage;

    @FXML
    private TextField txtNom;

    @FXML
    private TextField txtPrix;

    @FXML
    private TextField txtTopImage;
    public void initialize(){
        txtBackImage.setDisable(true);
        txtFrontImage.setDisable(true);
        txtTopImage.setDisable(true);
    }

    @FXML
    void handleCancel(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/afficher-produit-admin.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter Produit");
            stage.show();
            Stage stage2 = (Stage) btnCancel.getScene().getWindow();
            stage2.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    @FXML
    void handleSave(ActionEvent event) {
        if (!controleSaisie()) {
            return;
        }
        if (currentProduit == null) {
            Produit newProduit = new Produit();
            newProduit.setNom(txtNom.getText());
            newProduit.setDescription(txtDescription.getText());
            newProduit.setCategorie(txtCategorie.getText());
            newProduit.setPrix(Double.parseDouble(txtPrix.getText()));
            newProduit.setEtat("Disponible");
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
        }else{
            currentProduit.setNom(txtNom.getText());
            currentProduit.setDescription(txtDescription.getText());
            currentProduit.setCategorie(txtCategorie.getText());
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
                System.out.println("Erreur lors du téléchargement de l'image front : " + ex.getMessage());
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
                System.out.println("Erreur lors du téléchargement de l'image back : " + ex.getMessage());
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
                System.out.println("Erreur lors du téléchargement de l'image top : " + ex.getMessage());
            }
        }
    }
    private boolean controleSaisie(){
        String errors="";
        if (txtNom.getText() == null || txtNom.getText().isEmpty()) {
            errors += "Nom vide\n";
        } else if (txtNom.getText().length() < 3) {
            errors += "Nom doit contenir au moins 3 caractères\n";
        } else if (!txtNom.getText().matches("[a-zA-Z\\s]+")) {
            errors += "Nom doit contenir uniquement des lettres\n";
        }
        if (txtDescription.getText() == null || txtDescription.getText().isEmpty()) {
            errors += "Description vide\n";
        } else if (txtDescription.getText().length() < 10) {
            errors += "Description doit contenir au moins 10 caractères\n";
        }
        if (txtCategorie.getText() == null || txtCategorie.getText().isEmpty()) {
            errors += "Catégorie vide\n";
        }
        if (txtPrix.getText() == null || txtPrix.getText().isEmpty()) {
            errors += "Prix vide\n";
        } else {
            try {
                double price = Double.parseDouble(txtPrix.getText());
                if (price <= 0) {
                    errors += "Prix doit être positif\n";
                }
            } catch (NumberFormatException e) {
                errors += "Prix invalide\n";
            }
        }
        if (txtEtatProduit.getText() == null || txtEtatProduit.getText().isEmpty()) {
            errors += "État Produit vide\n";
        }
        if (txtFrontImage.getText() == null || txtFrontImage.getText().isEmpty()) {
            errors += "Image front vide\n";
        }

        if (txtBackImage.getText() == null || txtBackImage.getText().isEmpty()) {
            errors += "Image back vide\n";
        }

        if (txtTopImage.getText() == null || txtTopImage.getText().isEmpty()) {
            errors += "Image top vide\n";
        }
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
