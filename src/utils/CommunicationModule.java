package utils;

import java.util.Scanner;
import java.util.Properties;
import java.util.Date;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class CommunicationModule {

	private static String fromEmail;
	private static String toEmail;
	private static String smtpServer;
	private static String port;
	private static String enableAuth;
	private static String enableSTARTLS;
	private static String username;
	private static String password;

	private static final File emailConfig = new File("./src/Config/EmailConfig.txt");

	/**
	* Creates a CommunicationModule object.
	*/
	public CommunicationModule() {
		if(this.fromEmail == null || this.toEmail == null || this.smtpServer == null || this.port == null
		|| this.enableAuth == null || this.username == null || this.password == null) {

				try {
						Scanner sc = new Scanner(this.emailConfig);

						int place = 0;

						while (sc.hasNextLine() && place <= 7) {
								String data = sc.nextLine();

								switch(place) {
									case 0:
										this.fromEmail = data;
										break;
									case 1:
										this.toEmail = data;
										break;
									case 2:
										this.smtpServer = data;
										break;
									case 3:
										this.port = data;
										break;
									case 4:
										this.enableAuth = data;
										break;
									case 5:
										this.enableSTARTLS = data;
										break;
									case 6:
										this.username = data;
										break;
									case 7:
										this.password = data;
										break;
									default:
										System.out.println("Too many parameters in EmailConfig.txt.\n Program exiting...");
										System.exit(0);
										break;
								}
								place++;
						}

						sc.close();
				} catch (FileNotFoundException e) {
						e.printStackTrace();
				}
		}
	}

	/**
	* Sends a email message.
	*
	* @param - String - Subject of the email.
	* @param - String - Body of the email.
	*/
	public void sendEmail (String subject, String body) {
		// Credit: http://www.journaldev.com/2532/javamail-example-send-mail-in-java-smtp
		Properties props = new Properties();
		props.put("mail.smtp.host", this.smtpServer);
		props.put("mail.smtp.port", this.port);
		props.put("mail.smtp.auth", this.enableAuth);
		props.put("mail.smtp.starttls.enable", this.enableSTARTLS);

		Authenticator auth = new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				String fromEmail = "";
				String password = "";
				File emailConfig = new File("./src/Config/EmailConfig.txt");

					try {
							Scanner sc = new Scanner(emailConfig);
							int place = 0;

							while (sc.hasNextLine() && place <= 7) {
									String data = sc.nextLine();

									switch(place) {
										case 0:
											fromEmail = data;
											break;
										case 7:
											password = data;
											break;
									}
									place++;
								}
						} catch (FileNotFoundException e) {
										e.printStackTrace();
						}
				return new PasswordAuthentication(fromEmail, password);
			}
		};

		Session session = Session.getInstance(props, auth);
		this.emailWorker(session, this.toEmail, subject, body);
	}

	/**
	 * Utility method to send simple HTML email
	 *
	 * @param - Session - A session object.
	 * @param - String - Email address where you want to send email.
	 * @param - String - Subject line of email.
	 * @param - String - Body of email.
	 */
	private void emailWorker(Session session, String toEmail, String subject, String body){
		try {
	      MimeMessage msg = new MimeMessage(session);
	      //Message Headers
	      msg.addHeader("Content-type", "text/HTML; charset=UTF-8");
	      msg.addHeader("format", "flowed");
	      msg.addHeader("Content-Transfer-Encoding", "8bit");

	      msg.setFrom(new InternetAddress(this.fromEmail, this.fromEmail));
	      msg.setReplyTo(InternetAddress.parse(this.fromEmail, false));
	      msg.setSubject(subject, "UTF-8");
	      msg.setText(body, "UTF-8");
	      msg.setSentDate(new Date());

	      msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail, false));
    	  Transport.send(msg);
	    }
	    catch (Exception e) {
	      e.printStackTrace();
	    }
	}

	/**
	 * In Development - Send SMS notifications.
	 *
	 * @param - int - Phone number to send message to.
	 * @return - String - A message to send.
	 */
	public void sendSMS(int number, String message) {
		//https://www.twilio.com/blog/2016/04/sending-sms-with-java.html
	}

	/**
	 * Returns the email address that will be receiving email notifications.
	 *
	 * @return - String - Email address receiving notifications.
	 */
	public String getToEmail() {
		return this.toEmail;
	}

	/**
	 * Returns the email address that will be sending email notifications.
	 *
	 * @return - String - Email address sending notifications.
	 */
	public String getFromEmail() {
		return this.fromEmail;
	}
}
