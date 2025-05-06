package Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Classe utilitaire pour la journalisation
 * Permet d'enregistrer les événements et les erreurs dans un fichier de log
 */
public class LogUtils {
    
    private static final String LOG_DIRECTORY = "logs";
    private static final String LOG_FILE = "artphoria_app.log";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    static {
        // S'assurer que le répertoire de logs existe
        File directory = new File(LOG_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdir();
        }
    }
    
    /**
     * Enregistre un message d'information dans le fichier de log
     * @param tag Étiquette identifiant la source du message
     * @param message Le message à enregistrer
     */
    public static void info(String tag, String message) {
        log("INFO", tag, message, null);
        // Afficher également sur la console pour le débogage
        System.out.println("[INFO][" + tag + "] " + message);
    }
    
    /**
     * Enregistre un message d'erreur dans le fichier de log
     * @param tag Étiquette identifiant la source du message
     * @param message Le message d'erreur
     * @param e L'exception associée (peut être null)
     */
    public static void error(String tag, String message, Throwable e) {
        log("ERROR", tag, message, e);
        // Afficher également sur la console pour le débogage
        System.err.println("[ERROR][" + tag + "] " + message);
        if (e != null) {
            e.printStackTrace();
        }
    }
    
    /**
     * Méthode interne pour enregistrer un message dans le fichier de log
     */
    private static void log(String level, String tag, String message, Throwable e) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_DIRECTORY + File.separator + LOG_FILE, true))) {
            LocalDateTime now = LocalDateTime.now();
            writer.println(now.format(DATE_FORMAT) + " [" + level + "][" + tag + "] " + message);
            
            if (e != null) {
                writer.println("Exception: " + e.getMessage());
                e.printStackTrace(writer);
                writer.println();
            }
        } catch (IOException ex) {
            System.err.println("Impossible d'écrire dans le fichier de log: " + ex.getMessage());
        }
    }
} 