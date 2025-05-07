package tn.esprit.service;


import tn.esprit.entities.Review;
import tn.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceReview implements IService<Review> {
    private final Connection connection;

    public ServiceReview() {
        connection = MyDataBase.getInstance().getCon();
    }

    @Override
    public void ajouter(Review review) {
        String sql = "INSERT INTO review (evenement_id, produit_id, comment, note, created_at) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            if (review.getEvenement_id() !=0) {
                preparedStatement.setInt(1, review.getEvenement_id());
            } else {
                preparedStatement.setNull(1, Types.INTEGER);
            }
            if (review.getProduit_id() != 0) {
                preparedStatement.setInt(2, review.getProduit_id());
            } else {
                preparedStatement.setNull(2, Types.INTEGER);
            }
            preparedStatement.setString(3, review.getComment());
            preparedStatement.setInt(4, review.getNote());
            preparedStatement.setTimestamp(5, Timestamp.valueOf(review.getCreated_at()));
            preparedStatement.executeUpdate();
            System.out.println("Review ajoutée avec succès.");
        } catch (SQLException e) {
            System.out.println("Erreur ajout review : " + e.getMessage());
        }
    }

    @Override
    public void modifier(Review review) {
        String sql = "UPDATE review SET evenement_id=?, produit_id=?, comment=?, note=?, created_at=? WHERE id=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            if (review.getEvenement_id() !=0) {
                preparedStatement.setInt(1, review.getEvenement_id());
            } else {
                preparedStatement.setNull(1, Types.INTEGER);
            }
            if (review.getProduit_id() != 0) {
                preparedStatement.setInt(2, review.getProduit_id());
            } else {
                preparedStatement.setNull(2, Types.INTEGER);
            }
            preparedStatement.setString(3, review.getComment());
            preparedStatement.setInt(4, review.getNote());
            preparedStatement.setTimestamp(5, Timestamp.valueOf(review.getCreated_at()));
            preparedStatement.setInt(6, review.getId());
            preparedStatement.executeUpdate();
            System.out.println("Review modifiée avec succès.");
        } catch (SQLException e) {
            System.out.println("Erreur modification review : " + e.getMessage());
        }
    }

    @Override
    public void supprimer(int id) {
        String sql = "DELETE FROM review WHERE id=?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
            System.out.println("Review supprimée avec succès.");
        } catch (SQLException e) {
            System.out.println("Erreur suppression review : " + e.getMessage());
        }
    }

    @Override
    public List<Review> afficher() {
        List<Review> reviews = new ArrayList<>();
        String sql = "SELECT * FROM review";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                Review review = new Review();
                review.setId(resultSet.getInt("id"));
                review.setEvenement_id(resultSet.getInt("evenement_id"));
                review.setProduit_id(resultSet.getInt("produit_id"));
                review.setComment(resultSet.getString("comment"));
                review.setNote(resultSet.getInt("note"));
                review.setCreated_at(resultSet.getTimestamp("created_at").toLocalDateTime());
                reviews.add(review);
            }
        } catch (SQLException e) {
            System.out.println("Erreur affichage reviews : " + e.getMessage());
        }
        return reviews;
    }
}
