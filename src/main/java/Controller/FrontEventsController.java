package Controller;

import Entity.Evenement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.EvenementService;
import services.SessionService;
import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class FrontEventsController implements Initializable {
    
    @FXML private FlowPane eventsContainer;
    @FXML private TextField searchField;
    @FXML private ComboBox<String> filterCategory;
    @FXML private ComboBox<String> filterDate;
    @FXML private Button btnBack;
    
    private EvenementService evenementService;
    private SessionService sessionService;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            evenementService = new EvenementService();
            sessionService = new SessionService();
            
            // Configuration des filtres
            setupFilters();
            
            // Chargement des événements
            loadEvents();
            
            // Configuration du bouton retour
            btnBack.setOnAction(event -> handleBack());
            
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, 
                     "Erreur de connexion", 
                     "Impossible de se connecter à la base de données", 
                     e.getMessage());
        }
    }
    
    private void setupFilters() {
        // Configuration des catégories
        filterCategory.getItems().addAll("Tous", "Concert", "Théâtre", "Exposition", "Sport", "Conférence");
        filterCategory.setValue("Tous");
        
        // Configuration des dates
        filterDate.getItems().addAll("Tous", "Aujourd'hui", "Cette semaine", "Ce mois-ci");
        filterDate.setValue("Tous");
        
        // Configurez les actions de filtrage
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterEvents();
        });
        
        filterCategory.setOnAction(event -> filterEvents());
        filterDate.setOnAction(event -> filterEvents());
    }
    
    private void loadEvents() {
        try {
            // Vider le conteneur
            eventsContainer.getChildren().clear();
            
            List<Evenement> events = evenementService.getAllEvents();
            
            // Ajouter chaque événement
            for (Evenement event : events) {
                VBox eventCard = createEventCard(event);
                eventsContainer.getChildren().add(eventCard);
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de chargement", 
                     "Une erreur est survenue lors du chargement des événements.");
        }
    }
    
    private VBox createEventCard(Evenement event) {
        try {
            // Créer une copie du template
            VBox card = new VBox();
            card.setPrefWidth(350);
            card.setPrefHeight(450);
            card.setStyle("-fx-background-color: white; -fx-background-radius: 10; " +
                         "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 10);");
            
            // Image de l'événement
            ImageView imageView = new ImageView();
            if (event.getImage() != null && !event.getImage().isEmpty()) {
                try {
                    String fullPath = "C:\\xampp\\htdocs\\imageP\\" + event.getImage();
                    File file = new File(fullPath);
                    if (file.exists()) {
                        Image image = new Image(file.toURI().toString());
                        imageView.setImage(image);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            imageView.setFitWidth(350);
            imageView.setFitHeight(200);
            imageView.setPreserveRatio(true);
            
            // Contenu de la carte
            VBox content = new VBox(10);
            content.setStyle("-fx-padding: 15;");
            
            // Titre
            Label titleLabel = new Label(event.getTitre());
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
            titleLabel.setWrapText(true);
            
            // Type
            Label typeLabel = new Label("Type: " + event.getType());
            typeLabel.setStyle("-fx-text-fill: #7f8c8d;");
            
            // Dates
            Label dateStartLabel = new Label("Début: " + event.getDateD().format(formatter));
            dateStartLabel.setStyle("-fx-text-fill: #7f8c8d;");
            
            Label dateEndLabel = new Label("Fin: " + event.getDateF().format(formatter));
            dateEndLabel.setStyle("-fx-text-fill: #7f8c8d;");
            
            // Lieu
            Label locationLabel = new Label("Lieu: " + event.getLocation());
            locationLabel.setStyle("-fx-text-fill: #7f8c8d;");
            
            // Description
            String shortDesc = event.getDescription();
            if (shortDesc != null && shortDesc.length() > 100) {
                shortDesc = shortDesc.substring(0, 97) + "...";
            }
            Label descLabel = new Label(shortDesc);
            descLabel.setStyle("-fx-text-fill: #7f8c8d;");
            descLabel.setWrapText(true);
            
            // Places disponibles
            Label capacityLabel;
            if (event.getNbPlace() <= 0) {
                capacityLabel = new Label("SOLD OUT");
                capacityLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-weight: bold; -fx-font-size: 14px;");
            } else {
                capacityLabel = new Label("Places disponibles: " + event.getNbPlace());
                capacityLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
            }
            
            // Espace flexible
            Region spacer = new Region();
            VBox.setVgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
            
            // Boutons d'action
            Button sessionsButton = new Button("Voir Sessions");
            sessionsButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; " +
                                  "-fx-font-weight: bold; -fx-min-width: 120px;");
            sessionsButton.setOnAction(e -> showSessionsModal(event));
            
            Button reserveButton = new Button("Réserver");
            if (event.getNbPlace() <= 0) {
                reserveButton.setDisable(true);
                reserveButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; " +
                                     "-fx-font-weight: bold; -fx-min-width: 100px;");
            } else {
                reserveButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; " +
                                     "-fx-font-weight: bold; -fx-min-width: 100px;");
            }
            reserveButton.setOnAction(e -> showReservationDialog(event));
            
            // Nouveaux boutons pour la météo et la carte
            Button weatherButton = new Button("Météo");
            weatherButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; " +
                                  "-fx-font-weight: bold; -fx-min-width: 100px; -fx-font-size: 14px;");
            weatherButton.setOnAction(e -> showWeatherDialog(event.getLocation()));
            
            Button mapButton = new Button("OpenStreetMap");
            mapButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; " +
                              "-fx-font-weight: bold; -fx-min-width: 150px; -fx-font-size: 14px;");
            mapButton.setOnAction(e -> showMapDialog(event.getLocation()));
            
            // Rangée 1 de boutons
            HBox buttonRow1 = new HBox(10);
            buttonRow1.setAlignment(javafx.geometry.Pos.CENTER);
            buttonRow1.getChildren().addAll(sessionsButton, reserveButton);
            
            // Rangée 2 de boutons
            HBox buttonRow2 = new HBox(10);
            buttonRow2.setAlignment(javafx.geometry.Pos.CENTER);
            buttonRow2.getChildren().addAll(weatherButton, mapButton);
            
            // Conteneur vertical pour les deux rangées de boutons
            VBox buttonContainer = new VBox(10);
            buttonContainer.getChildren().addAll(buttonRow1, buttonRow2);
            
            // Assembler tous les éléments
            content.getChildren().addAll(
                titleLabel, typeLabel, dateStartLabel, dateEndLabel, 
                locationLabel, descLabel, capacityLabel, spacer, buttonContainer
            );
            
            // Assembler la carte complète
            card.getChildren().addAll(imageView, content);
            
            return card;
        } catch (Exception e) {
            e.printStackTrace();
            return new VBox(); // Retourner une carte vide en cas d'erreur
        }
    }
    
    private void filterEvents() {
        try {
            // Récupérer les valeurs de filtre
            String searchText = searchField.getText().toLowerCase();
            String category = filterCategory.getValue();
            String date = filterDate.getValue();
            
            // Vider le conteneur
            eventsContainer.getChildren().clear();
            
            // Obtenir tous les événements
            List<Evenement> allEvents = evenementService.getAllEvents();
            
            // Filtrer et ajouter les événements
            for (Evenement event : allEvents) {
                boolean matchesSearch = searchText == null || searchText.isEmpty() ||
                    event.getTitre().toLowerCase().contains(searchText) ||
                    event.getDescription().toLowerCase().contains(searchText) ||
                    event.getType().toLowerCase().contains(searchText) ||
                    event.getLocation().toLowerCase().contains(searchText);
                
                boolean matchesCategory = "Tous".equals(category) || event.getType().equals(category);
                
                boolean matchesDate = true; // Todo: implémenter le filtrage par date
                
                if (matchesSearch && matchesCategory && matchesDate) {
                    VBox eventCard = createEventCard(event);
                    eventsContainer.getChildren().add(eventCard);
                }
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de filtrage", 
                     "Une erreur est survenue lors du filtrage des événements.");
        }
    }
    
    private void showSessionsModal(Evenement event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/GestionSessions.fxml"));
            Parent root = loader.load();
            
            GestionSessionsController controller = loader.getController();
            controller.setEvenement(event);
            
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Sessions de l'événement : " + event.getTitre());
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur d'affichage", 
                     "Impossible d'afficher les sessions de l'événement : " + e.getMessage());
        }
    }
    
    private void showReservationDialog(Evenement event) {
        try {
            // Vérifier si l'événement est complet
            if (event.getNbPlace() <= 0) {
                showAlert(Alert.AlertType.INFORMATION, 
                         "Événement complet", 
                         "Cet événement est complet", 
                         "Il n'y a plus de places disponibles pour cet événement.");
                return;
            }
            
            // Créer un dialogue personnalisé
            Dialog<Integer> dialog = new Dialog<>();
            dialog.setTitle("Réserver des places");
            dialog.setHeaderText("Réservation pour : " + event.getTitre());
            
            // Configurer les boutons
            ButtonType reserveButtonType = new ButtonType("Réserver", ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(reserveButtonType, ButtonType.CANCEL);
            
            // Créer le contenu du dialogue
            VBox content = new VBox(15);
            content.setStyle("-fx-padding: 20;");
            
            // Informations sur l'événement
            Label eventTitleLabel = new Label(event.getTitre());
            eventTitleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
            
            Label eventDateLabel = new Label("Du " + event.getDateD().format(formatter) + " au " + event.getDateF().format(formatter));
            eventDateLabel.setStyle("-fx-text-fill: #7f8c8d;");
            
            Label eventLocationLabel = new Label("Lieu: " + event.getLocation());
            eventLocationLabel.setStyle("-fx-text-fill: #7f8c8d;");
            
            // Informations sur les places
            Label placesLabel = new Label("Places disponibles : " + event.getNbPlace());
            placesLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #27ae60;");
            
            Label infoLabel = new Label("Combien de places souhaitez-vous réserver ?");
            
            HBox spinnerBox = new HBox(10);
            spinnerBox.setAlignment(javafx.geometry.Pos.CENTER);
            
            Button decreaseButton = new Button("-");
            decreaseButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 30px;");
            
            Spinner<Integer> placesSpinner = new Spinner<>(1, event.getNbPlace(), 1);
            placesSpinner.setEditable(true);
            placesSpinner.setPrefWidth(100);
            
            Button increaseButton = new Button("+");
            increaseButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 30px;");
            
            // Prix fictif (à ajuster selon votre modèle de données)
            double prixUnitaire = 25.0; // Prix fictif par place
            Label priceLabel = new Label(String.format("Prix unitaire : %.2f €", prixUnitaire));
            
            Label priceTotalLabel = new Label(String.format("Total : %.2f €", prixUnitaire));
            priceTotalLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
            
            // Ajout des écouteurs d'événements
            placesSpinner.getValueFactory().valueProperty().addListener((obs, oldVal, newVal) -> {
                // Mettre à jour le total
                updateTotalPrice(newVal, priceTotalLabel, prixUnitaire);
            });
            
            decreaseButton.setOnAction(e -> {
                int currentValue = placesSpinner.getValue();
                if (currentValue > 1) {
                    placesSpinner.getValueFactory().setValue(currentValue - 1);
                }
            });
            
            increaseButton.setOnAction(e -> {
                int currentValue = placesSpinner.getValue();
                if (currentValue < event.getNbPlace()) {
                    placesSpinner.getValueFactory().setValue(currentValue + 1);
                }
            });
            
            spinnerBox.getChildren().addAll(decreaseButton, placesSpinner, increaseButton);
            
            // Fonction pour mettre à jour le prix total
            updateTotalPrice(1, priceTotalLabel, prixUnitaire);
            
            content.getChildren().addAll(
                eventTitleLabel, eventDateLabel, eventLocationLabel, 
                new Separator(), placesLabel, infoLabel, spinnerBox, 
                new Separator(), priceLabel, priceTotalLabel
            );
            
            dialog.getDialogPane().setContent(content);
            dialog.getDialogPane().setPrefWidth(400);
            
            // Convertir le résultat
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == reserveButtonType) {
                    return placesSpinner.getValue();
                }
                return null;
            });
            
            // Afficher le dialogue et traiter le résultat
            Optional<Integer> result = dialog.showAndWait();
            result.ifPresent(places -> {
                if (places > 0 && places <= event.getNbPlace()) {
                    // Effectuer la réservation
                    boolean success = evenementService.reserverPlace(event.getId(), places);
                    
                    if (success) {
                        // Mettre à jour l'affichage
                        event.setNbPlace(event.getNbPlace() - places);
                        
                        // Rafraîchir l'affichage
                        filterEvents();
                        
                        showAlert(Alert.AlertType.INFORMATION, 
                                 "Réservation confirmée", 
                                 "Réservation effectuée avec succès", 
                                 "Vous avez réservé " + places + " place(s) pour l'événement \"" + event.getTitre() + "\".\n" +
                                 String.format("Total payé : %.2f €", places * prixUnitaire));
                    } else {
                        showAlert(Alert.AlertType.ERROR, 
                                 "Erreur de réservation", 
                                 "La réservation a échoué", 
                                 "Une erreur est survenue lors de la réservation.");
                    }
                }
            });
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, 
                     "Erreur", 
                     "Erreur lors de la réservation", 
                     "Une erreur est survenue : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void updateTotalPrice(int quantity, Label totalLabel, double prixUnitaire) {
        double total = quantity * prixUnitaire;
        totalLabel.setText(String.format("Total : %.2f €", total));
    }
    
    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AffichageEvent.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur de navigation", 
                     "Impossible de retourner à la page principale : " + e.getMessage());
        }
    }
    
    private void showAlert(Alert.AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleSearch() {
        filterEvents();
    }

    @FXML
    private void handleReset() {
        filterCategory.setValue("Tous");
        filterDate.setValue("Tous");
        searchField.clear();
        filterEvents();
    }

    // Méthodes pour gérer les nouveaux boutons
    private void showWeatherDialog(String location) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Météo pour " + location);
        dialog.setHeaderText("Informations météorologiques");
        
        VBox content = new VBox(15);
        content.setPadding(new javafx.geometry.Insets(20));
        content.setStyle("-fx-background-color: linear-gradient(to bottom, #87CEFA, #1E90FF);");
        
        Label loadingLabel = new Label("Chargement des données météo...");
        loadingLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setStyle("-fx-progress-color: white;");
        content.getChildren().addAll(loadingLabel, progressIndicator);
        
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().setPrefWidth(400);
        
        // Charger les données météo réelles via l'API OpenWeatherMap
        new Thread(() -> {
            try {
                // Remplacer par votre propre clé API
                String apiKey = "d05bb0e2f9502e33faadbaeb0c83845d";
                String encodedLocation = java.net.URLEncoder.encode(location, "UTF-8");
                String url = "https://api.openweathermap.org/data/2.5/weather?q=" + encodedLocation + "&units=metric&lang=fr&appid=" + apiKey;
                
                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
                connection.setRequestMethod("GET");
                
                int responseCode = connection.getResponseCode();
                
                if (responseCode == 200) {
                    java.io.BufferedReader reader = new java.io.BufferedReader(
                            new java.io.InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    
                    // Parser la réponse JSON
                    org.json.JSONObject jsonResponse = new org.json.JSONObject(response.toString());
                    
                    // Extraire les données météo
                    double temperature = jsonResponse.getJSONObject("main").getDouble("temp");
                    int humidity = jsonResponse.getJSONObject("main").getInt("humidity");
                    double pressure = jsonResponse.getJSONObject("main").getDouble("pressure");
                    double windSpeed = jsonResponse.getJSONObject("wind").getDouble("speed");
                    String weatherDescription = jsonResponse.getJSONArray("weather").getJSONObject(0).getString("description");
                    String weatherIcon = jsonResponse.getJSONArray("weather").getJSONObject(0).getString("icon");
                    
                    // Obtenir le nom de la ville à partir de la réponse
                    String cityName = jsonResponse.getString("name");
                    String country = jsonResponse.getJSONObject("sys").getString("country");
                    
                    // Mettre à jour l'interface utilisateur
                    javafx.application.Platform.runLater(() -> {
                        content.getChildren().clear();
                        
                        Label locationLabel = new Label(cityName + ", " + country);
                        locationLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 5, 0, 0, 1);");
                        
                        // Ajouter une icône météo
                        ImageView weatherIconView = new ImageView();
                        try {
                            // Utiliser l'icône réelle d'OpenWeatherMap
                            String iconUrl = "http://openweathermap.org/img/wn/" + weatherIcon + "@2x.png";
                            Image image = new Image(iconUrl);
                            weatherIconView.setImage(image);
                            weatherIconView.setFitWidth(80);
                            weatherIconView.setFitHeight(80);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        
                        Label temperatureLabel = new Label(String.format("%.1f°C", temperature));
                        temperatureLabel.setStyle("-fx-font-size: 36px; -fx-font-weight: bold; -fx-text-fill: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 5, 0, 0, 1);");
                        
                        HBox mainWeatherInfo = new HBox(20);
                        mainWeatherInfo.setAlignment(javafx.geometry.Pos.CENTER);
                        mainWeatherInfo.getChildren().addAll(weatherIconView, temperatureLabel);
                        
                        Label conditionLabel = new Label(weatherDescription);
                        conditionLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.6), 3, 0, 0, 1);");
                        conditionLabel.setAlignment(javafx.geometry.Pos.CENTER);
                        
                        Separator separator = new Separator();
                        separator.setStyle("-fx-background-color: white;");
                        
                        // Créer un GridPane pour les détails météo
                        GridPane weatherDetails = new GridPane();
                        weatherDetails.setHgap(15);
                        weatherDetails.setVgap(10);
                        weatherDetails.setStyle("-fx-background-color: rgba(255,255,255,0.2); -fx-background-radius: 10;");
                        weatherDetails.setPadding(new javafx.geometry.Insets(15));
                        
                        Label humidityTitle = new Label("Humidité");
                        humidityTitle.setStyle("-fx-text-fill: white;");
                        Label humidityValue = new Label(humidity + "%");
                        humidityValue.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");
                        
                        Label windTitle = new Label("Vent");
                        windTitle.setStyle("-fx-text-fill: white;");
                        Label windValue = new Label(String.format("%.1f km/h", windSpeed * 3.6)); // Convertir m/s en km/h
                        windValue.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");
                        
                        Label pressureTitle = new Label("Pression");
                        pressureTitle.setStyle("-fx-text-fill: white;");
                        Label pressureValue = new Label(String.format("%.0f hPa", pressure));
                        pressureValue.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");
                        
                        weatherDetails.add(humidityTitle, 0, 0);
                        weatherDetails.add(humidityValue, 0, 1);
                        weatherDetails.add(windTitle, 1, 0);
                        weatherDetails.add(windValue, 1, 1);
                        weatherDetails.add(pressureTitle, 0, 2);
                        weatherDetails.add(pressureValue, 0, 3);
                        
                        content.getChildren().addAll(
                            locationLabel,
                            mainWeatherInfo,
                            conditionLabel,
                            separator,
                            weatherDetails
                        );
                    });
                } else {
                    javafx.application.Platform.runLater(() -> {
                        content.getChildren().clear();
                        
                        Label errorLabel = new Label("Impossible de récupérer les données météo");
                        errorLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
                        
                        Label detailsLabel = new Label("Erreur " + responseCode + " : Vérifiez le nom de la ville");
                        detailsLabel.setStyle("-fx-text-fill: white;");
                        
                        content.getChildren().addAll(errorLabel, detailsLabel);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                javafx.application.Platform.runLater(() -> {
                    content.getChildren().clear();
                    
                    Label errorLabel = new Label("Erreur de connexion");
                    errorLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
                    
                    Label detailsLabel = new Label("Vérifiez votre connexion internet");
                    detailsLabel.setStyle("-fx-text-fill: white;");
                    
                    content.getChildren().addAll(errorLabel, detailsLabel);
                });
            }
        }).start();
        
        dialog.showAndWait();
    }
    
    private void showMapDialog(String location) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Localisation de " + location);
        dialog.setHeaderText("Localisation et Carte");
        dialog.getDialogPane().setPrefWidth(700);
        dialog.getDialogPane().setPrefHeight(600);
        
        // Création d'un TabPane pour gérer les onglets
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        
        // Onglet 1: Informations de localisation
        Tab infoTab = new Tab("Informations");
        VBox infoContent = new VBox(15);
        infoContent.setPadding(new javafx.geometry.Insets(20));
        infoContent.setStyle("-fx-background-color: white;");
        
        // Conteneur pour les informations qui seront mises à jour après la géocodification
        VBox locationInfoBox = new VBox(10);
        locationInfoBox.setStyle("-fx-background-color: #f8f9fa; -fx-padding: 20; -fx-background-radius: 5; -fx-border-color: #e9ecef; -fx-border-radius: 5;");
        
        Label titleLabel = new Label("Informations sur " + location);
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        
        Label loadingInfoLabel = new Label("Chargement des informations...");
        ProgressIndicator loadingInfoIndicator = new ProgressIndicator();
        loadingInfoIndicator.setMaxSize(30, 30);
        
        HBox loadingBox = new HBox(10, loadingInfoIndicator, loadingInfoLabel);
        loadingBox.setAlignment(javafx.geometry.Pos.CENTER);
        
        locationInfoBox.getChildren().add(loadingBox);
        
        // Initialiser les boutons d'action qui seront mis à jour plus tard
        HBox actionButtons = new HBox(10);
        actionButtons.setAlignment(javafx.geometry.Pos.CENTER);
        
        Button copyButton = new Button("Copier les coordonnées");
        copyButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        copyButton.setDisable(true);
        
        Button openButton = new Button("Ouvrir dans le navigateur");
        openButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        openButton.setDisable(true);
        
        actionButtons.getChildren().addAll(copyButton, openButton);
        
        // Note sur OpenStreetMap
        Label attributionLabel = new Label("© OpenStreetMap contributors");
        attributionLabel.setStyle("-fx-font-size: 10px; -fx-text-fill: #7f8c8d;");
        
        // Assembler l'interface de l'onglet info
        infoContent.getChildren().addAll(titleLabel, locationInfoBox, actionButtons, attributionLabel);
        infoTab.setContent(infoContent);
        
        // Onglet 2: Carte OpenStreetMap
        Tab mapTab = new Tab("Carte");
        
        // Créer le StackPane qui contiendra la WebView et l'indicateur de chargement
        StackPane mapStack = new StackPane();
        
        // Créer un WebView pour afficher la carte
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();
        
        // Créer une barre de progression
        ProgressIndicator progressIndicator = new ProgressIndicator();
        progressIndicator.setMaxSize(50, 50);
        
        Label loadingLabel = new Label("Chargement de la carte...");
        loadingLabel.setStyle("-fx-font-size: 14px;");
        
        VBox loadingMapBox = new VBox(10, progressIndicator, loadingLabel);
        loadingMapBox.setAlignment(javafx.geometry.Pos.CENTER);
        
        mapStack.getChildren().addAll(webView, loadingMapBox);
        mapTab.setContent(mapStack);
        
        // Ajouter les onglets au TabPane
        tabPane.getTabs().addAll(infoTab, mapTab);
        
        // Définir le TabPane comme contenu du dialogue
        dialog.getDialogPane().setContent(tabPane);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        dialog.getDialogPane().lookupButton(ButtonType.CLOSE).setStyle("-fx-font-weight: bold;");
        
        // Montrer le dialogue avant de commencer le chargement des données
        dialog.show();
        
        // Thread pour obtenir les informations de localisation via l'API de géocodage
        new Thread(() -> {
            try {
                // Encoder le nom de la localisation pour l'URL
                String encodedLocation = java.net.URLEncoder.encode(location, "UTF-8");
                String apiUrl = "https://nominatim.openstreetmap.org/search?q=" + encodedLocation + "&format=json&limit=1";
                
                java.net.HttpURLConnection connection = (java.net.HttpURLConnection) new java.net.URL(apiUrl).openConnection();
                connection.setRequestMethod("GET");
                // Définir un User-Agent pour respecter les conditions d'utilisation de Nominatim
                connection.setRequestProperty("User-Agent", "ArtphoriaApp/1.0");
                
                int responseCode = connection.getResponseCode();
                
                if (responseCode == 200) {
                    java.io.BufferedReader reader = new java.io.BufferedReader(
                            new java.io.InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                    
                    // Parser la réponse JSON
                    org.json.JSONArray jsonArray = new org.json.JSONArray(response.toString());
                    
                    if (jsonArray.length() > 0) {
                        org.json.JSONObject locationInfo = jsonArray.getJSONObject(0);
                        
                        // Extraire les informations de localisation
                        double lat = locationInfo.getDouble("lat");
                        double lon = locationInfo.getDouble("lon");
                        String displayName = locationInfo.getString("display_name");
                        
                        // Formater les coordonnées pour l'affichage et les actions
                        String coordsString = String.format("%.6f, %.6f", lat, lon);
                        String mapUrl = "https://www.openstreetmap.org/?mlat=" + lat + "&mlon=" + lon + "&zoom=15";
                        
                        // Mettre à jour l'interface utilisateur
                        javafx.application.Platform.runLater(() -> {
                            // Mettre à jour la boîte d'informations
                            locationInfoBox.getChildren().clear();
                            
                            Label locationNameLabel = new Label(location);
                            locationNameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
                            
                            Label locationCoordLabel = new Label("Coordonnées: " + coordsString);
                            locationCoordLabel.setStyle("-fx-font-size: 14px;");
                            
                            Label locationAddressLabel = new Label("Adresse: " + displayName);
                            locationAddressLabel.setStyle("-fx-font-size: 14px;");
                            locationAddressLabel.setWrapText(true);
                            
                            // Créer le séparateur et la grille pour les détails
                            Separator separator = new Separator();
                            separator.setPadding(new javafx.geometry.Insets(5, 0, 5, 0));
                            
                            GridPane detailsGrid = new GridPane();
                            detailsGrid.setHgap(15);
                            detailsGrid.setVgap(10);
                            
                            Label transportLabel = new Label("Transport:");
                            transportLabel.setStyle("-fx-font-weight: bold;");
                            Label transportValue = new Label("Informations non disponibles");
                            
                            Label parkingLabel = new Label("Parking:");
                            parkingLabel.setStyle("-fx-font-weight: bold;");
                            Label parkingValue = new Label("Informations non disponibles");
                            
                            Label accessLabel = new Label("Accessibilité:");
                            accessLabel.setStyle("-fx-font-weight: bold;");
                            Label accessValue = new Label("Informations non disponibles");
                            
                            Label nearbyLabel = new Label("À proximité:");
                            nearbyLabel.setStyle("-fx-font-weight: bold;");
                            Label nearbyValue = new Label("Informations non disponibles");
                            
                            detailsGrid.add(transportLabel, 0, 0);
                            detailsGrid.add(transportValue, 1, 0);
                            detailsGrid.add(parkingLabel, 0, 1);
                            detailsGrid.add(parkingValue, 1, 1);
                            detailsGrid.add(accessLabel, 0, 2);
                            detailsGrid.add(accessValue, 1, 2);
                            detailsGrid.add(nearbyLabel, 0, 3);
                            detailsGrid.add(nearbyValue, 1, 3);
                            
                            locationInfoBox.getChildren().addAll(locationNameLabel, locationCoordLabel, locationAddressLabel, separator, detailsGrid);
                            
                            // Activer les boutons et ajouter leurs actions
                            copyButton.setDisable(false);
                            copyButton.setOnAction(e -> {
                                javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
                                javafx.scene.input.ClipboardContent clipContent = new javafx.scene.input.ClipboardContent();
                                clipContent.putString(coordsString);
                                clipboard.setContent(clipContent);
                                
                                // Afficher un message de confirmation
                                Label confirmLabel = new Label("Coordonnées copiées !");
                                confirmLabel.setStyle("-fx-text-fill: #28a745;");
                                if (!locationInfoBox.getChildren().contains(confirmLabel)) {
                                    locationInfoBox.getChildren().add(confirmLabel);
                                    
                                    // Faire disparaître le message après 2 secondes
                                    javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(2));
                                    pause.setOnFinished(event -> locationInfoBox.getChildren().remove(confirmLabel));
                                    pause.play();
                                }
                            });
                            
                            openButton.setDisable(false);
                            openButton.setOnAction(e -> {
                                try {
                                    // Ouvrir l'URL dans le navigateur par défaut
                                    java.awt.Desktop.getDesktop().browse(new java.net.URI(mapUrl));
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                    // Afficher un message d'erreur
                                    Label errorLabel = new Label("Impossible d'ouvrir le navigateur !");
                                    errorLabel.setStyle("-fx-text-fill: #e74c3c;");
                                    if (!locationInfoBox.getChildren().contains(errorLabel)) {
                                        locationInfoBox.getChildren().add(errorLabel);
                                        
                                        // Faire disparaître le message après 3 secondes
                                        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.seconds(3));
                                        pause.setOnFinished(event -> locationInfoBox.getChildren().remove(errorLabel));
                                        pause.play();
                                    }
                                }
                            });
                            
                            // Charger la carte avec les coordonnées réelles
                            // Construire le HTML pour la carte OpenStreetMap avec Leaflet
                            String mapHtml = "<!DOCTYPE html>\n" +
                                    "<html>\n" +
                                    "<head>\n" +
                                    "    <meta charset=\"utf-8\">\n" +
                                    "    <title>OpenStreetMap</title>\n" +
                                    "    <link rel=\"stylesheet\" href=\"https://unpkg.com/leaflet@1.7.1/dist/leaflet.css\"\n" +
                                    "          integrity=\"sha512-xodZBNTC5n17Xt2atTPuE1HxjVMSvLVW9ocqUKLsCC5CXdbqCmblAshOMAS6/keqq/sMZMZ19scR4PsZChSR7A==\"\n" +
                                    "          crossorigin=\"\"/>\n" +
                                    "    <script src=\"https://unpkg.com/leaflet@1.7.1/dist/leaflet.js\"\n" +
                                    "            integrity=\"sha512-XQoYMqMTK8LvdxXYG3nZ448hOEQiglfqkJs1NOQV44cWnUrBc8PkAOcXy20w0vlaXaVUearIOBhiXZ5V3ynxwA==\"\n" +
                                    "            crossorigin=\"\"></script>\n" +
                                    "    <style>\n" +
                                    "        html, body { margin: 0; padding: 0; height: 100%; }\n" +
                                    "        #map { width: 100%; height: 100%; }\n" +
                                    "    </style>\n" +
                                    "</head>\n" +
                                    "<body>\n" +
                                    "    <div id=\"map\"></div>\n" +
                                    "    <script>\n" +
                                    "        var map = L.map('map').setView([" + lat + ", " + lon + "], 15);\n" +
                                    "        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {\n" +
                                    "            attribution: '&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors'\n" +
                                    "        }).addTo(map);\n" +
                                    "        var marker = L.marker([" + lat + ", " + lon + "]).addTo(map);\n" +
                                    "        marker.bindPopup(\"" + location.replace("\"", "\\\"") + "\").openPopup();\n" +
                                    "    </script>\n" +
                                    "</body>\n" +
                                    "</html>";
                            
                            webEngine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
                                if (newState == javafx.concurrent.Worker.State.SUCCEEDED) {
                                    loadingMapBox.setVisible(false);
                                }
                            });
                            
                            webEngine.loadContent(mapHtml);
                        });
                        
                    } else {
                        // Aucun résultat trouvé
                        javafx.application.Platform.runLater(() -> {
                            locationInfoBox.getChildren().clear();
                            
                            Label errorLabel = new Label("Aucune information trouvée pour cette localisation");
                            errorLabel.setStyle("-fx-font-weight: bold;");
                            
                            locationInfoBox.getChildren().add(errorLabel);
                            
                            // Désactiver les boutons
                            copyButton.setDisable(true);
                            openButton.setDisable(true);
                            
                            // Afficher un message d'erreur dans la carte
                            webEngine.loadContent("<html><body style='display:flex;justify-content:center;align-items:center;height:100%;background:#f8f9fa;'><h2 style='color:#e74c3c;text-align:center;'>Aucune information cartographique disponible</h2></body></html>");
                            loadingMapBox.setVisible(false);
                        });
                    }
                } else {
                    // Erreur de l'API
                    javafx.application.Platform.runLater(() -> {
                        locationInfoBox.getChildren().clear();
                        
                        Label errorLabel = new Label("Erreur lors de la récupération des informations de localisation");
                        errorLabel.setStyle("-fx-font-weight: bold;");
                        
                        Label errorCodeLabel = new Label("Code d'erreur: " + responseCode);
                        
                        locationInfoBox.getChildren().addAll(errorLabel, errorCodeLabel);
                        
                        // Désactiver les boutons
                        copyButton.setDisable(true);
                        openButton.setDisable(true);
                        
                        // Afficher un message d'erreur dans la carte
                        webEngine.loadContent("<html><body style='display:flex;justify-content:center;align-items:center;height:100%;background:#f8f9fa;'><h2 style='color:#e74c3c;text-align:center;'>Erreur de connexion à l'API de cartographie</h2></body></html>");
                        loadingMapBox.setVisible(false);
                    });
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                javafx.application.Platform.runLater(() -> {
                    locationInfoBox.getChildren().clear();
                    
                    Label errorLabel = new Label("Erreur lors de la récupération des informations");
                    errorLabel.setStyle("-fx-font-weight: bold;");
                    
                    Label errorDetailsLabel = new Label(e.getMessage());
                    errorDetailsLabel.setWrapText(true);
                    
                    locationInfoBox.getChildren().addAll(errorLabel, errorDetailsLabel);
                    
                    // Désactiver les boutons
                    copyButton.setDisable(true);
                    openButton.setDisable(true);
                    
                    // Afficher un message d'erreur dans la carte
                    webEngine.loadContent("<html><body style='display:flex;justify-content:center;align-items:center;height:100%;background:#f8f9fa;'><h2 style='color:#e74c3c;text-align:center;'>Erreur lors du chargement de la carte</h2></body></html>");
                    loadingMapBox.setVisible(false);
                });
            }
        }).start();
    }
}