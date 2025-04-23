package tn.esprit.entities;

import java.util.Objects;

public class Produit {
    private int id;
    private String nom;
    private String description;
    private String categorie;
    private double prix;
    private String etat;
    private String etat_produit;
    private String front_image;
    private String back_image;
    private String top_image;
    private int command_id;
    private int user_id;

    public Produit() {
    }

    public Produit(String nom, String description, String categorie, double prix, String etat, String etat_produit, String front_image, String back_image, String top_image, int command_id, int user_id) {
        this.nom = nom;
        this.description = description;
        this.categorie = categorie;
        this.prix = prix;
        this.etat = etat;
        this.etat_produit = etat_produit;
        this.front_image = front_image;
        this.back_image = back_image;
        this.top_image = top_image;
        this.command_id = command_id;
        this.user_id = user_id;
    }

    public Produit(int id, String nom, String description, String categorie, double prix, String etat, String etat_produit, String front_image, String back_image, String top_image, int command_id, int user_id) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.categorie = categorie;
        this.prix = prix;
        this.etat = etat;
        this.etat_produit = etat_produit;
        this.front_image = front_image;
        this.back_image = back_image;
        this.top_image = top_image;
        this.command_id = command_id;
        this.user_id = user_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategorie() {
        return categorie;
    }

    public void setCategorie(String categorie) {
        this.categorie = categorie;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public String getEtat_produit() {
        return etat_produit;
    }

    public void setEtat_produit(String etat_produit) {
        this.etat_produit = etat_produit;
    }

    public String getFront_image() {
        return front_image;
    }

    public void setFront_image(String front_image) {
        this.front_image = front_image;
    }

    public String getBack_image() {
        return back_image;
    }

    public void setBack_image(String back_image) {
        this.back_image = back_image;
    }

    public String getTop_image() {
        return top_image;
    }

    public void setTop_image(String top_image) {
        this.top_image = top_image;
    }

    public int getCommand_id() {
        return command_id;
    }

    public void setCommand_id(int command_id) {
        this.command_id = command_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Produit produit)) return false;
        return Double.compare(getPrix(), produit.getPrix()) == 0 && getCommand_id() == produit.getCommand_id() && getUser_id() == produit.getUser_id() && Objects.equals(getNom(), produit.getNom()) && Objects.equals(getDescription(), produit.getDescription()) && Objects.equals(getCategorie(), produit.getCategorie()) && Objects.equals(getEtat(), produit.getEtat()) && Objects.equals(getEtat_produit(), produit.getEtat_produit()) && Objects.equals(getFront_image(), produit.getFront_image()) && Objects.equals(getBack_image(), produit.getBack_image()) && Objects.equals(getTop_image(), produit.getTop_image());
    }


    @Override
    public String toString() {
        return "Produit {" +
                "\n  Nom           : " + nom +
                "\n  Description   : " + description +
                "\n  Catégorie     : " + categorie +
                "\n  Prix          : " + prix +
                "\n  État          : " + etat +
                "\n  État Produit  : " + etat_produit +
                "\n  Front Image   : " + front_image +
                "\n  Back Image    : " + back_image +
                "\n  Top Image     : " + top_image +
                "\n}";
    }

}
