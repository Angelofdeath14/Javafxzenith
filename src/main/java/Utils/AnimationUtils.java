package Utils;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.util.Duration;

/**
 * Classe utilitaire pour gérer les animations dans l'application Artphoria
 */
public class AnimationUtils {

    /**
     * Crée une animation d'apparition en fondu pour un nœud
     * 
     * @param node Le nœud à animer
     * @param delay Le délai avant de démarrer l'animation (ms)
     * @param duration La durée de l'animation (ms)
     * @return L'animation à jouer
     */
    public static FadeTransition createFadeIn(Node node, int delay, int duration) {
        if (node == null) return null;
        
        // Initialiser la transparence
        node.setOpacity(0);
        
        FadeTransition fade = new FadeTransition(Duration.millis(duration), node);
        fade.setDelay(Duration.millis(delay));
        fade.setFromValue(0);
        fade.setToValue(1);
        
        return fade;
    }
    
    /**
     * Crée une animation de déplacement vertical pour un nœud
     * 
     * @param node Le nœud à animer
     * @param fromY Position verticale de départ
     * @param toY Position verticale d'arrivée
     * @param delay Le délai avant de démarrer l'animation (ms)
     * @param duration La durée de l'animation (ms)
     * @return L'animation à jouer
     */
    public static TranslateTransition createVerticalTranslation(Node node, double fromY, double toY, int delay, int duration) {
        if (node == null) return null;
        
        // Définir la position initiale
        node.setTranslateY(fromY);
        
        TranslateTransition translate = new TranslateTransition(Duration.millis(duration), node);
        translate.setDelay(Duration.millis(delay));
        translate.setFromY(fromY);
        translate.setToY(toY);
        
        return translate;
    }
    
