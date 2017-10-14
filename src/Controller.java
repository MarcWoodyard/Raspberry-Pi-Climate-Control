import java.io.File;
import java.io.FileNotFoundException;

import utils.CommunicationModule;
import utils.Logger;

import sensors.DHT11;
import sensors.Servo;

import java.util.Scanner;

public class Controller {

	//Servo Motor
	private Servo servo = new Servo();

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
	*
	* @param - double - Max Temperature.
	* @param - double - Min Temperature.
	*/
	public Controller(double max, double min) {
		this.log("[SYSTEM]", "Setting up AC Controller. Please wait...");

		this.maxTemperature = max;
		this.minTemperature = min;
	}

	/**
	* Turn AC on/off.
	*/
	public void switchAC() {
		this.servo.switchAC();

		//Reset tempWatch();
		this.tooHot = 0;
		this.tooCold = 0;
	}

	/**
	* Updates room temperature and humidity values.
	*/
	public void temperatureUpdate() {
		this.tempSensor.updateTemperature(7);
		this.curTemp = this.tempSensor.getTemperature();
		this.curHumidity = this.tempSensor.gethumidity();
	}

	/**
	* Ensures the room doesn't get too hot or cold.
	*/
	public void tempWatch() {
		//Room temperature is too hot. Servo didn't hit AC button correctly.
		if(this.curTemp >= this.maxTemperature) {
			this.tooHot++;

			if(this.tooHot == 1) {
				this.log("[ERROR]", "Temperature too hot. Correcting...");
				this.alert("Temperature Above Max Value!", "The temperature in your room has exceded the limit you set.");
				this.servo.moveServo(50, 600, 60);
			}
			else if(this.tooHot >= 2) {
				this.log("[ERROR]", "Temperature too hot. Creating new Java servo object.");
				this.alert("Creating New Servo Object", "Temperature too hot. Creating new Java servo object.");
				this.tooHot = 0;
				this.servo = new Servo();
				this.servo.moveServo(50, 600, 60);
			}
		}

		//Room temperature is too cold. Servo didn't hit AC button correctly.
		else if(this.curTemp < this.minTemperature) {
			this.tooCold++;

			if(this.tooCold == 1) {
				this.log("[ERROR]", "Temperature too cold. Correcting...");
				this.alert("Temperature Below Min Value!", "The temperature in your room has dropped below the limit you set.");
				this.servo.moveServo(50, 600, 60);
			}
			else if(this.tooCold >= 2) {
				this.log("[ERROR]", "Temperature too cold. Creating new Java servo object.");
				this.alert("Creating New Servo Object", "Temperature too cold. Creating new Java servo object.");
				this.tooCold = 0;
				this.servo = new Servo();
				this.servo.moveServo(50, 600, 60);
			}
		}
	}

	/**
	* Returns current servo status.
	*
	* @return - boolean - Return current servo status.
	*/
	public boolean getServoStatus() {
		return this.servo.getServoStatus();
	}

	/**
	* Returns current room temperature.
	*
	* @return - double - Current room temperature.
	*/
	public double getTemperature() {
		return this.curTemp;
	}

	/**
	* Returns current room humidity.
	*
	* @return - double - Current room humidity.
	*/
	public double getHumidity() {
		return this.curHumidity;
	}

	/**
	* Returns the max temperature set in the config file.
	*
	* @return - double - Min temperature.
	*/
	public double getMaxTemp() {
		return this.maxTemperature;
	}

	/**
	* Returns the min temperature set in the config file.
	*
	* @return - double - Min temperature.
	*/
	public double getMinTemp() {
		return this.minTemperature;
	}

	/**
	* Current thread sleeps for a set period of time.
	*
	* @param - int - Minutes that current thread should sleep for.
	*/
	public void sleep(int minutes) {
		try {
			Thread.sleep(minutes * 60 * 1000); //Minutes to sleep * seconds to a minute * miliseconds to a second.
		} catch(Exception e) {
			this.log("[ERROR]", "Exception occured: " + e.getMessage() + "\nMinutes: " + minutes);
			this.alert("AC Controller - Sleep Method Error", "The sleep method in the Controller class encountered an error.");
		}
	}

	/**
	* Send a string to the logger to be logged in a text file.
	*
	* @param - String - What type of event is it ([ERROR], [INFO]...).
	* @param - String - Data to be logged.
	*/
	public void log(String type, String info) {
		this.log.add(type, info);
	}

	/**
	* Send an email alert.
	*
	* @param - String - Subject of email.
	* @param - String - Body of email.
	*/
	public void alert(String subject, String body) {
		this.log.alert(subject, body);
	}

	/**
	* Shutdown AC Controller.
	*/
	public void shutdown() {
		if(this.servo.getServoStatus() == true)
			this.switchAC();
		this.log("[SHUTDOWN]", "Shutting down...");
		this.alert("AC Controller Shutting Down", "The AC Controller is shutting down.");
		System.exit(0);
	}

	/**
	* Cleans up the AC controller system logs.
	*/
	public void cleanLogs() {
		this.log.cleanLogs();
	}
}
