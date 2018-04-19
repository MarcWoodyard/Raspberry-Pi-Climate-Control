package utils;

import java.util.Date;
import java.util.ArrayList;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;

public class Logger {

	// Log Files
	private static File log = new File("./src/Logs/Log.txt");
	private static File errorLog = new File("./src/Logs/ErrorLog.txt");
	private static double logSize = (double) log.length() / (1024 * 1024); // Size in MB
	private static double errorLogSize = (double) errorLog.length() / (1024 * 1024); // Size in MB

	// File Writer
	private FileWriter logWriter = null;
	private static FileWriter errorWriter = null;

	// Dates
	private static DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a MM/dd/yyyy");
	private static DateFormat day = new SimpleDateFormat("dd");

	// Communication Module
	private static CommunicationModule coms = new CommunicationModule();

	public Logger() {
		try {
			if (logWriter == null) {
				logWriter = new FileWriter(log, true);
			}

			if (errorWriter == null) {
				errorWriter = new FileWriter(errorLog, true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Logs a program event to a log file and outputs the data to the console.
	 * @param String - Specifies the type of log entry ([ERROR], [INFO]).
	 * @param String - Log data to be written to the log file and output data to console.
	 */
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

	/**
	 * Add log enteries to new log file after deleting the old one.
	 */
	private void add(String data) {
		try {
			if (data.contains("[ERROR]")) {
				errorWriter.write(data);
				errorWriter.flush();
			}
			logWriter.write(data);
			logWriter.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Cleans up old log entries.
	 */
	private void cleanLogs() {
		try {
			if (logSize > 5.0) {
				BufferedReader br = new BufferedReader(new FileReader(log));
				ArrayList<String> logEnteries = new ArrayList<>();

				for (int i = 0; i <= 100; i++)
					logEnteries.add(br.readLine());

				String logPath = log.getPath();
				log.delete();
				log = new File(logPath);

				for (int j = 0; j < logEnteries.size(); j++)
					this.add(logEnteries.get(j));

				br.close();
			} else if (errorLogSize > 5.0) {
				BufferedReader br = new BufferedReader(new FileReader(log));
				ArrayList<String> logEnteries = new ArrayList<>();

				for (int i = 0; i <= 100; i++)
					logEnteries.add(br.readLine());

				String logPath = errorLog.getPath();
				errorLog.delete();
				errorLog = new File(logPath);

				for (int j = 0; j < logEnteries.size(); j++)
					this.add(logEnteries.get(j));

				br.close();
			}
		} catch (Exception e) {
			this.alert("[ERROR] Exception in Logger.java", "An Exception Occured in Logger.java " + e);
		}
	}

	/**
	 * Sends an alert email.
	 * @param String - Subject of email.
	 * @param String - Body of email.
	 */
	public void alert(String subject, String body) {
		coms.sendEmail(subject, body);
		this.add(subject, body);
	}
}
