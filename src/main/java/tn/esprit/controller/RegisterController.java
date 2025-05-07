package tn.esprit.controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.esprit.entities.User;
import tn.esprit.service.ServiceUser;
import tn.esprit.service.UserDao;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class RegisterController {
    private ServiceUser serviceUser=new ServiceUser();
    private final UserDao userDao = new UserDao();

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;


    @FXML
    private Text agreeError;


    @FXML
    private Text cPassError;

    @FXML
    private Text emailError;

    @FXML
    private Text fnError;

    @FXML
    private PasswordField fpCofirm;

    @FXML
    private PasswordField fpPass;


    @FXML
    private CheckBox ftAgree;


    @FXML
    private TextField ftEmail;


    @FXML
    private TextField ftlast_name;


    @FXML
    private TextField ftfirst_name;



    @FXML
    private Text lnError;

    @FXML
    private Text passError;


    void clearError(){
        passError.setText("");
        lnError.setText("");
        fnError.setText("");
        emailError.setText("");
        cPassError.setText("");
        agreeError.setText("");
        ftfirst_name.getStyleClass().remove("error");
        ftEmail.getStyleClass().remove("error");
        ftAgree.getStyleClass().remove("error");
        fpPass.getStyleClass().remove("error");
        fpCofirm.getStyleClass().remove("error");
        ftlast_name.getStyleClass().remove("error");
    }
    @FXML
    void createAccount(ActionEvent event) {

        clearError();
        boolean isValid = true;

        // Phone number validation


        // First name validation
        if (ftfirst_name.getText().isEmpty()) {
            isValid = false;
            ftfirst_name.getStyleClass().add("error");
            fnError.setText("First name is required");
        } else if (ftfirst_name.getText().length()<4) {
            fnError.setText("First name must contain at least 4 characters.");
            isValid = false;
            ftfirst_name.getStyleClass().add("error");
        }

        // Last name validation
        if (ftlast_name.getText().isEmpty()) {
            isValid = false;
            lnError.setText("Last name is required");
            ftlast_name.getStyleClass().add("error");
        } else if (ftlast_name.getText().length()<4) {
            lnError.setText("Last name must contain at least 4 characters.");
            ftlast_name.getStyleClass().add("error");
            isValid = false;
        }

        // Email validation
        if (ftEmail.getText().isEmpty()) {
            isValid = false;
            ftEmail.getStyleClass().add("error");
            emailError.setText("Email address is required.");
        }else if (!ftEmail.getText().matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}")){
            emailError.setText("invalid email address.");
            ftEmail.getStyleClass().add("error");
            isValid = false;
        }else if(serviceUser.emailExists(ftEmail.getText())){
            emailError.setText("Email already exists.");
            ftEmail.getStyleClass().add("error");
            isValid = false;
        }

        // Password validation
        if (fpPass.getText().isEmpty()) {
            isValid = false;
            passError.setText("Password is required");
            fpPass.getStyleClass().add("error");

        } else if (fpPass.getText().length()<8) {
            isValid = false;
            fpPass.getStyleClass().add("error");
            passError.setText("Password must contain at least 8 characters.");
        }

        // Confirm password validation
        if (!fpCofirm.getText().equals(fpPass.getText())) {
            fpCofirm.getStyleClass().add("error");
            isValid = false;
            cPassError.setText("Passwords do not match");
        }




        if (!ftAgree.isSelected()){
            isValid = false;
            agreeError.setText("You must agree to the terms and conditions");

        }
        if(isValid){
            User user = new User();
            user.setfirst_name(ftfirst_name.getText());
            user.setlast_name(ftlast_name.getText());
            user.setEmail(ftEmail.getText());
            user.setPassword(fpPass.getText());

            try{
                serviceUser.insertOne(user);
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Sign Up");
                alert.setContentText("You have an account now.");
                alert.show();
                userDao.login(ftEmail.getText(),fpPass.getText());
                Stage stage = (Stage) this.ftlast_name.getScene().getWindow(); // Get reference to the login window's stage
                stage.setTitle("Login");


                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
                Parent p = loader.load();
                Scene scene = new Scene(p);

                stage.setScene(scene);
            }
            catch (SQLException e){
                System.out.println(e.getMessage());
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("SQLException");
                alert.setContentText(e.getMessage());
                alert.show();
            }catch (Exception e){
                System.out.println(e.getMessage());
            }

        }
    }

    @FXML
    void goToLogin(ActionEvent event) {
        try {
            Stage stage = (Stage) ftEmail.getScene().getWindow(); // Get reference to the login window's stage
            stage.setTitle("Login");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
            Parent p = loader.load();
            Scene scene = new Scene(p);

            stage.setScene(scene);

            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            // Handle navigation failure
        }

    }

    @FXML
    void initialize() {
        assert agreeError != null : "fx:id=\"agreeError\" was not injected: check your FXML file 'Register.fxml'.";
        assert cPassError != null : "fx:id=\"cPassError\" was not injected: check your FXML file 'Register.fxml'.";
        assert emailError != null : "fx:id=\"emailError\" was not injected: check your FXML file 'Register.fxml'.";
        assert fnError != null : "fx:id=\"fnError\" was not injected: check your FXML file 'Register.fxml'.";
        assert fpCofirm != null : "fx:id=\"fpCofirm\" was not injected: check your FXML file 'Register.fxml'.";
        assert fpPass != null : "fx:id=\"fpPass\" was not injected: check your FXML file 'Register.fxml'.";
        assert ftAgree != null : "fx:id=\"ftAgree\" was not injected: check your FXML file 'Register.fxml'.";
        assert ftEmail != null : "fx:id=\"ftEmail\" was not injected: check your FXML file 'Register.fxml'.";
        assert ftlast_name != null : "fx:id=\"ftLastName\" was not injected: check your FXML file 'Register.fxml'.";
        assert ftfirst_name != null : "fx:id=\"ftName\" was not injected: check your FXML file 'Register.fxml'.";

        assert lnError != null : "fx:id=\"lnError\" was not injected: check your FXML file 'Register.fxml'.";
        assert passError != null : "fx:id=\"passError\" was not injected: check your FXML file 'Register.fxml'.";

    }

}