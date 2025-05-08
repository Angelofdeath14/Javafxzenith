package tn.esprit.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.esprit.entities.Reclamation;
import tn.esprit.service.ServiceReclamation;
import tn.esprit.service.session.UserSession;
import tn.esprit.utils.ExportToExcel;
import tn.esprit.utils.ReclamationClassifier;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AfficherReclamationAdminController implements Initializable {
    @FXML private Text currentUserName;
    @FXML private ListView<Reclamation> lvReclamation;
    @FXML private TextField tfSearch;
    @FXML private ComboBox<String> cbSort;
    @FXML private ComboBox<String> cbLanguage;
    @FXML private Button btnStats, btnExport;
    @FXML private Button btnDashboard, btnEvents, btnAddEvent,
            btnUsers, btnProduit, btnCommands, btnReclamation, btnLogout;

    private final ServiceReclamation service = new ServiceReclamation();
    private final ObservableList<Reclamation> display = FXCollections.observableArrayList();
    private List<Reclamation> master;
    private final ReclamationClassifier classifier = new ReclamationClassifier();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // current user
        currentUserName.setText(UserSession.CURRENT_USER.getUserLoggedIn().getfirst_name());

        // nav buttons
        btnDashboard .setOnAction(e -> goToDash());
        btnEvents    .setOnAction(e -> goToEvents());
        btnAddEvent  .setOnAction(e -> goToAddEvent());
        btnUsers     .setOnAction(e -> goToUsers());
        btnProduit   .setOnAction(e -> goToProduit());
        btnCommands  .setOnAction(e -> goToCommands());
        btnReclamation .setOnAction(e -> refreshList());
        btnLogout    .setOnAction(e -> logout());

        // sort / language combo
        cbSort.getItems().addAll(
                "Titre A→Z","Titre Z→A",
                "Date ↑","Date ↓",
                "Urgence H→L","Urgence L→H"
        );
        cbSort.setOnAction(e -> refreshList());

        cbLanguage.getItems().addAll("Default","en","fr","es","de","ar","zh");
        cbLanguage.setOnAction(e -> refreshList());

        // search field
        tfSearch.textProperty().addListener((obs, o, n) -> refreshList());

        // custom cell
        lvReclamation.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Reclamation r, boolean empty) {
                super.updateItem(r, empty);
                if (empty || r==null) {
                    setText(null);
                } else {
                    String date = r.getDate_creation()
                            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                    String u = classifier.classifyUrgency(r.getDescription());
                    setText(String.format("[%s] %s (%s)%n%s", u, r.getTitre(), date, r.getDescription()));
                }
            }
        });

        // stats/export
        btnStats .setOnAction(e -> openStats());
        btnExport.setOnAction(e -> exportToExcel());

        // load
        master = service.afficher();
        refreshList();
    }

    private void refreshList() {
        // (re)translate if needed
        String lang = cbLanguage.getValue();
        if (lang != null && !lang.equals("Default")) {
            try {
                var mapper = new ObjectMapper();
                List<String> texts = master.stream()
                        .flatMap(r-> List.of(r.getTitre(),r.getDescription()).stream())
                        .collect(Collectors.toList());
                String body = mapper.writeValueAsString(Map.of("texts",texts,"target_language",lang));
                HttpRequest req = HttpRequest.newBuilder()
                        .uri(URI.create("http://localhost:5000/translate"))
                        .header("Content-Type","application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(body))
                        .build();
                HttpResponse<String> resp = HttpClient.newHttpClient()
                        .send(req, HttpResponse.BodyHandlers.ofString());
                JsonNode arr = mapper.readTree(resp.body()).get("translated_texts");
                for(int i=0;i<master.size();i++){
                    int idx = i*2;
                    master.get(i).setTitre(arr.get(idx).asText());
                    master.get(i).setDescription(arr.get(idx+1).asText());
                }
            } catch(Exception ex){
                ex.printStackTrace();
            }
        } else {
            master = service.afficher();
        }

        // filter / sort
        String f = tfSearch.getText().toLowerCase();
        var filtered = master.stream()
                .filter(r -> f.isEmpty()
                        || r.getTitre().toLowerCase().contains(f)
                        || r.getDescription().toLowerCase().contains(f))
                .collect(Collectors.toList());

        String k = cbSort.getValue();
        if (k != null) switch(k) {
            case "Titre A→Z" -> filtered.sort((a,b)->a.getTitre().compareToIgnoreCase(b.getTitre()));
            case "Titre Z→A" -> filtered.sort((a,b)->b.getTitre().compareToIgnoreCase(a.getTitre()));
            case "Date ↑"   -> filtered.sort((a,b)->a.getDate_creation().compareTo(b.getDate_creation()));
            case "Date ↓"   -> filtered.sort((a,b)->b.getDate_creation().compareTo(a.getDate_creation()));
            case "Urgence H→L"
                    -> filtered.sort((a,b)->classifier.classifyUrgency(a.getDescription())
                    .compareTo(classifier.classifyUrgency(b.getDescription())));
            case "Urgence L→H"
                    -> filtered.sort((a,b)->classifier.classifyUrgency(b.getDescription())
                    .compareTo(classifier.classifyUrgency(a.getDescription())));
        }

        display.setAll(filtered);
        lvReclamation.setItems(display);
    }

    @FXML private void openStats() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/reclamation-stat.fxml"));
            Stage s = new Stage();
            s.setTitle("Statistiques Réclamations");
            s.setScene(new Scene(root));
            s.show();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @FXML private void exportToExcel() {
        try {
            File f = new File("reclamations.xlsx");
            ExportToExcel.exportToExcel(display, f);
            Desktop.getDesktop().open(f);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    // Navigation helpers
    public void goToDash()        { navigate("/Dashboard.fxml","Tableau de bord"); }
    public void goToEvents()      { navigate("/AffichageEvent.fxml","Événements"); }
    public void goToAddEvent()    { navigate("/AjouterEvent.fxml","Ajouter Événement"); }
    public void goToUsers()       { navigate("/ListUsers.fxml","Utilisateurs"); }
    public void goToProduit()     { navigate("/produit-admin.fxml","Produits"); }
    public void goToCommands()    { navigate("/command-admin.fxml","Commandes"); }
    public void logout()          { navigate("/Login.fxml","Connexion"); }
    private void navigate(String fxml, String title) {
        try {
            Parent r = FXMLLoader.load(getClass().getResource(fxml));
            Stage st = (Stage) lvReclamation.getScene().getWindow();
            st.setTitle(title);
            st.setScene(new Scene(r));
        } catch(IOException ex) {
            new Alert(Alert.AlertType.ERROR,"Navigation failed: "+ex.getMessage())
                    .showAndWait();
        }
    }
}
