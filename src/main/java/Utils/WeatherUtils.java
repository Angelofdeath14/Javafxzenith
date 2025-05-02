package Utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Classe utilitaire pour simuler des données météorologiques
 */
public class WeatherUtils {
    
    private static final Random random = new Random();
    private static final Map<String, WeatherData> CACHED_WEATHER = new HashMap<>();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm, dd/MM/yyyy");
    
    // Conditions météo possibles avec leurs icônes
    private static final String[][] WEATHER_CONDITIONS = {
        {"Ensoleillé", "☀️"},
        {"Partiellement nuageux", "⛅"},
        {"Nuageux", "☁️"},
        {"Pluie légère", "🌦️"},
        {"Pluie", "🌧️"},
        {"Orage", "⛈️"},
        {"Neige", "❄️"},
        {"Brouillard", "🌫️"}
    };
    
    /**
     * Génère des données météo pour un lieu spécifique
     * @param location Nom du lieu
     * @return Données météo générées
     */
    public static WeatherData getWeatherData(String location) {
        // Vérifier si des données existent en cache et sont récentes (moins de 30 minutes)
        if (CACHED_WEATHER.containsKey(location)) {
            WeatherData cached = CACHED_WEATHER.get(location);
            LocalDateTime now = LocalDateTime.now();
            if (cached.getTimestamp().isAfter(now.minusMinutes(30))) {
                return cached;
            }
        }
        
        // Générer de nouvelles données météo
        WeatherData data = generateWeatherData(location);
        CACHED_WEATHER.put(location, data);
        return data;
    }
    
    /**
     * Génère aléatoirement des données météo réalistes
     * @param location Nom du lieu
     * @return Données météo générées
     */
    private static WeatherData generateWeatherData(String location) {
        // Obtenir les coordonnées pour le lieu
        LocationUtils.Coordinates coords = LocationUtils.getCoordinates(location);
        
        // Déterminer la saison actuelle
        int month = LocalDateTime.now().getMonthValue();
        boolean isSummer = month >= 6 && month <= 9;
        boolean isWinter = month == 12 || month <= 2;
        
        // Générer une température adaptée à la saison
        double baseTemp = isSummer ? 28.0 : (isWinter ? 12.0 : 20.0);
        double temperature = baseTemp + (random.nextDouble() * 8) - 4;
        
        // Générer l'humidité (plus élevée près de la côte)
        boolean isCoastal = coords.getLongitude() > 10.0;
        int baseHumidity = isCoastal ? 65 : 45;
        int humidity = baseHumidity + random.nextInt(25);
        
        // Générer la pression atmosphérique
        double pressure = 1013.0 + (random.nextDouble() * 10) - 5;
        
        // Générer la vitesse du vent
        double windSpeed = (random.nextDouble() * 20);
        
        // Déterminer les conditions météo
        int conditionIndex;
        if (isSummer) {
            // En été, plus de chances de temps ensoleillé
            conditionIndex = random.nextInt(100) < 70 ? 0 : random.nextInt(3);
        } else if (isWinter) {
            // En hiver, plus de chances de pluie ou nuages
            conditionIndex = random.nextInt(100) < 70 ? 2 + random.nextInt(3) : random.nextInt(WEATHER_CONDITIONS.length);
        } else {
            // Au printemps/automne, conditions variées
            conditionIndex = random.nextInt(WEATHER_CONDITIONS.length - 1); // Exclure la neige
        }
        
        String weatherDescription = WEATHER_CONDITIONS[conditionIndex][0];
        String weatherIcon = WEATHER_CONDITIONS[conditionIndex][1];
        
        // Ajuster la température en fonction des conditions météo
        if (weatherDescription.contains("Pluie") || weatherDescription.contains("Nuageux")) {
            temperature -= 2 + random.nextDouble() * 3;
        } else if (weatherDescription.contains("Orage")) {
            temperature -= 4 + random.nextDouble() * 3;
        } else if (weatherDescription.contains("Neige")) {
            temperature = Math.min(temperature, 3.0);
        }
        
        return new WeatherData(
            location,
            "Tunisie",
            temperature,
            humidity,
            pressure,
            windSpeed,
            weatherDescription,
            weatherIcon,
            LocalDateTime.now()
        );
    }
    
    /**
     * Classe pour stocker les données météorologiques
     */
    public static class WeatherData {
        private String cityName;
        private String country;
        private double temperature;
        private int humidity;
        private double pressure;
        private double windSpeed;
        private String weatherDescription;
        private String weatherIcon;
        private LocalDateTime timestamp;
        
        public WeatherData(String cityName, String country, double temperature, int humidity, 
                          double pressure, double windSpeed, String weatherDescription, 
                          String weatherIcon, LocalDateTime timestamp) {
            this.cityName = cityName;
            this.country = country;
            this.temperature = temperature;
            this.humidity = humidity;
            this.pressure = pressure;
            this.windSpeed = windSpeed;
            this.weatherDescription = weatherDescription;
            this.weatherIcon = weatherIcon;
            this.timestamp = timestamp;
        }
        
        public String getCityName() {
            return cityName;
        }
        
        public String getCountry() {
            return country;
        }
        
        public double getTemperature() {
            return temperature;
        }
        
        public int getHumidity() {
            return humidity;
        }
        
        public double getPressure() {
            return pressure;
        }
        
        public double getWindSpeed() {
            return windSpeed;
        }
        
        public String getWeatherDescription() {
            return weatherDescription;
        }
        
        public String getWeatherIcon() {
            return weatherIcon;
        }
        
        public LocalDateTime getTimestamp() {
            return timestamp;
        }
        
        public String getFormattedTimestamp() {
            return timestamp.format(formatter);
        }
    }
} 