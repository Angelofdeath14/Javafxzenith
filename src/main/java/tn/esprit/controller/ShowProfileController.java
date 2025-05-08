package tn.esprit.controller;


import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import tn.esprit.service.session.UserSession;

import java.sql.SQLException;

public class ShowProfileController extends NavigationController {






    @FXML
    private Label navigate;

    @FXML
    private Text ftEmail;


    @FXML
    private Text ftlast_name;

    @FXML
    private Text ftfirst_name;








    @FXML
    void initialize() throws SQLException {


        assert ftEmail != null : "fx:id=\"ftEmail\" was not injected: check your FXML file 'ShowProfile.fxml'.";
        assert ftlast_name != null : "fx:id=\"ftlast_name\" was not injected: check your FXML file 'ShowProfile.fxml'.";
        assert ftfirst_name != null : "fx:id=\"ftfirst_name\" was not injected: check your FXML file 'ShowProfile.fxml'.";
        ftlast_name.setText(UserSession.CURRENT_USER.getUserLoggedIn().getlast_name());
        ftEmail.setText(UserSession.CURRENT_USER.getUserLoggedIn().getEmail());
        ftfirst_name.setText(UserSession.CURRENT_USER.getUserLoggedIn().getfirst_name());

    }

}