package tn.esprit.utils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

/**
 * Classe utilitaire pour créer des statistiques et des visualisations
 * professionnelles pour l'application Artphoria
 */
public class StatsUtils {

    /**
     * Classe pour stocker les données d'un graphique circulaire
     */
    public static class PieChartData {
        private final String label;
        private final int value;

        public PieChartData(String label, int value) {
            this.label = label;
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public int getValue() {
            return value;
        }
    }

    /**
     * Classe pour stocker les données d'un graphique à barres
     */
    public static class BarChartData {
        private final String category;
        private final int value;

        public BarChartData(String category, int value) {
            this.category = category;
            this.value = value;
        }

        public String getCategory() {
            return category;
        }

        public int getValue() {
            return value;
        }
    }
    
    /**
     * Classe pour stocker les données de tendance
     */
    public static class TrendData {
        private final String date;
        private final int value;
        private final int total;

        public TrendData(String date, int value, int total) {
            this.date = date;
            this.value = value;
            this.total = total;
        }

        public String getDate() {
            return date;
        }

        public int getValue() {
            return value;
        }
        
        public int getTotal() {
            return total;
        }
    }

    /**
     * Crée une carte de statistique simple
     * 
     * @param title Le titre de la statistique
     * @param value La valeur à afficher
     * @param valueClass Classe CSS pour le style de la valeur (success, warning, danger ou null)
     * @return Un nœud contenant la carte de statistique
     */
    public static VBox createStatCard(String title, String value, String valueClass) {
        VBox card = new VBox(10);
        card.getStyleClass().add("stats-card");
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(15));
        
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("stats-title");
        titleLabel.setWrapText(true);
        titleLabel.setAlignment(Pos.CENTER);
        
        Label valueLabel = new Label(value);
        if (valueClass != null && !valueClass.isEmpty()) {
            valueLabel.getStyleClass().add("stats-value-" + valueClass);
        } else {
            valueLabel.getStyleClass().add("stats-value");
        }
        
        card.getChildren().addAll(titleLabel, valueLabel);
        return card;
    }
    
    /**
     * Crée une carte de statistique avec un graphique en pourcentage, design amélioré
     * 
     * @param title Le titre de la statistique
     * @param value La valeur à afficher
     * @param percentage Le pourcentage (0-100)
     * @return Un nœud contenant la carte de statistique avec graphique
     */
    public static VBox createPercentageCard(String title, String value, double percentage) {
        VBox card = new VBox(15);
        card.getStyleClass().add("stats-card");
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(25));
        card.setMinHeight(220);
        
        // En-tête avec le titre
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("stats-title");
        titleLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: #2b2d42; -fx-alignment: center;");
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        titleLabel.setAlignment(Pos.CENTER);
        
        // Valeur sous forme fraction
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #8d99ae; -fx-alignment: center;");
        valueLabel.setMaxWidth(Double.MAX_VALUE);
        valueLabel.setAlignment(Pos.CENTER);
        
        // Grand pourcentage au centre avec un cercle de progression
        StackPane circleProgressPane = new StackPane();
        circleProgressPane.setMinHeight(120);
        circleProgressPane.setMaxHeight(120);
        circleProgressPane.setMinWidth(120);
        circleProgressPane.setMaxWidth(120);
        
        // Cercle extérieur (fond)
        Circle outerCircle = new Circle(55);
        outerCircle.setFill(Color.web("#f8f9fa"));
        
        // Déterminer la couleur en fonction du pourcentage
        String fillColor;
        if (percentage >= 70) {
            fillColor = "#06d6a0"; // vert succès
        } else if (percentage >= 40) {
            fillColor = "#ffd166"; // orange avertissement
        } else {
            fillColor = "#ef476f"; // rouge danger
        }
        
        // Arc de progression (partie remplie)
        double startAngle = 90; // Commence en haut
        double arcLength = 360 * (percentage / 100.0);
        
        javafx.scene.shape.Arc progressArc = new javafx.scene.shape.Arc(0, 0, 50, 50, startAngle, arcLength);
        progressArc.setType(javafx.scene.shape.ArcType.ROUND);
        progressArc.setFill(Color.TRANSPARENT);
        progressArc.setStroke(Color.web(fillColor));
        progressArc.setStrokeWidth(10);
        progressArc.setStrokeLineCap(javafx.scene.shape.StrokeLineCap.ROUND);
        
