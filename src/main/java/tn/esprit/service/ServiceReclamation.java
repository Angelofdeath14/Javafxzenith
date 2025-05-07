package tn.esprit.service;




import tn.esprit.entity.Reclamation;
import tn.esprit.utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class ServiceReclamation implements IService<Reclamation>{
    private Connection connection;
    public ServiceReclamation() {
        connection= MyDatabase.getInstance().getConnection();
    }

    @Override
    public void ajouter(Reclamation reclmation) {
        String sql="INSERT INTO `reclamation`(`titre`, `description`, `date_creation`, `id_user`) VALUES (?,?,?,?)";
        try(PreparedStatement preparedStatement=connection.prepareStatement(sql)){
            preparedStatement.setString(1,reclmation.getTitre());
            preparedStatement.setString(2,reclmation.getDescription());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(reclmation.getDate_creation()));
            preparedStatement.setInt(4,reclmation.getId_user());
            preparedStatement.executeUpdate();

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }


    }

    @Override
    public void modifier(Reclamation reclmation) {
        String sql="UPDATE `reclamation` SET `titre`=?,`description`=?,`date_creation`=?,`id_user`=? WHERE id=?";
        try(PreparedStatement preparedStatement=connection.prepareStatement(sql)){
            preparedStatement.setString(1,reclmation.getTitre());
            preparedStatement.setString(2,reclmation.getDescription());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(reclmation.getDate_creation()));
            preparedStatement.setInt(4,reclmation.getId_user());
            preparedStatement.setInt(5,reclmation.getId());
            preparedStatement.executeUpdate();

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void supprimer(int id) {
        String sql="DELETE FROM reclamation WHERE id=?";
        try(PreparedStatement preparedStatement=connection.prepareStatement(sql)){
            preparedStatement.setInt(1,id);
            preparedStatement.executeUpdate();

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Reclamation> afficher() {
        List<Reclamation> reclamations=new ArrayList<>();
        String sql="SELECT * FROM reclamation";
        try(Statement statement=connection.createStatement()){
            ResultSet resultSet=statement.executeQuery(sql);
            while(resultSet.next()){
                Reclamation reclmation=new Reclamation();
                reclmation.setId(resultSet.getInt("id"));
                reclmation.setTitre(resultSet.getString("titre"));
                reclmation.setDescription(resultSet.getString("description"));
                reclmation.setDate_creation(resultSet.getTimestamp("date_creation").toLocalDateTime());
                reclmation.setId_user(resultSet.getInt("id_user"));
                reclamations.add(reclmation);
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return reclamations;
    }
}
