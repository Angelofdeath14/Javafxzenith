package Utils;

import javafx.scene.Scene;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.Pane;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.DialogPane;
import javafx.geometry.Insets;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.util.Duration;
import javafx.scene.control.Tooltip;
import java.lang.reflect.Field;
import javafx.scene.control.ScrollPane;

/**
 * Classe utilitaire pour appliquer des styles et des corrections
 * aux éléments JavaFX dans l'application Artphoria
 */
public class MainStyleFixer {
    
    // Couleurs principales de l'application
    private static final String PRIMARY_COLOR = "#3498db";
    private static final String SECONDARY_COLOR = "#2ecc71";
    private static final String ACCENT_COLOR = "#e74c3c";
    private static final String TEXT_COLOR = "#2c3e50";
    private static final String BACKGROUND_COLOR = "#f8f9fa";
    private static final String CARD_BACKGROUND = "white";
    
    /**
     * Initialise les correctifs de style pour l'application
     */
    public static void initialize() {
        System.out.println("Initialisation des styles professionnels pour Artphoria");
    }
    
    /**
     * Applique le style professionnel à une scène entière
     * 
     * @param scene La scène à styliser
     */
    public static void applyProfessionalStyle(Scene scene) {
        if (scene == null) return;
        
        try {
            // Appliquer le style CSS global
            java.net.URL cssUrl = MainStyleFixer.class.getResource("/professional_style.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }
            
            // Définir une taille plus grande pour la fenêtre
            Stage stage = (Stage) scene.getWindow();
            if (stage != null) {
                stage.setWidth(1280);
                stage.setHeight(720);
                stage.setMinWidth(800);
                stage.setMinHeight(600);
                stage.setMaxWidth(1920);
                stage.setMaxHeight(1080);
            }
            
            // Parcourir tous les nœuds pour appliquer les styles
            if (scene.getRoot() != null) {
                hideAllScrollBars(scene.getRoot());
                applyStyleToNode(scene.getRoot());
            }
            
            System.out.println("Style professionnel appliqué à la scène");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'application du style professionnel : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Masque toutes les barres de défilement dans les ScrollPane
     */
    private static void hideAllScrollBars(Node node) {
        if (node instanceof ScrollPane scrollPane) {
            scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scrollPane.setPadding(new Insets(0));
            scrollPane.setFitToWidth(true);
            scrollPane.getStyleClass().add("transparent-scroll-pane");
        }
        
        if (node instanceof ListView<?> listView) {
            listView.setPadding(new Insets(0));
        }
        
        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                hideAllScrollBars(child);
            }
        }
    }
    
    /**
     * Applique le style clair par défaut à une scène
     * 
     * @param scene La scène à styliser
     */
    public static void applyLightModeToScene(Scene scene) {
        if (scene == null) return;
        
        try {
            // Charger le style clair
            java.net.URL cssUrl = MainStyleFixer.class.getResource("/map_style_light.css");
            if (cssUrl != null) {
                scene.getStylesheets().removeIf(stylesheet -> 
                    stylesheet.contains("map_style"));
                scene.getStylesheets().add(cssUrl.toExternalForm());
            }
            
            // Parcourir tous les enfants pour trouver les rectangles de fond
            if (scene.getRoot() != null) {
                applyLightModeToNode(scene.getRoot());
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'application du style clair : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Parcourt récursivement un nœud et ses enfants pour appliquer le style clair
     * 
     * @param node Le nœud à traiter
     */
    private static void applyLightModeToNode(Node node) {
        // Si c'est un rectangle, vérifier s'il s'agit d'un fond de carte
        if (node instanceof Rectangle rectangle) {
            if (rectangle.getWidth() >= 500 && rectangle.getHeight() >= 400) {
                MapStyleUtils.applyLightModeToBackground(rectangle);
            }
        }
        
        // Si c'est un conteneur, parcourir ses enfants
        if (node instanceof Parent parent) {
            for (Node child : parent.getChildrenUnmodifiable()) {
                applyLightModeToNode(child);
            }
        }
    }
    
    /**
     * Applique le style professionnel à un nœud et ses enfants
     * 
     * @param node Le nœud à styliser
     */
    private static void applyStyleToNode(Node node) {
        if (node == null) return;
        
        try {
            // Appliquer des styles spécifiques selon le type de nœud
            if (node instanceof Button button) {
                styleButton(button);
            } else if (node instanceof Label label) {
                styleLabel(label);
            } else if (node instanceof TextField textField) {
                styleTextField(textField);
            } else if (node instanceof ComboBox<?> comboBox) {
                styleComboBox(comboBox);
            } else if (node instanceof VBox vbox) {
                styleVBox(vbox);
            } else if (node instanceof HBox hbox) {
                styleHBox(hbox);
            } else if (node instanceof Region region) {
                // Ajouter une marge par défaut pour les régions
                if (region.getPadding() == null || region.getPadding() == Insets.EMPTY) {
                    region.setPadding(new Insets(10));
                }
            }
            
            // Traiter récursivement les enfants si c'est un parent
            if (node instanceof Parent parent) {
                for (Node child : parent.getChildrenUnmodifiable()) {
                    applyStyleToNode(child);
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'application du style au nœud : " + e.getMessage());
        }
    }
    
    /**
     * Stylise un bouton selon les standards de l'application
     * 
     * @param button Le bouton à styliser
     */
    public static void styleButton(Button button) {
        if (button == null) return;
        
        String currentStyle = button.getStyle();
        
        // Ne pas modifier les boutons déjà stylisés
        if (currentStyle != null && (
            currentStyle.contains("-fx-background-color:") ||
            currentStyle.contains("-fx-font-weight: bold"))) {
            return;
        }
        
        button.setStyle(
            "-fx-background-color: " + PRIMARY_COLOR + "; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 5; " +
            "-fx-padding: 8 15; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 3, 0, 0, 3);"
        );
    }
    
    /**
     * Stylise une étiquette selon les standards de l'application
     * 
     * @param label L'étiquette à styliser
     */
    public static void styleLabel(Label label) {
        if (label == null) return;
        
        String currentStyle = label.getStyle();
        
        // Ne pas modifier les étiquettes déjà stylisées
        if (currentStyle != null && (
            currentStyle.contains("-fx-font-weight:") ||
            currentStyle.contains("-fx-text-fill:"))) {
            return;
        }
        
        label.setStyle("-fx-text-fill: " + TEXT_COLOR + ";");
    }
    
    /**
     * Stylise un champ de texte selon les standards de l'application
     * 
     * @param textField Le champ de texte à styliser
     */
    public static void styleTextField(TextField textField) {
        if (textField == null) return;
        
        textField.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #e0e0e0; " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 3; " +
            "-fx-padding: 8;"
        );
    }
    
    /**
     * Stylise une liste déroulante selon les standards de l'application
     * 
     * @param comboBox La liste déroulante à styliser
     */
    public static void styleComboBox(ComboBox<?> comboBox) {
        if (comboBox == null) return;
        
        comboBox.setStyle(
            "-fx-background-color: white; " +
            "-fx-border-color: #e0e0e0; " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 3; " +
            "-fx-padding: 5;"
        );
    }
    
    /**
     * Stylise un conteneur VBox selon les standards de l'application
     * 
     * @param vbox Le conteneur à styliser
     */
    public static void styleVBox(VBox vbox) {
        if (vbox == null) return;
        
        // Vérifier si le VBox est déjà stylisé comme une carte
        if (vbox.getStyle() != null && vbox.getStyle().contains("-fx-background-radius: 10")) {
            return;
        }
        
        // Ajouter un espacement par défaut
        if (vbox.getSpacing() == 0) {
            vbox.setSpacing(10);
        }
        
        // Ajouter un padding par défaut
        if (vbox.getPadding() == null || vbox.getPadding() == Insets.EMPTY) {
            vbox.setPadding(new Insets(15));
        }
    }
    
    /**
     * Stylise un conteneur HBox selon les standards de l'application
     * 
     * @param hbox Le conteneur à styliser
     */
    public static void styleHBox(HBox hbox) {
        if (hbox == null) return;
        
        // Ajouter un espacement par défaut
        if (hbox.getSpacing() == 0) {
            hbox.setSpacing(10);
        }
        
        // Ajouter un padding par défaut
        if (hbox.getPadding() == null || hbox.getPadding() == Insets.EMPTY) {
            hbox.setPadding(new Insets(5));
        }
    }
    
    /**
     * Stylise une carte d'événement pour un affichage professionnel et moderne
     * 
     * @param eventCard La carte d'événement à styliser
     */
    public static void styleEventCard(VBox eventCard) {
        if (eventCard == null) return;
        
        eventCard.setStyle(
            "-fx-background-color: " + CARD_BACKGROUND + "; " +
            "-fx-background-radius: 10; " +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 10, 0, 0, 6); " +
            "-fx-border-color: #e0e0e0; " +
            "-fx-border-width: 1; " +
            "-fx-border-radius: 10;"
        );
    }
    
    /**
     * Applique le style clair à un dialogue contenant une carte
     * 
     * @param dialogPane Le panneau de dialogue à modifier
     */
    public static void applyLightModeToDialog(DialogPane dialogPane) {
        if (dialogPane == null) return;
        
        try {
            // Charger le style clair
            java.net.URL cssUrl = MainStyleFixer.class.getResource("/map_style_light.css");
            if (cssUrl != null) {
                dialogPane.getStylesheets().removeIf(stylesheet -> 
                    stylesheet.contains("map_style"));
                dialogPane.getStylesheets().add(cssUrl.toExternalForm());
            }
            
            // Parcourir tous les enfants pour trouver les rectangles de fond
            applyLightModeToNode(dialogPane);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'application du style clair au dialogue : " + e.getMessage());
        }
    }
    
    /**
     * Applique un style professionnel à un dialogue
     * 
     * @param dialogPane Le panneau de dialogue à modifier
     */
    public static void styleProfessionalDialog(DialogPane dialogPane) {
        if (dialogPane == null) return;
        
        // Appliquer la feuille de style professionnelle
        dialogPane.getStylesheets().add(MainStyleFixer.class.getResource("/professional_style.css").toExternalForm());
        
        // Améliorer l'apparence des boutons
        dialogPane.getButtonTypes().forEach(buttonType -> {
            Button button = (Button) dialogPane.lookupButton(buttonType);
            if (button != null) {
                // Ajouter une classe selon le type de bouton
                switch (buttonType.getButtonData()) {
                    case OK_DONE:
                    case APPLY:
                    case FINISH:
                    case YES:
                        button.getStyleClass().add("button");
                        break;
                    case CANCEL_CLOSE:
                    case NO:
                        button.getStyleClass().add("button");
                        button.getStyleClass().add("secondary");
                        break;
                    default:
                        button.getStyleClass().add("button");
                        break;
                }
            }
        });
        
        // Améliorer le header et le contenu
        if (dialogPane.getHeader() != null && dialogPane.getHeader() instanceof Label) {
            Label headerLabel = (Label) dialogPane.getHeader();
            headerLabel.getStyleClass().add("title");
        }
        
        if (dialogPane.getContent() != null && dialogPane.getContent() instanceof Label) {
            Label contentLabel = (Label) dialogPane.getContent();
            contentLabel.getStyleClass().add("text");
            contentLabel.setWrapText(true);
        }
    }
    
    /**
     * Applique un style professionnel à un Stage (fenêtre)
     * 
     * @param stage Le Stage à styliser
     */
    public static void styleProfessionalStage(Stage stage) {
        if (stage == null) return;
        
        try {
            // Ajuster la taille et les limites pour une interface plus grande
            stage.setWidth(1280);
            stage.setHeight(720);
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.setMaxWidth(1920);
            stage.setMaxHeight(1080);
            
            // Si c'est une fenêtre de détails, la rendre légèrement plus petite
            String title = stage.getTitle();
            if (title != null && (title.contains("Détails") || title.contains("Detail"))) {
                stage.setWidth(1024);
                stage.setHeight(680);
                stage.setMinWidth(800);
                stage.setMinHeight(600);
                stage.setMaxWidth(1280);
                stage.setMaxHeight(720);
            }
            
            // Styliser la scène
            Scene scene = stage.getScene();
            if (scene != null) {
                hideAllScrollBars(scene.getRoot());
                applyStyleToNode(scene.getRoot());
            }
            
            System.out.println("Style professionnel appliqué au stage: " + (title != null ? title : "Inconnu"));
        } catch (Exception e) {
            System.err.println("Erreur lors de l'application du style à la fenêtre: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Améliore l'apparence d'une infobulle spécifique
     * 
     * @param tooltip L'infobulle à améliorer
     */
    public static void enhanceTooltip(Tooltip tooltip) {
        if (tooltip == null) return;
        
        // Réduire le délai avant l'affichage
        tooltip.setShowDelay(Duration.millis(300));
        
        // Prolonger la durée d'affichage
        tooltip.setShowDuration(Duration.seconds(20));
        
        // Délai avant de pouvoir afficher une autre infobulle
        tooltip.setHideDelay(Duration.millis(200));
        
        // Appliquer des styles CSS
        tooltip.setStyle("-fx-font-size: 12px; -fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 5;");
    }
    
    /**
     * Applique la feuille de style professionnelle et les animations
     * à un Parent (racine d'une scène ou d'un composant)
     *
     * @param root Le nœud parent à styliser
     */
    public static void applyProfessionalStyle(Parent root) {
        if (root == null) return;
        
        // Appliquer la feuille de style professionnelle
        root.getStylesheets().add(MainStyleFixer.class.getResource("/professional_style.css").toExternalForm());
        
        // Ajouter un effet d'entrée
        if (root instanceof VBox || root instanceof javafx.scene.layout.HBox) {
            // Animation d'apparition progressive
            AnimationUtils.fadeIn(root, 0, 500);
        }
    }
    
    /**
     * Ajoute des ombres portées et des effets de surbrillance aux boutons
     * 
     * @param parent Le nœud parent contenant les boutons
     */
    public static void enhanceButtons(Parent parent) {
        if (parent == null) return;
        
        // Trouver tous les boutons dans la hiérarchie
        parent.lookupAll(".button").forEach(node -> {
            if (node instanceof Button) {
                Button button = (Button) node;
                
                // Ajouter un effet de survol avec une transition fluide
                button.setOnMouseEntered(e -> {
                    button.setEffect(new javafx.scene.effect.DropShadow(10, 0, 2, 
                        javafx.scene.paint.Color.rgb(52, 152, 219, 0.5)));
                    button.setScaleX(1.03);
                    button.setScaleY(1.03);
                });
                
                button.setOnMouseExited(e -> {
                    button.setEffect(null);
                    button.setScaleX(1.0);
                    button.setScaleY(1.0);
                });
                
                // Ajouter une infobulle améliorée si nécessaire
                if (button.getTooltip() != null) {
                    enhanceTooltip(button.getTooltip());
                }
            }
        });
    }
    
    /**
     * Applique le mode plein écran à la fenêtre
     * @param stage La fenêtre à mettre en plein écran
     */
    public static void applyFullScreenMode(javafx.stage.Stage stage) {
        // Définir la taille pour occuper tout l'écran
        javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
        stage.setX(screenBounds.getMinX());
        stage.setY(screenBounds.getMinY());
        stage.setWidth(screenBounds.getWidth());
        stage.setHeight(screenBounds.getHeight());
        
        // Utiliser la maximisation plutôt que le mode plein écran
        // qui peut causer des problèmes avec certaines interfaces
        stage.setMaximized(true);
        
        // S'assurer que la fenêtre n'est pas en fullScreen qui masque la barre de titre
        stage.setFullScreen(false);
        
        // S'assurer que la décoration de fenêtre est présente
        stage.initStyle(javafx.stage.StageStyle.DECORATED);
        
        // S'assurer que la fenêtre est redimensionnable
        stage.setResizable(true);
    }
} 