        // Cercle intérieur (pour créer un effet donut)
        Circle innerCircle = new Circle(45);
        innerCircle.setFill(Color.WHITE);
        
        // Texte du pourcentage
        Label percentLabel = new Label(String.format("%.1f%%", percentage));
        percentLabel.setStyle("-fx-font-size: 26px; -fx-font-weight: bold;");
        percentLabel.setTextFill(Color.web(fillColor));
        
        // Ajouter tous les éléments au StackPane (cercle de progression)
        circleProgressPane.getChildren().addAll(outerCircle, progressArc, innerCircle, percentLabel);
        
        // Animation du cercle de progression
        javafx.animation.RotateTransition rotateTransition = new javafx.animation.RotateTransition(javafx.util.Duration.millis(1000), progressArc);
        rotateTransition.setFromAngle(-90);
        rotateTransition.setToAngle(-90 + arcLength);
        rotateTransition.setInterpolator(javafx.animation.Interpolator.EASE_OUT);
        rotateTransition.play();
        
        // Espace pour centrer verticalement le contenu
        Region spacerTop = new Region();
        spacerTop.setMinHeight(5);
        VBox.setVgrow(spacerTop, Priority.ALWAYS);
        
        Region spacerBottom = new Region();
        spacerBottom.setMinHeight(5);
        VBox.setVgrow(spacerBottom, Priority.ALWAYS);
        
        // Assembler la carte avec spacing vertical
        card.getChildren().addAll(titleLabel, spacerTop, circleProgressPane, valueLabel, spacerBottom);
        
