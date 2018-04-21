package sensors;

import utils.Logger;
import utils.ConfigImporter;

public class Servo {

	// Servo Configuration
	private ConfigImporter config = new ConfigImporter();

	// Servo Settings
	private int servoMax = config.getSwitchButton();
	private int servoResting = config.getRestingPosition();
	private int servoTimeout = config.getServoSleep();
	private boolean servoStatus = false;
	private Runtime runTime;

	// Logging
	private Logger log = new Logger();

	public Servo() {
		try {
			runTime = Runtime.getRuntime();
			runTime.exec("gpio mode 1 pwm");
			runTime.exec("gpio pwm-ms");
			runTime.exec("gpio pwmc 192");
			runTime.exec("gpio pwmr 2000");
			Thread.sleep(this.servoTimeout);
		} catch(Exception e) {
					this.log.alert("AC Controller Servo Error",
							"The AC controller encountered a servo motor error.\n\nException occured:\n" + e.getMessage());
	    }
	}

	/**
	* Turns AC on/off.
	*/
	public void switchAC() {
	    try {
	    	Thread.sleep(this.servoTimeout);
			runTime.exec("gpio pwm 1 " + this.servoResting); // Center
			Thread.sleep(this.servoTimeout);
			runTime.exec("gpio pwm 1 " + this.servoMax); // Turn on/off
			Thread.sleep(this.servoTimeout);
			runTime.exec("gpio pwm 1 " + this.servoResting); // Center
			Thread.sleep(this.servoTimeout);
	    } catch(Exception e) {
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
