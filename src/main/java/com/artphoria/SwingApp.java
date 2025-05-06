package com.artphoria;

import javax.swing.*;
import java.awt.*;

public class SwingApp {
    public static void main(String[] args) {
        // Assurer que l'interface est créée dans l'Event Dispatch Thread de Swing
        SwingUtilities.invokeLater(() -> {
            try {
                // Créer une fenêtre
                JFrame frame = new JFrame("Artphoria - Test Swing");
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.setSize(400, 300);
                
                // Créer des composants d'interface
                JLabel label = new JLabel("Test de l'application avec Swing");
                label.setFont(new Font("Arial", Font.BOLD, 16));
                
                JButton button = new JButton("Cliquez ici");
                button.addActionListener(e -> JOptionPane.showMessageDialog(frame, "Swing fonctionne correctement!"));
                
                // Ajouter les composants à la fenêtre
                JPanel panel = new JPanel();
                panel.setLayout(new BorderLayout(20, 20));
                panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
                panel.add(label, BorderLayout.NORTH);
                panel.add(button, BorderLayout.CENTER);
                
                frame.add(panel);
                frame.setLocationRelativeTo(null); // Centrer la fenêtre
                frame.setVisible(true);
                
                System.out.println("Application Swing lancée avec succès!");
                
            } catch (Exception e) {
                System.err.println("Erreur au lancement de l'application Swing: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }
} 