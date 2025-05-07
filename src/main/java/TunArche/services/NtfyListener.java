package TunArche.services;

import java.awt.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;
public class NtfyListener extends Thread {
    private final String topic;

    public NtfyListener(String topic) {
        this.topic = topic;
    }

    @Override
    public void run() {
        try {
            URL url = new URL("https://ntfy.sh/" + topic);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Accept", "text/event-stream");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder fullEvent = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    if (fullEvent.toString().contains("event: message")) {
                        String data = extractData(fullEvent.toString());
                        showNotification("Notification", data);
                    }
                    fullEvent.setLength(0);
                } else {
                    fullEvent.append(line).append("\n");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String extractData(String event) {
        try {
            for (String line : event.split("\n")) {
                if (line.startsWith("data:")) {
                    JSONObject json = new JSONObject(line.substring(5).trim());
                    return json.optString("message", "(vide)");
                }
            }
        } catch (Exception e) {
            return "Erreur parsing";
        }
        return "";
    }

    private void showNotification(String title, String message) {
        if (!SystemTray.isSupported()) {
            System.out.println("❌ SystemTray non supporté");
            return;
        }
        try {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().createImage(new byte[0]);
            TrayIcon trayIcon = new TrayIcon(image, "Ntfy Notifier");
            trayIcon.setImageAutoSize(true);

            boolean alreadyAdded = false;
            for (TrayIcon icon : tray.getTrayIcons()) {
                if (icon.getToolTip().equals("Ntfy Notifier")) {
                    alreadyAdded = true;
                    break;
                }
            }

            if (!alreadyAdded) tray.add(trayIcon);

            trayIcon.displayMessage(title, message, TrayIcon.MessageType.INFO);
            System.out.println("✅ Notification affichée: " + message);
        } catch (Exception e) {
            System.out.println("❌ Erreur notification: " + e.getMessage());
        }
    }
}