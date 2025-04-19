package TunArche.services;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class chatbot {

        private static final String API_KEY = "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"; // Remplace par ta clé OpenRouter ici
        private static final String API_URL = "https://openrouter.ai/api/v1/chat/completions";

    public static void main(String[] args) throws IOException {
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print("Tu: ");
            String input = userInput.readLine();

            if (input.equalsIgnoreCase("exit")) break;

            String payload = """
                        {
                            "model": "openai/gpt-3.5-turbo",
                            "messages": [
                                { "role": "system", "content": "Tu es un assistant utile." },
                                { "role": "user", "content": "%s" }
                            ]
                        }
                    """.formatted(input);

            HttpURLConnection conn = (HttpURLConnection) new URL(API_URL).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] inputBytes = payload.getBytes(StandardCharsets.UTF_8);
                os.write(inputBytes, 0, inputBytes.length);
            }

            StringBuilder jsonResponse = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = br.readLine()) != null) {
                    jsonResponse.append(line.trim());
                }
            }

            // ➕ Parsing JSON pour afficher uniquement le message utile
            JSONObject responseObj = new JSONObject(jsonResponse.toString());
            JSONArray choices = responseObj.getJSONArray("choices");
            String content = choices.getJSONObject(0).getJSONObject("message").getString("content");

            System.out.println("Bot: " + content);
        }
    }
}