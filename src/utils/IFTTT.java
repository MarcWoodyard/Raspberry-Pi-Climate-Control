package utils;

import java.io.File;

import java.util.Scanner;

import java.net.Socket;
import java.net.InetAddress;

public class IFTTT {

  private static InetAddress addr;
  private static String baseWebhookURL = "";
  private static String webhookURL = "";
  private static Socket sock;
  private static String webhookKey = "";
  private IFTTTKey keyObj = new IFTTTKey();

  private static Logger log = new Logger();

  public IFTTT() {
    baseWebhookURL = "https://maker.ifttt.com/trigger/" + keyObj.getEvent() + "/with/key/" + keyObj.getKey() + "?";
    try {
      sock = new Socket(webhookURL, 443);
    } catch(Exception e){
      e.printStackTrace();
    }
  }

 /**
  * @param String - Name of alert.
  * @param String - Message alert will contain.
  */
  public void sendAlert(String alert, String data) {
    webhookURL = baseWebhookURL + alert + "=" + data;
    try {
      addr = sock.getInetAddress();
    } catch(Exception e) {
      log.alert("[ERROR] IFTT Webhook Connection Broken", "Couldn't connect to IFTTT webhook service. " + e.getStackTrace());
    }
  }

  public class IFTTTKey {

    private String key = null;
    private String event = null;

		public IFTTTKey() {
			try {
				Scanner sc = new Scanner(new File("./src/Config/IFTTTConfig.txt"));

        // Import Data
				key = sc.nextLine();
				event = sc.nextLine();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public String getKey() {
			return key;
		}

		public String getEvent() {
			return event;
		}
	}

}
