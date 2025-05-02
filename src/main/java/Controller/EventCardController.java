package Controller;

import Entity.Evenement;
import Entity.Session;
import Utils.AnimationUtils;
import Utils.MainStyleFixer;
import Service.SessionService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.function.Consumer;

/**
 * Contrôleur pour les cartes d'événements stylisées
 * Permet d'afficher les événements dans des cadres visuellement attrayants
 */
public class EventCardController {
    @FXML private VBox eventCard;
    @FXML private ImageView eventImage;
    @FXML private Label eventTitle;
    @FXML private Label eventDate;
    @FXML private Label eventLocation;
    @FXML private Label eventType;
    @FXML private Label eventPrice;
    @FXML private Label availabilityBadge;
    @FXML private Button btnDetails;
    @FXML private Button btnReserver;
    
    private Evenement event;
    private Consumer<Evenement> onDetailsClickHandler;
    private Consumer<Evenement> onReserveClickHandler;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
    
    /**
     * Initialise la carte d'événement avec les données fournies
     * @param event Événement à afficher
     * @param onDetailsClick Action à exécuter lors du clic sur Détails
     * @param onReserveClick Action à exécuter lors du clic sur Réserver
     */
    public void initialize(Evenement event, Consumer<Evenement> onDetailsClick, Consumer<Evenement> onReserveClick) {
        this.event = event;
        this.onDetailsClickHandler = onDetailsClick;
        this.onReserveClickHandler = onReserveClick;
        
        setupCardUI();
        loadEventData();
        setupButtonActions();
        applyHoverEffects();
    }
    
