package tn.esprit.utils;

import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service d'accès à la fonctionnalité Text-to-Speech (TTS)
 * Utilise le moteur TTS de Windows via PowerShell et inclut un son de confirmation
 */
public class TTSService {
    
    private static TTSService instance;
    private boolean isSpeaking = false;
    private Process currentProcess;
    private MediaPlayer mediaPlayer;
    private static final Logger LOGGER = Logger.getLogger(TTSService.class.getName());
    
    private TTSService() {
        // Constructeur privé pour Singleton
        // Précharger le son de notification
        try {
            // Essayer de précharger un son de notification pour utilisation ultérieure
            URL soundURL = getClass().getResource("/sounds/notification.mp3");
            if (soundURL != null) {
                Media sound = new Media(soundURL.toString());
                mediaPlayer = new MediaPlayer(sound);
                mediaPlayer.setVolume(0.5);
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Impossible de précharger le son de notification", e);
        }
    }
    
    /**
     * Obtenir l'instance unique du service TTS
     */
    public static TTSService getInstance() {
        if (instance == null) {
            instance = new TTSService();
        }
        return instance;
    }
    
    /**
     * Joue un bref son de notification
     * @return true si le son a été joué avec succès
     */
    public boolean playNotificationSound() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.play();
                return true;
            }
            
