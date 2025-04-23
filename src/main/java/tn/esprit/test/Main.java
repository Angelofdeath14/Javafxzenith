package tn.esprit.test;

import tn.esprit.entities.Command;
import tn.esprit.entities.Produit;
import tn.esprit.service.ServiceCommand;
import tn.esprit.service.ServiceProduit;

import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        ServiceCommand serviceCommand = new ServiceCommand();
        ServiceProduit serviceProduit = new ServiceProduit();

        // ===========================
        //        SERVICE COMMAND
        // ===========================

        System.out.println("\n=== AJOUT COMMANDE ===");
        Command cmd = new Command();
        cmd.setId_user(1);
        cmd.setCreate_at(LocalDateTime.now());
        cmd.setStatus("EN_ATTENTE");
        cmd.setTotal_amount(199.99);
        cmd.setDelivery_address("123 Rue Principale, Tunis");
        cmd.setNotes("Livrer entre 9h et 18h");
        serviceCommand.ajouter(cmd);
        afficherCommandes(serviceCommand);

        System.out.println("\n=== MODIFIER COMMANDE ===");
        List<Command> commands = serviceCommand.afficher();
        if (!commands.isEmpty()) {
            Command toModify = commands.get(0);
            toModify.setStatus("LIVREE");
            toModify.setNotes("Commande livrée avec succès");
            serviceCommand.modifier(toModify);
            afficherCommandes(serviceCommand);
        }

        System.out.println("\n=== SUPPRIMER COMMANDE ===");
        if (!commands.isEmpty()) {
            int lastCommandId = commands.get(commands.size() - 1).getId();
            serviceCommand.supprimer(lastCommandId);
            afficherCommandes(serviceCommand);
        }

        // ===========================
        //        SERVICE PRODUIT
        // ===========================

        System.out.println("\n=== AJOUT PRODUIT ===");
        Produit p = new Produit();
        p.setNom("PC Portable Dell");
        p.setDescription("i7, 16GB RAM, 512GB SSD");
        p.setCategorie("Informatique");
        p.setPrix(1800.50);
        p.setEtat("Neuf");
        p.setEtat_produit("Disponible");
        p.setFront_image("front.jpg");
        p.setBack_image("back.jpg");
        p.setTop_image("top.jpg");
        p.setCommand_id(1); // Ensure this ID exists
        p.setUser_id(1);    // Ensure this ID exists
        serviceProduit.ajouter(p);
        afficherProduits(serviceProduit);

        System.out.println("\n=== MODIFIER PRODUIT ===");
        List<Produit> produits = serviceProduit.afficher();
        if (!produits.isEmpty()) {
            Produit toModify = produits.get(0);
            toModify.setPrix(1750.00);
            toModify.setEtat("Comme neuf");
            serviceProduit.modifier(toModify);
            afficherProduits(serviceProduit);
        }

        System.out.println("\n=== SUPPRIMER PRODUIT ===");
        if (!produits.isEmpty()) {
            int lastProductId = produits.get(produits.size() - 1).getId();
            serviceProduit.supprimer(lastProductId);
            afficherProduits(serviceProduit);
        }
    }

    private static void afficherCommandes(ServiceCommand serviceCommand) {
        System.out.println("--- Liste des commandes ---");
        List<Command> commands = serviceCommand.afficher();
        for (Command c : commands) {
            System.out.println(c);
        }
    }

    private static void afficherProduits(ServiceProduit serviceProduit) {
        System.out.println("--- Liste des produits ---");
        List<Produit> produits = serviceProduit.afficher();
        for (Produit p : produits) {
            System.out.println(p);
        }
    }
}
