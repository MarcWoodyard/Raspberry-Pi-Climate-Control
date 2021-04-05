package com.marcwoodyard.RaspberryPiThermostat.coms;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;

public class Email {

    private static MailAuth mailAuth = new MailAuth();

    void sendEmail(String subject, String body) {
        Properties props = new Properties();
        props.put("mail.smtp.host", mailAuth.getSMTPServer());
        props.put("mail.smtp.port", mailAuth.getPort());
        props.put("mail.smtp.auth", mailAuth.getEnableAuth());
        props.put("mail.smtp.starttls.enable", mailAuth.getEnableSTARTLS());
        this.emailWorker(Session.getInstance(props, mailAuth.getAuth()), mailAuth.getToEmail(), subject, body);
    }

    private void emailWorker(Session session, String toEmail, String subject, String body) {
        try {
            MimeMessage msg = new MimeMessage(session);
            msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
            msg.addHeader("format", "flowed");
            msg.addHeader("Content-Transfer-Encoding", "8bit");
            msg.setFrom(new InternetAddress(mailAuth.getFromEmail(), mailAuth.getFromEmail()));
            msg.setReplyTo(InternetAddress.parse(mailAuth.getFromEmail(), false));
            msg.setSubject(subject, "UTF-8");
            msg.setText(body, "UTF-8");
            msg.setSentDate(new Date());
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
            Transport.send(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class MailAuth {

        private String toEmail;
        private String fromEmail;
        private String smtpServer;
        private String port;
        private String enableAuth;
        private String enableSTARTLS;
        private String username;
        private String password;
        private Authenticator auth;

        MailAuth() {
            try {
                Scanner SCANNER = new Scanner(new File("NotificationSettings.ini"));

                // Import Data
                toEmail = SCANNER.nextLine();
                fromEmail = SCANNER.nextLine();
                smtpServer = SCANNER.nextLine();
                port = SCANNER.nextLine();
                enableAuth = SCANNER.nextLine();
                enableSTARTLS = SCANNER.nextLine();
                username = SCANNER.nextLine();
                password = SCANNER.nextLine();

                // Setup Authenticator
                auth = new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                };
            } catch (Exception e) {
                System.out.println("[ERROR] NotificationSettings.ini not found");
            }
        }

        private String getFromEmail() {
            return fromEmail;
        }

        private String getToEmail() {
            return toEmail;
        }

        private String getSMTPServer() {
            return smtpServer;
        }

        private String getPort() {
            return port;
        }

        private String getEnableAuth() {
            return enableAuth;
        }

        private String getEnableSTARTLS() {
            return enableSTARTLS;
        }

        private Authenticator getAuth() {
            return auth;
        }
    }
}