package client.utils;

import client.ConfigLoader;
import com.google.inject.Inject;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailManager {
    private final ConfigLoader configLoader;
    private boolean haveCredentials = false;
    private String email;
    private String password;

    @Inject
    public EmailManager(ConfigLoader configLoader) {
        this.configLoader = configLoader;
        loadAndCheckCredentials();
    }

    private void loadAndCheckCredentials() {
        this.email = (String) this.configLoader.getProperty("email");
        this.password = (String) this.configLoader.getProperty("password");
        if (!this.email.isEmpty() && !this.password.isEmpty() && this.areCredentialsValid()) {
            haveCredentials = true;
        }
    }

    public boolean areCredentialsValid() {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", this.getEmailProvider());
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props);

        try {
            Transport transport = session.getTransport("smtp");
            transport.connect(this.email, this.password);
            transport.close();
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sendEmail(String to, String title, String body) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", this.getEmailProvider());
        props.put("mail.smtp.port", "587");
    
        String email = this.email;
        String password = this.password;
        
        Session session = Session.getInstance(props,
            new javax.mail.Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(email, password);
                }
            });

        boolean status;
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(email));
            message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(to));
            message.setSubject(title);
            message.setText(body);

            Transport.send(message);
            
            status = true;
        } catch (MessagingException e) {
            status = false;
            e.printStackTrace();
        }
        return status;
    }

    private String getEmailProvider() {
        String smtpHost = "";
        switch (this.email) {
            case "gmail.com" -> smtpHost = "smtp.gmail.com";
            case "yahoo.com" -> smtpHost = "smtp.mail.yahoo.com";
            case "outlook.com" -> smtpHost = "smtp-mail.outlook.com";
            default -> {
                smtpHost = "smtp.gmail.com";
                System.out.println("Unsupported email provider. Using default settings.");
            }
        }
        return smtpHost;
    }

}
