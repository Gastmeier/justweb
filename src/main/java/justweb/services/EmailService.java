package justweb.services;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailService {

    public MimeMessage newMessage(String to, String subject) {
        final Properties properties = System.getProperties();
        properties.setProperty("mail.smtp.host", "smtp.1blu.de");
        properties.setProperty("mail.smtp.auth", "true");

        final javax.mail.Session session = javax.mail.Session.getInstance(
                properties,
                new Authenticator() {
                    @Override protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("f172880_0-noreply", "b*zLi%`.+;dV9;XNJYc:4!uoEG4qWd#BvW0scl@9");
                    }
                }
        );

        MimeMessage message = new MimeMessage(session);

        try {
            message.setFrom(new InternetAddress("noreply@urtricks.com"));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }

        return message;
    }

    public void send(MimeMessage message) throws MessagingException {
        Transport.send(message);
    }

}
