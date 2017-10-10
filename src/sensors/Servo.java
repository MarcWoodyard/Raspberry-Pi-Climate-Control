package sensors;

import utils.Logger;

public class Servo {

	private boolean servoStatus = false;
	private Runtime runTime = Runtime.getRuntime();
	private Logger log = new Logger();

	/**
	* Creates a Servo object.
	*/
	public Servo() {
		//Setup Servo
		try {
			this.runTime.exec("gpio mode 1 pwm");
			this.runTime.exec("gpio pwm-ms");
			this.runTime.exec("gpio pwmc 192");
			this.runTime.exec("gpio pwmr 2000");
			this.runTime.exec("gpio pwm 1 60");
		} catch(Exception e) {
			this.log.add("[ERROR]", "Exception occured: " + e.getMessage());
			this.log.alert("AC Controller Servo Error", "The AC controller encountered a servo motor error.");
		}
	}

	/**
	* Turns AC on/off.
	*/
	public void switchAC() {
		this.moveServo(55, 600, 60);
	}

	/**
	* Sends info to servo for it to move.
	*
	* @param - String - Servo info for move 1.
	* @param - int - How long it takes for the servo to move in miliseconds.
	* @param - String - Servo info for move 1.
	*/
	public void moveServo(int move1, int sleep, int move2) {

		//Stops the servo motor from going out of bounds.
		if(move1 > 70 || move2 > 70) {
			this.log.alert("AC Controller Servo Error", "The servo was blocked from moving .");
			this.log.add("[ERROR]", "Exception occured: ");
			return;
		}

		try {
			this.runTime.exec("gpio pwm 1 " + move1); //Turn AC on/off.
			Thread.sleep(sleep);
			this.runTime.exec("gpio pwm 1 " + move2); //Center Servo
		} catch(Exception e) {
			this.log.add("[ERROR]", "Exception occured: " + e.getMessage() + "\nMove 1: " + move1 + " Sleep: " + sleep + " Move 2: " + move2);
			this.log.alert("AC Controller Servo Error", "The AC controller encountered a servo motor error.");
		}

		if (this.servoStatus == true)
			this.servoStatus = false;
		else if (this.servoStatus == false)
			this.servoStatus = true;
	}

	/**
	* Returns current servo status.
	*
	* @return - boolean - Return current servo status.
	*/
	public boolean getServoStatus() {
		return this.servoStatus;
	}
}
