package Utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe utilitaire pour gérer les coordonnées géographiques et les informations de localisation
 */
public class LocationUtils {
    
    // Map contenant les coordonnées des lieux populaires en Tunisie
    private static final Map<String, Coordinates> LOCATION_COORDINATES = new HashMap<>();
    
    // Initialisation des données de coordonnées
    static {
        // Tunis et environs
        LOCATION_COORDINATES.put("Tunis", new Coordinates(36.8065, 10.1815, "Centre-ville de Tunis"));
        LOCATION_COORDINATES.put("La Marsa", new Coordinates(36.8789, 10.3246, "La Marsa, banlieue de Tunis"));
        LOCATION_COORDINATES.put("Carthage", new Coordinates(36.8610, 10.3283, "Site archéologique de Carthage"));
        LOCATION_COORDINATES.put("Sidi Bou Said", new Coordinates(36.8688, 10.3414, "Village pittoresque de Sidi Bou Said"));
        LOCATION_COORDINATES.put("Hammamet", new Coordinates(36.4087, 10.6124, "Ville balnéaire d'Hammamet"));
        
        // Autres villes importantes
        LOCATION_COORDINATES.put("Sousse", new Coordinates(35.8256, 10.6408, "Ville de Sousse"));
        LOCATION_COORDINATES.put("Sfax", new Coordinates(34.7406, 10.7603, "Ville de Sfax"));
        LOCATION_COORDINATES.put("Monastir", new Coordinates(35.7643, 10.8113, "Ville de Monastir"));
        LOCATION_COORDINATES.put("Djerba", new Coordinates(33.8076, 10.8451, "Île de Djerba"));
        LOCATION_COORDINATES.put("Tozeur", new Coordinates(33.9198, 8.1229, "Oasis de Tozeur"));
        
        // Lieux culturels et touristiques
        LOCATION_COORDINATES.put("El Jem", new Coordinates(35.3000, 10.7167, "Amphithéâtre d'El Jem"));
        LOCATION_COORDINATES.put("Kairouan", new Coordinates(35.6784, 10.0957, "Ville historique de Kairouan"));
        LOCATION_COORDINATES.put("Musée du Bardo", new Coordinates(36.8095, 10.1340, "Musée national du Bardo"));
        LOCATION_COORDINATES.put("Parc Ennahli", new Coordinates(36.8664, 10.1634, "Parc naturel d'Ennahli"));
        LOCATION_COORDINATES.put("Théâtre municipal de Tunis", new Coordinates(36.7988, 10.1812, "Théâtre municipal de Tunis"));
    }
    
    /**
     * Récupère les coordonnées d'un lieu
     * @param location Nom du lieu
     * @return Les coordonnées du lieu ou des coordonnées par défaut pour Tunis
     */
    public static Coordinates getCoordinates(String location) {
        // Recherche exacte
        if (LOCATION_COORDINATES.containsKey(location)) {
            return LOCATION_COORDINATES.get(location);
        }
        
        // Recherche partielle
        for (Map.Entry<String, Coordinates> entry : LOCATION_COORDINATES.entrySet()) {
            if (location.toLowerCase().contains(entry.getKey().toLowerCase()) || 
                entry.getKey().toLowerCase().contains(location.toLowerCase())) {
                return entry.getValue();
            }
        }
        
        // Par défaut, retourner les coordonnées de Tunis
        return LOCATION_COORDINATES.get("Tunis");
    }
    
    /**
     * Génère une adresse fictive basée sur le lieu
     * @param location Nom du lieu
     * @return Une adresse fictive
     */
    public static String generateAddress(String location) {
        String[] streetNumbers = {"1", "2", "3", "5", "7", "10", "12", "15", "20", "25", "30"};
        String[] streetTypes = {"Avenue", "Rue", "Boulevard", "Place", "Impasse"};
        String[] streetNames = {"des Oliviers", "de la Liberté", "de l'Indépendance", "Habib Bourguiba", 
                               "de Carthage", "de la République", "Ibn Khaldoun", "14 Janvier",
                               "des Jasmins", "de la Méditerranée", "de la Paix"};
        
        int numIdx = (int) (Math.random() * streetNumbers.length);
        int typeIdx = (int) (Math.random() * streetTypes.length);
        int nameIdx = (int) (Math.random() * streetNames.length);
        
        return streetNumbers[numIdx] + ", " + streetTypes[typeIdx] + " " + streetNames[nameIdx] + ", " + location;
    }
    
    /**
     * Génère des informations de transport aléatoires
     * @return Informations de transport
     */
    public static String generateTransportInfo() {
        String[] transportTypes = {
            "Métro ligne 1, Bus 14, 21",
            "Taxi disponible 24/7",
            "Bus 20, 32, 57",
            "Métro ligne 3, Bus 11",
            "Métro ligne 2, 4, Bus 65",
            "Bus 19, 22, 36, 74",
            "Service de navette disponible"
        };
        
        int idx = (int) (Math.random() * transportTypes.length);
        return transportTypes[idx];
    }
    
    /**
     * Génère des informations de parking aléatoires
     * @return Informations de parking
     */
    public static String generateParkingInfo() {
        String[] parkingOptions = {
            "Parking disponible (150 places)",
            "Parking gratuit",
            "Parking payant (5 DT/heure)",
            "Parking souterrain (200 places)",
            "Parking limité",
            "Grand parking disponible",
            "Parking à proximité"
        };
        
        int idx = (int) (Math.random() * parkingOptions.length);
        return parkingOptions[idx];
    }
    
    /**
     * Classe interne pour représenter des coordonnées géographiques
     */
    public static class Coordinates {
        private double latitude;
        private double longitude;
        private String description;
        
        public Coordinates(double latitude, double longitude, String description) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.description = description;
        }
        
        public double getLatitude() {
            return latitude;
        }
        
        public double getLongitude() {
            return longitude;
        }
        
        public String getDescription() {
            return description;
        }
        
        @Override
        public String toString() {
            return String.format("%.4f, %.4f", latitude, longitude);
        }
    }
} 