            // Si MediaPlayer n'est pas disponible, essayer avec un beep système
            return systemBeep();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Échec de la lecture du son de notification", e);
            return systemBeep(); // Fallback vers un beep système
        }
    }
    
    /**
     * Génère un beep système simple
     * @return true si le beep a été généré avec succès
     */
    private boolean systemBeep() {
        try {
            // Essayer un beep via Java
            java.awt.Toolkit.getDefaultToolkit().beep();
            
            // Essayer également un beep via PowerShell pour plus de fiabilité
            Process process = Runtime.getRuntime().exec(
                "powershell -Command \"[System.Console]::Beep(800, 200)\""
            );
            process.waitFor(500, java.util.concurrent.TimeUnit.MILLISECONDS);
            
            return true;
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Échec du beep système: " + e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * Lit le texte fourni à voix haute
     * @param text Le texte à lire
     * @param async Si true, la lecture est asynchrone 
     */
    public void speak(String text, boolean async) {
        if (text == null || text.trim().isEmpty()) {
            LOGGER.warning("Tentative de lecture d'un texte vide");
            return;
        }
        
        // Jouer un son de notification avant de commencer la lecture
        playNotificationSound();
        
        // Arrêter toute lecture en cours
        if (isSpeaking) {
            stop();
        }
        
        Task<Void> speakTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    isSpeaking = true;
                    
                    // Nettoyer le texte
                    String cleanText = text.replace("\"", "\\\"")
                                           .replace("\n", " ")
                                           .replace("\r", " ");
                    
                    // Utiliser une commande PowerShell avec détection de voix française
                    String command = String.format(
                        "powershell -Command \"" +
                        "Add-Type -AssemblyName System.Speech; " +
                        "$speak = New-Object System.Speech.Synthesis.SpeechSynthesizer; " +
                        "$speak.Volume = 100; " +
                        "$speak.Rate = 0; " +
                        "$frenchVoices = $speak.GetInstalledVoices() | Where-Object { $_.VoiceInfo.Culture.Name -like 'fr*' }; " +
                        "if ($frenchVoices) { $speak.SelectVoice($frenchVoices[0].VoiceInfo.Name) }; " +
                        "$speak.Speak('%s')\"",
                        cleanText
                    );
                    
                    LOGGER.info("Exécution de la commande TTS: " + command);
                    
                    // Exécution de la commande
                    currentProcess = Runtime.getRuntime().exec(command);
                    
                    // Afficher la sortie standard pour le débogage
                    java.io.BufferedReader reader = new java.io.BufferedReader(
                            new java.io.InputStreamReader(currentProcess.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        LOGGER.info("TTS output: " + line);
                    }
                    
                    // Afficher les erreurs éventuelles
                    java.io.BufferedReader errorReader = new java.io.BufferedReader(
                            new java.io.InputStreamReader(currentProcess.getErrorStream()));
                    while ((line = errorReader.readLine()) != null) {
                        LOGGER.warning("TTS error: " + line);
                    }
                    
                    // Attendre la fin de l'exécution
                    int exitCode = currentProcess.waitFor();
                    
                    if (exitCode != 0) {
                        LOGGER.log(Level.WARNING, "La commande de synthèse vocale s'est terminée avec le code {0}", exitCode);
                    }
                    
                    isSpeaking = false;
                    currentProcess = null;
                    
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Erreur lors de la lecture du texte", e);
                    isSpeaking = false;
                    currentProcess = null;
                    throw e;
                }
                return null;
            }
        };
        
        Thread thread = new Thread(speakTask);
        thread.setDaemon(true);
        thread.start();
        
        // Si non asynchrone, attendre la fin de la lecture
        if (!async) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LOGGER.log(Level.WARNING, "Interruption lors de l'attente de la fin de la lecture", e);
            }
        }
    }
    
    /**
     * Version alternative utilisant un fichier temporaire
     * (utile si le texte est long ou contient des caractères spéciaux)
     */
    public void speakFromFile(String text, boolean async) {
        try {
            // Jouer un son de notification avant de commencer la lecture
            playNotificationSound();
            
            // Arrêter toute lecture en cours
            if (isSpeaking) {
                stop();
            }
            
            // Nettoyer le texte avant de l'écrire dans le fichier
            String cleanedText = text
                .replace("\"", " ")
                .replace("'", " ")
                .replace(";", ", ")
                .replace("<", " ")
                .replace(">", " ")
                .replace("\\", " ")
                .replace("/", " ")
                .replace("\n", ". ") // Remplacer les sauts de ligne par des points pour marquer les pauses
                .replace("\r", " ")
                .replace("\t", " ");
                
            // Création d'un fichier temporaire pour stocker le texte
            File tempFile = File.createTempFile("tts_text", ".txt");
            tempFile.deleteOnExit();
            
            // Écriture du texte dans le fichier avec encodage UTF-8
            java.nio.file.Files.write(tempFile.toPath(), cleanedText.getBytes("UTF-8"));
            
            Task<Void> speakTask = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    try {
                        isSpeaking = true;
                        
                        // Utilisation de la commande PowerShell plus robuste avec pause entre les phrases
                        String command = String.format(
                            "powershell -Command \"Add-Type -AssemblyName System.Speech; " +
                            "$speak = New-Object System.Speech.Synthesis.SpeechSynthesizer; " +
                            "$speak.Volume = 100; " +
                            "$speak.Rate = -1; " + // Parler légèrement plus lentement (-10 à 10)
                            "$frenchVoices = $speak.GetInstalledVoices() | " +
                            "  Where-Object { $_.VoiceInfo.Culture.Name -like 'fr*' -or $_.VoiceInfo.Name -like '*French*' }; " +
                            "if ($frenchVoices -and $frenchVoices.Count -gt 0) { " +
                            "  $speak.SelectVoice($frenchVoices[0].VoiceInfo.Name); " +
                            "  Write-Host ('Voix française sélectionnée: ' + $frenchVoices[0].VoiceInfo.Name); " +
                            "} else { " +
                            "  Write-Host 'Aucune voix française trouvée, utilisation de la voix par défaut'; " +
                            "} " +
                            "$textToRead = [System.IO.File]::ReadAllText('%s'); " +
                            "$paragraphs = $textToRead -split '\\.\\s+'; " +
                            "foreach ($p in $paragraphs) { " +
                            "  if ($p.Trim() -ne '') { " +
                            "    $speak.Speak($p.Trim() + '.') " +
                            "  } " +
                            "} \"",
                            tempFile.getAbsolutePath().replace("\\", "\\\\")
                        );
                        
                        LOGGER.info("Exécution de la commande TTS via fichier: " + command);
                        
                        // Exécution de la commande
                        currentProcess = Runtime.getRuntime().exec(command);
                        
                        // Capturer la sortie standard pour le débogage
                        java.io.BufferedReader reader = new java.io.BufferedReader(
                                new java.io.InputStreamReader(currentProcess.getInputStream()));
                        String line;
                        while ((line = reader.readLine()) != null) {
                            LOGGER.info("TTS output: " + line);
                        }
                        
                        // Capturer les erreurs éventuelles
                        java.io.BufferedReader errorReader = new java.io.BufferedReader(
                                new java.io.InputStreamReader(currentProcess.getErrorStream()));
                        while ((line = errorReader.readLine()) != null) {
                            LOGGER.warning("TTS error: " + line);
                        }
                        
                        // Attendre la fin de l'exécution
                        int exitCode = currentProcess.waitFor();
                        
                        if (exitCode != 0) {
                            LOGGER.log(Level.WARNING, "La commande de synthèse vocale s'est terminée avec le code {0}", exitCode);
                            // Essayer une méthode alternative en cas d'échec
                            return fallbackSpeak(text);
                        }
                        
                        isSpeaking = false;
                        currentProcess = null;
                        
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "Erreur lors de la lecture du texte", e);
                        isSpeaking = false;
                        currentProcess = null;
                        throw e;
                    }
                    return null;
                }
            };
            
            Thread thread = new Thread(speakTask);
            thread.setDaemon(true);
            thread.start();
            
            // Si non asynchrone, attendre la fin de la lecture
            if (!async) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    LOGGER.log(Level.WARNING, "Interruption lors de l'attente de la fin de la lecture", e);
                }
            }
            
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'utilisation du TTS système", e);
            showAlert("Erreur de lecture", "Une erreur est survenue lors de la lecture du texte avec le système : " + e.getMessage());
        }
    }
    
    /**
     * Méthode de repli en cas d'échec de la méthode principale
     */
    private Void fallbackSpeak(String text) {
        try {
            // Méthode alternative si la première échoue
            String command = String.format(
                "powershell -Command \"" +
                "Add-Type -AssemblyName System.Speech; " +
                "$speak = New-Object System.Speech.Synthesis.SpeechSynthesizer; " +
                "$speak.Volume = 100; " +
                "$speak.Speak('%s')\"",
                text.replace("\"", "\\\"").replace("\n", " ").replace("\r", " ")
            );
            
            LOGGER.info("Tentative de repli avec: " + command);
            
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Échec également de la méthode de repli", e);
        } finally {
            isSpeaking = false;
            currentProcess = null;
        }
        return null;
    }
    
    /**
     * Arrête la lecture en cours
     */
    public void stop() {
        if (isSpeaking && currentProcess != null) {
            try {
                currentProcess.destroy();
                isSpeaking = false;
                currentProcess = null;
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Erreur lors de l'arrêt de la lecture", e);
            }
        }
    }
    
    /**
     * Vérifie si une lecture est en cours
     */
    public boolean isSpeaking() {
        return isSpeaking;
    }
    
    /**
     * Teste si le son fonctionne sur le système
     * @return true si le son a été joué avec succès
     */
    public boolean testSound() {
        // Essayer de jouer le son de notification
        boolean notificationPlayed = playNotificationSound();
        
        // Si ça ne marche pas, essayer un beep système
        if (!notificationPlayed) {
            try {
                // Utiliser System.out pour envoyer un bip au terminal
                System.out.print("\u0007");
                
                // Essayer également un beep via PowerShell
                Process process = Runtime.getRuntime().exec(
                    "powershell -Command \"[System.Console]::Beep(1000, 300)\""
                );
                process.waitFor(300, java.util.concurrent.TimeUnit.MILLISECONDS);
                
                return true;
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Échec du test sonore: " + e.getMessage(), e);
                return false;
            }
        }
        
        return notificationPlayed;
    }
    
    /**
     * Affiche une alerte
     */
    private void showAlert(String title, String message) {
        javafx.application.Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
} 