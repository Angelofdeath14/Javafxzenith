package test;

import java.util.Properties;
import jakarta.mail.*;
import jakarta.mail.internet.*;

public class TestMail {

    public static void main(String[] args) {
        // Adresse Gmail et mot de passe d'application (PAS ton mot de passe classique)
        final String username = "erijnasr@gmail.com"; // Remplace par ton email Gmail
        final String password = "whxh xkuj ecap xvoh"; // Remplace par ton mot de passe d'application

        // Adresse du destinataire
        String toEmail = "erijnasr@gmail.com"; // Pour le test, tu peux envoyer à toi-même

        // Configuration des propriétés SMTP
        Properties prop = new Properties();
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.starttls.enable", "true"); // TLS
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "587");
        prop.put("mail.smtp.ssl.trust", "smtp.gmail.com"); // Ajout pour éviter SSLHandshakeException

        // Création de la session avec authentification
        Session session = Session.getInstance(prop, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            // Création du message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(toEmail)
            );
            message.setSubject("✔️ Confirmation de compte");
            message.setText("Bonjour,\n\nVoici un email de test depuis Java.\nClique ici pour confirmer ton compte : https://mon-site.com/confirm?code=1234\n\nCordialement.");

            // Envoi du message
            Transport.send(message);

            System.out.println("✅ Email envoyé avec succès !");
        } catch (MessagingException e) {
            System.err.println("❌ Erreur lors de l'envoi de l'email :");
            e.printStackTrace();
        }
    }
}
