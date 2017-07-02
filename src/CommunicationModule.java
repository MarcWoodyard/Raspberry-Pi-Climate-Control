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

	private String fromEmail;
	private String toEmail;
	private String smtpServer;
	private String port;
	private String enableAuth;
	private String enableSTARTLS;
	private String username;
	private String password;
	
	/**
	* Creates a CommunicationModule object.
	* @param - None
	* @return - None
	*/
	public CommunicationModule() {

		System.out.println("[Launcher] Setting up communication channels.");
		
		File file = new File("EmailConfig.txt");

	    try {
	        Scanner sc = new Scanner(file);

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

	/**
	* Creates a CommunicationModule object.
	* @param - String - Subject of the email.
	* @param - String - Body of the email.
	* @param - String - Email address of the recipient.
	* @return - None
	*/
	public void sendEmail (String subject, String body, String toEmail) {
		//Credit: http://www.journaldev.com/2532/javamail-example-send-mail-in-java-smtp
		Properties props = new Properties();
		props.put("mail.smtp.host", this.smtpServer);
		props.put("mail.smtp.port", this.port);
		props.put("mail.smtp.auth", this.enableAuth);
		props.put("mail.smtp.starttls.enable", this.enableSTARTLS);

		Authenticator auth = new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				String fromEmail = "";
				String password = "";

				File file = new File("EmailConfig.txt");
					try {
							Scanner sc = new Scanner(file);
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
		this.emailWorker(session, toEmail, subject, body);
	}

	/**
	 * Utility method to send simple HTML email
	 * @param session
	 * @param toEmail
	 * @param subject
	 * @param body
	 */
	private void emailWorker(Session session, String toEmail, String subject, String body){
		try
	    {
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

	public void sendSMS() {
		//https://www.twilio.com/blog/2016/04/sending-sms-with-java.html
	}

	public String getToEmail() {
		return this.toEmail;
	}

	public String getFromEmail() {
		return this.fromEmail;
	}
}
