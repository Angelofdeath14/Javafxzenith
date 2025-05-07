package tn.esprit.controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import tn.esprit.entities.Reclamation;
import tn.esprit.service.ServiceReclamation;
import tn.esprit.utils.ReclamationClassifier;

import java.net.URL;
import java.util.*;
public class ReclamationStatController implements Initializable{
    @FXML private PieChart pieChart;
    @FXML private Button btnClose;
    private final ServiceReclamation service = new ServiceReclamation();
    private final ReclamationClassifier classifier = new ReclamationClassifier();
    @Override
    public void initialize(URL location, ResourceBundle resources){ build(); btnClose.setOnAction(e->((Stage)btnClose.getScene().getWindow()).close()); }
    private void build(){
        List<Reclamation> list = service.afficher();
        Map<String,Long> counts = new LinkedHashMap<>();
        for(Reclamation r:list){
            String u = classifier.classifyUrgency(r.getDescription());
            counts.put(u, counts.getOrDefault(u,0L)+1);
        }
        List<PieChart.Data> data = new ArrayList<>();
        counts.forEach((k,v)->data.add(new PieChart.Data(k+" ("+v+")",v)));
        pieChart.setData(FXCollections.observableArrayList(data));
    }
}
