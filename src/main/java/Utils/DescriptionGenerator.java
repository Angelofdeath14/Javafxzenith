package Utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Classe utilitaire pour générer des descriptions automatiques
 * pour les événements et sessions à partir du titre, date et lieu
 */
public class DescriptionGenerator {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final Random random = new Random();
    
    // Tableaux de modèles de phrases pour les événements
    private static final String[] EVENT_TEMPLATES = {
        "Rejoignez-nous pour {titre}, un événement unique qui se tiendra à {lieu} du {dateDebut} au {dateFin}. Un moment exceptionnel à ne pas manquer !",
        "Découvrez {titre}, l'événement incontournable qui se déroulera à {lieu}. Marquez vos calendriers du {dateDebut} au {dateFin} !",
        "{titre} vous ouvre ses portes à {lieu} ! Du {dateDebut} au {dateFin}, venez vivre une expérience artistique inoubliable.",
        "Nous sommes ravis de vous présenter {titre}, qui aura lieu à {lieu} du {dateDebut} au {dateFin}. Préparez-vous à vivre des moments uniques !",
        "{titre} : l'événement artistique de l'année vous attend à {lieu} du {dateDebut} au {dateFin}. Réservez dès maintenant !",
        "Immergez-vous dans l'univers de {titre} à {lieu}. Rendez-vous du {dateDebut} au {dateFin} pour une aventure culturelle exceptionnelle."
    };
    
    // Tableaux de modèles de phrases pour les sessions
    private static final String[] SESSION_TEMPLATES = {
        "Cette session de {titre} se déroulera le {dateDebut} à {heureDebut} à {lieu}. Une occasion parfaite pour découvrir cet événement dans un cadre privilégié.",
        "Participez à notre session spéciale de {titre} le {dateDebut} à {heureDebut} à {lieu}. Places limitées, ne tardez pas à réserver !",
        "Le {dateDebut} à {heureDebut}, retrouvez-nous à {lieu} pour une session immersive de {titre}. Une expérience à ne pas manquer !",
        "Session exclusive de {titre} à {lieu} : rendez-vous le {dateDebut} à {heureDebut} pour vivre un moment d'exception.",
        "{titre} vous propose une session unique le {dateDebut} à {heureDebut} à {lieu}. Venez découvrir cette expérience artistique en petit comité.",
        "Réservez votre place pour la session de {titre} qui se tiendra le {dateDebut} à {heureDebut} à {lieu}. Un moment privilégié pour tous les amateurs d'art."
    };
    
    // Tableaux de modèles de phrases courtes pour les événements
    private static final String[] SHORT_EVENT_TEMPLATES = {
        "{titre} - {lieu}, {dateDebut} au {dateFin}",
        "Événement {titre} à {lieu} du {dateDebut} au {dateFin}",
        "{titre} ({lieu}) - {dateDebut}-{dateFin}",
        "{titre} | {dateDebut}-{dateFin} | {lieu}",
        "{titre} - {dateDebut} à {dateFin}, {lieu}"
    };
    
    // Tableaux de modèles de phrases courtes pour les sessions
    private static final String[] SHORT_SESSION_TEMPLATES = {
        "Session {titre} - {dateDebut}, {heureDebut} ({lieu})",
        "{titre} | {dateDebut} à {heureDebut} | {lieu}",
        "{dateDebut} {heureDebut}: {titre} à {lieu}",
        "{titre} - {lieu}, {dateDebut} {heureDebut}",
        "Session {titre}: {dateDebut} ({heureDebut}), {lieu}"
    };
    
    /**
     * Génère une description pour un événement
     * @param titre Le titre de l'événement
     * @param lieu Le lieu de l'événement
     * @param dateDebut La date de début
     * @param dateFin La date de fin
     * @return Une description générée automatiquement
     */
    public static String generateEventDescription(String titre, String lieu, LocalDateTime dateDebut, LocalDateTime dateFin) {
        // Sélectionner un modèle aléatoirement
        String template = EVENT_TEMPLATES[random.nextInt(EVENT_TEMPLATES.length)];
        
        // Formatter les dates
        String dateDebutStr = dateDebut.format(DATE_FORMATTER);
        String dateFinStr = dateFin.format(DATE_FORMATTER);
        
        // Remplacer les variables dans le modèle
        return template
                .replace("{titre}", titre)
                .replace("{lieu}", lieu)
                .replace("{dateDebut}", dateDebutStr)
                .replace("{dateFin}", dateFinStr);
    }
    
