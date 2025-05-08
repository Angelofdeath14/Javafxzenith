package tn.esprit.service.session;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import java.util.Properties;

public class MailService {
    // Il est conseillé d'utiliser des variables d'environnement pour des raisons de sécurité
    private static final String FROM_EMAIL = "erijnasr@gmail.com"; // Ton email
    private static final String PASSWORD = "whxh xkuj ecap xvoh";   // Ton mot de passe d'application Gmail
    public static void send(String recipientEmail, String subject, String body) throws MessagingException {
        // Vérifie que les identifiants sont bien présents
        if (FROM_EMAIL == null || PASSWORD == null) {
            throw new IllegalStateException("Les identifiants email ne sont pas définis dans les variables d'environnement.");
        }

        // Configuration du serveur SMTP
        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");
        properties.setProperty("mail.smtp.ssl.protocols", "TLSv1.2");
        properties.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        // Authentification
        Authenticator authenticator = new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, PASSWORD);
            }
        };

        // Création de la session email
        Session session = Session.getInstance(properties, authenticator);
        session.setDebug(true); // Pour activer les logs de débogage

        // Création du message
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
        message.setSubject(subject);

        // Corps du message
        MimeBodyPart bodyPart = new MimeBodyPart();
        bodyPart.setContent(body, "text/plain");

        // Ajout du corps au message multipart
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(bodyPart);

        message.setContent(multipart);

        // Envoi
        Transport.send(message);
    }
}