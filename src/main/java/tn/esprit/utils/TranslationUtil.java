package tn.esprit.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import tn.esprit.entities.Reclamation;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
public class TranslationUtil {
    private static final ObjectMapper mapper = new ObjectMapper();
    public static List<Reclamation> translate(List<Reclamation> recs, String lang) throws Exception {
        List<String> texts = recs.stream().flatMap(r->List.of(r.getTitre(),r.getDescription()).stream()).collect(Collectors.toList());
        String payload = mapper.writeValueAsString(Map.of("texts",texts,"target_language",lang));
        HttpRequest req = HttpRequest.newBuilder().uri(URI.create("http://localhost:5000/translate"))
                .header("Content-Type","application/json").POST(HttpRequest.BodyPublishers.ofString(payload)).build();
        HttpResponse<String> resp = HttpClient.newHttpClient().send(req,HttpResponse.BodyHandlers.ofString());
        JsonNode arr = mapper.readTree(resp.body()).get("translated_texts");
        for(int i=0;i<recs.size();i++){ recs.get(i).setTitre(arr.get(2*i).asText()); recs.get(i).setDescription(arr.get(2*i+1).asText()); }
        return recs;
    }
}
