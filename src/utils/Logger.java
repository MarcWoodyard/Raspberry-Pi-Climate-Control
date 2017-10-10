package utils;

import java.util.Date;
import java.util.Scanner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;

public class Logger {
	private static final File log = new File("./src/Logs/Log.txt");
	private static final File errorLog = new File("./src/Logs/ErrorLog.txt");

	private Scanner logScan;
	private Scanner errorScan;

	private static DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a MM/dd/yyyy");
	private static DateFormat day = new SimpleDateFormat("dd");

	private static CommunicationModule coms = new CommunicationModule();

	/**
	 * Creates a Logger object.
	 */
	public Logger() {
		try {
			this.logScan = new Scanner(log);
			this.errorScan = new Scanner(errorLog);
		}
		catch (Exception e) {
			this.alert("An Exception Occured in Logger.java","" + e);
			e.printStackTrace();
		}
	}

	/**
	 * Logs a program event to a log file and outputs the data to the console.
	 *
	 * @param - String - Log data to be written to the log file and output data to console.
	 */
	public void add(String type, String info) {

		int date = Integer.parseInt(this.day.format(new Date()));
		if(date == 11 || date == 25)
			this.cleanLogs();

		if(type.equals("[ERROR]")) {
			try {
				FileWriter errorWriter = new FileWriter(errorLog, true);
				errorWriter.write("\r\n[" + this.dateFormat.format(new Date()) + "]" + info);
				errorWriter.flush();
				errorWriter.close();
			} catch (IOException e) {
				this.alert("An Exception Occured in Logger.java","" + e);
			}
		}

		try {
			FileWriter logWriter = new FileWriter(log, true);
			logWriter.write("\r\n[" + this.dateFormat.format(new Date()) + "]" + info);
			logWriter.flush();
			logWriter.close();
		} catch (IOException e) {
			this.alert("An Exception Occured in Logger.java","" + e);
		}

		System.out.println(type + " [" + this.dateFormat.format(new Date()) + "] " + info);
	}

	/**
	* Cleans up old log entries.
	*/
	public boolean cleanLogs() {
		try {

			File tmp = this.log;
			double fileSize;

			double bytes = 0.0;
			double kilobytes = (bytes / 1024);
			double megabytes = (kilobytes / 1024);
			// double gigabytes = (megabytes / 1024);
			// double terabytes = (gigabytes / 1024);
			// double petabytes = (terabytes / 1024);
			// double exabytes = (petabytes / 1024);
			// double zettabytes = (exabytes / 1024);
			// double yottabytes = (zettabytes / 1024);

			for(int i = 0; i <= 1; i++) {

				if(i == 0)
					tmp = this.log;
				else
					tmp = this.errorLog;

				bytes = tmp.length();
				fileSize = megabytes;

				if(fileSize > 5.0) {
					tmp.delete();
					tmp.createNewFile();
				}
			}


		} catch (Exception e) {
			this.alert("An Exception Occured in Logger.java","" + e);
			e.printStackTrace();
			return false;
		}

		return true;
	}


	/**
	* Sends an alert email.
	*
	* @param - String - Subject of email.
	* @param - String - Body of email.
	*/
	public void alert(String subject, String body) {
			this.coms.sendEmail(subject, body);
	}
}
