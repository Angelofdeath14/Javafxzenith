package tn.esprit.utils;

import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

/**
 * Utilitaire pour l'intégration de cartes interactives dans l'interface utilisateur
 * en utilisant des services de cartographie via WebView
 */
public class MapAPI {
    
    private static final String API_KEY = "votre_clé_api"; // À remplacer par une vraie clé API Google Maps ou OpenStreetMap
    
    /**
     * Charge une carte Google Maps dans un composant WebView
     * @param webView Le composant WebView qui affichera la carte
     * @param latitude Latitude du centre de la carte
     * @param longitude Longitude du centre de la carte
     * @param zoom Niveau de zoom (1-20)
     */
    public static void loadGoogleMap(WebView webView, double latitude, double longitude, int zoom) {
        WebEngine webEngine = webView.getEngine();
        
        String htmlContent = 
            "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <meta name=\"viewport\" content=\"initial-scale=1.0, user-scalable=no\">\n" +
            "    <style>\n" +
            "        html, body, #map-canvas {\n" +
            "            height: 100%;\n" +
            "            margin: 0;\n" +
            "            padding: 0;\n" +
            "        }\n" +
            "    </style>\n" +
            "    <script src=\"https://maps.googleapis.com/maps/api/js?key=" + API_KEY + "\"></script>\n" +
            "    <script>\n" +
            "        function initialize() {\n" +
            "            var mapOptions = {\n" +
            "                center: new google.maps.LatLng(" + latitude + ", " + longitude + "),\n" +
            "                zoom: " + zoom + "\n" +
            "            };\n" +
            "            var map = new google.maps.Map(document.getElementById('map-canvas'), mapOptions);\n" +
            "            var marker = new google.maps.Marker({\n" +
            "                position: new google.maps.LatLng(" + latitude + ", " + longitude + "),\n" +
            "                map: map,\n" +
            "                title: 'Emplacement'\n" +
            "            });\n" +
            "        }\n" +
            "        google.maps.event.addDomListener(window, 'load', initialize);\n" +
            "    </script>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <div id=\"map-canvas\"></div>\n" +
            "</body>\n" +
            "</html>";
        
        webEngine.loadContent(htmlContent);
    }
    
    /**
     * Charge une carte OpenStreetMap dans un composant WebView (alternative gratuite)
     * @param webView Le composant WebView qui affichera la carte
     * @param latitude Latitude du centre de la carte
     * @param longitude Longitude du centre de la carte
     * @param zoom Niveau de zoom (1-19)
     */
    public static void loadOpenStreetMap(WebView webView, double latitude, double longitude, int zoom) {
        WebEngine webEngine = webView.getEngine();
        
        String htmlContent = 
            "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "    <meta charset=\"UTF-8\">\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
            "    <link rel=\"stylesheet\" href=\"https://unpkg.com/leaflet@1.7.1/dist/leaflet.css\" />\n" +
            "    <style>\n" +
            "        html, body, #map {\n" +
            "            height: 100%;\n" +
            "            margin: 0;\n" +
            "            padding: 0;\n" +
            "        }\n" +
            "    </style>\n" +
            "    <script src=\"https://unpkg.com/leaflet@1.7.1/dist/leaflet.js\"></script>\n" +
            "</head>\n" +
            "<body>\n" +
            "    <div id=\"map\"></div>\n" +
            "    <script>\n" +
            "        var map = L.map('map').setView([" + latitude + ", " + longitude + "], " + zoom + ");\n" +
            "        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {\n" +
            "            attribution: '&copy; <a href=\"https://www.openstreetmap.org/copyright\">OpenStreetMap</a> contributors'\n" +
            "        }).addTo(map);\n" +
            "        L.marker([" + latitude + ", " + longitude + "]).addTo(map)\n" +
            "            .bindPopup('Emplacement')\n" +
            "            .openPopup();\n" +
            "    </script>\n" +
            "</body>\n" +
            "</html>";
        
        webEngine.loadContent(htmlContent);
    }
    
    /**
     * Génère un URL pour une image de carte statique Google Maps
     * Peut être utilisée avec une ImageView
     * @param latitude Latitude du centre de la carte
     * @param longitude Longitude du centre de la carte
     * @param zoom Niveau de zoom (1-20)
     * @param width Largeur de l'image en pixels
     * @param height Hauteur de l'image en pixels
     * @return URL de l'image de carte statique
     */
    public static String getStaticMapUrl(double latitude, double longitude, int zoom, int width, int height) {
        return "https://maps.googleapis.com/maps/api/staticmap?" +
               "center=" + latitude + "," + longitude +
               "&zoom=" + zoom +
               "&size=" + width + "x" + height +
               "&markers=color:red%7C" + latitude + "," + longitude +
               "&key=" + API_KEY;
    }
    
    /**
     * Génère un lien vers Google Maps pour obtenir les directions vers un lieu
     * @param latitude Latitude de destination
     * @param longitude Longitude de destination
     * @param nom Nom du lieu de destination (optionnel)
     * @return URL vers Google Maps avec les directions
     */
    public static String getDirectionsUrl(double latitude, double longitude, String nom) {
        String destination = latitude + "," + longitude;
        if (nom != null && !nom.isEmpty()) {
            destination = nom.replace(" ", "+");
        }
        return "https://www.google.com/maps/dir/?api=1&destination=" + destination;
    }
} 