package TunArche.entities;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

public class EmailUtil {

    public static void sendEmail(String to, String subject, String body) {
        // NE PAS utiliser UserSession ici — on veut envoyer à n'importe qui, même non connecté
        String host = "smtp.gmail.com";
        String from = "marwenazouzi44@gmail.com";
        String password = "kkzc xgbp ognw zsvy"; // Mot de passe d'application Gmail

        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");

        Session emailSession = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(emailSession);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setText(body);

            Transport.send(message);
            System.out.println("✅ Email envoyé avec succès à : " + to);
        } catch (MessagingException e) {
            System.out.println("❌ Erreur lors de l'envoi de l'email à : " + to);
            e.printStackTrace();
        }
    }

    public static void sendEmailWithAttachment(String to, String subject, String body, File file) {
        String host = "smtp.gmail.com";
        String from = "marwenazouzi44@gmail.com";
        String password = "kkzc xgbp ognw zsvy"; // mot de passe d'application Gmail

        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", "587");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(from, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);

            // Texte du mail
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText(body);

            // Fichier attaché
            MimeBodyPart attachmentPart = new MimeBodyPart();
            attachmentPart.attachFile(file);

            // Multipart (texte + pièce jointe)
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);
            multipart.addBodyPart(attachmentPart);

            message.setContent(multipart);

            Transport.send(message);
            System.out.println("✅ Email avec pièce jointe envoyé à " + to);
        } catch (MessagingException | IOException e) {
            System.out.println("❌ Échec de l'envoi avec pièce jointe.");
            e.printStackTrace();
        }
    }

}
