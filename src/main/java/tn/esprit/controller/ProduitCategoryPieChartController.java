package tn.esprit.controller;

import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import tn.esprit.entities.Produit;
import tn.esprit.service.ServiceProduit;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProduitCategoryPieChartController {
    @FXML private PieChart pieChart;
    private ServiceProduit serviceProduit = new ServiceProduit();

    @FXML
    public void initialize() {
        List<Produit> produits = serviceProduit.afficher();
        Map<String, Long> counts = produits.stream()
                .collect(Collectors.groupingBy(Produit::getCategorie, Collectors.counting()));
        ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
        counts.forEach((categorie, count) ->
                data.add(new PieChart.Data(categorie + " (" + count + ")", count))
        );
        pieChart.setData(data);
    }
}
