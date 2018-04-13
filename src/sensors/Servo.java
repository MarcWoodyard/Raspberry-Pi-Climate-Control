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

	}

	/**
	* Turns AC on/off.
	*/
	public void switchAC() {
    try {
			 runTime.exec("gpio mode 100 pwm");
			 runTime.exec("gpio pwm-ms");
			 runTime.exec("gpio pwmc 192");
			 runTime.exec("gpio pwmr 2000");
			 runTime.exec("gpio pwm 1 " + this.servoResting);
			 Thread.sleep(this.servoTimeout);
			 runTime.exec("gpio pwm 1 " + this.servoMax);
			 Thread.sleep(this.servoTimeout);
			 runTime.exec("gpio pwm 1 " + this.servoResting);
    } catch(Exception e) {
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
