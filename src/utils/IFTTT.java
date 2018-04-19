package utils;

import java.io.File;

import java.util.Scanner;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.Certificate;
import java.io.*;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLPeerUnverifiedException;

public class IFTTT {

  private static String baseWebhookURL = "";
  private static String webhookURL = "";

  private IFTTTKey keyObj = new IFTTTKey();

  private static Logger log = new Logger();

  public IFTTT() {
    baseWebhookURL = "https://maker.ifttt.com/trigger/" + keyObj.getEvent() + "/with/key/" + keyObj.getKey() + "?";
  }

 /**
  * @param String - Name of alert.
  * @param String - Message alert will contain.
  */
  public void sendAlert(String alert, String data) {
    webhookURL = baseWebhookURL + alert + "=" + data;
    URL url;
    try {
        url = new URL(webhookURL);
        HttpsURLConnection con = (HttpsURLConnection)url.openConnection();
        log.add("[INFO]", this.print_content(con));
    } catch(Exception e) {
      e.printStackTrace();
      //log.alert("[ERROR] IFTTT Webhook Connection Broken", "Couldn't connect to IFTTT webhook service. " + e.getStackTrace());
    }
  }

  private String print_content(HttpsURLConnection con){
    String result = "";
   if(con != null) {
      try {
         BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
         String input;

         while ((input = br.readLine()) != null){
            result = result + input;
         }
         br.close();

      } catch (IOException e) {
         e.printStackTrace();
      }
   }
   return result;
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
        sc.close();
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