    /**
     * Joue une animation d'apparition en fondu séquentielle pour plusieurs nœuds
     * 
     * @param nodes Les nœuds à animer
     * @param delayBetween Le délai entre chaque animation (ms)
     * @param duration La durée de chaque animation (ms)
     */
    public static void sequentialFadeIn(Node[] nodes, int delayBetween, int duration) {
        if (nodes == null || nodes.length == 0) return;
        
        SequentialTransition sequence = new SequentialTransition();
        
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] == null) continue;
            
            // Créer une animation combinée de fondu et déplacement
            ParallelTransition combined = new ParallelTransition();
            
            // Animation de fondu
            FadeTransition fade = createFadeIn(nodes[i], 0, duration);
            
            // Animation de déplacement vers le haut
            TranslateTransition translate = createVerticalTranslation(nodes[i], 20, 0, 0, duration);
            
            // Ajouter les animations à la combinaison
            combined.getChildren().addAll(fade, translate);
            
            // Ajouter la combinaison à la séquence
            sequence.getChildren().add(combined);
            
            // Ajouter un délai après chaque nœud sauf le dernier
            if (i < nodes.length - 1) {
                javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(Duration.millis(delayBetween));
                sequence.getChildren().add(pause);
            }
        }
        
        // Jouer la séquence
        sequence.play();
    }
    
    /**
     * Une méthode alternative de fadeIn pour maintenir la compatibilité avec le code existant
     * Cette méthode est appelée avec seulement deux paramètres par certains contrôleurs
     * 
     * @param node Le nœud à animer
     * @param delay Le délai avant de démarrer l'animation (ms)
     */
    public static void fadeIn(javafx.scene.Node node, int delay) {
        // Appeler la version à 3 paramètres avec une durée par défaut de 500ms
        fadeIn(node, delay, 500);
    }
    
    /**
     * Joue une animation d'apparition en fondu pour un seul nœud
     * 
     * @param node Le nœud à animer
     * @param delay Le délai avant de démarrer l'animation (ms)
     * @param duration La durée de l'animation (ms)
     */
    public static void fadeIn(Node node, int delay, int duration) {
        if (node == null) return;
        
        // Créer une animation combinée de fondu et déplacement
        ParallelTransition combined = new ParallelTransition();
        
        // Animation de fondu
        FadeTransition fade = createFadeIn(node, delay, duration);
        
        // Animation de déplacement vers le haut
        TranslateTransition translate = createVerticalTranslation(node, 20, 0, delay, duration);
        
        // Ajouter les animations à la combinaison
        combined.getChildren().addAll(fade, translate);
        
        // Jouer l'animation
        combined.play();
    }
    
    /**
     * Crée un effet de pulse pour un nœud (grossit légèrement puis revient à la taille normale)
     * 
     * @param node Le nœud à animer
     * @param duration La durée de l'animation (ms)
     */
    public static void pulse(Node node, int duration) {
        if (node == null) return;
        
        javafx.animation.ScaleTransition scale = new javafx.animation.ScaleTransition(Duration.millis(duration), node);
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(1.05);
        scale.setToY(1.05);
        scale.setCycleCount(2);
        scale.setAutoReverse(true);
        
        scale.play();
    }
    
    /**
     * Ajoute un effet de survol à un nœud
     * 
     * @param node Le nœud à animer
     */
    public static void addHoverEffect(Node node) {
        if (node == null) return;
        
        // Sauvegarder le style original
        String originalStyle = node.getStyle();
        
        node.setOnMouseEntered(e -> {
            // Créer un effet de survol avec une légère élévation
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(150), node);
            scaleTransition.setToX(1.03);
            scaleTransition.setToY(1.03);
            scaleTransition.play();
            
            // Ajouter un effet d'ombre plus prononcé
            if (node instanceof Region region) {
                region.setStyle(originalStyle + "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.25), 15, 0, 0, 10);");
            }
        });
        
        node.setOnMouseExited(e -> {
            // Revenir à la taille normale
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(150), node);
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
            scaleTransition.play();
            
            // Restaurer le style original
            node.setStyle(originalStyle);
        });
    }
    
    /**
     * Ajoute un effet de clic à un bouton
     * 
     * @param button Le bouton à animer
     */
    public static void addClickEffect(Button button) {
        if (button == null) return;
        
        button.setOnMousePressed(e -> {
            // Effet d'enfoncement
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), button);
            scaleTransition.setToX(0.95);
            scaleTransition.setToY(0.95);
            scaleTransition.play();
        });
        
        button.setOnMouseReleased(e -> {
            // Revenir à la taille normale
            ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(100), button);
            scaleTransition.setToX(1.0);
            scaleTransition.setToY(1.0);
            scaleTransition.play();
        });
    }
    
    /**
     * Ajoute un effet de fondu à la disparition d'un nœud
     * 
     * @param node Le nœud à animer
     * @param durationMillis La durée de l'animation en millisecondes
     */
    public static void fadeOut(Node node, double durationMillis) {
        if (node == null) return;
        
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(durationMillis), node);
        fadeTransition.setFromValue(1);
        fadeTransition.setToValue(0);
        fadeTransition.play();
    }
    
    /**
     * Ajoute un effet d'entrée glissée à un nœud
     * 
     * @param node Le nœud à animer
     * @param fromX Position X de départ
     * @param durationMillis La durée de l'animation en millisecondes
     */
    public static void slideInFromLeft(Node node, double fromX, double durationMillis) {
        if (node == null) return;
        
        node.setOpacity(0);
        node.setTranslateX(fromX);
        
        // Animation de glissement
        TranslateTransition translateTransition = new TranslateTransition(Duration.millis(durationMillis), node);
        translateTransition.setFromX(fromX);
        translateTransition.setToX(0);
        
        // Animation de fondu
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(durationMillis), node);
        fadeTransition.setFromValue(0);
        fadeTransition.setToValue(1);
        
        // Démarrer les animations
        translateTransition.play();
        fadeTransition.play();
    }
    
    /**
     * Crée une transition de fondu entre deux écrans
     * @param oldRoot L'ancien nœud racine
     * @param newRoot Le nouveau nœud racine
     * @param scene La scène dans laquelle effectuer la transition
     */
    public static void fadeTransition(Parent oldRoot, Parent newRoot, Scene scene) {
        // Création d'une transition de fondu sortant
        FadeTransition fadeOut = new FadeTransition(Duration.millis(150), oldRoot);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        
        // Création d'une transition de fondu entrant
        FadeTransition fadeIn = new FadeTransition(Duration.millis(150), newRoot);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        
        // Créer une transition séquentielle
        SequentialTransition st = new SequentialTransition(fadeOut, fadeIn);
        
        // Remplacer la racine après la première transition
        fadeOut.setOnFinished(e -> scene.setRoot(newRoot));
        
        // Lancer la transition
        st.play();
    }
    
    /**
     * Ajoute un effet de déplacement vertical au survol
     * @param node Le nœud auquel ajouter l'effet
     * @param distance La distance de déplacement
     */
    public static void addHoverTranslateEffect(Node node, double distance) {
        node.setOnMouseEntered(e -> {
            TranslateTransition tt = new TranslateTransition(Duration.millis(150), node);
            tt.setToY(-distance);
            tt.play();
        });
        
        node.setOnMouseExited(e -> {
            TranslateTransition tt = new TranslateTransition(Duration.millis(150), node);
            tt.setToY(0);
            tt.play();
        });
    }
} 