    /**
     * Configure l'apparence de la carte
     */
    private void setupCardUI() {
        // Appliquer un style moderne à la carte
        eventCard.getStyleClass().add("event-card");
        eventCard.setPadding(new Insets(0));
        eventCard.setSpacing(8);
        
        // Appliquer des coins arrondis à l'image
        Rectangle clip = new Rectangle(eventImage.getFitWidth(), eventImage.getFitHeight());
        clip.setArcWidth(15);
        clip.setArcHeight(15);
        eventImage.setClip(clip);
        
        // Ajouter un effet d'ombre à la carte
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.2));
        shadow.setOffsetX(0);
        shadow.setOffsetY(3);
        shadow.setRadius(6);
        eventCard.setEffect(shadow);
        
        // Styliser les badges de disponibilité
        styleBadge();
        
        // Styliser les boutons
        styleButtons();
    }
    
    /**
     * Charge les données de l'événement dans la carte
     */
    private void loadEventData() {
        // Titre de l'événement
        eventTitle.setText(event.getTitre());
        eventTitle.setWrapText(true);
        
        // Date formatée
        String dateText = "";
        if (event.getDateD() != null) {
            dateText = event.getDateD().format(formatter);
            if (event.getDateF() != null && !event.getDateD().equals(event.getDateF())) {
                dateText += " - " + event.getDateF().format(formatter);
            }
        }
        eventDate.setText(dateText);
        
        // Lieu de l'événement
        eventLocation.setText(event.getLocation());
        
        // Type d'événement
        eventType.setText(event.getType());
        
        // Prix (simulé)
        int price = 15 + (event.getId() % 35); // Prix simulé entre 15€ et 50€
        eventPrice.setText(price + " €");
        
        // Chargement de l'image
        loadEventImage();
        
        // Vérifier la disponibilité et configurer le badge
        updateAvailabilityBadge();
    }
    
    /**
     * Charge l'image de l'événement
     */
    private void loadEventImage() {
        try {
            String imagePath = event.getImage();
            
            if (imagePath != null && !imagePath.isEmpty()) {
                Image image;
                
                // Vérifier si c'est une URL ou un chemin local
                if (imagePath.startsWith("http") || imagePath.startsWith("www")) {
                    // Image à partir d'une URL
                    image = new Image(imagePath, true);
                } else {
                    // Image locale
                    File file = new File(imagePath);
                    if (file.exists()) {
                        image = new Image(file.toURI().toString());
                    } else {
                        // Image par défaut si le fichier n'existe pas
                        loadDefaultImage(event.getType());
                        return;
                    }
                }
                
                eventImage.setImage(image);
                
                // Gérer les erreurs de chargement d'image
                image.errorProperty().addListener((obs, oldError, error) -> {
                    if (error) {
                        loadDefaultImage(event.getType());
                    }
                });
            } else {
                loadDefaultImage(event.getType());
            }
        } catch (Exception e) {
            loadDefaultImage(event.getType());
        }
    }
    
    /**
     * Charge une image par défaut basée sur le type d'événement
     * @param type Type d'événement
     */
    private void loadDefaultImage(String type) {
        String defaultImage;
        
        // Choisir une image par défaut selon le type d'événement
        switch (type.toLowerCase()) {
            case "concert":
                defaultImage = "/images/default_concert.jpg";
                break;
            case "exposition":
                defaultImage = "/images/default_exposition.jpg";
                break;
            case "théâtre":
            case "theatre":
                defaultImage = "/images/default_theatre.jpg";
                break;
            case "festival":
                defaultImage = "/images/default_festival.jpg";
                break;
            default:
                defaultImage = "/images/default_event.jpg";
        }
        
        try {
            Image image = new Image(getClass().getResourceAsStream(defaultImage));
            eventImage.setImage(image);
        } catch (Exception e) {
            // Si l'image par défaut ne peut pas être chargée, utiliser un placeholder avec emoji
            createEmojiPlaceholder();
        }
    }
    
    /**
     * Crée un placeholder avec emoji quand aucune image n'est disponible
     */
    private void createEmojiPlaceholder() {
        // Obtenir l'emoji approprié selon le type d'événement
        String emoji;
        switch (event.getType().toLowerCase()) {
            case "concert":
                emoji = "🎵";
                break;
            case "exposition":
                emoji = "🎨";
                break;
            case "théâtre":
            case "theatre":
                emoji = "🎭";
                break;
            case "festival":
                emoji = "🎪";
                break;
            default:
                emoji = "🎟️";
        }
        
        // Créer un conteneur stylisé avec l'emoji
        StackPane placeholder = new StackPane();
        placeholder.setStyle("-fx-background-color: #f0f0f0;");
        placeholder.setPrefSize(eventImage.getFitWidth(), eventImage.getFitHeight());
        
        Label emojiLabel = new Label(emoji);
        emojiLabel.setStyle("-fx-font-size: 48px;");
        emojiLabel.setAlignment(Pos.CENTER);
        
        placeholder.getChildren().add(emojiLabel);
        
        // Remplacer l'ImageView par le placeholder
        int indexOfImage = eventCard.getChildren().indexOf(eventImage);
        if (indexOfImage >= 0) {
            eventCard.getChildren().set(indexOfImage, placeholder);
        }
    }
    
    /**
     * Met à jour le badge de disponibilité selon les places disponibles
     */
    private void updateAvailabilityBadge() {
        try {
            // Récupérer les sessions associées à cet événement
            SessionService sessionService = new SessionService();
            List<Session> sessions = sessionService.getSessionsByEvent(event.getId());
            
            int totalCapacity = 0;
            int totalAvailable = 0;
            
            for (Session session : sessions) {
                totalCapacity += session.getAvailableSeats();
                totalAvailable += session.getCapacity();
            }
            
            int reservedSeats = totalCapacity - totalAvailable;
            double fillRate = totalCapacity > 0 ? (double) reservedSeats / totalCapacity * 100 : 0;
            
            // Configurer le texte et la classe CSS selon la disponibilité
            if (fillRate >= 98) {
                availabilityBadge.setText("COMPLET");
                availabilityBadge.getStyleClass().add("badge-sold-out");
            } else if (fillRate >= 85) {
                availabilityBadge.setText("DERNIÈRES PLACES");
                availabilityBadge.getStyleClass().add("badge-last-seats");
            } else {
                int placesRestantes = totalCapacity - reservedSeats;
                availabilityBadge.setText(placesRestantes + " PLACES");
                availabilityBadge.getStyleClass().add("badge-available");
            }
            
        } catch (SQLException e) {
            // En cas d'erreur, masquer le badge
            availabilityBadge.setVisible(false);
        }
    }
    
    /**
     * Style le badge de disponibilité
     */
    private void styleBadge() {
        availabilityBadge.getStyleClass().add("event-badge");
        availabilityBadge.setPadding(new Insets(3, 8, 3, 8));
    }
    
    /**
     * Style les boutons d'action
     */
    private void styleButtons() {
        // Style du bouton Détails
        btnDetails.getStyleClass().add("secondary");
        
        // Style du bouton Réserver
        btnReserver.getStyleClass().add("button");
    }
    
    /**
     * Configure les actions des boutons
     */
    private void setupButtonActions() {
        btnDetails.setOnAction(e -> {
            if (onDetailsClickHandler != null) {
                onDetailsClickHandler.accept(event);
            }
        });
        
        btnReserver.setOnAction(e -> {
            if (onReserveClickHandler != null) {
                onReserveClickHandler.accept(event);
            }
        });
    }
    
    /**
     * Applique des effets de survol à la carte
     */
    private void applyHoverEffects() {
        // Effet de survol pour toute la carte
        AnimationUtils.addHoverEffect(eventCard);
        
        // Effets de clic pour les boutons
        AnimationUtils.addClickEffect(btnDetails);
        AnimationUtils.addClickEffect(btnReserver);
    }
    
    /**
     * Crée une carte d'événement complète
     * @param event L'événement à afficher
     * @param onDetails Action lors du clic sur Détails
     * @param onReserve Action lors du clic sur Réserver
     * @return Le nœud de la carte d'événement
     */
    public static VBox createEventCard(Evenement event, Consumer<Evenement> onDetails, Consumer<Evenement> onReserve) {
        try {
            FXMLLoader loader = new FXMLLoader(EventCardController.class.getResource("/EventCard.fxml"));
            VBox card = loader.load();
            
            EventCardController controller = loader.getController();
            controller.initialize(event, onDetails, onReserve);
            
            return card;
        } catch (IOException e) {
            // En cas d'erreur, créer une carte simplifiée
            return createSimpleEventCard(event, onDetails, onReserve);
        }
    }
    
    /**
     * Crée une carte d'événement simplifiée (fallback en cas d'erreur)
     * @param event L'événement à afficher
     * @param onDetails Action lors du clic sur Détails
     * @param onReserve Action lors du clic sur Réserver
     * @return Le nœud de la carte d'événement
     */
    private static VBox createSimpleEventCard(Evenement event, Consumer<Evenement> onDetails, Consumer<Evenement> onReserve) {
        VBox card = new VBox(10);
        card.getStyleClass().add("event-card");
        card.setPadding(new Insets(15));
        card.setMinWidth(280);
        card.setMaxWidth(280);
        
        Label title = new Label(event.getTitre());
        title.getStyleClass().add("event-title");
        title.setWrapText(true);
        
        Label date = new Label(event.getDateD() != null ? 
                event.getDateD().format(DateTimeFormatter.ofPattern("dd MMM yyyy")) : "");
        date.getStyleClass().add("event-date");
        
        Label location = new Label(event.getLocation());
        location.getStyleClass().add("event-location");
        
        HBox buttons = new HBox(10);
        buttons.setAlignment(Pos.CENTER);
        
        Button btnDetails = new Button("Détails");
        btnDetails.getStyleClass().add("secondary");
        btnDetails.setOnAction(e -> {
            if (onDetails != null) onDetails.accept(event);
        });
        
        Button btnReserver = new Button("Réserver");
        btnReserver.setOnAction(e -> {
            if (onReserve != null) onReserve.accept(event);
        });
        
        buttons.getChildren().addAll(btnDetails, btnReserver);
        
        card.getChildren().addAll(title, date, location, buttons);
        
        // Appliquer les effets
        AnimationUtils.addHoverEffect(card);
        
        return card;
    }
} 