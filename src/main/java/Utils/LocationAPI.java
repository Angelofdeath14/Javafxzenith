package Utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
import org.json.JSONArray;

/**
 * Utilitaire pour obtenir des informations de localisation via une API externe
 */
public class LocationAPI {
    
    private static final String API_KEY = "votre_clé_api"; // À remplacer par une vraie clé API
    private static final String BASE_URL = "https://api.weatherapi.com/v1";
    
    /**
     * Recherche des lieux par nom
     * @param query Nom du lieu à rechercher
     * @return JSONArray contenant les résultats de recherche
     */
    public static JSONArray searchLocation(String query) {
        try {
            String urlString = BASE_URL + "/search.json?key=" + API_KEY + "&q=" + query;
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
            
            return new JSONArray(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return new JSONArray();
        }
    }
    
    /**
     * Obtient les coordonnées géographiques (latitude/longitude) d'un lieu
     * @param location Nom du lieu
     * @return Tableau contenant latitude et longitude [lat, lon] ou null en cas d'erreur
     */
    public static double[] getCoordinates(String location) {
        try {
            JSONArray results = searchLocation(location);
            if (results.length() > 0) {
                JSONObject firstResult = results.getJSONObject(0);
                double lat = firstResult.getDouble("lat");
                double lon = firstResult.getDouble("lon");
                return new double[] {lat, lon};
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Obtient le nom complet d'un lieu à partir d'une recherche partielle
     * @param query Recherche partielle
     * @return Nom complet du lieu (ville, région, pays) ou null si non trouvé
     */
    public static String getFullLocationName(String query) {
        try {
            JSONArray results = searchLocation(query);
            if (results.length() > 0) {
                JSONObject firstResult = results.getJSONObject(0);
                return firstResult.getString("name") + ", " + 
                       firstResult.getString("region") + ", " + 
                       firstResult.getString("country");
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
} 