package tn.esprit.controller;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.utils.StripeUtil;

public class PaymentFormController {

    @FXML private TextField cardNumberField;
    @FXML private TextField expMonthField;
    @FXML private TextField expYearField;
    @FXML private TextField cvcField;

    private long amount;
    private String currency;
    private boolean paymentSuccessful = false;

    public void setPaymentDetails(long amount, String currency) {
        this.amount = amount;
        this.currency = currency;
    }

    public boolean isPaymentSuccessful() {
        return paymentSuccessful;
    }

    @FXML
    private void onPay(ActionEvent event) {
        String raw = cardNumberField.getText().replaceAll("\\s+", "");
        String pmId = switch (raw) {
            case "4242424242424242"   -> "pm_card_visa";
            case "5555555555554444"   -> "pm_card_mastercard";
            case "378282246310005"    -> "pm_card_amex";
            case "6011111111111117"   -> "pm_card_discover";
            default                   -> null;
        };

        if (pmId == null) {
            new Alert(Alert.AlertType.WARNING,
                    "Numéro de carte invalide.\n")

                            .showAndWait();
            return;
        }

        try {
            PaymentIntent intent = StripeUtil.chargeWithTestPM(pmId, amount, currency);
            if ("succeeded".equalsIgnoreCase(intent.getStatus())) {
                paymentSuccessful = true;
                new Alert(Alert.AlertType.INFORMATION, "Paiement réussi !").showAndWait();
                closeWindow();
            } else {
                new Alert(Alert.AlertType.ERROR,
                        "Le paiement a échoué. Statut : " + intent.getStatus())
                        .showAndWait();
            }
        } catch (StripeException e) {
            new Alert(Alert.AlertType.ERROR,
                    "Erreur Stripe : " + e.getMessage())
                    .showAndWait();
        }
    }

    private void closeWindow() {
        Stage stage = (Stage) cardNumberField.getScene().getWindow();
        stage.close();
    }
}
