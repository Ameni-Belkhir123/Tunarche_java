package TunArche.services;


import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.URLEncoder;

public class ProfanityCheckService {

    public boolean containsProfanity(String inputText) {
        try {
            // Encodage du texte pour l’URL (très important !)
            String encodedText = URLEncoder.encode(inputText, "UTF-8");

            // Construction de l’URL avec le texte encodé
            String urlString = "https://www.purgomalum.com/service/containsprofanity?text=" + encodedText;
            URL url = new URL(urlString);

            // Connexion à l’API
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Lecture de la réponse
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // Vérifie si la réponse est "true"
            return response.toString().equalsIgnoreCase("true");

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}