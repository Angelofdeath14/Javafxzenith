package tn.esprit.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.esprit.entities.Produit;
import tn.esprit.service.ServiceProduit;
import tn.esprit.service.session.UserSession;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ProduitAdminController implements Initializable {
    @FXML private Text currentUserName;
    @FXML private ListView<Produit> lvproduit;
    @FXML private Button btnAccept, btnReject, btnCommandsView, btnStat;
    @FXML private Button btnDashboard, btnEvents, btnAddEvent, btnUsers, btnProduit, btnCommands, btnReclamation, btnLogout;
    @FXML private VBox mainContainer;
    @FXML private ScrollPane scrollPane;

    private final ServiceProduit serviceProduit = new ServiceProduit();
    private final ObservableList<Produit> produitList = FXCollections.observableArrayList();
    private final String uploadsPath = "C:\\Users\\Abir12\\Desktop\\artyphoria - Copy\\public\\uploads\\";

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // show current user
        currentUserName.setText(UserSession.CURRENT_USER.getUserLoggedIn().getfirst_name());

        // set up nav buttons
        btnDashboard.setOnAction(e -> goToDash());
        btnEvents.setOnAction(e -> goToEvents());
        btnAddEvent.setOnAction(e -> goToAddEvent());
        btnUsers.setOnAction(e -> goToUsers());
        btnProduit.setOnAction(e -> refreshList());
        btnCommands.setOnAction(e -> goToCommands());
        btnReclamation.setOnAction(e -> goToReclamation());
        btnLogout.setOnAction(e -> logout());

        // list cell factory
        lvproduit.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Produit p, boolean empty) {
                super.updateItem(p, empty);
                if (empty || p == null) {
                    setGraphic(null);
                } else {
                    // images
                    HBox images = new HBox(5,
                            makeImageView(p.getFront_image()),
                            makeImageView(p.getBack_image()),
                            makeImageView(p.getTop_image())
                    );
                    // details
                    VBox details = new VBox(3,
                            new Label("Nom: " + p.getNom()),
                            new Label("Desc: " + p.getDescription()),
                            new Label("Cat: " + p.getCategorie()),
                            new Label("Prix: " + p.getPrix()),
                            new Label("Etat: " + p.getEtat()),
                            new Label("EtatProd: " + p.getEtat_produit())
                    );
                    setGraphic(new HBox(15, images, details));
                }
            }
        });

        // wire action buttons
        btnAccept.setOnAction(e -> changeState("Accepted"));
        btnReject.setOnAction(e -> changeState("Rejected"));


        // initial load
        refreshList();
    }

    private ImageView makeImageView(String fileName) {
        var iv = new ImageView();
        try {
            File f = new File(uploadsPath + fileName);
            if (f.exists()) {
                iv.setImage(new javafx.scene.image.Image(new FileInputStream(f)));
                iv.setFitWidth(50);
                iv.setFitHeight(50);
                iv.setPreserveRatio(true);
            }
        } catch (Exception ignored) {}
        return iv;
    }

    private void refreshList() {
        produitList.setAll(serviceProduit.afficher());
        lvproduit.setItems(produitList);
    }

    private void changeState(String newState) {
        Produit sel = lvproduit.getSelectionModel().getSelectedItem();
        if (sel != null && "Pending".equals(sel.getEtat())) {
            sel.setEtat(newState);
            serviceProduit.modifier(sel);
            refreshList();
        }
    }

    // --- Navigation helpers ---
    public void goToDash()        { navigate("/Dashboard.fxml",                "Tableau de bord"); }
    public void goToEvents()      { navigate("/AffichageEvent.fxml",           "Événements");    }
    public void goToAddEvent()    { navigate("/AjouterEvent.fxml",             "Ajouter Événement"); }
    public void goToUsers()       { navigate("/ListUsers.fxml",                "Utilisateurs");  }
    public void goToCommands()    { navigate("/command-admin.fxml",           "Commandes");     }
    public void goToReclamation() { navigate("/afficher-reclamation-admin.fxml","Réclamations"); }
    public void goToStat()        { navigate("/product-stat.fxml",             "Stats Produits");}
    public void logout()          { navigate("/Login.fxml",                    "Connexion");     }

    private void navigate(String fxml, String title) {
        try {
            Parent r = FXMLLoader.load(getClass().getResource(fxml));
            Stage st = (Stage) lvproduit.getScene().getWindow();
            st.setTitle(title);
            st.setScene(new Scene(r));
        } catch (IOException ex) {
            new Alert(Alert.AlertType.ERROR, "Navigation failed: "+ex.getMessage()).showAndWait();
        }
    }
    /** Refresh the ListView with the latest data */
    @FXML
    private void refreshList(ActionEvent event) {
        refreshList();
    }

    /** Accept the currently selected product */
    @FXML
    private void accept(ActionEvent event) {
        changeState("Accepted");
    }

    /** Reject the currently selected product */
    @FXML
    private void reject(ActionEvent event) {
        changeState("Rejected");
    }
    @FXML
    private void goToStat(ActionEvent event) {
        try {
            Parent statRoot = FXMLLoader.load(getClass().getResource("/product-stat.fxml"));
            Stage statStage = new Stage();
            statStage.setTitle("Statistiques Produits");
            statStage.setScene(new Scene(statRoot));
            statStage.show();   // <-- show, not showAndWait, and we do NOT close the current stage
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }



}
