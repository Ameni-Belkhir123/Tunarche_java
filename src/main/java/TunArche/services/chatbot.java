package TunArche.services;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }

                // 🧠 Extraire juste le message du bot avec split
                String raw = response.toString();
                String split1 = raw.split("\"content\":\"")[1];
                String result = split1.split("\",\"refusal\"")[0];

                // Affichage nettoyé
                System.out.println("Bot: " + result.replace("\\n", "\n").replace("\\\"", "\""));
            }
        }
    }
    public static String askBot(String input) {
        try {
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
                os.write(payload.getBytes(StandardCharsets.UTF_8));
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line.trim());
                }

                String raw = response.toString();
                String content = raw.split("\"content\":\"")[1].split("\",\"")[0];
                return content.replace("\\n", "\n").replace("\\\"", "\"");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Erreur lors de la communication avec le chatbot.";
        }
    }

}