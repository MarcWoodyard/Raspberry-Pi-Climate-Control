package utils;

import java.io.File;

public class CommunicationModule {

    private Email email = new Email();
    private IFTTT ifttt = null;

    private static Logger log = new Logger();

    public CommunicationModule() {
        if (new File("./src/Config/IFTTTConfig.txt").exists())
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