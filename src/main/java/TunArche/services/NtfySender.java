package TunArche.services;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NtfySender {
    public static void sendNotification(String topic, String message) {
        try {
            URL url = new URL("https://ntfy.sh/" + topic);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);

            // Écrire le corps du message
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = message.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int code = conn.getResponseCode();
            System.out.println("✅ Notification envoyée. Code HTTP : " + code);
        } catch (Exception e) {
            System.out.println("❌ Erreur lors de l'envoi de la notification : " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        sendNotification("test-java", "👋 Hello vous ete inscrit a la formation !");
    }
}

