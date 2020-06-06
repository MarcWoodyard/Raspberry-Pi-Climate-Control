package coms;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;

class Email {

    private MailAuth mailAuth = new MailAuth();

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

        private String fromEmail = null;
        private String toEmail = null;
        private String smtpServer = null;
        private String port = null;
        private String enableAuth = null;
        private String enableSTARTLS = null;
        private String username = null;
        private String password = null;
        private Authenticator auth = null;

        MailAuth() {
            try {
                Scanner SCANNER = new Scanner(new File("EmailConfig.ini"));

                // Import Data
                fromEmail = SCANNER.nextLine();
                toEmail = SCANNER.nextLine();
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
                System.out.println("[ERROR] EmailConfig.ini not found");
                System.exit(0);
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