package tn.esprit.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.esprit.entity.Reclamation;
import tn.esprit.service.ServiceReclamation;
import tn.esprit.utils.ExportToExcel;
import tn.esprit.utils.ReclamationClassifier;

import java.awt.*;
import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
public class AfficherReclamationAdminController {
    @FXML private ListView<Reclamation> lvReclamation;
    @FXML private TextField tfSearch;
    @FXML private ComboBox<String> cbSort;
    @FXML private ComboBox<String> cbLanguage;
    @FXML private javafx.scene.control.Button btnStats;
    @FXML private javafx.scene.control.Button btnExport;
    private final ServiceReclamation service = new ServiceReclamation();
    private List<Reclamation> master;
    private final ObservableList<Reclamation> display = FXCollections.observableArrayList();
    private final ReclamationClassifier classifier = new ReclamationClassifier();
    @FXML public void initialize() {
        master = service.afficher();
        tfSearch.textProperty().addListener((o,old,nw)->refresh());
        cbSort.getItems().addAll("Titre A→Z","Titre Z→A","Date ↑","Date ↓","Urgence H→L","Urgence L→H");
        cbSort.valueProperty().addListener((o,old,nw)->refresh());
        cbLanguage.getItems().addAll("Default","en","fr","es","de","ar","zh");
        cbLanguage.valueProperty().addListener((o,old,nw)->{
            if(nw==null||nw.equals("Default")) master = service.afficher();
            else {
                List<Reclamation> copy = service.afficher();
                try{
                    ObjectMapper mapper = new ObjectMapper();
                    List<String> texts = copy.stream().flatMap(r->java.util.stream.Stream.of(r.getTitre(),r.getDescription())).collect(Collectors.toList());
                    String json = mapper.writeValueAsString(Map.of("texts",texts,"target_language",nw));
                    HttpRequest req = HttpRequest.newBuilder()
                            .uri(URI.create("http://localhost:5000/translate"))
                            .header("Content-Type","application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(json)).build();
                    HttpResponse<String> resp = HttpClient.newHttpClient().send(req, HttpResponse.BodyHandlers.ofString());
                    JsonNode arr = mapper.readTree(resp.body()).get("translated_texts");
                    for(int i=0;i<copy.size();i++){
                        int idx=i*2;
                        copy.get(i).setTitre(arr.get(idx).asText());
                        copy.get(i).setDescription(arr.get(idx+1).asText());
                    }
                }catch(Exception e){e.printStackTrace();}
                master = copy;
            }
            refresh();
        });
        lvReclamation.setCellFactory(lv->new ListCell<>(){
            protected void updateItem(Reclamation r,boolean empty){
                super.updateItem(r,empty);
                if(empty||r==null) setText(null);
                else{
                    String date=r.getDate_creation().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                    String u=classifier.classifyUrgency(r.getDescription());
                    setText(String.format("[%s] %s (%s) - %s",u,r.getTitre(),date,r.getDescription()));
                }
            }
        });
        btnStats.setOnAction(e->{try{Parent root=FXMLLoader.load(getClass().getResource("/reclamation-stat.fxml"));Stage s=new Stage();s.setScene(new Scene(root));s.show();}catch(Exception ex){ex.printStackTrace();}});
        btnExport.setOnAction(e->{try{File f=new File("reclamations.xlsx");ExportToExcel.exportToExcel(display,f);Desktop.getDesktop().open(f);}catch(Exception ex){ex.printStackTrace();}});
        refresh();
    }
    private void refresh(){
        String f=tfSearch.getText().toLowerCase();
        List<Reclamation> filtered = master.stream()
                .filter(r->f.isEmpty()||r.getTitre().toLowerCase().contains(f)||r.getDescription().toLowerCase().contains(f))
                .collect(Collectors.toList());
        String k=cbSort.getValue();

        if(k!=null) switch(k){
            case "Titre A→Z"->filtered.sort((a,b)->a.getTitre().compareToIgnoreCase(b.getTitre()));
            case "Titre Z→A"->filtered.sort((a,b)->b.getTitre().compareToIgnoreCase(a.getTitre()));
            case "Date ↑"->filtered.sort((a,b)->a.getDate_creation().compareTo(b.getDate_creation()));
            case "Date ↓"->filtered.sort((a,b)->b.getDate_creation().compareTo(a.getDate_creation()));
            case "Urgence H→L"->filtered.sort((a,b)->classifier.classifyUrgency(a.getDescription()).compareTo(classifier.classifyUrgency(b.getDescription())));
            case "Urgence L→H"->filtered.sort((a,b)->classifier.classifyUrgency(b.getDescription()).compareTo(classifier.classifyUrgency(a.getDescription())));
        }
        display.setAll(filtered);
        lvReclamation.setItems(display);
    }
}