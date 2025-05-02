$content = @"
package Controller;

import Entity.Evenement;
import Entity.Session;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.EvenementService;
import services.SessionService;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

"@

# Récupérer le contenu existant du fichier
$existing = Get-Content -Path "src\main\java\Controller\FrontEventsController.java" -Raw

# Trouver l'index où commence la classe
$classStart = $existing.IndexOf("public class FrontEventsController implements Initializable")
if ($classStart -eq -1) {
    Write-Host "Impossible de trouver le début de la classe"
    exit 1
}

# Extraire la définition de classe et les méthodes
$classDefinition = $existing.Substring($classStart)

# Combiner l'en-tête propre avec le corps de la classe
$newContent = $content + $classDefinition

# Écrire le nouveau contenu dans le fichier
Set-Content -Path "src\main\java\Controller\FrontEventsController.java" -Value $newContent

Write-Host "Le fichier a été nettoyé avec succès" 