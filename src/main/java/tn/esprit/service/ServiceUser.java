package tn.esprit.service;

import jakarta.mail.MessagingException;
import org.mindrot.jbcrypt.BCrypt;
import tn.esprit.entities.Roles;
import tn.esprit.entities.User;
import tn.esprit.service.session.MailService;
import tn.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.List;
import java.util.UUID;

public class ServiceUser implements CRUD<User> {

    private final Connection cnx;
    private final UserRepository userRepository;

    public ServiceUser() {
        this.cnx = MyDataBase.getInstance().getCon();
        this.userRepository = new UserRepository();
        createDefaultAdmin();
    }

    // Vérifie si l'email existe déjà dans la base de données
    public boolean emailExists(String email) {
        String query = "SELECT COUNT(*) FROM user WHERE email = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Erreur vérification email : " + e.getMessage());
            return false;
        }
    }

    // Génère un code de confirmation de 6 chiffres
    private String generateActivationCode() {
        // Génère un nombre aléatoire entre 100000 et 999999 (6 chiffres)
        int code = (int) (Math.random() * 900000) + 100000;
        return String.valueOf(code);
    }


    // Génère un token UUID (restToken)
    private String generateRestToken() {
        return UUID.randomUUID().toString();
    }

    // Insérer un utilisateur
    @Override
    public boolean insertOne(User user) throws SQLException {
        if (emailExists(user.getEmail())) {
            System.err.println("Erreur : l'email existe déjà.");
            return false;
        }

        // Génère le token unique (resetToken)
        String restToken = generateRestToken();  // Utiliser le resetToken ici
        // Hasher le mot de passe
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPassword);

        // Définir rôle par défaut si null
        if (user.getRoles() == null) {
            user.setRoles(Roles.ROLE_USER);
        }
        String roleString = user.getRoles().toString();

        // Insertion dans la base de données
        String sql = "INSERT INTO user (email, roles, password, first_name, last_name, isBanned, Enabled, registration_date, resetToken) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, user.getEmail());
            ps.setString(2, roleString);
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getfirst_name());
            ps.setString(5, user.getlast_name());
            ps.setBoolean(6, false); // pas banni
            ps.setBoolean(7, false); // pas encore activé
            ps.setDate(8, new Date(System.currentTimeMillis()));
            ps.setString(9, restToken); // Utilisation du resetToken

            ps.executeUpdate();
        }

        // Envoyer le token de réinitialisation (resetToken) par email
        sendConfirmationEmail(user, restToken);  // Utiliser resetToken ici

        return true;
    }

    // Envoi du mail avec le resetToken
    private void sendConfirmationEmail(User user, String resetToken) {
        String subject = "Votre code de confirmation";
        String body = "Bonjour " + user.getfirst_name() + ",\n\n" +
                "Merci pour votre inscription. Voici votre code de confirmation :\n\n" +
                "**" + resetToken + "**\n\n" + // Utiliser resetToken dans l'email
                "Veuillez entrer ce code dans l'application pour activer votre compte.\n\n" +
                "Cordialement,\nL'équipe.";

        try {
            MailService.send(user.getEmail(), subject, body);  // Envoi du mail avec MailService
        } catch (MessagingException e) {
            System.err.println("Erreur lors de l'envoi du mail de confirmation : " + e.getMessage());
        }
    }

    private String generateResetToken() {
        return UUID.randomUUID().toString();  // Génère un token unique
    }
    private void saveResetTokenForUser(User user, String resetToken) {
        // Implémentation de la sauvegarde du token pour l'utilisateur
        // Par exemple, mettre à jour l'utilisateur avec le resetToken dans la table users
        user.setResetToken(resetToken);
    }

    // Confirmer l'activation du compte avec un code
    public boolean confirmAccount(String confirmationCode) {
        try {
            String sql = "SELECT * FROM user WHERE resetToken = ?";
            PreparedStatement ps = cnx.prepareStatement(sql);
            ps.setString(1, confirmationCode);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String updateSql = "UPDATE user SET Enabled = TRUE, resetToken = NULL WHERE resetToken = ?";
                PreparedStatement updatePs = cnx.prepareStatement(updateSql);
                updatePs.setString(1, confirmationCode);
                updatePs.executeUpdate();
                return true; // Compte activé
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la confirmation du compte : " + e.getMessage());
        }
        return false; // Code invalide ou autre erreur
    }

    // Supprimer un utilisateur
    @Override
    public void deleteOne(User user) throws SQLException {
        userRepository.deleteUser(user.getId());
    }

    // Sélectionner tous les utilisateurs
    public List<User> selectAll() throws SQLException {
        return userRepository.getAllUsers();
    }

    // Créer un admin par défaut si aucun administrateur n'existe
    public void createDefaultAdmin() {
        String email = "admin@gmail.com";

        String query = "SELECT COUNT(*) FROM user WHERE email = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) == 0) {
                    User admin = new User();
                    admin.setEmail(email);
                    admin.setfirst_name("Admin");
                    admin.setlast_name("Super");
                    admin.setPassword(BCrypt.hashpw("admin123", BCrypt.gensalt())); // Hashage du mot de passe
                    admin.setRoles(Roles.ROLE_ADMIN);
                    admin.setEnabled(true);

                    insertOne(admin);
                    System.out.println("✅ Admin par défaut créé avec succès.");
                } else {
                    System.out.println("ℹ️ Un admin existe déjà.");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur création admin : " + e.getMessage());
        }
    }

    // Trouver un utilisateur par email
    public User findByEmail(String email) throws SQLException {
        String query = "SELECT * FROM user WHERE email = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(rs.getString("password"));
                    user.setfirst_name(rs.getString("first_name"));
                    user.setlast_name(rs.getString("last_name"));
                    user.setRoles(Roles.valueOf(rs.getString("roles")));
                    user.setBanned(rs.getBoolean("isBanned"));
                    user.setEnabled(rs.getBoolean("Enabled"));
                    user.setResetToken(rs.getString("resetToken"));
                    return user;
                }
            }
        }
        return null;
    }

    // Authentifier l'utilisateur (vérifier si le compte est activé)
    public boolean authenticate(String email, String password) throws SQLException {
        User user = findByEmail(email); // Trouver l'utilisateur par email
        if (user != null && user.isEnabled()) {
            // Vérifier le mot de passe avec BCrypt
            if (BCrypt.checkpw(password, user.getPassword())) {
                return true; // Authentification réussie
            }
        }
        return false; // Utilisateur non activé ou mot de passe incorrect
    }

    // Mise à jour de l'utilisateur
    @Override
    public void updateOne(User user) throws SQLException {
        String query = "SELECT password FROM user WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, user.getId());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String existingPassword = rs.getString("password");

                if (user.getPassword() != null && !user.getPassword().isEmpty() && !BCrypt.checkpw(user.getPassword(), existingPassword)) {
                    user.setPassword(BCrypt.hashpw(user.getPassword(), BCrypt.gensalt()));
                } else {
                    user.setPassword(existingPassword);
                }

                String roleString = (user.getRoles() != null) ? user.getRoles().toString() : Roles.ROLE_USER.toString();

                String sql = "UPDATE user SET first_name=?, last_name=?, email=?, roles=?, password=?, isBanned=? WHERE id=?";
                try (PreparedStatement updatePs = cnx.prepareStatement(sql)) {
                    updatePs.setString(1, user.getfirst_name());
                    updatePs.setString(2, user.getlast_name());
                    updatePs.setString(3, user.getEmail());
                    updatePs.setString(4, roleString);
                    updatePs.setString(5, user.getPassword());
                    updatePs.setBoolean(6, user.isBanned());
                    updatePs.setInt(7, user.getId());
                    updatePs.executeUpdate();
                }
            }
        }
    }
}