        return card;
    }
    
    /**
     * Crée une carte de statistique avancée avec une comparaison et une tendance - design amélioré
     * 
     * @param title Le titre de la statistique
     * @param value La valeur actuelle
     * @param previousValue La valeur précédente pour comparaison
     * @param subtitle Texte descriptif supplémentaire (période ou contexte)
     * @param icon Code SVG pour l'icône (peut être null)
     * @return Un nœud contenant la carte de statistique avancée
     */
    public static VBox createAdvancedStatCard(String title, double value, double previousValue, String subtitle, String icon) {
        VBox card = new VBox(10);
        card.getStyleClass().add("stats-card");
        card.setPadding(new Insets(20));
        card.setMinWidth(280);
        card.setMinHeight(180);
        
        // En-tête avec titre et icône
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        
        // Icône SVG si disponible
        if (icon != null && !icon.isEmpty()) {
            StackPane iconContainer = new StackPane();
            iconContainer.setMinSize(32, 32);
            iconContainer.setMaxSize(32, 32);
            
            // Cercle de fond pour l'icône
            Circle iconBackground = new Circle(16);
            iconBackground.setFill(Color.web("#3498db", 0.1));
            
            // Créer l'icône SVG
            SVGPath iconPath = new SVGPath();
            iconPath.setContent(icon);
            iconPath.setFill(Color.web("#3498db"));
            iconPath.setScaleX(0.7);
            iconPath.setScaleY(0.7);
            
            iconContainer.getChildren().addAll(iconBackground, iconPath);
            header.getChildren().add(iconContainer);
        }
        
        // Titre
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("stats-title");
        titleLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: #2b2d42;");
        header.getChildren().add(titleLabel);
        
        // Espace flexible pour la distribution verticale
        Region spacer = new Region();
        spacer.setMinHeight(15);
        VBox.setVgrow(spacer, Priority.ALWAYS);
        
        // Valeur principale en grand, avec animation de comptage
        Label valueLabel = new Label("0");
        valueLabel.setStyle("-fx-font-size: 48px; -fx-font-weight: bold; -fx-text-fill: #2b2d42;");
        
        // Animation de comptage
        javafx.animation.Timeline countAnimation = new javafx.animation.Timeline();
        javafx.animation.KeyFrame keyFrame = new javafx.animation.KeyFrame(
            javafx.util.Duration.millis(1500),
            event -> valueLabel.setText(String.format("%.0f", value)),
            new javafx.animation.KeyValue(new javafx.beans.property.SimpleDoubleProperty(0), value, 
                javafx.animation.Interpolator.EASE_OUT)
        );
        countAnimation.getKeyFrames().add(keyFrame);
        countAnimation.play();
        
        // Calcul du pourcentage de variation
        double changePercent = previousValue > 0 ? ((value - previousValue) / previousValue) * 100 : 0;
        
        // Étiquette de variation avec icône
        HBox changeBox = new HBox(5);
        changeBox.setAlignment(Pos.CENTER_LEFT);
        
        // Fond coloré pour le pourcentage
        StackPane percentBackground = new StackPane();
        Rectangle rect = new Rectangle(70, 26);
        rect.setArcWidth(13);
        rect.setArcHeight(13);
        
        Label changeIcon = new Label();
        String trendText;
        
        // Style basé sur la tendance
        String textColor;
        if (changePercent > 0) {
            changeIcon.setText("▲ ");
            textColor = "#06d6a0"; // vert
            rect.setFill(Color.web("#06d6a0", 0.15));
            trendText = String.format("+%.1f%%", changePercent);
        } else if (changePercent < 0) {
            changeIcon.setText("▼ ");
            textColor = "#ef476f"; // rouge
            rect.setFill(Color.web("#ef476f", 0.15));
            trendText = String.format("%.1f%%", changePercent);
        } else {
            changeIcon.setText("■ ");
            textColor = "#8d99ae"; // gris
            rect.setFill(Color.web("#8d99ae", 0.15));
            trendText = "0.0%";
        }
        
        changeIcon.setTextFill(Color.web(textColor));
        
        Label changeLabel = new Label(trendText);
        changeLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");
        changeLabel.setTextFill(Color.web(textColor));
        
        StackPane trendBadge = new StackPane();
        trendBadge.getChildren().addAll(rect, new HBox(changeIcon, changeLabel));
        
        // Description sous la variation
        Label subtitleLabel = new Label(subtitle);
        subtitleLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #8d99ae;");
        
        // Ajouter les éléments à la carte
        card.getChildren().addAll(header, spacer, valueLabel, trendBadge, subtitleLabel);
        
        return card;
    }
    
    /**
     * Crée un graphique circulaire simple pour la visualisation des données
     * 
     * @param title Le titre du graphique
     * @param data Les données au format [{nom, valeur}, ...]
     * @return Un nœud contenant le graphique
     */
    public static VBox createSimplePieChart(String title, List<PieChartData> data) {
        VBox container = new VBox(15);
        container.getStyleClass().add("stats-card");
        container.setPadding(new Insets(15));
        
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("stats-title");
        
        PieChart pieChart = new PieChart();
        pieChart.setLabelsVisible(true);
        pieChart.setLegendVisible(true);
        
        // Ajouter les données
        for (PieChartData item : data) {
            PieChart.Data slice = new PieChart.Data(item.getLabel(), item.getValue());
            pieChart.getData().add(slice);
        }
        
        // Styliser le graphique
        pieChart.setStyle("-fx-pie-label-visible: true; -fx-pie-label-radius: 0.75; -fx-pie-label-line-length: 10;");
        
        // Animation d'apparition
        pieChart.setAnimated(true);
        pieChart.setStartAngle(90);
        
        // Ajouter des tooltips améliorés
        pieChart.getData().forEach(data1 -> {
            Node node = data1.getNode();
            String tooltipText = String.format("%s: %.1f", data1.getName(), data1.getPieValue());
            javafx.scene.control.Tooltip tooltip = new javafx.scene.control.Tooltip(tooltipText);
            javafx.scene.control.Tooltip.install(node, tooltip);
            
            // Ajouter un effet d'hover
            node.setOnMouseEntered(event -> node.setScaleX(1.1));
            node.setOnMouseEntered(event -> node.setScaleY(1.1));
            node.setOnMouseExited(event -> node.setScaleX(1.0));
            node.setOnMouseExited(event -> node.setScaleY(1.0));
        });
        
        container.getChildren().addAll(titleLabel, pieChart);
        return container;
    }
    
    /**
     * Crée un histogramme pour la visualisation des données
     * 
     * @param title Le titre du graphique
     * @param xAxisLabel Libellé de l'axe X
     * @param yAxisLabel Libellé de l'axe Y
     * @param data Les données au format [{catégorie, valeur}, ...]
     * @return Un nœud contenant le graphique
     */
    public static VBox createBarChart(String title, String xAxisLabel, String yAxisLabel, List<BarChartData> data) {
        VBox container = new VBox(15);
        container.getStyleClass().add("stats-card");
        container.setPadding(new Insets(15));
        
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("stats-title");
        
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel(xAxisLabel);
        
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(yAxisLabel);
        
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("");
        barChart.setLegendVisible(false);
        barChart.setBarGap(8);
        barChart.setCategoryGap(20);
        
        // Créer la série
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        
        // Ajouter les données
        for (BarChartData item : data) {
            series.getData().add(new XYChart.Data<>(item.getCategory(), item.getValue()));
        }
        
        barChart.getData().add(series);
        barChart.setAnimated(true);
        
        // Ajouter des étiquettes de valeur sur les barres
        series.getData().forEach(data1 -> {
            Node node = data1.getNode();
            
            // Créer une étiquette de valeur
            Label valueLabel = new Label(String.format("%.0f", data1.getYValue()));
            valueLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: white;");
            
            // Placer l'étiquette au-dessus de la barre
            StackPane.setAlignment(valueLabel, Pos.TOP_CENTER);
            ((StackPane) node).getChildren().add(valueLabel);
            
            // Ajouter des effets de survol
            node.setOnMouseEntered(event -> {
                node.setStyle("-fx-background-color: #ff9800;"); // Orange hover
                valueLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: white;");
            });
            node.setOnMouseExited(event -> {
                node.setStyle(null);
                valueLabel.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: white;");
            });
        });
        
        container.getChildren().addAll(titleLabel, barChart);
        return container;
    }
    
    /**
     * Crée un en-tête pour la section des statistiques
     * 
     * @param title Le titre de la section
     * @return Un nœud contenant l'en-tête
     */
    public static HBox createStatsHeader(String title) {
        HBox header = new HBox();
        header.getStyleClass().add("stats-header");
        header.setPadding(new Insets(15, 20, 15, 20));
        header.setAlignment(Pos.CENTER_LEFT);
        
        Label titleLabel = new Label(title);
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: white;");
        
        // Espace flexible
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        // Date
        Label dateLabel = new Label("Période : " + java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        dateLabel.getStyleClass().add("date-range");
        
        header.getChildren().addAll(titleLabel, spacer, dateLabel);
        return header;
    }
    
    /**
     * Crée une carte avec un mini graphique de tendance (sparkline) - design amélioré
     * 
     * @param title Le titre de la statistique
     * @param currentValue La valeur actuelle
     * @param trendData Les données historiques pour le graphique (max 5-7 valeurs recommandées)
     * @param trendLabel Description de la tendance
     * @param positiveIsBetter Si true, une tendance à la hausse est considérée comme positive
     * @return Un nœud contenant la carte avec le graphique de tendance
     */
    public static VBox createTrendCard(String title, double currentValue, List<Double> trendData, String trendLabel, boolean positiveIsBetter) {
        VBox card = new VBox(15);
        card.getStyleClass().add("stats-card");
        card.setPadding(new Insets(25));
        card.setMinWidth(300);
        card.setMinHeight(220);
        
        // Titre
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("stats-title");
        titleLabel.setStyle("-fx-font-size: 15px; -fx-text-fill: #2b2d42; -fx-alignment: center;");
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        titleLabel.setAlignment(Pos.CENTER);
        
        // Valeur actuelle en grand
        Label valueLabel = new Label("0");
        valueLabel.setStyle("-fx-font-size: 52px; -fx-font-weight: bold; -fx-text-fill: #2b2d42;");
        valueLabel.setMaxWidth(Double.MAX_VALUE);
        valueLabel.setAlignment(Pos.CENTER);
        
        // Animation de comptage
        javafx.animation.Timeline countAnimation = new javafx.animation.Timeline();
        javafx.animation.KeyFrame keyFrame = new javafx.animation.KeyFrame(
            javafx.util.Duration.millis(1500),
            event -> valueLabel.setText(String.format("%.1f", currentValue)),
            new javafx.animation.KeyValue(new javafx.beans.property.SimpleDoubleProperty(0), currentValue, 
                javafx.animation.Interpolator.EASE_OUT)
        );
        countAnimation.getKeyFrames().add(keyFrame);
        countAnimation.play();
        
        // Créer un graphique en ligne avec effet d'ombre et dégradé
        VBox chartContainer = new VBox();
        chartContainer.setMinHeight(80);
        chartContainer.setMaxHeight(80);
        
        // Déterminer les valeurs min et max pour l'échelle
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for (Double value : trendData) {
            min = Math.min(min, value);
            max = Math.max(max, value);
        }
        min = Math.max(0, min * 0.9); // Garantir que min ne soit pas négatif et ajouter une marge
        max = max * 1.1; // Ajouter une marge en haut
        
        // Écart entre les valeurs min et max
        double range = max - min;
        if (range == 0) range = 1; // Éviter la division par zéro
        
        // Créer un graphique en ligne personnalisé
        javafx.scene.shape.Polyline sparkline = new javafx.scene.shape.Polyline();
        sparkline.setStroke(Color.web("#3498db"));
        sparkline.setStrokeWidth(2.5);
        sparkline.setStrokeLineJoin(javafx.scene.shape.StrokeLineJoin.ROUND);
        
        // Ajouter un effet d'ombre
        sparkline.setEffect(new javafx.scene.effect.DropShadow(5, 0, 3, Color.web("#3498db", 0.3)));
        
        double width = 250;
        double height = 60;
        double intervalX = width / (trendData.size() - 1);
        
        // Créer les points du graphique en ligne
        for (int i = 0; i < trendData.size(); i++) {
            double value = trendData.get(i);
            double normalizedY = height - ((value - min) / range) * height;
            
            // Ajouter le point (x, y) à la polyligne
            sparkline.getPoints().add(i * intervalX);
            sparkline.getPoints().add(normalizedY);
        }
        
        // Créer une zone sous la ligne (remplissage dégradé)
        javafx.scene.shape.Polygon fillPolygon = new javafx.scene.shape.Polygon();
        
        // Créer une liste de points pour le polygone
        Double[] polygonPoints = new Double[sparkline.getPoints().size() + 4];
        
        // Ajouter d'abord les points du graphique en ligne
        for (int i = 0; i < sparkline.getPoints().size(); i++) {
            polygonPoints[i] = sparkline.getPoints().get(i);
        }
        
        // Fermer le polygone en ajoutant les points inférieurs
        polygonPoints[sparkline.getPoints().size()] = width;
        polygonPoints[sparkline.getPoints().size() + 1] = height;
        polygonPoints[sparkline.getPoints().size() + 2] = 0.0;
        polygonPoints[sparkline.getPoints().size() + 3] = height;
        
        fillPolygon.getPoints().addAll(polygonPoints);
        
        // Créer un dégradé pour le remplissage
        javafx.scene.paint.LinearGradient gradient = new javafx.scene.paint.LinearGradient(
            0, 0, 0, 1, true, javafx.scene.paint.CycleMethod.NO_CYCLE,
            new javafx.scene.paint.Stop(0, Color.web("#3498db", 0.3)),
            new javafx.scene.paint.Stop(1, Color.web("#3498db", 0.05))
        );
        fillPolygon.setFill(gradient);
        
        // Pane pour contenir le graphique et la zone remplie
        Pane chartPane = new Pane();
        chartPane.getChildren().addAll(fillPolygon, sparkline);
        
        // Ajouter des points pour marquer les données
        for (int i = 0; i < trendData.size(); i++) {
            double value = trendData.get(i);
            double normalizedY = height - ((value - min) / range) * height;
            double x = i * intervalX;
            
            Circle dataPoint = new Circle(4);
            dataPoint.setCenterX(x);
            dataPoint.setCenterY(normalizedY);
            
            // Dernier point (valeur actuelle) en évidence
            if (i == trendData.size() - 1) {
                dataPoint.setFill(Color.web("#ff9800"));
                dataPoint.setRadius(5);
                dataPoint.setStroke(Color.WHITE);
                dataPoint.setStrokeWidth(2);
            } else {
                dataPoint.setFill(Color.web("#3498db"));
            }
            
            chartPane.getChildren().add(dataPoint);
        }
        
        chartContainer.getChildren().add(chartPane);
        
        // Espace flexible
        Region spacerTop = new Region();
        spacerTop.setMinHeight(10);
        VBox.setVgrow(spacerTop, Priority.ALWAYS);
        
        // Calculer la tendance (pourcentage de changement entre la première et la dernière valeur)
        double firstValue = trendData.get(0);
        double lastValue = trendData.get(trendData.size() - 1);
        double change = firstValue != 0 ? ((lastValue - firstValue) / firstValue) * 100 : 0;
        
        // Déterminer si la tendance est positive ou négative
        boolean isPositive = change > 0;
        boolean isTrendGood = (isPositive && positiveIsBetter) || (!isPositive && !positiveIsBetter);
        
        // Créer un badge pour afficher la tendance
        StackPane trendBadge = new StackPane();
        trendBadge.setMaxWidth(200);
        trendBadge.setPadding(new Insets(5, 10, 5, 10));
        
        Rectangle badgeBackground = new Rectangle();
        badgeBackground.setWidth(160);
        badgeBackground.setHeight(28);
        badgeBackground.setArcWidth(14);
        badgeBackground.setArcHeight(14);
        
        HBox trendContent = new HBox(5);
        trendContent.setAlignment(Pos.CENTER);
        
        String trendIcon;
        String badgeColor;
        if (isPositive) {
            trendIcon = "▲";
            badgeColor = positiveIsBetter ? "#06d6a0" : "#ef476f";
        } else if (change < 0) {
            trendIcon = "▼";
            badgeColor = positiveIsBetter ? "#ef476f" : "#06d6a0";
        } else {
            trendIcon = "■";
            badgeColor = "#8d99ae";
        }
        
        badgeBackground.setFill(Color.web(badgeColor, 0.15));
        
        Label iconLabel = new Label(trendIcon);
        iconLabel.setTextFill(Color.web(badgeColor));
        iconLabel.setStyle("-fx-font-weight: bold;");
        
        Label trendInfoLabel = new Label(String.format("%s (%+.1f%%)", trendLabel, change));
        trendInfoLabel.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
        trendInfoLabel.setTextFill(Color.web(badgeColor));
        
        trendContent.getChildren().addAll(iconLabel, trendInfoLabel);
        trendBadge.getChildren().addAll(badgeBackground, trendContent);
        
        // Assembler la carte
        card.getChildren().addAll(titleLabel, spacerTop, valueLabel, chartContainer, trendBadge);
        
        return card;
    }
    
    /**
     * Crée une carte avec une barre de progression pour afficher un taux
     */
    public static VBox createFillRateCard(double fillRate, double change, String title, String description, String iconPath) {
        VBox card = new VBox(15);
        card.setPadding(new Insets(15));
        card.setAlignment(Pos.CENTER_LEFT);
        card.getStyleClass().add("stats-card");
        
        // En-tête avec icône et titre
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);
        
        // Icône SVG
        SVGPath icon = new SVGPath();
        icon.setContent(iconPath);
        icon.setFill(Color.web("#3f51b5"));
        StackPane iconContainer = new StackPane(icon);
        iconContainer.setMinSize(30, 30);
        iconContainer.setMaxSize(30, 30);
        
        // Titre
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        titleLabel.setTextFill(Color.web("#737373"));
        
        header.getChildren().addAll(iconContainer, titleLabel);
        
        // Conteneur pour la valeur et le pourcentage
        HBox valueContainer = new HBox(10);
        valueContainer.setAlignment(Pos.CENTER_LEFT);
        
        // Valeur en pourcentage
        Label valueLabel = new Label(String.format("%.1f%%", fillRate));
        valueLabel.setFont(Font.font("System", FontWeight.BOLD, 24));
        
        // Variation
        boolean isPositive = change >= 0;
        Label changeLabel = new Label(String.format("%s%.1f%%", isPositive ? "+" : "", change));
        changeLabel.setTextFill(isPositive ? Color.web("#4caf50") : Color.web("#f44336"));
        changeLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        valueContainer.getChildren().addAll(valueLabel, changeLabel);
        
        // Barre de progression
        ProgressBar progressBar = new ProgressBar(fillRate / 100.0);
        progressBar.setPrefHeight(10);
        progressBar.setPrefWidth(Double.MAX_VALUE);
        progressBar.getStyleClass().add("custom-progress-bar");
        
        // Personnaliser la couleur de la barre en fonction du taux
        String progressStyle = "";
        if (fillRate >= 75) {
            progressStyle = "-fx-accent: #4caf50;"; // Vert
        } else if (fillRate >= 50) {
            progressStyle = "-fx-accent: #ff9800;"; // Orange
        } else {
            progressStyle = "-fx-accent: #f44336;"; // Rouge
        }
        progressBar.setStyle(progressStyle);
        
        // Description
        Label descriptionLabel = new Label(description);
        descriptionLabel.setFont(Font.font("System", 12));
        descriptionLabel.setTextFill(Color.web("#757575"));
        
        // Ajouter tous les éléments
        card.getChildren().addAll(header, valueContainer, progressBar, descriptionLabel);
        
        return card;
    }
    
    /**
     * Crée un graphique circulaire à partir des données fournies
     */
    public static VBox createPieChart(String title, List<PieChartData> data, int total) {
        VBox container = new VBox(10);
        container.setPadding(new Insets(15));
        container.setAlignment(Pos.CENTER);
        
        // Titre
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        // Créer le graphique
        PieChart pieChart = new PieChart();
        pieChart.setTitle("");
        pieChart.setLegendVisible(true);
        pieChart.setLabelsVisible(true);
        pieChart.setMinHeight(250);
        
        // Ajouter les données
        for (PieChartData item : data) {
            PieChart.Data slice = new PieChart.Data(item.getLabel(), item.getValue());
            pieChart.getData().add(slice);
        }
        
        // Total
        Label totalLabel = new Label("Total: " + total);
        totalLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        
        container.getChildren().addAll(titleLabel, pieChart, totalLabel);
        
        return container;
    }
    
    /**
     * Crée un graphique à barres à partir des données fournies
     */
    public static VBox createBarChart(String title, List<BarChartData> data) {
        VBox container = new VBox(10);
        container.setPadding(new Insets(15));
        container.setAlignment(Pos.CENTER);
        
        // Titre
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        // Créer les axes
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Sessions");
        yAxis.setLabel("Nombre de réservations");
        
        // Créer le graphique
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("");
        barChart.setLegendVisible(false);
        barChart.setAnimated(true);
        barChart.setMinHeight(250);
        
        // Créer la série de données
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        
        // Ajouter les données
        for (BarChartData item : data) {
            series.getData().add(new XYChart.Data<>(item.getCategory(), item.getValue()));
        }
        
        barChart.getData().add(series);
        
        container.getChildren().addAll(titleLabel, barChart);
        
        return container;
    }
    
    /**
     * Crée un graphique de tendance sur plusieurs jours
     */
    public static VBox createTrendChart(String title, List<TrendData> data) {
        VBox container = new VBox(10);
        container.setPadding(new Insets(15));
        container.setAlignment(Pos.CENTER);
        
        // Titre
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        // Créer les axes
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Jours");
        yAxis.setLabel("Nombre de réservations");
        
        // Créer le graphique de ligne
        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("");
        lineChart.setCreateSymbols(true);
        lineChart.setAnimated(true);
        lineChart.setMinHeight(250);
        
        // Série pour les réservations
        XYChart.Series<String, Number> reservationSeries = new XYChart.Series<>();
        reservationSeries.setName("Réservations");
        
        // Série pour la capacité totale
        XYChart.Series<String, Number> capacitySeries = new XYChart.Series<>();
        capacitySeries.setName("Capacité totale");
        
        // Ajouter les données
        for (TrendData item : data) {
            reservationSeries.getData().add(new XYChart.Data<>(item.getDate(), item.getValue()));
            capacitySeries.getData().add(new XYChart.Data<>(item.getDate(), item.getTotal()));
        }
        
        lineChart.getData().addAll(reservationSeries, capacitySeries);
        
        container.getChildren().addAll(titleLabel, lineChart);
        
        return container;
    }
    
    /**
     * Crée un graphique circulaire simple
     */
    public static VBox createPieChart(String title, List<PieChartData> data) {
        // Calculer le total
        int total = data.stream().mapToInt(PieChartData::getValue).sum();
        return createPieChart(title, data, total);
    }
} 