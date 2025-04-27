package TunArche.services;


import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class GeminiChatbot {

    private static final String API_KEY = "//api_key";
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;

    public static String askGemini(String message) {
        OkHttpClient client = new OkHttpClient();

        // Format JSON correct
        JSONObject textPart = new JSONObject().put("text", message);
        JSONArray partsArray = new JSONArray().put(textPart);
        JSONObject content = new JSONObject().put("parts", partsArray);
        JSONArray contentsArray = new JSONArray().put(content);
        JSONObject finalPayload = new JSONObject().put("contents", contentsArray);

        RequestBody body = RequestBody.create(
                finalPayload.toString(),
                MediaType.get("application/json")
        );

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                JSONObject json = new JSONObject(responseBody);
                JSONArray candidates = json.getJSONArray("candidates");
                return candidates.getJSONObject(0).getJSONObject("content").getJSONArray("parts").getJSONObject(0).getString("text");
            } else {
                return "Erreur API Gemini : " + response.code() + " - " + response.body().string();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Erreur réseau : " + e.getMessage();
        }


    }
    OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)  // Connexion
            .writeTimeout(30, TimeUnit.SECONDS)    // Envoi
            .readTimeout(60, TimeUnit.SECONDS)     // Réception
            .build();
}