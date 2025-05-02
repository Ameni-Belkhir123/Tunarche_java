package com.example.oeuvre.Services;

import com.example.oeuvre.config.EmailConfig;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailService {
    private final String username;
    private final String password;
    private final Properties props;

    public EmailService() {
        this.username = EmailConfig.USERNAME;
        this.password = EmailConfig.PASSWORD;

        props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", EmailConfig.SMTP_HOST);
        props.put("mail.smtp.port", EmailConfig.SMTP_PORT);
    }

    public void sendApprovalEmail(String toEmail) throws MessagingException {
        String subject = "Your Artwork Assignment Request Has Been Approved";
        String content = "Dear Artist,\n\n" +
                "We are pleased to inform you that your request to assign your artwork has been approved.\n\n" +
                "Thank you for using our platform.\n\n" +
                "Best regards,\n" +
                "Tunarche team";

        sendEmail(toEmail, subject, content);
    }

    private void sendEmail(String to, String subject, String content) throws MessagingException {
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(EmailConfig.FROM_EMAIL));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(content);

        Transport.send(message);
    }
}