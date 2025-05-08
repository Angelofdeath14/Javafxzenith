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
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.esprit.entities.Command;
import tn.esprit.service.ServiceCommand;
import tn.esprit.service.SessionService;
import tn.esprit.service.session.UserSession;
import tn.esprit.utils.EmailSender;

import javax.mail.MessagingException;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class CommandAdminController implements Initializable {
    @FXML private Text currentUserName;
    @FXML private ListView<Command> lvcommand;
    @FXML private Button btnAccept, btnReject;
    @FXML private Button btnDashboard, btnEvents, btnAddEvent, btnUsers,
            btnProduit, btnCommands, btnReclamation, btnLogout;
    @FXML private VBox mainContainer;
    @FXML private ScrollPane scrollPane;

    private final ServiceCommand serviceCmd = new ServiceCommand();
    private ObservableList<Command> commandList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // show current user
        currentUserName.setText(UserSession.CURRENT_USER.getUserLoggedIn().getfirst_name());

        // nav buttons
        btnDashboard.setOnAction(e -> goToDash());
        btnEvents.setOnAction(e -> goToEvents());
        btnAddEvent.setOnAction(e -> goToAddEvent());
        btnUsers.setOnAction(e -> goToUsers());
        btnProduit.setOnAction(e -> goToProduit());
        btnCommands.setOnAction(e -> refreshList());
        btnReclamation.setOnAction(e -> goToReclamation());
        btnLogout.setOnAction(e -> logout());

        // wire actions
        btnAccept.setOnAction(this::accept);
        btnReject.setOnAction(this::reject);

        // initial load
        refreshList();
    }

    private void refreshList() {
        List<Command> cmds = serviceCmd.afficher();
        commandList.setAll(cmds);
        lvcommand.setItems(commandList);
    }

    @FXML
    private void refreshList(ActionEvent event) {
        refreshList();
    }

    @FXML
    private void accept(ActionEvent event) {
        Command sel = lvcommand.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner une commande.");
            return;
        }
        if ("Pending".equalsIgnoreCase(sel.getStatus())) {
            sel.setStatus("Accepted");
            serviceCmd.modifier(sel);

            showAlert(Alert.AlertType.INFORMATION, "Commande acceptée",
                    "Commande #" + sel.getId() + " acceptée.");
            refreshList();
        } else {
            showAlert(Alert.AlertType.WARNING, "Opération invalide",
                    "Seules les commandes \"Pending\" peuvent être acceptées.");
        }
    }

    @FXML
    private void reject(ActionEvent event) {
        Command sel = lvcommand.getSelectionModel().getSelectedItem();
        if (sel == null) {
            showAlert(Alert.AlertType.WARNING, "Aucune sélection", "Veuillez sélectionner une commande.");
            return;
        }
        if ("Pending".equalsIgnoreCase(sel.getStatus())) {
            sel.setStatus("Rejected");
            serviceCmd.modifier(sel);
            showAlert(Alert.AlertType.INFORMATION, "Commande rejetée",
                    "Commande #" + sel.getId() + " rejetée.");
            refreshList();
        } else {
            showAlert(Alert.AlertType.WARNING, "Opération invalide",
                    "Seules les commandes \"Pending\" peuvent être rejetées.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // --- Navigation helpers ---
    public void goToDash()        { navigate("/Dashboard.fxml",                 "Tableau de bord"); }
    public void goToEvents()      { navigate("/AffichageEvent.fxml",            "Événements");     }
    public void goToAddEvent()    { navigate("/AjouterEvent.fxml",              "Ajouter Événement"); }
    public void goToUsers()       { navigate("/ListUsers.fxml",                 "Utilisateurs");   }
    public void goToProduit()     { navigate("/produit-admin.fxml",             "Produits");       }
    public void goToReclamation(){ navigate("/afficher-reclamation-admin.fxml","Réclamations"); }
    public void logout()          { navigate("/Login.fxml",                     "Connexion");      }

    private void navigate(String fxml, String title) {
        try {
            Parent r = FXMLLoader.load(getClass().getResource(fxml));
            Stage st = (Stage) lvcommand.getScene().getWindow();
            st.setTitle(title);
            st.setScene(new Scene(r));
        } catch (IOException ex) {
            new Alert(Alert.AlertType.ERROR, "Navigation échouée: " + ex.getMessage())
                    .showAndWait();
        }
    }
}
