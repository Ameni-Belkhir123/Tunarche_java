package TunArche.services;

import java.net.*;
import java.io.*;
import org.json.JSONObject;  // Besoin d'une librairie JSON

public class FindMyCountry {
    public static void main(String[] args) {
        try {
            URL url = new URL("http://ip-api.com/json/");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            JSONObject json = new JSONObject(response.toString());
            System.out.println("Ton pays : " + json.getString("country"));
            System.out.println("Ton code pays : " + json.getString("countryCode"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
