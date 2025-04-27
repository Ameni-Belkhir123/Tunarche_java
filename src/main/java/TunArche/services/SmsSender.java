package TunArche.services;

import okhttp3.*;

public class SmsSender {
    public static void envoyerSms(String numero, String messageTexte) {
        OkHttpClient client = new OkHttpClient();

        MediaType mediaType = MediaType.parse("application/json");
        String jsonBody = "{\n" +
                "  \"messages\": [\n" +
                "    {\n" +
                "      \"destinations\": [\n" +
                "        {\"to\": \"+21658499352\"}\n" +
                "      ],\n" +
                "      \"from\": \"TunArche\",\n" + // Personnalise le nom de l'expéditeur
                "      \"text\": \"Félicitations ! Vous êtes inscrit à la formation.\"\n" +
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
    }
}
