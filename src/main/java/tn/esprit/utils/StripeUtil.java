package tn.esprit.utils;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import java.util.List;

public class StripeUtil {
    static{
        Stripe.apiKey="sk_test_51OqXPiEYbRLEiKYMznKOWqxGQYtuNKXF0PX3YwIJoHfXi9uFezAWBsZBR6itB2T2c5LI1z50zl3UFuhW2eHGPv3U000ypwtVxC";
    }
    private static final List<String> ALLOWED_PM_IDS = List.of(
            "pm_card_visa",
            "pm_card_mastercard",
            "pm_card_amex",
            "pm_card_discover"
    );
    public static PaymentIntent chargeWithTestPM(
            String paymentMethodId,
            long amount,
            String currency
    ) throws StripeException {
        if (!ALLOWED_PM_IDS.contains(paymentMethodId)) {
            throw new IllegalArgumentException(
                    "Invalid test payment method: " + paymentMethodId
            );
        }
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount)
                .setCurrency(currency)
                .addPaymentMethodType("card")
                .setPaymentMethod(paymentMethodId)
                .setConfirm(true)
                .build();

        return PaymentIntent.create(params);
    }
}
