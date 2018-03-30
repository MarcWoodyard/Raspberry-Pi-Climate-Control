package sensors;

import utils.Logger;
import utils.ConfigImporter;

public class Servo {

	// Servo Configuration
	ConfigImporter config = new ConfigImporter();

	// Servo Settings
	private int servoMax = config.getSwitchButton();
	private int servoResting = config.getRestingPosition();
	private int servoTimeout = config.getServoSleep();

	// Servo
	private boolean servoStatus = false;
	private Runtime runTime = Runtime.getRuntime();

	// Logging
	private Logger log = new Logger();

	/**
	* Creates a Servo object.
	*/
	public Servo() {
		this.setupServo();
	}

	/**
	 * Sets up & aligns the servo motor.
	 */
	private void setupServo() {
		try {
			this.runTime.exec("gpio mode 1 pwm");
			this.runTime.exec("gpio pwm-ms");
			this.runTime.exec("gpio pwmc " + this.servoResting + 20); // Servo Alignment???
			this.runTime.exec("gpio pwmr 2000");
			this.runTime.exec("gpio pwm 1 " + this.servoResting);
		} catch (Exception e) {
			this.log.alert("[ERROR] AC Controller Servo",
					"The AC controller encountered a servo motor error. " + e.getMessage());
		}
	}

	/**
	* Turns AC on/off.
	*/
	public void switchAC() {
		try {
			this.runTime.exec("gpio pwm 1 " + this.servoMax); // Turn AC On/Off.
			Thread.sleep(this.servoTimeout); // Wait for Servo to Move.
			this.runTime.exec("gpio pwm 1 " + this.servoResting); // Center Servo
		} catch (Exception e) {
			this.log.add("[ERROR]", "Exception occured: " + e.getMessage());
			this.log.alert("AC Controller Servo Error",
					"The AC controller encountered a servo motor error.\n\nException occured:\n" + e.getMessage());
		}

		// Update Servo State
		this.servoStatus = !this.servoStatus;
	}

	/**
	* Returns current servo status.
	* @return boolean - Return current servo status.
	*/
	public boolean getServoStatus() {
		return this.servoStatus;
	}
}
