package tn.esprit.service;

import tn.esprit.entities.Command;
import tn.esprit.utils.MyDataBase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServiceCommand implements IService<Command> {

    private final Connection con;

    public ServiceCommand() {
        con = MyDataBase.getInstance().getCon();
    }

    @Override
    public void ajouter(Command command) {
        String sql = "INSERT INTO command (id_user, create_at, status, total_amount, delivery_address, notes) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, command.getId_user());
            ps.setTimestamp(2, Timestamp.valueOf(command.getCreate_at()));
            ps.setString(3, command.getStatus());
            ps.setDouble(4, command.getTotal_amount());
            ps.setString(5, command.getDelivery_address());
            ps.setString(6, command.getNotes());
            ps.executeUpdate();
            System.out.println("Commande ajoutée avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la commande : " + e.getMessage());
        }
    }

    public int ajouterWithReturningId(Command command) {
        String sql = "INSERT INTO command (id_user, create_at, status, total_amount, delivery_address, notes) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, command.getId_user());
            ps.setTimestamp(2, Timestamp.valueOf(command.getCreate_at()));
            ps.setString(3, command.getStatus());
            ps.setDouble(4, command.getTotal_amount());
            ps.setString(5, command.getDelivery_address());
            ps.setString(6, command.getNotes());
            ps.executeUpdate();
            ResultSet keys=ps.getGeneratedKeys();
            if (keys.next()) {

                int generatedId=keys.getInt(1);
                command.setId(generatedId);
                return generatedId;
            }
            System.out.println("Commande ajoutée avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la commande : " + e.getMessage());
        }
        return -1;
    }

    @Override
    public void modifier(Command command) {
        String sql = "UPDATE command SET id_user = ?, create_at = ?, status = ?, total_amount = ?, delivery_address = ?, notes = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, command.getId_user());
            ps.setTimestamp(2, Timestamp.valueOf(command.getCreate_at()));
            ps.setString(3, command.getStatus());
            ps.setDouble(4, command.getTotal_amount());
            ps.setString(5, command.getDelivery_address());
            ps.setString(6, command.getNotes());
            ps.setInt(7, command.getId());
            ps.executeUpdate();
            System.out.println("Commande modifiée avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de la commande : " + e.getMessage());
        }
    }

    @Override
    public void supprimer(int id) {
        String sql = "DELETE FROM command WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Commande supprimée avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la commande : " + e.getMessage());
        }
    }

    @Override
    public List<Command> afficher() {
        List<Command> commands = new ArrayList<>();
        String sql = "SELECT * FROM command";
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Command command = new Command();
                command.setId(rs.getInt("id"));
                command.setId_user(rs.getInt("id_user"));
                command.setCreate_at(rs.getTimestamp("create_at").toLocalDateTime());
                command.setStatus(rs.getString("status"));
                command.setTotal_amount(rs.getDouble("total_amount"));
                command.setDelivery_address(rs.getString("delivery_address"));
                command.setNotes(rs.getString("notes"));
                commands.add(command);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de l'affichage des commandes : " + e.getMessage());
        }

        return commands;
    }
}
