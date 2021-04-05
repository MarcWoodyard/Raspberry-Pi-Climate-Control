package com.marcwoodyard.RaspberryPiThermostat.coms;

import com.marcwoodyard.RaspberryPiThermostat.utils.Logger;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Scanner;

public class IFTTT {

    private static String baseWebhookURL = "";
    private static String webhookURL = "";
    private static Logger log = new Logger();
    private IFTTTKey keyObj = new IFTTTKey();

    public IFTTT() {
        baseWebhookURL = "https://maker.ifttt.com/trigger/" + keyObj.getEvent() + "/with/key/" + keyObj.getKey() + "?";
    }

    void sendAlert(String alert, String data) {
        webhookURL = baseWebhookURL + alert + "=" + data;
        URL url;
        try {
            url = new URL(webhookURL);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            log.add("[INFO]", this.printContent(con));
        } catch (Exception e) {
            e.printStackTrace();
            // log.alert("[ERROR] IFTTT Webhook Connection Broken", "Couldn't connect to
            // IFTTT webhook service. " + e.getStackTrace());
        }
    }

    private String printContent(HttpsURLConnection con) {
        StringBuilder result = new StringBuilder();
        if (con != null) {
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String input;

                while ((input = br.readLine()) != null) {
                    result.append(input);
                }
                br.close();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result.toString();
    }

    public static class IFTTTKey {

        private String key = null;
        private String event = null;

        IFTTTKey() {
            try {
                Scanner sc = new Scanner(new File("IFTTTConfig.ini"));

                // Import Data
                key = sc.nextLine();
                event = sc.nextLine();
                sc.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private String getKey() {
            return key;
        }

        private String getEvent() {
            return event;
        }
    }

}
