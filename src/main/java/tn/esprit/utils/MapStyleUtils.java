package tn.esprit.utils;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.net.URL;

/**
 * Classe utilitaire pour gérer les styles de la carte.
 */
public class MapStyleUtils {
    
    // Modes disponibles
    public enum MapMode {
        LIGHT, DARK
    }
    
    private static MapMode currentMode = MapMode.LIGHT; // Mode par défaut
    
    /**
     * Applique le style clair ou sombre à une scène ou un dialogue.
     * 
     * @param scene La scène à styliser
     * @param mode Le mode à appliquer (LIGHT ou DARK)
     * @return true si le style a été appliqué, false sinon
     */
    public static boolean applyMapStyle(Scene scene, MapMode mode) {
        try {
            // Retirer les anciens styles de carte
            scene.getStylesheets().removeIf(stylesheet -> 
                stylesheet.contains("map_style") || 
                stylesheet.contains("map_style_light") || 
                stylesheet.contains("map_style_dark"));
            
            // Ajouter le nouveau style
            String stylePath;
            if (mode == MapMode.LIGHT) {
                stylePath = "/map_style_light.css";
            } else {
                stylePath = "/map_style_dark.css";
            }
            
            URL styleUrl = MapStyleUtils.class.getResource(stylePath);
            if (styleUrl != null) {
                scene.getStylesheets().add(styleUrl.toExternalForm());
                currentMode = mode;
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Impossible d'appliquer le style de carte: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Crée un bouton pour basculer entre les modes clair et sombre.
     * 
     * @param container Le conteneur parent (scène ou dialogue)
     * @param targetPane Le panneau qui contient la carte
     * @return Le bouton de changement de mode
     */
    public static Button createModeToggleButton(Scene container, Pane targetPane) {
        Button toggleButton = new Button(currentMode == MapMode.LIGHT ? "🌙 Mode sombre" : "☀️ Mode clair");
        toggleButton.getStyleClass().add("mode-toggle");
        
        toggleButton.setOnAction(e -> {
            // Changer le mode
            MapMode newMode = (currentMode == MapMode.LIGHT) ? MapMode.DARK : MapMode.LIGHT;
            
            // Mettre à jour le texte du bouton
            toggleButton.setText(newMode == MapMode.LIGHT ? "🌙 Mode sombre" : "☀️ Mode clair");
            
            // Appliquer le nouveau style
            if (applyMapStyle(container, newMode)) {
                // Mettre à jour l'apparence du panneau de carte
                if (newMode == MapMode.LIGHT) {
                    targetPane.setStyle("-fx-background-color: #ffffff;");
                } else {
                    targetPane.setStyle("-fx-background-color: #121212;");
                }
            }
        });
        
        return toggleButton;
    }
    
    /**
     * Positionne le bouton de changement de mode en haut à droite du panneau spécifié.
     * 
     * @param button Le bouton de changement de mode
     * @param parentPane Le panneau parent
     */
    public static void positionModeButton(Button button, StackPane parentPane) {
        button.setTranslateX(parentPane.getWidth() / 2 - 60);
        button.setTranslateY(-parentPane.getHeight() / 2 + 20);
    }
    
    /**
     * Configure un fond de carte en mode clair
     * 
     * @param background Le rectangle de fond à configurer
     */
    public static void applyLightModeToBackground(Rectangle background) {
        if (background != null) {
            background.setFill(javafx.scene.paint.Color.web("#ffffff"));
            background.getStyleClass().clear();
            background.getStyleClass().add("map-light-mode");
        }
    }
    
    /**
     * Ajoute des éléments visuels à une carte interactive
     * 
     * @param parent Le panneau parent sur lequel ajouter les éléments de la carte
     * @param location Nom du lieu à afficher
     */
    public static void renderDemoMap(Pane parent, String location, LocationUtils.Coordinates coords) {
        // S'assurer que le parent est initialisé
        if (parent == null) return;
        
        // Nettoyer les éléments existants
        parent.getChildren().clear();
        
        // Fond de carte
        Rectangle background = new Rectangle(0, 0, parent.getPrefWidth(), parent.getPrefHeight());
        background.setFill(javafx.scene.paint.Color.web(currentMode == MapMode.LIGHT ? "#ffffff" : "#121212"));
        background.getStyleClass().add(currentMode == MapMode.LIGHT ? "map-light-mode" : "map-dark-mode");
        
        // Routes simplifiées (basées sur les dimensions du parent)
        double centerX = parent.getPrefWidth() / 2;
        double centerY = parent.getPrefHeight() / 2;
        double roadWidth = parent.getPrefWidth() * 0.8;
        double roadHeight = parent.getPrefHeight() * 0.8;
        
        // Route horizontale principale
        Line mainRoad = new Line(centerX - roadWidth/2, centerY, centerX + roadWidth/2, centerY);
        mainRoad.setStroke(javafx.scene.paint.Color.web(currentMode == MapMode.LIGHT ? "#cccccc" : "#505050"));
        mainRoad.setStrokeWidth(15);
        mainRoad.getStyleClass().add("map-road-main");
        
        // Route verticale principale
        Line road1 = new Line(centerX, centerY - roadHeight/2, centerX, centerY + roadHeight/2);
        road1.setStroke(javafx.scene.paint.Color.web(currentMode == MapMode.LIGHT ? "#cccccc" : "#505050"));
        road1.setStrokeWidth(15);
        road1.getStyleClass().add("map-road-main");
        
        // Routes diagonales secondaires
        Line road2 = new Line(centerX - roadWidth/3, centerY - roadHeight/3, centerX + roadWidth/3, centerY + roadHeight/3);
        road2.setStroke(javafx.scene.paint.Color.web(currentMode == MapMode.LIGHT ? "#dddddd" : "#3d3d3d"));
        road2.setStrokeWidth(8);
        road2.getStyleClass().add("map-road");
        
        Line road3 = new Line(centerX - roadWidth/3, centerY + roadHeight/3, centerX + roadWidth/3, centerY - roadHeight/3);
        road3.setStroke(javafx.scene.paint.Color.web(currentMode == MapMode.LIGHT ? "#dddddd" : "#3d3d3d"));
        road3.setStrokeWidth(8);
        road3.getStyleClass().add("map-road");
        
        // Marqueur d'emplacement central
        Circle locationMarker = new Circle(centerX, centerY, 15);
        locationMarker.setFill(javafx.scene.paint.Color.web("#e74c3c"));
        locationMarker.setStroke(javafx.scene.paint.Color.WHITE);
        locationMarker.setStrokeWidth(3);
        locationMarker.getStyleClass().add("event-marker");
        
        // Étiquette pour le lieu
        Label locationLabel = new Label(location);
        locationLabel.setLayoutX(centerX - (location.length() * 3));
        locationLabel.setLayoutY(centerY - 35);
        locationLabel.setStyle("-fx-background-color: white; -fx-padding: 5 10; -fx-background-radius: 5; " +
                            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 3, 0, 0, 1);");
        
        // Ajouter des points d'intérêt (POIs) répartis autour du marqueur central
        Circle poi1Circle = new Circle(10);
        poi1Circle.setFill(javafx.scene.paint.Color.web("#3498db"));
        poi1Circle.setStroke(javafx.scene.paint.Color.WHITE);
        poi1Circle.setStrokeWidth(1.5);
        
        Label poi1Symbol = new Label("P");
        poi1Symbol.setTextFill(javafx.scene.paint.Color.WHITE);
        poi1Symbol.setStyle("-fx-font-weight: bold;");
        
        StackPane poi1Stack = new StackPane();
        poi1Stack.getChildren().addAll(poi1Circle, poi1Symbol);
        poi1Stack.setLayoutX(centerX + roadWidth/4);
        poi1Stack.setLayoutY(centerY + roadHeight/4);
        
        Label poi1Label = new Label("Parking");
        poi1Label.setStyle("-fx-background-color: white; -fx-padding: 2 4; -fx-background-radius: 2; -fx-font-size: 10;");
        
        VBox poi1 = new VBox(2);
        poi1.setLayoutX(centerX + roadWidth/4);
        poi1.setLayoutY(centerY + roadHeight/4);
        poi1.setAlignment(javafx.geometry.Pos.CENTER);
        poi1.getChildren().addAll(poi1Stack, poi1Label);
        
        // Ajouter d'autres POIs similaires
        Circle poi2Circle = new Circle(10);
        poi2Circle.setFill(javafx.scene.paint.Color.web("#9b59b6"));
        poi2Circle.setStroke(javafx.scene.paint.Color.WHITE);
        poi2Circle.setStrokeWidth(1.5);
        
        Label poi2Symbol = new Label("M");
        poi2Symbol.setTextFill(javafx.scene.paint.Color.WHITE);
        poi2Symbol.setStyle("-fx-font-weight: bold;");
        
        StackPane poi2Stack = new StackPane();
        poi2Stack.getChildren().addAll(poi2Circle, poi2Symbol);
        
        Label poi2Label = new Label("Metro");
        poi2Label.setStyle("-fx-background-color: white; -fx-padding: 2 4; -fx-background-radius: 2; -fx-font-size: 10;");
        
        VBox poi2 = new VBox(2);
        poi2.setLayoutX(centerX - roadWidth/4);
        poi2.setLayoutY(centerY - roadHeight/4);
        poi2.setAlignment(javafx.geometry.Pos.CENTER);
        poi2.getChildren().addAll(poi2Stack, poi2Label);
        
        // Ajouter tous les éléments à la carte
        parent.getChildren().addAll(
            background,
            mainRoad, road1, road2, road3,
            locationMarker, locationLabel,
            poi1, poi2
        );
        
        // Ajouter une légende
        VBox legendBox = new VBox(10);
        legendBox.getStyleClass().add("legend-container");
        legendBox.setLayoutX(20);
        legendBox.setLayoutY(20);
        
        Label legendTitle = new Label("Légende");
        legendTitle.getStyleClass().add("legend-title");
        
        HBox eventLegend = new HBox(10);
        eventLegend.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Circle eventSample = new Circle(6, javafx.scene.paint.Color.web("#e74c3c"));
        eventSample.setStroke(javafx.scene.paint.Color.WHITE);
        eventSample.setStrokeWidth(1.5);
        Label eventLabel = new Label("Lieu de l'événement");
        eventLegend.getChildren().addAll(eventSample, eventLabel);
        
        HBox parkingLegend = new HBox(10);
        parkingLegend.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Circle parkingSample = new Circle(6, javafx.scene.paint.Color.web("#3498db"));
        Label parkingItemLabel = new Label("Parking");
        parkingLegend.getChildren().addAll(parkingSample, parkingItemLabel);
        
        HBox metroLegend = new HBox(10);
        metroLegend.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        Circle metroSample = new Circle(6, javafx.scene.paint.Color.web("#9b59b6"));
        Label metroItemLabel = new Label("Station de métro");
        metroLegend.getChildren().addAll(metroSample, metroItemLabel);
        
        legendBox.getChildren().addAll(legendTitle, eventLegend, parkingLegend, metroLegend);
        
        // Ajouter les coordonnées en bas de la carte
        Label coordsLabel = new Label("Lat: " + coords.getLatitude() + ", Long: " + coords.getLongitude());
        coordsLabel.getStyleClass().add("coords-display");
        coordsLabel.setLayoutX(centerX - 80);  
        coordsLabel.setLayoutY(parent.getPrefHeight() - 40);
        
        parent.getChildren().addAll(legendBox, coordsLabel);
    }
} 