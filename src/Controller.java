import java.io.File;
import java.io.FileNotFoundException;

import utils.Logger;
import utils.ImageAnalyzer;
import utils.ConfigImporter;
import utils.CommunicationModule;

import sensors.DHT11;
import sensors.Servo;
import sensors.RaspiStill;

import java.util.Scanner;
import java.util.TreeMap;
import java.util.ArrayList;

public class Controller {

	//Servo Motor
	private Servo servo = new Servo();

	// Program Configuration
	private ConfigImporter configImport = new ConfigImporter();

	//Temperature Sensor
	private double curTemp;
	private double curHumidity;
	private double maxTemperature = configImport.getMaxTemp();
	private double minTemperature = configImport.getMinTemp();
	private DHT11 tempSensor = new DHT11();

	// Image Analyzer
	private RaspiStill raspStill = new RaspiStill();
	private ImageAnalyzer imgAnalyzer = new ImageAnalyzer();

	// Logging
	private Logger log = new Logger();

	/**
	 * Creates a Controller object.
	 * @param double - Max Temperature.
	 * @param double - Min Temperature.
	 */
	public Controller() {
		this.log.add("[SYSTEM]", "Setting up AC Controller. Please wait...");
	}

	/**
	 * Turn AC on/off.
	 */
	public void switchAC() {
		this.servo.switchAC();
	}

	/**
	 * Updates room temperature and humidity values.
	 */
	public void temperatureUpdate() {
		this.tempSensor.updateTemperature();
		this.curTemp = this.tempSensor.getTemperature();
		this.curHumidity = this.tempSensor.gethumidity();
	}

	/**
	 * Returns current room temperature.
	 * @return double - Current room temperature.
	 */
	public double getTemperature() {
		return this.curTemp;
	}

	/**
	 * Returns current room humidity.
	 * @return double - Current room humidity.
	 */
	public double getHumidity() {
		return this.curHumidity;
	}

	/**
	 * Returns the max temperature set in the config file.
	 * @return double - Min temperature.
	 */
	public double getMaxTemp() {
		return this.maxTemperature;
	}

	/**
	 * Returns the min temperature set in the config file.
	 * @return double - Min temperature.
	 */
	public double getMinTemp() {
		return this.minTemperature;
	}

	/**
	 * Current thread sleeps for a set period of time.
	 */
	public void sleep(int minutes) {
		try {
			Thread.sleep(minutes * 60 * 1000); //Minutes to sleep * seconds to a minute * miliseconds to a second.
		} catch (Exception e) {
			this.log.alert("[ERROR] Controller.sleep(int)", "Exception occured: " + e.getMessage() + "\nMinutes: " + minutes);
		}
	}

	public void sleep(Double miliseconds) {
		try {
			Thread.sleep(Math.round(miliseconds));
		} catch (Exception e) {
			this.log.alert("[ERROR] Controller.sleep(long)",
					"Exception occured: " + e.getMessage() + "\nSeconds: " + miliseconds);
		}
	}

	/**
	 * Returns the physical state of the AC by taking a picture of the remote control
	 * and anayzing the pixels for any status activity.
	 * @return Boolean - True if AC is in the on position.
	 */
	public boolean acStatus() {
		this.raspStill.takePicture();
		this.sleep(5000.0);
		TreeMap<Integer, Integer> colorMap = this.imgAnalyzer.mapPixels(new File("image.jpg"));
		this.sleep(5000.0);
		ArrayList<String> colorArray = this.imgAnalyzer.extractColors(colorMap);

		if (colorArray.contains("Green"))
			return true;

		return false;
	}

	/**
	 * Shutdown AC Controller.
	 */
	public void shutdown() {
		this.log.alert("[SHUTDOWN] AC Controller Shutting Down", "The AC Controller is shutting down.");

		do {
			// If the AC is on, shut it down.
			if (this.servo.getServoStatus() == true)
				this.switchAC();
			// If servoStatus has the wrong variable.
			else if (this.acStatus() == true)
				this.switchAC();
		} while (this.acStatus() == true);

		System.exit(0);
	}

	public static void main(String[] args) {
		Logger log = new Logger();
		ConfigImporter config = new ConfigImporter();
		Controller a = new Controller();
		log.alert("AC Controller Starting Up", "Your Raspberry Pi AC controller just started up.");

		int count = 0;
		int offCounter = 0;

		while (true) {
			try {
				a.temperatureUpdate();

				// Room Too Hot ---------------------------------- 
				if (a.getTemperature() >= a.getMaxTemp()) {
					log.add("[AC ON]", "Turning AC on. Temperature: " + a.getTemperature());
					a.switchAC();

					do {
						a.sleep(config.getSleepTime());
						a.temperatureUpdate();
						log.add("[AC ON]", "Temperature: " + a.getTemperature() + " Humidity: " + (int) a.getHumidity() + "%");
						count++;

						// Verify AC Off
						if (a.acStatus() == false) {
							a.switchAC();
							count++;
						}

						if (count >= 5) {
							log.alert("[ERROR] Can't Turn AC On", "We're having trouble turning the AC on.");
							count = 0;
						}
					} while (a.getTemperature() > a.getMinTemp());

					log.add("[AC OFF]", "Turning AC off.");
					a.switchAC();

					log.add("[INFO]", "Temperature: " + a.getTemperature() + " Humidity: " + (int) a.getHumidity() + "%");
					a.sleep(config.getSleepTime());
				}
				// -----------------------------------------------

				// Verify AC Off
				if (a.acStatus() == true) {
					a.switchAC();
					offCounter++;
				}

				if (offCounter >= 5) {
					log.alert("[ERROR] Can't Turn AC Off", "We're having trouble turning the AC off.");
					offCounter = 0;
				}

				log.add("[INFO]", "Temperature: " + a.getTemperature() + " Humidity: " + (int) a.getHumidity() + "%");
				a.sleep(config.getSleepTime());

			} catch (Exception e) {
				log.alert("AC Controller ERROR!",
						"An error occured while the AC Controller was running. \n\nDebug Information:\n----------------\n\n"
								+ e.getStackTrace());
			}
		}
	}

}