    /**
     * Génère une description pour une session
     * @param titre Le titre de la session
     * @param lieu Le lieu de la session
     * @param dateDebut La date et heure de début
     * @return Une description générée automatiquement
     */
    public static String generateSessionDescription(String titre, String lieu, LocalDateTime dateDebut) {
        // Sélectionner un modèle aléatoirement
        String template = SESSION_TEMPLATES[random.nextInt(SESSION_TEMPLATES.length)];
        
        // Formatter la date et l'heure
        String dateDebutStr = dateDebut.format(DATE_FORMATTER);
        String heureDebutStr = dateDebut.format(TIME_FORMATTER);
        
        // Remplacer les variables dans le modèle
        return template
                .replace("{titre}", titre)
                .replace("{lieu}", lieu)
                .replace("{dateDebut}", dateDebutStr)
                .replace("{heureDebut}", heureDebutStr);
    }
    
    /**
     * Version enrichie pour générer une description plus détaillée pour un événement
     * @param titre Le titre de l'événement
     * @param type Le type d'événement (concert, exposition, etc.)
     * @param lieu Le lieu de l'événement
     * @param dateDebut La date de début
     * @param dateFin La date de fin
     * @return Une description détaillée générée automatiquement
     */
    public static String generateDetailedEventDescription(String titre, String type, String lieu, LocalDateTime dateDebut, LocalDateTime dateFin) {
        StringBuilder description = new StringBuilder();
        
        // Base description
        description.append(generateEventDescription(titre, lieu, dateDebut, dateFin));
        description.append("\n\n");
        
        // Ajouter des détails selon le type d'événement
        if (type != null && !type.isEmpty()) {
            switch (type.toLowerCase()) {
                case "concert":
                    description.append("Ce concert promet une ambiance musicale exceptionnelle. ");
                    description.append("Venez vibrer au rythme des artistes et partagez un moment musical inoubliable !");
                    break;
                case "exposition":
                    description.append("Cette exposition vous invite à découvrir des œuvres uniques et inspirantes. ");
                    description.append("Une immersion artistique qui ne manquera pas de vous émouvoir et de stimuler votre créativité.");
                    break;
                case "festival":
                    description.append("Ce festival vous offre une programmation riche et diversifiée. ");
                    description.append("Une célébration culturelle pour tous les goûts, à partager entre amis ou en famille.");
                    break;
                case "théâtre":
                case "theatre":
                    description.append("Cette représentation théâtrale vous transportera dans un univers captivant. ");
                    description.append("Laissez-vous emporter par le talent des comédiens et l'émotion des textes.");
                    break;
                case "danse":
                    description.append("Ce spectacle de danse vous éblouira par sa grâce et sa créativité. ");
                    description.append("Une performance artistique qui célèbre le mouvement et l'expression corporelle.");
                    break;
                default:
                    description.append("Cet événement artistique vous promet des moments d'émotion et de découverte. ");
                    description.append("Une occasion unique de vous immerger dans l'univers culturel et de partager votre passion.");
            }
        }
        
        return description.toString();
    }
    
    /**
     * Génère une description courte pour un événement
     * @param titre Le titre de l'événement
     * @param lieu Le lieu de l'événement
     * @param dateDebut La date de début
     * @param dateFin La date de fin
     * @return Une description courte générée automatiquement
     */
    public static String generateShortEventDescription(String titre, String lieu, LocalDateTime dateDebut, LocalDateTime dateFin) {
        // Sélectionner un modèle aléatoirement
        String template = SHORT_EVENT_TEMPLATES[random.nextInt(SHORT_EVENT_TEMPLATES.length)];
        
        // Formatter les dates
        String dateDebutStr = dateDebut.format(DATE_FORMATTER);
        String dateFinStr = dateFin.format(DATE_FORMATTER);
        
        // Remplacer les variables dans le modèle
        return template
                .replace("{titre}", titre)
                .replace("{lieu}", lieu)
                .replace("{dateDebut}", dateDebutStr)
                .replace("{dateFin}", dateFinStr);
    }
    
    /**
     * Génère une description courte pour une session
     * @param titre Le titre de la session
     * @param lieu Le lieu de la session
     * @param dateDebut La date et heure de début
     * @return Une description courte générée automatiquement
     */
    public static String generateShortSessionDescription(String titre, String lieu, LocalDateTime dateDebut) {
        // Sélectionner un modèle aléatoirement
        String template = SHORT_SESSION_TEMPLATES[random.nextInt(SHORT_SESSION_TEMPLATES.length)];
        
        // Formatter la date et l'heure
        String dateDebutStr = dateDebut.format(DATE_FORMATTER);
        String heureDebutStr = dateDebut.format(TIME_FORMATTER);
        
        // Remplacer les variables dans le modèle
        return template
                .replace("{titre}", titre)
                .replace("{lieu}", lieu)
                .replace("{dateDebut}", dateDebutStr)
                .replace("{heureDebut}", heureDebutStr);
    }
} 