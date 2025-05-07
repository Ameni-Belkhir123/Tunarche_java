package TunArche.services;

import javax.mail.*;
import javax.mail.internet.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

public class EmailService {

    // Gmail SMTP configuration
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String EMAIL_USERNAME = "saharboughanmi756@gmail.com";
    private static final String EMAIL_PASSWORD = "jgqhhydmxyoaqmex";

    /**
     * Sends a confirmation email to the participant
     *
     * @param recipientEmail Email address of the recipient
     * @param recipientName Name of the recipient
     * @param concoursTitle Title of the contest
     * @return boolean indicating success or failure of email sending
     */
    public boolean sendConfirmationEmail(String recipientEmail, String recipientName, String concoursTitle) {
        // Set email properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.ssl.trust", SMTP_HOST);
        props.put("mail.smtp.connectiontimeout", "10000");
        props.put("mail.smtp.timeout", "10000");
        props.put("mail.smtp.writetimeout", "10000");

        // Create session with authenticator
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL_USERNAME, EMAIL_PASSWORD);
            }
        });

        try {
            // Create a new message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(EMAIL_USERNAME, "TunArche Concours"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Confirmation de Participation - " + concoursTitle);

            // Get current date for the email footer
            String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            // Build the enhanced HTML email body
            String emailBody =
                    "<!DOCTYPE html>" +
                            "<html>" +
                            "<head>" +
                            "    <meta charset=\"UTF-8\">" +
                            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">" +
                            "    <title>Confirmation de Participation</title>" +
                            "</head>" +
                            "<body style=\"margin: 0; padding: 0; font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;\">" +
                            "    <table role=\"presentation\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" align=\"center\" width=\"100%\" style=\"max-width: 600px; margin: auto; border-collapse: collapse;\">" +
                            "        <!-- Header with Logo -->" +
                            "        <tr>" +
                            "            <td style=\"padding: 20px 0; text-align: center; background-color: #2c3e50;\">" +
                            "                <h1 style=\"color: #ffffff; margin: 0; font-size: 28px; letter-spacing: 1px;\">TunArche</h1>" +
                            "                <p style=\"color: #ecf0f1; margin: 5px 0 0;\">Concours Artistiques</p>" +
                            "            </td>" +
                            "        </tr>" +
                            "        <!-- Main Content -->" +
                            "        <tr>" +
                            "            <td style=\"background-color: #f9f9f9; padding: 40px 30px;\">" +
                            "                <table role=\"presentation\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" width=\"100%\">" +
                            "                    <tr>" +
                            "                        <td style=\"padding-bottom: 20px;\">" +
                            "                            <h2 style=\"color: #2ecc71; margin: 0; font-size: 24px;\">Confirmation de Participation</h2>" +
                            "                        </td>" +
                            "                    </tr>" +
                            "                    <tr>" +
                            "                        <td style=\"padding-bottom: 20px;\">" +
                            "                            <p style=\"margin: 0; line-height: 24px; color: #333333;\">Bonjour <strong style=\"color: #2c3e50;\">" + recipientName + "</strong>,</p>" +
                            "                            <p style=\"margin: 15px 0 0; line-height: 24px; color: #333333;\">" +
                            "                                Nous avons bien reçu votre participation au concours <strong style=\"color: #2c3e50;\">" + concoursTitle + "</strong>." +
                            "                                Votre candidature a été enregistrée avec succès dans notre système." +
                            "                            </p>" +
                            "                        </td>" +
                            "                    </tr>" +
                            "                    <tr>" +
                            "                        <td style=\"padding-bottom: 20px;\">" +
                            "                            <p style=\"margin: 0; line-height: 24px; color: #333333;\">" +
                            "                                Votre participation sera examinée par notre jury. Les résultats seront " +
                            "                                communiqués à tous les participants à la clôture du concours." +
                            "                            </p>" +
                            "                        </td>" +
                            "                    </tr>" +
                            "                    <tr>" +
                            "                        <td style=\"padding: 20px 0;\">" +
                            "                            <table role=\"presentation\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" align=\"center\">" +
                            "                                <tr>" +
                            "                               <td>" +
                            "                                </tr>" +
                            "                            </table>" +
                            "                        </td>" +
                            "                    </tr>" +
                            "                    <tr>" +
                            "                        <td style=\"padding-top: 10px;\">" +
                            "                            <p style=\"margin: 0; line-height: 24px; color: #333333;\">" +
                            "                                Nous vous remercions pour votre participation et nous vous souhaitons bonne chance!" +
                            "                            </p>" +
                            "                        </td>" +
                            "                    </tr>" +
                            "                </table>" +
                            "            </td>" +
                            "        </tr>" +
                            "        <!-- Information Block -->" +
                            "        <tr>" +
                            "            <td style=\"padding: 30px; background-color: #ecf0f1; text-align: center;\">" +
                            "                <h3 style=\"margin: 0 0 15px; color: #2c3e50; font-size: 18px;\">À Propos du Concours</h3>" +
                            "                <p style=\"margin: 0; line-height: 22px; color: #34495e;\">" +
                            "                    TunArche organise des concours artistiques pour promouvoir l'art et la culture en Tunisie. " +
                            "                    Nos concours sont ouverts à tous les artistes, professionnels comme amateurs." +
                            "                </p>" +
                            "            </td>" +
                            "        </tr>" +
                            "        <!-- Footer -->" +
                            "        <tr>" +
                            "            <td style=\"padding: 20px 30px; background-color: #2c3e50; text-align: center;\">" +
                            "                <p style=\"margin: 0; font-size: 14px; line-height: 20px; color: #ffffff;\">" +
                            "                    &copy; TunArche " + LocalDate.now().getYear() + ". Tous droits réservés." +
                            "                </p>" +
                            "                <p style=\"margin: 5px 0 0; font-size: 14px; line-height: 20px; color: #ecf0f1;\">" +
                            "                    Email envoyé le " + currentDate + "<br>" +
                            "                    <a href=\"mailto:contact@tunarche.com\" style=\"color: #3498db; text-decoration: none;\">contact@tunarche.com</a>" +
                            "                </p>" +
                            "                <p style=\"margin: 10px 0 0; font-size: 13px; line-height: 20px; color: #bdc3c7;\">" +
                            "                    Si vous avez des questions concernant votre participation, n'hésitez pas à nous contacter." +
                            "                </p>" +
                            "            </td>" +
                            "        </tr>" +
                            "    </table>" +
                            "</body>" +
                            "</html>";

            message.setContent(emailBody, "text/html; charset=utf-8");

            // Send the message
            Transport.send(message);

            System.out.println("Email de confirmation envoyé à " + recipientEmail);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur lors de l'envoi de l'email: " + e.getMessage());
            return false;
        }
    }
}