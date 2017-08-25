public class Servo {

	private boolean servoStatus = false;
	private Runtime runTime = Runtime.getRuntime();
	private Logger log = new Logger();

	/**
	* Creates a Servo object.
	* @param - None
	* @return - None
	*/
	public Servo() {
		//Setup Servo
		try {
			this.runTime.exec("gpio mode 1 pwm");
			this.runTime.exec("gpio pwm-ms");
			this.runTime.exec("gpio pwmc 192");
			this.runTime.exec("gpio pwmr 2000");
			this.runTime.exec("gpio pwm 1 130");
		} catch(Exception e) {
			this.log.add("[ERROR]", "Exception occured: " + e.getMessage());
			this.log.alert("AC Controller Servo Error", "The AC controller encountered a servo motor error.");
		}
	}

	/**
	* Turns AC on/off.
	* @param - None
	* @return - None
	*/
	public void switchAC() {
		this.moveServo(48, 500, 130);

		if (this.servoStatus == true)
			this.servoStatus = false;
		else if (this.servoStatus == false)
			this.servoStatus = true;
	}

	/**
	* Sends info to servo for it to move.
	* @param - String - Servo info for move 1.
	* @param - int - How long it takes for the servo to move in miliseconds.
	* @param - String - Servo info for move 1.
	* @return - None
	*/
	public void moveServo(int move1, int sleep, int move2) {

		//Stops the servo motor from breaking the .
		if(move1 > 130 || move2 > 130) {
			this.log.alert("AC Controller Servo Error", "The servo was blocked from moving .");
			this.log.add("[ERROR]", "Exception occured: ");
			return;
		}

		try {
			this.runTime.exec("gpio pwm 1 130");
			this.runTime.exec("gpio pwm 1 " + move1); //Turn AC on/off.
			Thread.sleep(sleep);
			this.runTime.exec("gpio pwm 1 " + move2); //Center Servo
		} catch(Exception e) {
			this.log.add("[ERROR]", "Exception occured: " + e.getMessage() + "\nMove 1: " + move1 + " Sleep: " + sleep + " Move 2: " + move2);
			this.log.alert("AC Controller Servo Error", "The AC controller encountered a servo motor error.");
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
}
