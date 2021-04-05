package com.marcwoodyard.RaspberryPiThermostat.coms;

import com.marcwoodyard.RaspberryPiThermostat.utils.Logger;

import java.io.File;

/**
 * Handles email/IFTTT notifications
 */
public class CommunicationModule {

    private static Email email = new Email();
    private static IFTTT ifttt = null;

    private static Logger log = new Logger();

    public static void sendEmail(String subject, String message) {
        email.sendEmail(subject, message);
    }

    public static void sendIFTTT(String alert, String data) {
        if (ifttt == null && new File("IFTTTConfig.ini").exists())
            ifttt = new IFTTT();

        ifttt.sendAlert(alert, data);
    }

}