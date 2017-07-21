import java.util.Date;
import java.util.Scanner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;

public class Logger {

	private File log = new File("Logs", "Log.txt");
	private File errorLog = new File("Logs", "ErrorLog.txt");

	private Scanner logScan;
	private Scanner errorScan;

	private DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a MM/dd/yyyy");

	private CommunicationModule coms = new CommunicationModule();

	/**
	 * Creates a Logger object.
	 * @param - None
	 * @return - None
	 */
	public Logger() {

		try {
			this.logScan = new Scanner(log);
			this.errorScan = new Scanner(errorLog);
			this.logScan = new Scanner(log);
			this.errorScan = new Scanner(errorLog);
		}

		catch (Exception e) {
			this.alert("An Exception Occured in Logger.java","" + Exception);
			e.printStackTrace();
		}
/*
		catch (FileNotFoundException fileEx) {
			this.alert("An Exception Occured in Logger.java","" + fileEx);
			fileEx.printStackTrace();
		}
*/
	}

	/**
	 * Logs a program event to a log file and outputs the data to the console.
	 * @param - String - Log data to be written to the log file and output data to console.
	 * @return - None
	 */
	public void add(String type, String info) {

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
	* @param - None
	* @return - None
	*/ /*
	public boolean cleanLogs() {
		try {
			String logEntry = logScan.nextLine().subString(9,31);
		} catch (Exception e) {
			this.alert("An Exception Occured in Logger.java","" + e);
			e.printStackTrace();
		}

		return false;
	}
	*/

	/**
	* Returns current room humidity.
	* @param - String - Subject of email.
	* @param - String - Body of email.
	* @return - None
	*/
	public void alert(String subject, String body) {
			this.coms.sendEmail(subject, body);
	}
}
