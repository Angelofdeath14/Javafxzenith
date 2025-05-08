package tn.esprit.controller;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.esprit.entities.User;
import tn.esprit.service.ServiceUser;
import tn.esprit.service.UserDao;
import tn.esprit.service.session.UserSession;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class ListUsersController implements Initializable {

    // --- FXML injections ---
    @FXML private Text currentUserName;
    @FXML private TreeTableView<User> tableView;
    @FXML private TreeTableColumn<User, Integer> ftId;
    @FXML private TreeTableColumn<User, String> ftfirst_name;
    @FXML private TreeTableColumn<User, String> ftlast_name;
    @FXML private TreeTableColumn<User, String> ftEmail;
    @FXML private TreeTableColumn<User, Boolean> ftStatus;
    @FXML private TreeTableColumn<User, HBox> ftAction;

    @FXML private Button btnDashboard;
    @FXML private Button btnEvents;
    @FXML private Button btnAddEvent;
    @FXML private Button btnUsers;
    @FXML private Button btnProduit;
    @FXML private Button btnCommands;
    @FXML private Button btnReclamation;
    @FXML private Button btnLogout;

    // --- Services & model ---
    private final ServiceUser serviceUser = new ServiceUser();
    private final UserDao userDao = new UserDao();
    private final ObservableList<User> userList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Display current user's first name
        currentUserName.setText(UserSession.CURRENT_USER.getUserLoggedIn().getfirst_name());

        // Populate the table
        loadUsers();
    }

    private void loadUsers() {
        try {
            userList.clear();
            List<User> users = serviceUser.selectAll();
            userList.addAll(users);

            TreeItem<User> root = new TreeItem<>();
            for (User u : userList) {
                root.getChildren().add(new TreeItem<>(u));
            }
            tableView.setRoot(root);
            tableView.setShowRoot(false);

            // Bind columns
            ftId.setCellValueFactory(cd ->
                    new SimpleObjectProperty<>(cd.getValue().getValue().getId())
            );
            ftfirst_name.setCellValueFactory(cd ->
                    new SimpleStringProperty(cd.getValue().getValue().getfirst_name())
            );
            ftlast_name.setCellValueFactory(cd ->
                    new SimpleStringProperty(cd.getValue().getValue().getlast_name())
            );
            ftEmail.setCellValueFactory(cd ->
                    new SimpleStringProperty(cd.getValue().getValue().getEmail())
            );
            ftStatus.setCellValueFactory(cd ->
                    new SimpleObjectProperty<>(cd.getValue().getValue().isBanned())
            );

            // Actions column
            ftAction.setCellValueFactory(cd ->
                    new SimpleObjectProperty<>(createActionButtons(cd.getValue().getValue()))
            );
            ftAction.setCellFactory(col -> new TreeTableCell<>() {
                @Override
                protected void updateItem(HBox item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : item);
                }
            });

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR,
                    "Erreur",
                    "Chargement",
                    "Impossible de charger les utilisateurs: " + e.getMessage());
        }
    }

    private HBox createActionButtons(User user) {
        Button deleteBtn = new Button("Delete");
        Button banBtn    = new Button("Ban");
        deleteBtn.getStyleClass().add("delete-button");
        banBtn.getStyleClass().add("ban-button");

        deleteBtn.setOnAction(evt -> {
            try {
                serviceUser.deleteOne(user);
                loadUsers();
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Erreur", null, "Suppression échouée: " + ex.getMessage());
            }
        });
        banBtn.setOnAction(evt -> {
            try {
                userDao.banUser(user.getId());
                loadUsers();
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Erreur", null, "Bannissement échoué: " + ex.getMessage());
            }
        });

        return new HBox(10, deleteBtn, banBtn);
    }

    // --- Navigation handlers ---

    @FXML private void refreshDashboard()   { navigateTo("/Dashboard.fxml",                 "Tableau de bord"); }
    @FXML private void goToEvents()         { navigateTo("/AffichageEvent.fxml",            "Événements");    }
    @FXML private void goToAddEvent()       { navigateTo("/AjouterEvent.fxml",              "Ajouter Événement"); }
    @FXML private void goToUsers()          { navigateTo("/ListUsers.fxml",                 "Utilisateurs");  }
    @FXML private void goToProduit()        { navigateTo("/produit-admin.fxml",    "Produits");      }
    @FXML private void goToCommands()       { navigateTo("/command-admin.fxml",            "Commandes");     }
    @FXML private void goToReclamation()    { navigateTo("/afficher-reclamation-admin.fxml","Réclamations");  }
    @FXML private void logout()             { navigateTo("/Login.fxml",                     "Connexion");     }

    private void navigateTo(String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) tableView.getScene().getWindow();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR,
                    "Erreur",
                    "Navigation",
                    "Impossible d'aller vers " + title + ": " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert a = new Alert(type);
        a.setTitle(title);
        a.setHeaderText(header);
        a.setContentText(content);
        a.showAndWait();
    }
}
