package utils;

import coms.CommunicationModule;

import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

    // Log Files
    private static File log = new File("Log.txt");
    private static File errorLog = new File("ErrorLog.txt");
    private static double logSize = (double) log.length() / (1024 * 1024); // Size in MB
    private static double errorLogSize = (double) errorLog.length() / (1024 * 1024); // Size in MB

    // Dates
    private static DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a MM/dd/yyyy");
    private static DateFormat day = new SimpleDateFormat("dd");

    // Communication Module
    private static CommunicationModule coms = new CommunicationModule();

    // File Writer
    private static FileWriter logWriter = null;
    private static FileWriter errorWriter = null;

    public Logger() {
        try {
            if (logWriter == null)
                logWriter = new FileWriter(log, true);

            if (errorWriter == null)
                errorWriter = new FileWriter(errorLog, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void add(String type, String info) {
        if (Integer.parseInt(day.format(new Date())) == 11 || Integer.parseInt(day.format(new Date())) == 25)
            this.cleanLogs();

        if (type.contains("[ERROR]")) {
            try {
                errorWriter.write("\r\n[" + dateFormat.format(new Date()) + "]" + info);
                errorWriter.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        try {
            logWriter.write("\r\n[" + dateFormat.format(new Date()) + "]" + info);
            logWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(type + " [" + dateFormat.format(new Date()) + "] " + info);
    }

    private void cleanLogs() {
        try {
            if (logSize > 5.0)
                log = new File(deleteLog(log.getPath()));

            if (errorLogSize > 5.0)
                errorLog = new File(deleteLog(errorLog.getPath()));

        } catch (Exception e) {
            this.alert("[ERROR] Exception in Logger.java", "An Exception Occured in Logger.java " + e.getMessage());
        }
    }

    private String deleteLog(String logPath) {
        try {
            new File(logPath).delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return logPath;
    }

    public void alert(String subject, String body) {
        coms.sendEmail(subject, body);
        //this.add(subject, body);
    }
}
