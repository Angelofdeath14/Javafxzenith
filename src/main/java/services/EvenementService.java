package services;

import Utils.MyDatabase;
import Entity.Evenement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EvenementService {
    private final Connection connection;

    public EvenementService() throws SQLException {
        this.connection = MyDatabase.getInstance().getConnection();
    }

    public List<Evenement> getAllEvents() {
        List<Evenement> events = new ArrayList<>();
        String query = "SELECT * FROM evenement";
        
        System.out.println("======= DÉBUT DU CHARGEMENT DES ÉVÉNEMENTS =======");
        System.out.println("Requête SQL: " + query);
        
        try (Statement statement = connection.createStatement()) {
            System.out.println("Exécution de la requête: " + query);
            System.out.println("Connexion utilisée: " + connection);
            
            // Vérifier si la connexion est valide
            if (connection == null || connection.isClosed()) {
                System.err.println("ERREUR: La connexion à la base de données est null ou fermée");
                return events;
            }
            
            ResultSet resultSet = statement.executeQuery(query);
            
            // Afficher les colonnes disponibles dans le résultat
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            System.out.println("Nombre de colonnes disponibles: " + columnCount);
            
            for (int i = 1; i <= columnCount; i++) {
                System.out.println("- Colonne " + i + ": " + metaData.getColumnName(i) + 
                                  " (" + metaData.getColumnTypeName(i) + ")");
            }
            
            while (resultSet.next()) {
                Evenement evenement = new Evenement();
                
                // ID (toujours présent)
                evenement.setId(resultSet.getInt("id"));
                System.out.println("ID: " + evenement.getId());
                
                // Titre (toujours présent)
                String titre = resultSet.getString("titre");
                evenement.setTitre(titre != null ? titre : "Sans titre");
                System.out.println("Titre: " + titre);
                
                // Description (peut être null)
                String description = resultSet.getString("description");
                evenement.setDescription(description != null ? description : "");
                System.out.println("Description: " + (description != null ? description.substring(0, Math.min(description.length(), 20)) + "..." : "null"));
                
                // Type
                try {
                    String type = resultSet.getString("type");
                    evenement.setType(type != null ? type : "");
                    System.out.println("Type: " + type);
                } catch (SQLException e) {
                    System.out.println("Colonne type non trouvée: " + e.getMessage());
                    evenement.setType("Général"); // Valeur par défaut
                }
                
                // Location
                try {
                    String location = resultSet.getString("location");
                    evenement.setLocation(location != null ? location : "");
                    System.out.println("Location: " + location);
                } catch (SQLException e) {
                    System.out.println("Colonne location non trouvée: " + e.getMessage());
                    evenement.setLocation("Non spécifié"); // Valeur par défaut
                }
                
                // Dates
                try {
                    // Protection contre les valeurs NULL
                    Timestamp dateD = resultSet.getTimestamp("dateD");
                    if (dateD != null) {
                        evenement.setDateD(dateD.toLocalDateTime());
                        System.out.println("DateD: " + dateD);
                    } else {
                        System.out.println("DateD est null, utilisation d'une date par défaut");
                        evenement.setDateD(LocalDateTime.now()); // Valeur par défaut
                    }
                } catch (SQLException e) {
                    System.out.println("Colonne dateD non trouvée: " + e.getMessage());
                    try {
                        Timestamp dateDebut = resultSet.getTimestamp("date_debut");
                        if (dateDebut != null) {
                            evenement.setDateD(dateDebut.toLocalDateTime());
                            System.out.println("Date_debut: " + dateDebut);
                        } else {
                            System.out.println("Date_debut est null, utilisation d'une date par défaut");
                            evenement.setDateD(LocalDateTime.now()); // Valeur par défaut
                        }
                    } catch (SQLException e2) {
                        System.out.println("Colonnes dateD et date_debut non trouvées: " + e2.getMessage());
                        evenement.setDateD(LocalDateTime.now()); // Valeur par défaut
                    }
                }
                
                try {
                    // Protection contre les valeurs NULL
                    Timestamp dateF = resultSet.getTimestamp("dateF");
                    if (dateF != null) {
                        evenement.setDateF(dateF.toLocalDateTime());
                        System.out.println("DateF: " + dateF);
                    } else {
                        System.out.println("DateF est null, utilisation d'une date par défaut");
                        evenement.setDateF(LocalDateTime.now().plusDays(7)); // Valeur par défaut
                    }
                } catch (SQLException e) {
                    System.out.println("Colonne dateF non trouvée: " + e.getMessage());
                    try {
                        Timestamp dateFin = resultSet.getTimestamp("date_fin");
                        if (dateFin != null) {
                            evenement.setDateF(dateFin.toLocalDateTime());
                            System.out.println("Date_fin: " + dateFin);
                        } else {
                            System.out.println("Date_fin est null, utilisation d'une date par défaut");
                            evenement.setDateF(LocalDateTime.now().plusDays(7)); // Valeur par défaut
                        }
                    } catch (SQLException e2) {
                        System.out.println("Colonnes dateF et date_fin non trouvées: " + e2.getMessage());
                        evenement.setDateF(LocalDateTime.now().plusDays(7)); // Valeur par défaut
                    }
                }
                
                // Image
                String image = resultSet.getString("image");
                evenement.setImage(image != null ? image : "");
                System.out.println("Image: " + image);
                
                // NbPlace
                try {
                    int nbPlace = resultSet.getInt("nbPlace");
                    evenement.setNbPlace(nbPlace);
                    System.out.println("NbPlace: " + nbPlace);
                } catch (SQLException e) {
                    System.out.println("Colonne nbPlace non trouvée: " + e.getMessage());
                    evenement.setNbPlace(0); // Valeur par défaut
                }
                
                // Prix
                try {
                    double prix = resultSet.getDouble("prix");
                    evenement.setPrix(prix);
                    System.out.println("Prix: " + prix);
                } catch (SQLException e) {
                    System.out.println("Colonne prix non trouvée: " + e.getMessage());
                    evenement.setPrix(0.0); // Valeur par défaut
                }
                
                events.add(evenement);
                System.out.println("Événement ajouté à la liste: " + evenement.getId() + " - " + evenement.getTitre());
            }
            
            System.out.println("Nombre total d'événements chargés: " + events.size());
            
        } catch (SQLException e) {
            System.err.println("ERREUR SQL lors du chargement des événements: " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("======= FIN DU CHARGEMENT DES ÉVÉNEMENTS =======");
        return events;
    }

    // Alias pour getAllEvents
    public List<Evenement> getAllEvenements() {
        return getAllEvents();
    }

    public Evenement getOne(int id) {
        String query = "SELECT * FROM evenement WHERE id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                Evenement evenement = new Evenement();
                evenement.setId(resultSet.getInt("id"));
                evenement.setTitre(resultSet.getString("titre"));
                evenement.setDescription(resultSet.getString("description"));
                
                // Ces champs pourraient ne pas exister dans la nouvelle structure de table
                try {
                    evenement.setType(resultSet.getString("type"));
                } catch (SQLException e) {
                    evenement.setType(""); // Valeur par défaut
                }
                
                try {
                    evenement.setLocation(resultSet.getString("lieu"));
                } catch (SQLException e) {
                    try {
                        evenement.setLocation(resultSet.getString("location"));
                    } catch (SQLException e2) {
                        evenement.setLocation(""); // Valeur par défaut
                    }
                }
                
                // Gestion des dates avec Timestamp pour éviter les problèmes de format
                try {
                    Timestamp dateD = resultSet.getTimestamp("date_debut");
                    if (dateD != null) {
                        evenement.setDateD(dateD.toLocalDateTime());
                    } else {
                        evenement.setDateD(LocalDateTime.now()); // Valeur par défaut
                    }
                } catch (SQLException e) {
                    try {
                        Timestamp dateD = resultSet.getTimestamp("dateD");
                        if (dateD != null) {
                            evenement.setDateD(dateD.toLocalDateTime());
                        } else {
                            evenement.setDateD(LocalDateTime.now()); // Valeur par défaut
                        }
                    } catch (SQLException e2) {
                        // Aucune date disponible, utiliser la date actuelle
                        evenement.setDateD(LocalDateTime.now());
                    }
                }
                
                try {
                    Timestamp dateF = resultSet.getTimestamp("date_fin");
                    if (dateF != null) {
                        evenement.setDateF(dateF.toLocalDateTime());
                    } else {
                        evenement.setDateF(LocalDateTime.now().plusDays(7)); // Valeur par défaut
                    }
                } catch (SQLException e) {
                    try {
                        Timestamp dateF = resultSet.getTimestamp("dateF");
                        if (dateF != null) {
                            evenement.setDateF(dateF.toLocalDateTime());
                        } else {
                            evenement.setDateF(LocalDateTime.now().plusDays(7)); // Valeur par défaut
                        }
                    } catch (SQLException e2) {
                        // Aucune date disponible, utiliser date actuelle + 7 jours
                        evenement.setDateF(LocalDateTime.now().plusDays(7));
                    }
                }
                
                evenement.setImage(resultSet.getString("image"));
                
                // Ce champ pourrait ne pas exister dans la nouvelle structure de table
                try {
                    evenement.setNbPlace(resultSet.getInt("nbPlace"));
                } catch (SQLException e) {
                    try {
                        evenement.setNbPlace(resultSet.getInt("nb_places"));
                    } catch (SQLException e2) {
                        evenement.setNbPlace(0); // Valeur par défaut
                    }
                }
                
                // Récupérer le prix s'il existe
                try {
                    evenement.setPrix(resultSet.getDouble("prix"));
                } catch (SQLException e) {
                    // La colonne prix n'existe peut-être pas encore, utiliser la valeur par défaut
                    evenement.setPrix(0.0);
                }
                
                return evenement;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'événement avec l'ID " + id + ": " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }

    public void ajouter(Evenement evenement) {
        try {
            // Vérifier la structure de la table
            DatabaseMetaData meta = connection.getMetaData();
            boolean prixExists = columnExists("evenement", "prix");
            boolean lieuExists = columnExists("evenement", "lieu");
            boolean locationExists = columnExists("evenement", "location");
            boolean dateDebutExists = columnExists("evenement", "date_debut");
            boolean dateDExists = columnExists("evenement", "dateD");
            boolean dateFinExists = columnExists("evenement", "date_fin");
            boolean dateFExists = columnExists("evenement", "dateF");
            
            System.out.println("Structure de la table evenement:");
            System.out.println("- prix: " + (prixExists ? "existe" : "n'existe pas"));
            System.out.println("- lieu: " + (lieuExists ? "existe" : "n'existe pas"));
            System.out.println("- location: " + (locationExists ? "existe" : "n'existe pas"));
            System.out.println("- date_debut: " + (dateDebutExists ? "existe" : "n'existe pas"));
            System.out.println("- dateD: " + (dateDExists ? "existe" : "n'existe pas"));
            System.out.println("- date_fin: " + (dateFinExists ? "existe" : "n'existe pas"));
            System.out.println("- dateF: " + (dateFExists ? "existe" : "n'existe pas"));
            
            // Construire la requête en fonction des colonnes existantes
            StringBuilder queryBuilder = new StringBuilder("INSERT INTO evenement (titre, description");
            
            if (lieuExists) {
                queryBuilder.append(", lieu");
            } else if (locationExists) {
                queryBuilder.append(", location");
            }
            
            if (dateDebutExists) {
                queryBuilder.append(", date_debut");
            } else if (dateDExists) {
                queryBuilder.append(", dateD");
            }
            
            if (dateFinExists) {
                queryBuilder.append(", date_fin");
            } else if (dateFExists) {
                queryBuilder.append(", dateF");
            }
            
            queryBuilder.append(", image");
            
            if (columnExists("evenement", "type")) {
                queryBuilder.append(", type");
            }
            
            if (columnExists("evenement", "nbPlace")) {
                queryBuilder.append(", nbPlace");
            } else if (columnExists("evenement", "nb_places")) {
                queryBuilder.append(", nb_places");
            }
            
            if (prixExists) {
                queryBuilder.append(", prix");
            }
            
            queryBuilder.append(") VALUES (?, ?");
            
            // Ajouter les placeholders pour chaque colonne
            if (lieuExists || locationExists) {
                queryBuilder.append(", ?");
            }
            
            if (dateDebutExists || dateDExists) {
                queryBuilder.append(", ?");
            }
            
            if (dateFinExists || dateFExists) {
                queryBuilder.append(", ?");
            }
            
            queryBuilder.append(", ?"); // image
            
            if (columnExists("evenement", "type")) {
                queryBuilder.append(", ?");
            }
            
            if (columnExists("evenement", "nbPlace") || columnExists("evenement", "nb_places")) {
                queryBuilder.append(", ?");
            }
            
            if (prixExists) {
                queryBuilder.append(", ?");
            }
            
            queryBuilder.append(")");
            
            String query = queryBuilder.toString();
            System.out.println("Requête d'insertion: " + query);
            
            try (PreparedStatement statement = connection.prepareStatement(query)) {
                int paramIndex = 1;
                
                statement.setString(paramIndex++, evenement.getTitre());
                statement.setString(paramIndex++, evenement.getDescription());
                
                if (lieuExists || locationExists) {
                    statement.setString(paramIndex++, evenement.getLocation());
                }
                
                if (dateDebutExists || dateDExists) {
                    statement.setTimestamp(paramIndex++, evenement.getDateD() != null ? 
                                         Timestamp.valueOf(evenement.getDateD()) : null);
                }
                
                if (dateFinExists || dateFExists) {
                    statement.setTimestamp(paramIndex++, evenement.getDateF() != null ? 
                                         Timestamp.valueOf(evenement.getDateF()) : null);
                }
                
                statement.setString(paramIndex++, evenement.getImage());
                
                if (columnExists("evenement", "type")) {
                    statement.setString(paramIndex++, evenement.getType());
                }
                
                if (columnExists("evenement", "nbPlace") || columnExists("evenement", "nb_places")) {
                    statement.setInt(paramIndex++, evenement.getNbPlace());
                }
                
                if (prixExists) {
                    statement.setDouble(paramIndex, evenement.getPrix() != null ? evenement.getPrix() : 0.0);
                }
                
                int result = statement.executeUpdate();
                System.out.println("Résultat de l'insertion: " + result + " ligne(s) affectée(s)");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout d'un événement: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void modifier(Evenement evenement) {
        try {
            // Vérifier si la colonne prix existe
            boolean prixExists = columnExists("evenement", "prix");
            
            String query;
            if (prixExists) {
                query = "UPDATE evenement SET titre = ?, description = ?, type = ?, location = ?, image = ?, nbPlace = ?, prix = ? WHERE id = ?";
            } else {
                query = "UPDATE evenement SET titre = ?, description = ?, type = ?, location = ?, image = ?, nbPlace = ? WHERE id = ?";
            }
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, evenement.getTitre());
            statement.setString(2, evenement.getDescription());
            statement.setString(3, evenement.getType());
            statement.setString(4, evenement.getLocation());
            statement.setString(5, evenement.getImage());
            statement.setInt(6, evenement.getNbPlace());
                
                if (prixExists) {
                    statement.setDouble(7, evenement.getPrix() != null ? evenement.getPrix() : 0.0);
                    statement.setInt(8, evenement.getId());
                } else {
                    statement.setInt(7, evenement.getId());
                }
                
            statement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Méthode utilitaire pour vérifier si une colonne existe dans une table
    private boolean columnExists(String tableName, String columnName) throws SQLException {
        DatabaseMetaData meta = connection.getMetaData();
        try (ResultSet rs = meta.getColumns(null, null, tableName, columnName)) {
            return rs.next();
        }
    }

    public void supprimer(int id) {
        String query = "DELETE FROM evenement WHERE id = ?";
        
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean reserverPlace(int evenementId, int nombrePlaces) {
        if (nombrePlaces <= 0) {
            System.err.println("Erreur: Le nombre de places doit être positif");
            return false;
        }
        
        // D'abord vérifier si l'événement existe et a assez de places
        String checkQuery = "SELECT nbPlace FROM evenement WHERE id = ?";
        
        try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
            checkStmt.setInt(1, evenementId);
            
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (!rs.next()) {
                    System.err.println("Erreur: Événement avec ID " + evenementId + " non trouvé");
                    return false;
                }
                
                int placesDisponibles = rs.getInt("nbPlace");
                if (placesDisponibles < nombrePlaces) {
                    System.err.println("Erreur: Pas assez de places disponibles. Demandé: " + nombrePlaces + ", Disponible: " + placesDisponibles);
                    return false;
                }
            }
            
            // Si on arrive ici, l'événement existe et a assez de places
            String updateQuery = "UPDATE evenement SET nbPlace = nbPlace - ? WHERE id = ?";
            
            try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                updateStmt.setInt(1, nombrePlaces);
                updateStmt.setInt(2, evenementId);
                
                int rowsAffected = updateStmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    System.out.println("Réservation réussie: " + nombrePlaces + " place(s) réservée(s) pour l'événement " + evenementId);
                    
                    // TODO: Ici on pourrait ajouter du code pour enregistrer la réservation dans une table dédiée
                    
                    return true;
                } else {
                    System.err.println("Échec de la réservation: Aucune ligne mise à jour");
                    return false;
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la réservation: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public int countEvents() {
        int count = 0;
        String query = "SELECT COUNT(*) FROM evenement";
        
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            
            if (resultSet.next()) {
                count = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return count;
    }
    
    /**
     * Récupère tous les types d'événements distincts de la base de données
     * @return Une liste des types d'événements
     */
    public List<String> getAllEventTypes() {
        List<String> types = new ArrayList<>();
        Set<String> uniqueTypes = new HashSet<>();
        
        // Récupérer tous les événements
        List<Evenement> events = getAllEvents();
        
        // Extraire les types uniques
        for (Evenement event : events) {
            if (event.getType() != null && !event.getType().isEmpty()) {
                uniqueTypes.add(event.getType());
            }
        }
        
        // Convertir en liste et trier
        types.addAll(uniqueTypes);
        Collections.sort(types);
        
        return types;
    }
}


