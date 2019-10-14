package coms;

import utils.Logger;

import java.io.File;

/**
 * Handles email/IFTTT notifications
 */
public class CommunicationModule {

    private static Email email = new Email();
    private static IFTTT ifttt = null;

    private static Logger log = new Logger();

    public CommunicationModule() {
        if (new File("IFTTTConfig.ini").exists())
            ifttt = new IFTTT();
    }

    public void sendEmail(String subject, String message) {
        email.sendEmail(subject, message);
    }

    public void sendIFTTT(String alert, String data) {
        if (ifttt == null) {
            log.add("[ERROR]", "IFTTT not setup. Please enable IFTTT to receive webhook notifications.");
            return;
        }

        ifttt.sendAlert(alert, data);
    }

}