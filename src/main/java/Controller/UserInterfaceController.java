package Controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import java.io.IOException;

public class UserInterfaceController {
    @FXML
    private StackPane contentArea;

    @FXML
    private void showEvents() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Evenements.fxml"));
            Parent eventsView = loader.load();
            contentArea.getChildren().setAll(eventsView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showReservations() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Reservations.fxml"));
            Parent reservationsView = loader.load();
            contentArea.getChildren().setAll(reservationsView);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
} 