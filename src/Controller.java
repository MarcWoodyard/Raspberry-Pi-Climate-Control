import java.io.File;
import java.io.FileNotFoundException;

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
	* @param - double - Max Temperature.
	* @param - double - Min Temperature.
	* @return - None
	*/
	public Controller() {
		this.log("[SYSTEM]", "Setting up AC Controller. Please wait...");

		//Get Temperature Presets
		this.scanTempFile();
	}

	/**
	* Turn AC on/off.
	* @param - None
	* @return - None
	*/
	public void switchAC() {
		this.servo.moveServo(48, 500, 130);

		//Reset tempWatch();
		this.tooHot = 0;
		this.tooCold = 0;
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
	* Ensures the room doesn't get too hot or cold.
	* @param - None
	* @return - None
	*/
	public void tempWatch() {
		//Room temperature is too hot. Servo didn't hit AC button correctly.
		if(this.curTemp >= this.maxTemperature) {
			this.tooHot++;

			if(this.tooHot == 3 || this.tooHot == 6) {
				this.log("[ERROR]", "Temperature too hot. Correcting...");
				this.alert("Temperature Above Max Value!", "The temperature in your room has exceded the limit you set.");
				this.servo.moveServo(47, 500, 130);
			}
			else if(this.tooHot > 6) {
				this.log("Creating New Servo Object", "Temperature too hot. Creating new Java servo object.");
				this.alert("Creating New Servo Object", "Temperature too hot. Creating new Java servo object.");
				this.tooHot = 0;
				this.servo = new Servo();
				this.servo.moveServo(47, 500, 130);
			}
		}

		//Room temperature is too cold. Servo didn't hit AC button correctly.
		else if(this.curTemp <= this.minTemperature) {
			this.tooCold++;

			if(this.tooCold == 5 || this.tooCold == 10) {
				this.log("[ERROR]", "Temperature too cold. Correcting...");
				this.alert("Temperature Below Min Value!", "The temperature in your room has dropped below the limit you set.");
				this.servo.moveServo(47, 500, 130);
			}
			else if(this.tooCold > 10) {
				this.log("Creating New Servo Object", "Temperature too hot. Creating new Java servo object.");
				this.alert("Creating New Servo Object", "Temperature too hot. Creating new Java servo object.");
				this.tooCold = 0;
				this.servo = new Servo();
				this.servo.moveServo(47, 500, 130);
			}
		}
	}

	/**
	* Returns current servo status.
	* @param - None
	* @return - boolean - Return current servo status.
	*/
	public boolean getServoStatus() {
		return this.servo.getServoStatus();
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

	/**
	* Returns the max temperature set in the config file.
	* @param - None
	* @return - double - Min temperature.
	*/
	public double getMaxTemp() {
		return this.maxTemperature;
	}

	/**
	* Returns the min temperature set in the config file.
	* @param - None
	* @return - double - Min temperature.
	*/
	public double getMinTemp() {
		return this.minTemperature;
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
			this.log("[ERROR]", "Exception occured: " + e.getMessage() + "\nMinutes: " + minutes);
			this.alert("AC Controller - Sleep Method Error", "The sleep method in the Controller class encountered an error.");
		}
	}

	/**
	* Send a string to the logger to be logged in a text file.
	* @param - String - What type of event is it ([ERROR], [INFO]...).
	* @param - String - Data to be logged.
	* @return - None
	*/
	public void log(String type, String info) {
		this.log.add(type, info);
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
	* Scans TemperatureConfig.txt to get the min and max temperature.
	* @param - None
	* @return - None
	*/
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

	/**
	* Shutdown AC Controller.
	* @param - None
	* @return - None
	*/
	public void shutdown() {
		if(this.servo.getServoStatus() == true)
			this.switchAC();
		this.log("[SHUTDOWN]", "Shutting down...");
		this.alert("AC Controller Shutting Down", "The AC Controller is shutting down.");
		System.exit(0);
	}
}
