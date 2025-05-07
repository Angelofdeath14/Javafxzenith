package tn.esprit.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.regex.Pattern;

public class BadWordFilter {

    private static final String API_URL  = "https://www.purgomalum.com/service/plain?text=";
    private static final String FILL_TEXT = "[bad]";

    public static String filterBadWords(String text) {
        try {
            String url = API_URL + URLEncoder.encode(text, "UTF-8") +
                    "&fill_text=" + URLEncoder.encode(FILL_TEXT, "UTF-8");

            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod("GET");

            try (BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()))) {

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) sb.append(line);
                return sb.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return text;
        }
    }


    public static int countBadWords(String filteredText) {
        return filteredText.split(Pattern.quote(FILL_TEXT)).length - 1;
    }
}
