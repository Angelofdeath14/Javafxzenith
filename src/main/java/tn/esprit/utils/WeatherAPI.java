package tn.esprit.utils;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Utilitaire pour obtenir des informations météorologiques via l'API WeatherAPI.com
 */
public class WeatherAPI {
    
    private static final String API_KEY = "votre_clé_api"; // À remplacer par une vraie clé API
    private static final String BASE_URL = "https://api.weatherapi.com/v1";
    
    /**
     * Obtient les conditions météo actuelles pour un lieu donné
     * @param location Nom du lieu ou coordonnées (lat,lon)
     * @return JSONObject contenant les données météo actuelles
     */
    public static JSONObject getCurrentWeather(String location) {
        try {
            String urlString = BASE_URL + "/current.json?key=" + API_KEY + "&q=" + location;
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            
            return new JSONObject(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }
    
    /**
     * Obtient les prévisions météo pour un lieu donné
     * @param location Nom du lieu ou coordonnées (lat,lon)
     * @param days Nombre de jours de prévision (max 14)
     * @return JSONObject contenant les prévisions météo
     */
    public static JSONObject getForecast(String location, int days) {
        try {
            String urlString = BASE_URL + "/forecast.json?key=" + API_KEY + 
                               "&q=" + location + "&days=" + days;
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            
            return new JSONObject(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONObject();
        }
    }
    
    /**
     * Extrait la température actuelle à partir des données météo
     * @param weatherData Données météo au format JSONObject
     * @return Température en degrés Celsius
     */
    public static double getCurrentTemperature(JSONObject weatherData) {
        try {
            if (weatherData.has("current")) {
                JSONObject current = weatherData.getJSONObject("current");
                return current.getDouble("temp_c");
            }
            return 0.0;
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }
    
    /**
     * Extrait les conditions météo actuelles (texte descriptif)
     * @param weatherData Données météo au format JSONObject
     * @return Description textuelle des conditions météo
     */
    public static String getCurrentCondition(JSONObject weatherData) {
        try {
            if (weatherData.has("current")) {
                JSONObject current = weatherData.getJSONObject("current");
                JSONObject condition = current.getJSONObject("condition");
                return condition.getString("text");
            }
            return "Indisponible";
        } catch (Exception e) {
            e.printStackTrace();
            return "Indisponible";
        }
    }
    
    /**
     * Extrait l'URL de l'icône représentant les conditions météo actuelles
     * @param weatherData Données météo au format JSONObject
     * @return URL de l'icône météo
     */
    public static String getWeatherIconUrl(JSONObject weatherData) {
        try {
            if (weatherData.has("current")) {
                JSONObject current = weatherData.getJSONObject("current");
                JSONObject condition = current.getJSONObject("condition");
                return "https:" + condition.getString("icon");
            }
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
} 