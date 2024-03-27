package client.utils;

import client.ConfigLoader;
import com.google.inject.Inject;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import java.util.Objects;
import java.util.Properties;

public class EmailManager {
    private final ConfigLoader configLoader;
    private boolean haveCredentials;
    private String valid = null;
    private String email;
    private String password;
    private Session session;

    @Inject
    public EmailManager(ConfigLoader configLoader) {
        this.configLoader = configLoader;
        loadAndCheckCredentials();
    }

    private void loadAndCheckCredentials() {
        this.email = (String) this.configLoader.getProperty("email");
        this.password = (String) this.configLoader.getProperty("password");
        if (!this.email.isEmpty() && !haveCredentials && !this.password.isEmpty() && this.areCredentialsValid()) {
            haveCredentials = true;
        }
    }

    public boolean areCredentialsValid() {
        if (this.email.isEmpty() || this.password.isEmpty()) {
            return false;
        }
        if (Objects.equals(valid, "true")) {
            return true;
        } else if (Objects.equals(valid, "false")) {
            return false;
        }
        boolean status = this.sendEmail(this.email, "Test", "Test for email configuration");
        if (status) {
            System.out.println("Creating email session");
        } else {
            System.out.println("Invalid credentials for session");
        }
        valid =  "" + status;
        return status;
    }

    public boolean sendEmail(String to, String title, String body) {
        if (this.session == null) {
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.host", this.getEmailProvider());
            props.put("mail.smtp.port", "587");

            String email = this.email;
            String password = this.password;
            this.session = Session.getInstance(props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(email.split("@")[0], password);
                    }
                });
        }

        boolean status;
        try {
            Message message = new MimeMessage(this.session);
            message.setFrom(new InternetAddress(email));
            message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(to));
            message.setSubject(title);
            message.setText(body);
            message.setRecipients(Message.RecipientType.CC,
                InternetAddress.parse(email));
            Transport.send(message);

            status = true;
        } catch (MessagingException e) {
            status = false;
        }
        return status;
    }

    private String getEmailProvider() {
        String smtpHost = "";
        switch (this.email.split("@")[1]) {
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
