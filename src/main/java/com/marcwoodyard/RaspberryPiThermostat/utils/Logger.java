package com.marcwoodyard.RaspberryPiThermostat.utils;

import com.marcwoodyard.RaspberryPiThermostat.coms.CommunicationModule;

import java.io.*;
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

    // File Writer
    private static FileWriter logWriter;
    private static FileWriter errorWriter;

    static {
        try {
            logWriter = new FileWriter(log, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static {
        try {
            errorWriter = new FileWriter(errorLog, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void add(String type, String info) {
        if (Integer.parseInt(day.format(new Date())) == 11 || Integer.parseInt(day.format(new Date())) == 25)
            cleanLogs();

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

    private static void cleanLogs() {
        try {
            if (logSize > 5.0)
                log = new File(deleteLog(log.getPath()));

            if (errorLogSize > 5.0)
                errorLog = new File(deleteLog(errorLog.getPath()));

        } catch (Exception e) {
            alert("[ERROR] Exception in Logger.java", "An Exception Occurred in Logger.java " + e.getMessage());
        }
    }

    private static String deleteLog(String logPath) {
        try {
            new File(logPath).delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return logPath;
    }

    public static void alert(String subject, String body) {
        CommunicationModule.sendEmail(subject, body);
    }

    public static String getLogs() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("Log.txt"));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            int count = 0;

            while (line != null && count <= 120) {
                sb.append(line);
                sb.append("\n");
                line = br.readLine();
                count++;
            }

            br.close();

            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return "Error grabbing log data.";
    }
}