package tn.esprit.service;

import tn.esprit.entities.Produit;
import tn.esprit.utils.MyDataBase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceProduit implements IService<Produit>{
    private Connection con;
    public ServiceProduit() {
        con= MyDataBase.getInstance().getCon();
    }
    @Override
    public void ajouter(Produit produit) {
        String sql="insert into produit (nom, description, categorie, prix, etat, etat_produit, front_image, back_image, top_image,command_id, user_id) values (?,?,?,?,?,?,?,?,?,?,?)";
        try(PreparedStatement ps=con.prepareStatement(sql)){
            ps.setString(1, produit.getNom());
            ps.setString(2, produit.getDescription());
            ps.setString(3, produit.getCategorie());
            ps.setDouble(4, produit.getPrix());
            ps.setString(5, produit.getEtat());
            ps.setString(6, produit.getEtat_produit());
            ps.setString(7, produit.getFront_image());
            ps.setString(8, produit.getBack_image());
            ps.setString(9, produit.getTop_image());
            if (produit.getCommand_id() == 0) {
                ps.setNull(10, java.sql.Types.INTEGER);
            } else {
                ps.setInt(10, produit.getCommand_id());
            }
            ps.setInt(11, produit.getUser_id());
            ps.executeUpdate();
            System.out.println("Product added successfully");
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void modifier(Produit produit) {
        String sql="UPDATE produit SET nom=?, description=?, categorie=?, prix=?, etat=?, etat_produit=?, front_image=?, back_image=?, top_image=?,command_id=?, user_id=? WHERE id=?";
        try(PreparedStatement ps=con.prepareStatement(sql)){
            ps.setString(1, produit.getNom());
            ps.setString(2, produit.getDescription());
            ps.setString(3, produit.getCategorie());
            ps.setDouble(4, produit.getPrix());
            ps.setString(5, produit.getEtat());
            ps.setString(6, produit.getEtat_produit());
            ps.setString(7, produit.getFront_image());
            ps.setString(8, produit.getBack_image());
            ps.setString(9, produit.getTop_image());
            if (produit.getCommand_id() == 0) {
                ps.setNull(10, java.sql.Types.INTEGER);
            } else {
                ps.setInt(10, produit.getCommand_id());
            }
            ps.setInt(11, produit.getUser_id());
            ps.setInt(12, produit.getId());
            ps.executeUpdate();
            System.out.println("Product modified successfully");
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void supprimer(int id) {
        String sql="DELETE FROM produit WHERE id=?";
        try(PreparedStatement ps=con.prepareStatement(sql)){
            ps.setInt(1, id);
            ps.executeUpdate();
            System.out.println("Product modified successfully");
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Produit> afficher() {
        List<Produit> produits=new ArrayList<>();
        String sql="SELECT * FROM produit";
        try(Statement ps=con.createStatement()){
            ResultSet rs=ps.executeQuery(sql);
            while (rs.next()){
                Produit produit=new Produit();
                produit.setId(rs.getInt("id"));
                produit.setNom(rs.getString("nom"));
                produit.setDescription(rs.getString("description"));
                produit.setCategorie(rs.getString("categorie"));
                produit.setPrix(rs.getDouble("prix"));
                produit.setEtat(rs.getString("etat"));
                produit.setEtat_produit(rs.getString("etat_produit"));
                produit.setFront_image(rs.getString("front_image"));
                produit.setBack_image(rs.getString("back_image"));
                produit.setTop_image(rs.getString("top_image"));
                produit.setCommand_id(rs.getInt("command_id"));
                produit.setUser_id(rs.getInt("user_id"));
                produits.add(produit);
            }

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return produits;

    }
}
