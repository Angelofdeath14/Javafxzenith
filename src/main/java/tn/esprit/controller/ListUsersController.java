package tn.esprit.controller;


import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import tn.esprit.entities.User;
import tn.esprit.service.ServiceUser;
import tn.esprit.service.UserDao;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class ListUsersController extends NavigationController implements Initializable {

    @FXML
    private Text currentUserName;
    @FXML
    private TreeTableColumn<User, Integer> ftId;

    @FXML
    private TreeTableColumn<User, HBox> ftAction;

    @FXML
    private TreeTableColumn<User, String> ftEmail;

    @FXML
    private TreeTableColumn<User, String> ftlast_name;

    @FXML
    private TreeTableColumn<User, String> ftfirst_name;

    @FXML
    private TreeTableColumn<User, Boolean> ftStatus;

    @FXML
    private TreeTableView<User> tableView;

    private final ServiceUser serviceUser = new ServiceUser();
    private final UserDao userDao = new UserDao();
    private final ObservableList<User> userList = FXCollections.observableArrayList();

    public static User userPass;

    @FXML
    public void initializeList() {
        try {
            // Clear previous list and load new data
            userList.clear();
            List<User> users = serviceUser.selectAll();
            userList.addAll(users);

            // Create root TreeItem
            TreeItem<User> root = new TreeItem<>();
            for (User user : userList) {
                TreeItem<User> userItem = new TreeItem<>(user);
                root.getChildren().add(userItem);
            }
            tableView.setRoot(root);
            tableView.setShowRoot(false); // Hides the root node

            // Set cell value factories for each column
            ftId.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getValue().getId()));  // Bind the id field

            ftfirst_name.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue().getfirst_name()));
            ftlast_name.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue().getlast_name()));
            ftEmail.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getValue().getEmail()));
            ftStatus.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getValue().isBanned()));

            // Set the cell factory for the action column
            ftAction.setCellValueFactory(cellData -> new SimpleObjectProperty<>(createActionButtons(cellData.getValue().getValue())));

            ftAction.setCellFactory(column -> new TreeTableCell<User, HBox>() {
                @Override
                protected void updateItem(HBox item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);  // No buttons if empty
                    } else {
                        setGraphic(item);  // Set the action buttons for the current row
                    }
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private HBox createActionButtons(User user) {
        Button deleteBtn = new Button("Delete");
        Button banBtn = new Button("Ban");

        // Create HBox and add buttons
        HBox btns = new HBox(deleteBtn, banBtn);
        deleteBtn.getStyleClass().add("delete-button");
        banBtn.getStyleClass().add("ban-button");

        // Define actions for buttons
        deleteBtn.setOnAction(event -> {
            try {
                serviceUser.deleteOne(user); // Assuming serviceUser has a delete method
                // Refresh the list after deletion
                initializeList();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });



        banBtn.setOnAction(event -> {
            try {
                userDao.banUser(user.getId());  // Assuming userDao has a ban method
                // Refresh the list after banning
                initializeList();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        return btns;
    }

    @FXML
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        assert currentUserName != null : "fx:id=\"currentUserName\" was not injected: check your FXML file 'ListUsers.fxml'.";
        assert tableView != null : "fx:id=\"tableView\" was not injected: check your FXML file 'ListUsers.fxml'.";
        initializeList();  // Populate the table when the scene is initialized
    }
}
