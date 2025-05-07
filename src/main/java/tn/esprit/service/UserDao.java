package tn.esprit.service;


import org.mindrot.jbcrypt.BCrypt;
import tn.esprit.service.session.AuthDTO;
import tn.esprit.service.session.UserSession;
import tn.esprit.utils.MyDataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {

    private Connection cnx;

    // Préparer les requêtes SQL
    PreparedStatement ps;
    UserSession userSession;
    private final ServiceUser serviceUser = new ServiceUser();
    private final UserRepository userRepository = new UserRepository();
    private final OTPService otpService = new OTPService();

    public UserDao() {
        cnx = MyDataBase.getInstance().getCon();
    }

    // Récupérer le mot de passe hashé de l'utilisateur depuis la base de données
    public String getHashedPasswordByUsername(String email) throws SQLException {
        String hashedPassword = null;

        if (cnx == null || cnx.isClosed()) {
            cnx = MyDataBase.getInstance().getCon();
        }

        String sql = "SELECT password FROM user WHERE email = ?";
        try (PreparedStatement statement = cnx.prepareStatement(sql)) {
            statement.setString(1, email);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    hashedPassword = rs.getString("password");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la récupération du mot de passe : " + e.getMessage());
            throw e;
        }

        return hashedPassword;
    }
    public boolean login(String email, String password) throws SQLException {
        if (UserSession.CURRENT_USER != null) {
            String currentEmail = UserSession.CURRENT_USER.getUserLoggedIn().getEmail();
            return currentEmail.equals(email);
        }

        String hashedPasswordFromDatabase = getHashedPasswordByUsername(email);
        if (hashedPasswordFromDatabase == null) {
            return false;
        }

        if (BCrypt.checkpw(password, hashedPasswordFromDatabase)) {
            String sql = "SELECT * FROM user WHERE email = ?";
            try (PreparedStatement ps = cnx.prepareStatement(sql)) {
                ps.setString(1, email);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        AuthDTO authDTO = new AuthDTO();
                        authDTO.setId(rs.getInt("id"));
                        authDTO.setEmail(email);
                        authDTO.setRoles(rs.getString("roles").trim().toUpperCase());
                        authDTO.setBanned(rs.getBoolean("isBanned"));
                        authDTO.setlast_name(rs.getString("last_name"));
                        authDTO.setfirst_name(rs.getString("first_name"));
                        authDTO.setRegistration_date(rs.getDate("registration_date"));
                        authDTO.setResetToken(rs.getString("resetToken"));
                        authDTO.setEnabled(rs.getBoolean("enabled"));

                        if (authDTO.isBanned()) {
                            System.out.println("Utilisateur banni.");
                            return false;
                        }

                        if (!authDTO.isEnabled()) {
                            UserSession.getSession(authDTO); // Utilisateur non encore activé
                            return true;
                        }

                        UserSession.getSession(authDTO); // Connexion réussie
                        return true;
                    }
                }
            }
        }

        return false;
    }
    public void activateUser(String email) throws SQLException {
        String sql = "UPDATE users SET enabled = 1 WHERE email = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, email);
        ps.executeUpdate();
    }

    public void banUser(int userId) throws SQLException {
        String query = "UPDATE user SET isBanned = TRUE WHERE id = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setInt(1, userId);
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Utilisateur banni avec succès.");
            } else {
                System.out.println("Aucun utilisateur trouvé avec cet ID.");
            }
        }
    }

    /**
     * Active un utilisateur après confirmation par email.
     */
    public boolean enableUser(String email) throws SQLException {
        String sql = "UPDATE user SET enabled = 1 WHERE email = ?"; // Met à jour l'attribut enabled

        try (PreparedStatement stmt = cnx.prepareStatement(sql)) {
            stmt.setString(1, email); // Remplace l'email de l'utilisateur
            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Utilisateur activé avec succès.");
                return true;
            } else {
                System.out.println("Aucun utilisateur trouvé avec cet email.");
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'activation de l'utilisateur : " + e.getMessage());
            throw e; // Propager l'exception pour la gestion d'erreur
        }
    }


    /**
     * Récupère le jeton de confirmation d'un utilisateur via son email.
     */
    public String getConfirmationTokenByEmail(String email) {
        String token = null;
        String sql = "SELECT resetToken FROM user WHERE email = ?";

        try (PreparedStatement stmt = cnx.prepareStatement(sql)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    token = rs.getString("resetToken");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du token : " + e.getMessage());
        }

        return token;
    }
    public void saveConfirmationCode(String email, String confirmationCode) throws SQLException {
        String query = "UPDATE users SET confirmation_code = ? WHERE email = ?";
        try (PreparedStatement stmt = cnx.prepareStatement(query)) {
            stmt.setString(1, confirmationCode); // Le code à enregistrer
            stmt.setString(2, email); // L'email de l'utilisateur
            stmt.executeUpdate(); // Mise à jour dans la base de données
        }
    }



}