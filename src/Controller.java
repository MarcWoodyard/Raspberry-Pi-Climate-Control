import java.io.File;
import java.io.FileNotFoundException;

import java.util.Scanner;

public class Controller {

	//Servo Motor
	private boolean servoStatus = false;
	private Runtime runTime = Runtime.getRuntime();

	//Temperature Sensor
	private DHT11 tempSensor = new DHT11();
	private double curTemp;
	private double curHumidity;
	private double maxTemperature;
	private double minTemperature;

	//Temperature Safeguards
	private int tooHot = 0;
	private int tooCold = 0;

	//Logging
	private Logger log = new Logger();

	/**
	* Creates a Controller object.
	* @param - double - Max Temperature.
	* @param - double - Min Temperature.
	* @return - None
	*/
	public Controller() {
		this.log("[SYSTEM]", "Setting up AC Controller. Please wait...");

		//Get Temperature Presets
		this.scanTempFile();

		//Setup Servo Motor
		try {
			this.runTime.exec("gpio mode 1 pwm");
			this.runTime.exec("gpio pwm-ms");
			this.runTime.exec("gpio pwmc 192");
			this.runTime.exec("gpio pwmr 2000");
			this.runTime.exec("gpio pwm 1 130");
		} catch(Exception e) {
			this.log("[ERROR]", "Exception occured: " + e.getMessage());
			this.log.alert("AC Controller Servo Error", "The AC controller encountered a servo motor error.");
		}
	}

	/**
	* Turn's AC on/off.
	* @param - None
	* @return - None
	*/
	public void switchAC() {
		this.moveServo("gpio pwm 1 46", 500, "gpio pwm 1 130");

		if (this.servoStatus == true)
			this.servoStatus = false;
		else if (this.servoStatus == false)
			this.servoStatus = true;
	}

	/**
	* Updates room temperature and humidity values.
	* @param - None
	* @return - None
	*/
	public void temperatureUpdate() {
		this.tempSensor.updateTemperature(7);
		this.curTemp = this.tempSensor.getTemperature();
		this.curHumidity = this.tempSensor.gethumidity();
	}

	/**
	* Ensures room doesn't get too hot or cold.
	* @param - None
	* @return - None
	*/
	public void tempWatch() {
		//Room temperature is too hot. Servo didn't hit AC button correctly.
		if(this.curTemp >= this.maxTemperature + 2.0) {
			this.tooHot++;

			if(this.tooHot == 2) {
				this.log.add("[ERROR]", "Temperature too hot. Correcting...");
				this.moveServo("gpio pwm 1 46", 500, "gpio pwm 1 130");
				this.alert("Temperature Above Max Value!", "The temperature in your room has exceded the limit you set.");
				this.log.add("[EMAIL]", "Email Notification Sent.");
				this.tooHot = 0;
			}
		}

		else if(this.curTemp <= this.minTemperature - 2.0) {
			this.tooCold++;

			if(this.tooCold == 2) {
				this.log.add("[ERROR]", "Temperature too cold. Correcting...");
				this.moveServo("gpio pwm 1 46", 500, "gpio pwm 1 130");
				this.alert("Temperature Below Min Value!", "The temperature in your room has dropped below the limit you set.");
				this.log.add("[EMIAL]", "Email Notification Sent.");
				this.tooCold = 0;
			}
		}
	}

	/**
	* Returns current servo status.
	* @param - None
	* @return - boolean - Return current servo status.
	*/
	public boolean getServoStatus() {
		return this.servoStatus;
	}

	/**
	* Returns current room temperature.
	* @param - None
	* @return - double - Current room temperature.
	*/
	public double getTemperature() {
		return this.curTemp;
	}

	/**
	* Returns current room humidity.
	* @param - None
	* @return - double - Current room humidity.
	*/
	public double getHumidity() {
		return this.curHumidity;
	}

	public double getMaxTemp() {
		return this.maxTemperature;
	}

	public double getMinTemp() {
		return this.minTemperature;
	}

	/**
	* Returns current room humidity.
	* @param - String - Subject of email.
	* @param - String - Body of email.
	* @return - None
	*/
	public void alert(String subject, String body) {
		this.log.alert(subject, body);
	}

	/**
	* Shutdown AC Controller.
	* @param - None
	* @return - None
	*/
	public void shutdown() {
		if(this.servoStatus == true)
			switchAC();
		this.log.add("[SHUTDOWN]", "Shutting down...");
		System.exit(0);
	}

	/**
	* Current thread sleeps for a set period of time.
	* @param - int - Minutes that current thread should sleep for.
	* @return - None
	*/
	public void sleep(int minutes) {
		try {
			Thread.sleep(minutes * 60 * 1000); //Minutes to sleep * seconds to a minute * miliseconds to a second.
		} catch(Exception e) {
			this.log.add("[ERROR]", "Exception occured: " + e.getMessage() + "\nMinutes: " + minutes);
			this.log.alert("AC Controller - Sleep Method Error", "The sleep method in the Controller class encountered an error.");
		}
	}

	/**
	* Sends info to servo for it to move.
	* @param - String - Servo info for move 1.
	* @param - int - How long it takes for the servo to move in miliseconds.
	* @param - String - Servo info for move 1.
	* @return - None
	*/
	private void moveServo(String move1, int sleep, String move2) {
		try {
			this.runTime.exec(move1); //Turn AC on/off.
			Thread.sleep(sleep);
			this.runTime.exec(move2); //Center Servo
		} catch(Exception e) {
			this.log.add("[ERROR]", "Exception occured: " + e.getMessage() + "\nMove 1: " + move1 + " Sleep: " + sleep + " Move 2: " + move2);
			this.log.alert("AC Controller - Servo Error", "The Controler.moveServo method encountered an error.");
		}
	}

	/**
	* Send a string to the logger to be logged in a text file.
	* @param - String - Data to be logged.
	* @return - None
	*/
	public void log(String type, String info) {
		this.log.add(type, info);
	}

	private void scanTempFile() {
		File file = new File("Config", "TemperatureConfig.txt");

	    try {
	        Scanner sc = new Scanner(file);
	        int place = 0;

	        while (sc.hasNextDouble() && place <= 1) {
	            double data = sc.nextDouble();

	            switch(place) {
	            	case 0:
	            		this.maxTemperature = data;
	            		break;
	            	case 1:
	            		this.minTemperature = data;
	            		break;
	            }
	            place++;
	        }

	        sc.close();
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	    }
	}
}
