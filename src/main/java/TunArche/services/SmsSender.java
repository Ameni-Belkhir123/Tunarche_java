package TunArche.services;

import okhttp3.*;
import TunArche.entities.Formation;

public class SmsSender {
    public static void envoyerSms(String numero, String messageTexte) {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        String jsonBody = "{\n" +
                "  \"messages\": [\n" +
                "    {\n" +
                "      \"destinations\": [\n" +
                "        {\"to\": \"" + numero + "\"}\n" +  // Utilise le numéro passé en paramètre
                "      ],\n" +
                "      \"from\": \"TunArche\",\n" +
                "      \"text\": \"" + messageTexte + "\"\n" +  // Utilise le message dynamique
                "    }\n" +
                "  ]\n" +
                "}";
        RequestBody body = RequestBody.create(jsonBody, mediaType);
        Request request = new Request.Builder()
                .url("https://e5r512.api.infobip.com/sms/2/text/advanced") // adapte le endpoint selon ton dashboard
                .post(body)
                .addHeader("Authorization", "App 9dee1b48064e31a17a381fc28d62d612-da627322-b6ea-4a66-b1e8-cbadb0cb0e6d") // Remplace ici
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                System.out.println("Message envoyé avec succès !");
                System.out.println(response.body().string());
            } else {
                System.out.println("Erreur lors de l'envoi : " + response.code());
                System.out.println(response.body().string());
            }
        } catch (Exception e) {
            e.printStackTrace();
}
}}