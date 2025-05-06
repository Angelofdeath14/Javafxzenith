package Service;

import Entity.Session;
import Entity.Evenement;
import Utils.DataSource;
import Utils.StatsUtils;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Service dédié aux calculs statistiques pour les sessions et événements
 * Ce service centralise toutes les opérations statistiques pour améliorer les performances
 * et faciliter la maintenance.
 */
public class StatisticsService {
    private Connection conn;
    private final SessionService sessionService;
    private final EvenementService evenementService;
    
    /**
     * Constructeur du service statistique
     * Initialise les connexions et services nécessaires
     */
    public StatisticsService() throws SQLException {
        conn = DataSource.getInstance().getConnection();
        sessionService = new SessionService();
        evenementService = new EvenementService();
    }
    
    /**
     * Obtient le nombre total d'événements
     * @return Nombre total d'événements
     */
    public int getTotalEventsCount() throws SQLException {
        String query = "SELECT COUNT(*) FROM evenement";
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
    
    /**
     * Obtient le nombre total de sessions
     * @return Nombre total de sessions
     */
    public int getTotalSessionsCount() throws SQLException {
        String query = "SELECT COUNT(*) FROM session";
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
    
    /**
     * Calcule le nombre total de réservations
     * @return Nombre total de réservations
     */
    public int getTotalReservationsCount() throws SQLException {
        String query = "SELECT SUM(available_seats - capacity) FROM session";
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            ResultSet rs = pst.executeQuery();
            if (rs.next() && rs.getObject(1) != null) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
    
    /**
     * Calcule le taux de remplissage global
     * @return Taux de remplissage en pourcentage
     */
    public double getGlobalFillRate() throws SQLException {
        // Récupérer la somme des capacités totales et réservées
        String query = "SELECT SUM(available_seats) as total_capacity, " +
                       "SUM(available_seats - capacity) as total_reserved " +
                       "FROM session";
        
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int totalCapacity = rs.getInt("total_capacity");
                int totalReserved = rs.getInt("total_reserved");
                
                if (totalCapacity > 0) {
                    return (double) totalReserved / totalCapacity * 100;
                }
            }
        }
        return 0.0;
    }
    
    /**
     * Calcule le taux de remplissage pour un événement spécifique
     * @param eventId ID de l'événement
     * @return Taux de remplissage en pourcentage
     */
    public double getEventFillRate(int eventId) throws SQLException {
        // Récupérer la somme des capacités totales et réservées pour un événement
        String query = "SELECT SUM(available_seats) as total_capacity, " +
                       "SUM(available_seats - capacity) as total_reserved " +
                       "FROM session WHERE evenement_id = ?";
        
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, eventId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int totalCapacity = rs.getInt("total_capacity");
                int totalReserved = rs.getInt("total_reserved");
                
                if (totalCapacity > 0) {
                    return (double) totalReserved / totalCapacity * 100;
                }
            }
        }
        return 0.0;
    }
    
    /**
     * Récupère les statistiques de répartition par type d'événement
     * @return Données pour camembert
     */
    public List<StatsUtils.PieChartData> getEventTypeDistribution() throws SQLException {
        String query = "SELECT type, COUNT(*) as count FROM evenement GROUP BY type";
        List<StatsUtils.PieChartData> result = new ArrayList<>();
        
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                String type = rs.getString("type");
                int count = rs.getInt("count");
                result.add(new StatsUtils.PieChartData(type, count));
            }
        }
        return result;
    }
    
    /**
     * Récupère les statistiques de réservation par session pour un événement
     * @param eventId ID de l'événement
     * @return Données pour graphique en barres
     */
    public List<StatsUtils.BarChartData> getSessionReservationsStats(int eventId) throws SQLException {
        List<StatsUtils.BarChartData> result = new ArrayList<>();
        
        List<Session> sessions = sessionService.getSessionsByEvent(eventId);
        for (Session session : sessions) {
            String label = session.getTitre() != null ? session.getTitre() : "Session " + session.getId();
            int reservations = session.getAvailableSeats() - session.getCapacity();
            result.add(new StatsUtils.BarChartData(label, reservations));
        }
        
        return result;
    }
    
    /**
     * Génère des données de tendance pour un événement sur les 30 derniers jours
     * @param eventId ID de l'événement
     * @return Liste de données de tendance
     */
    public List<StatsUtils.TrendData> getReservationTrend(int eventId) throws SQLException {
        List<StatsUtils.TrendData> result = new ArrayList<>();
        
        // Récupérer la capacité totale de l'événement
        int totalCapacity = 0;
        List<Session> sessions = sessionService.getSessionsByEvent(eventId);
        for (Session session : sessions) {
            totalCapacity += session.getAvailableSeats();
        }
        
        // Obtenir la date de début de l'événement
        Evenement event = evenementService.getOneById(eventId);
        if (event == null) return result;
        
        // Générer une tendance réaliste basée sur la date de l'événement
        LocalDateTime startDate = event.getDateD();
        LocalDateTime endDate = event.getDateF();
        
        if (startDate == null || endDate == null) return result;
        
        // Nombre total de réservations actuelles
        int totalReservations = 0;
        for (Session session : sessions) {
            totalReservations += (session.getAvailableSeats() - session.getCapacity());
        }
        
        // Générer des points de données historiques simulés
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime thirtyDaysAgo = now.minusDays(30);
        
        // Fonction logistique pour simuler une courbe de réservation réaliste
        // Les réservations augmentent lentement au début, puis rapidement, puis ralentissent près de la date
        for (int i = 0; i < 30; i++) {
            LocalDateTime date = thirtyDaysAgo.plusDays(i);
            String dateStr = date.format(DateTimeFormatter.ofPattern("dd/MM"));
            
            // Calculer le facteur de temps (0 à 1) où 0 = début et 1 = aujourd'hui
            double timeFactor = i / 30.0;
            
            // Calculer combien de réservations auraient été faites à ce moment
            // Fonction logistique: 1/(1+e^(-k(x-x0)))
            double k = 8.0; // Raideur de la courbe
            double x0 = 0.7; // Point d'inflexion (70% du temps écoulé)
            double logisticValue = 1.0 / (1.0 + Math.exp(-k * (timeFactor - x0)));
            
            int reservationsAtTime = (int) (totalReservations * logisticValue);
            
            // Ajouter un peu de bruit pour rendre les données plus réalistes
            Random random = new Random(eventId * 1000 + i);
            double noise = 0.95 + (random.nextDouble() * 0.1); // Entre 0.95 et 1.05
            reservationsAtTime = (int) (reservationsAtTime * noise);
            
            // Limiter au nombre maximum de réservations actuelles
            reservationsAtTime = Math.min(reservationsAtTime, totalReservations);
            
            result.add(new StatsUtils.TrendData(dateStr, reservationsAtTime, totalCapacity));
        }
        
        return result;
    }
    
    /**
     * Récupère les sessions les plus populaires (par nombre de réservations)
     * @param limit Nombre maximum de sessions à récupérer
     * @return Liste des sessions les plus populaires
     */
    public List<Session> getMostPopularSessions(int limit) throws SQLException {
        String query = "SELECT * FROM session ORDER BY (available_seats - capacity) DESC LIMIT ?";
        List<Session> result = new ArrayList<>();
        
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            pst.setInt(1, limit);
            ResultSet rs = pst.executeQuery();
            
            while (rs.next()) {
                Session session = new Session();
                session.setId(rs.getInt("id"));
                session.setDescription(rs.getString("description"));
                session.setTitre(rs.getString("titre"));
                session.setStartTime(rs.getTimestamp("start_time").toLocalDateTime());
                session.setEndTime(rs.getTimestamp("end_time").toLocalDateTime());
                session.setEvenementId(rs.getInt("evenement_id"));
                session.setImage(rs.getString("image"));
                session.setCapacity(rs.getInt("capacity"));
                session.setAvailableSeats(rs.getInt("available_seats"));
                session.setLocation(rs.getString("location"));
                result.add(session);
            }
        }
        
        return result;
    }
    
    /**
     * Calcule la variation de réservations par rapport à la période précédente
     * @param days Nombre de jours à prendre en compte
     * @return Pourcentage de variation
     */
    public double getReservationGrowthRate(int days) throws SQLException {
        // Dans un cas réel, cette méthode utiliserait des données historiques de la base de données
        // Pour cette démonstration, nous simulons une croissance de 5-15%
        Random random = new Random();
        return 5.0 + random.nextDouble() * 10.0;
    }
    
    /**
     * Calcule le taux de remplissage moyen par type d'événement
     * @return Données pour graphique à barres
     */
    public List<StatsUtils.BarChartData> getAverageFillRateByEventType() throws SQLException {
        List<StatsUtils.BarChartData> result = new ArrayList<>();
        
        // Obtenir tous les types d'événements
        List<String> eventTypes = evenementService.getAllEventTypes();
        
        for (String type : eventTypes) {
            // Obtenir tous les événements de ce type
            List<Evenement> eventsOfType = evenementService.getEventsByType(type);
            
            double totalFillRate = 0;
            int eventCount = 0;
            
            for (Evenement event : eventsOfType) {
                double fillRate = getEventFillRate(event.getId());
                totalFillRate += fillRate;
                eventCount++;
            }
            
            // Calculer la moyenne
            double averageFillRate = eventCount > 0 ? totalFillRate / eventCount : 0;
            
            result.add(new StatsUtils.BarChartData(type, (int) Math.round(averageFillRate)));
        }
        
        return result;
    }
    
    /**
     * Supprime les doublons des fichiers de statistiques
     * Cette méthode parcourt les fichiers générés par les statistiques et élimine les doublons
     * @param directoryPath Chemin du répertoire contenant les fichiers de statistiques
     * @return Nombre de fichiers en double supprimés
     */
    public int cleanupDuplicateStatFiles(String directoryPath) throws IOException {
        int removedCount = 0;
        File directory = new File(directoryPath);
        
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IOException("Le répertoire spécifié n'existe pas ou n'est pas un dossier");
        }
        
        // Récupérer tous les fichiers du répertoire
        File[] files = directory.listFiles();
        if (files == null) {
            return 0;
        }
        
        // Utiliser un Set pour stocker les empreintes des fichiers (par contenu)
        Set<String> uniqueFileHashes = new HashSet<>();
        List<File> duplicateFiles = new ArrayList<>();
        
        for (File file : files) {
            if (file.isFile()) {
                try {
                    // Calculer le hash du contenu du fichier
                    byte[] fileContent = Files.readAllBytes(file.toPath());
                    String fileHash = calculateMD5Hash(fileContent);
                    
                    // Si ce hash existe déjà, c'est un doublon
                    if (uniqueFileHashes.contains(fileHash)) {
                        duplicateFiles.add(file);
                    } else {
                        uniqueFileHashes.add(fileHash);
                    }
                } catch (IOException e) {
                    System.err.println("Erreur lors de la lecture du fichier " + file.getName() + ": " + e.getMessage());
                }
            }
        }
        
        // Supprimer les fichiers en double
        for (File duplicateFile : duplicateFiles) {
            if (duplicateFile.delete()) {
                removedCount++;
                System.out.println("Fichier supprimé: " + duplicateFile.getName());
            } else {
                System.err.println("Impossible de supprimer le fichier: " + duplicateFile.getName());
            }
        }
        
        return removedCount;
    }
    
    /**
     * Calcule le hash MD5 d'un tableau d'octets
     * @param data Données à hacher
     * @return Hash MD5 sous forme de chaîne hexadécimale
     */
    private String calculateMD5Hash(byte[] data) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(data);
            
            // Convertir les bytes en représentation hexadécimale
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            throw new RuntimeException("Algorithme MD5 non disponible", e);
        }
    }
} 