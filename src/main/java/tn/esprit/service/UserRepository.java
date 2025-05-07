package tn.esprit.service;


import org.mindrot.jbcrypt.BCrypt;
import tn.esprit.entities.Roles;
import tn.esprit.entities.User;
import tn.esprit.utils.MyDataBase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {
    private final Connection cnx;
    private PreparedStatement ps;

    public UserRepository() {
        cnx = MyDataBase.getInstance().getCon();
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM user";
        try {
            ps = cnx.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt(("id")));

                user.setfirst_name(rs.getString(("first_name")));
                user.setlast_name(rs.getString(("last_name")));
                user.setEmail(rs.getString(("email")));
                user.setPassword(rs.getString("password"));

                user.setBanned(rs.getBoolean("isBanned"));
                user.setEnabled(rs.getBoolean("Enabled"));

                // Set other properties
                users.add(user);
                System.out.println(users);
            }
        } finally {
            if (ps != null) {
                ps.close();
            }
        }
        return users;
    }
    // ✅ Supprimer un utilisateur
    public void deleteUser(int userId) throws SQLException {
        String query = "DELETE FROM user WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'utilisateur : " + e.getMessage());
            throw e;
        }
    }

    // ✅ Trouver un utilisateur par email
    public User findByEmail(String email) throws SQLException {
        String query = "SELECT * FROM user WHERE email = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par email : " + e.getMessage());
            throw e;
        }
        return null;
    }

    // ✅ Trouver un utilisateur par ID
    public User findById(int id) throws SQLException {
        String query = "SELECT * FROM user WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return extractUserFromResultSet(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche par ID : " + e.getMessage());
            throw e;
        }
        return null;
    }

    // ✅ Modifier le mot de passe d’un utilisateur
    public void changePassword(User user) throws SQLException {
        String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
        String req = "UPDATE user SET password = ? WHERE id = ?";

        try (PreparedStatement ps = cnx.prepareStatement(req)) {
            ps.setString(1, hashedPassword);
            ps.setInt(2, user.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la mise à jour du mot de passe : " + e.getMessage());
            throw e;
        }
    }

    // ✅ Utilitaire pour mapper un ResultSet à un objet User
    private User extractUserFromResultSet(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setfirst_name(rs.getString("first_name"));
        user.setlast_name(rs.getString("last_name"));
        user.setEmail(rs.getString("email"));
        user.setPassword(rs.getString("password"));
        user.setRoles(Roles.valueOf(rs.getString("roles").trim().toUpperCase()));
        user.setBanned(rs.getBoolean("isBanned"));
        user.setEnabled(rs.getBoolean("Enabled"));

        if (hasColumn(rs, "resetToken")) {
            user.setResetToken(rs.getString("resetToken"));
        }
        return user;
    }

    // ✅ Vérifie si une colonne existe (utile si "resetToken" est parfois absent)
    private boolean hasColumn(ResultSet rs, String columnName) {
        try {
            rs.findColumn(columnName);
            return true;
        } catch (SQLException e) {
            return false;
        }
    }
}
