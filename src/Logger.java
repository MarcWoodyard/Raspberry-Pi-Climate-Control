import java.util.Date;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Logger {

	private static File errorLog = new File("Logs", "ErrorLog.txt");
	private static File log = new File("Logs", "Log.txt");

	private static DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a yyyy/MM/dd");

	private static CommunicationModule loggerComs = new CommunicationModule();

	/**
	* Creates a Logger object.
	* @param - None
	* @return - None
	*/
	public Logger() {
		System.out.println("[Launcher] Starting logging service.");
	}


	/**
	* Logs a program event to a log file and outputs the data to the console.
	* @param - String - Log data to be written to the log file and output data to console.
	* @return - None
	*/
	public void add(String info) {

		if(info.contains("[ERROR]")) {
			try {
				FileWriter errorWriter = new FileWriter(errorLog, true);
				errorWriter.write("\r\n[" + this.dateFormat.format(new Date()) + "]" + info);
				errorWriter.flush();
				errorWriter.close();
			} catch (IOException e) {
				loggerComs.sendEmail("Logger Service Crashed", "The AC controller logger service crashed.", this.loggerComs.getToEmail());
				e.printStackTrace();
			}
		}

		try {
			FileWriter logWriter = new FileWriter(log, true);
			logWriter.write("\r\n[" + this.dateFormat.format(new Date()) + "]" + info);
			logWriter.flush();
			logWriter.close();
		} catch (IOException e) {
			loggerComs.sendEmail("Logger Service Crashed", "The AC controller logger service crashed.", this.loggerComs.getToEmail());
			e.printStackTrace();
		}

		System.out.println("[" + this.dateFormat.format(new Date()) + "]" + info);
	}
}
