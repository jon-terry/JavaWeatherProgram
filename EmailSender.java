import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;


public class EmailSender {

    public static void sendEmail(String recipient, String subject, String message) {
        Properties emailConfig = loadEmailConfig();
        String emailUsername = emailConfig.getProperty("emailUsername");
        String emailPassword = emailConfig.getProperty("emailPassword");

        Properties props = new Properties();

        // Set up JavaMail
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Create Session with authentication

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailUsername, emailPassword);
            }
        });

        try {
            // Create message
            Message emailMessage = new MimeMessage(session);
            emailMessage.setFrom(new InternetAddress(emailUsername));
            emailMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
            emailMessage.setSubject(subject);
            emailMessage.setText(message);

            // Send email
            Transport.send(emailMessage);

            System.out.println("Email sent successfully to: " + recipient);
        } catch (MessagingException e) {
            e.printStackTrace();
            System.out.println("Email sending has failed.");
        }

    }

    private static Properties loadEmailConfig() {
        Properties emailConfig = new Properties();
        String filepath = "config/config.properties";
        try (FileInputStream fileInputStream = new FileInputStream(filepath)) {
            emailConfig.load(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return emailConfig;
    }

}


// FileInputStream error