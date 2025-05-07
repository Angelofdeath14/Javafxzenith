package tn.esprit.service;



import tn.esprit.service.session.MailService;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class OTPService {

    private static final long OTP_EXPIRATION_DURATION = 60 * 1000; // 1 minute
    private static final Map<String, OTPDetails> otpMap = new HashMap<>();
    private static String email; // Email utilisé pour le reset

    // ✅ Génère un OTP et l’associe à l’email
    public static String generateOTP(String email, int length) {
        String otp = generateRandomOTP(length);
        otpMap.put(email, new OTPDetails(otp));
        setEmail(email); // on stocke l’email pour l’utiliser ensuite
        return otp;
    }

    // ✅ Envoie l’OTP à l’email
    public static boolean sendOTP(String email, String otp) {
        try {
            String subject = "Your OTP for Password Reset";
            String message = "Use this OTP to reset your password: " + otp + "\n\nIt expires in 1 minute.";
            MailService.send(email, subject, message);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ Valide un OTP reçu pour un email donné
    public static boolean validateOTP(String email, String userOTP) {
        OTPDetails otpDetails = otpMap.get(email);
        if (otpDetails != null && System.currentTimeMillis() - otpDetails.getTimestamp() <= OTP_EXPIRATION_DURATION) {
            boolean isValid = userOTP.equals(otpDetails.getOtp());
            if (isValid) {
                otpMap.remove(email); // supprimer après validation réussie
                return true;
            }
        }
        otpMap.remove(email); // supprimer si expiré ou invalide
        return false;
    }

    // ✅ Générateur OTP aléatoire
    private static String generateRandomOTP(int length) {
        StringBuilder otp = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            otp.append(random.nextInt(10)); // Chiffres de 0 à 9
        }
        return otp.toString();
    }

    // ✅ Nettoyage manuel des OTP expirés (optionnel si tu veux faire le ménage régulièrement)
    public static void removeExpiredOTPs() {
        long currentTime = System.currentTimeMillis();
        otpMap.entrySet().removeIf(entry -> currentTime - entry.getValue().getTimestamp() > OTP_EXPIRATION_DURATION);
    }

    // ✅ Stockage temporaire de l’email pour y accéder dans d’autres vues (comme le changement de mot de passe)
    public static void setEmail(String emailAddress) {
        email = emailAddress;
    }

    public static String getEmail() {
        return email;
    }

    // ✅ Classe interne pour stocker OTP + horodatage
    private static class OTPDetails {
        private final String otp;
        private final long timestamp;

        public OTPDetails(String otp) {
            this.otp = otp;
            this.timestamp = System.currentTimeMillis();
        }

        public String getOtp() {
            return otp;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }
}
