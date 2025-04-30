package Utils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;
import java.net.URL;

public class FXMLUtils {

    /**
     * Charge un fichier FXML de manière fiable en utilisant plusieurs méthodes
     * @param fxmlPath Chemin du fichier FXML (avec ou sans '/' initial)
     * @param controllerClass Classe du contrôleur (pour avoir accès au ClassLoader)
     * @return L'objet Parent chargé depuis le fichier FXML
     * @throws IOException Si le fichier ne peut pas être chargé
     */
    public static Parent loadFXML(String fxmlPath, Class<?> controllerClass) throws IOException {
        // Assurez-vous que le chemin commence par '/'
        String path = fxmlPath.startsWith("/") ? fxmlPath : "/" + fxmlPath;
        
        // Méthode 1: Utiliser getResource
        URL url = controllerClass.getResource(path);
        
        // Méthode 2: Si échec, utiliser le ClassLoader
        if (url == null) {
            url = controllerClass.getClassLoader().getResource(path.substring(1));
        }
        
        // Méthode 3: Si échec, utiliser getResourceAsStream
        if (url == null) {
            // Construire l'URL manuellement
            String resourcePath = "file:" + System.getProperty("user.dir") + "/target/classes" + path;
            url = new URL(resourcePath);
        }
        
        // Si toujours null, lever une exception
        if (url == null) {
            throw new IOException("Le fichier FXML '" + fxmlPath + "' n'a pas pu être trouvé via aucune méthode.");
        }
        
        // Charger le FXML
        FXMLLoader loader = new FXMLLoader(url);
        return loader.load();
    }
} 