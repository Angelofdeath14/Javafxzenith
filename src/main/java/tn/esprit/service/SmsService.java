package tn.esprit.service;

import com.twilio.rest.api.v2010.account.Message;

public class SmsService {

    // 🔥 Met tes vraies infos Twilio ici

    String ACCOUNT_SID = System.getenv("TWILIO_ACCOUNT_SID");
    String AUTH_TOKEN = System.getenv("TWILIO_AUTH_TOKEN");

    public static final String TWILIO_PHONE_NUMBER = ""; // ton numéro Twilio

    public static void sendSms(String toPhoneNumber, String code) {
        

        Message message = Message.creator(
                new com.twilio.type.PhoneNumber(toPhoneNumber),
                new com.twilio.type.PhoneNumber(TWILIO_PHONE_NUMBER),
                "Votre code de confirmation est : " + code
        ).create();

        System.out.println("✅ SMS envoyé à " + toPhoneNumber + " avec SID : " + message.getSid());
    }
}
