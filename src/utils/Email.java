package utils;

import java.io.File;
import java.util.Date;
import java.util.Properties;
import java.util.Scanner;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Email {

	private MailAuth mailAuth = new MailAuth();

	/**
	 * Sends a email message.
	 * 
	 * @param String
	 *            - Subject of the email.
	 * @param String
	 *            - Body of the email.
	 */
	public void sendEmail(String subject, String body) {
		Properties props = new Properties();
		props.put("mail.smtp.host", mailAuth.getSMTPServer());
		props.put("mail.smtp.port", mailAuth.getPort());
		props.put("mail.smtp.auth", mailAuth.getEnableAuth());
		props.put("mail.smtp.starttls.enable", mailAuth.getEnableSTARTLS());
		this.emailWorker(Session.getInstance(props, mailAuth.getAuth()), mailAuth.getToEmail(), subject, body);
	}

	/**
	 * Utility method to send simple HTML email
	 * 
	 * @param Session
	 *            - A session object.
	 * @param String
	 *            - Email address where you want to send email.
	 * @param String
	 *            - Subject line of email.
	 * @param String
	 *            - Body of email.
	 */
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

	public class MailAuth {

		private Scanner SCANNER = null;
		private String fromEmail = null;
		private String toEmail = null;
		private String smtpServer = null;
		private String port = null;
		private String enableAuth = null;
		private String enableSTARTLS = null;
		private String username = null;
		private String password = null;
		private Authenticator auth = null;

		public MailAuth() {
			try {
				SCANNER = new Scanner(new File("./data/config/EmailConfig.txt"));

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
				e.printStackTrace();
			}
		}

		public String getFromEmail() {
			return fromEmail;
		}

		public String getToEmail() {
			return toEmail;
		}

		public String getSMTPServer() {
			return smtpServer;
		}

		public String getPort() {
			return port;
		}

		public String getEnableAuth() {
			return enableAuth;
		}

		public String getEnableSTARTLS() {
			return enableSTARTLS;
		}

		public Authenticator getAuth() {
			return auth;
		}
	}

}