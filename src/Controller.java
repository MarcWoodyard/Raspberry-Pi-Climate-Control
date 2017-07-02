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
	private int tooHot = 0;

	//Communication
	private CommunicationModule coms;
	private int coldEmail = 0;

	//Logging
	private Logger log;

	/**
	* Creates a Controller object.
	* @param - double - Max Temperature.
	* @param - double - Min Temperature.
	* @return - None
	*/
	public Controller(double maxTemp, double minTemp) {

		//Setup Temperature Sensor
		this.maxTemperature = maxTemp;
		this.minTemperature = minTemp;

		//Set up Communications
		this.coms = new CommunicationModule();

		//Set up Logger
		this.log = new Logger();

		//Setup Servo Motor
		System.out.println("[Launcher] Calibrating servo motor.");

		try {
			this.log("[INFO] Starting Controller module.");
			this.runTime.exec("gpio mode 1 pwm");
			this.runTime.exec("gpio pwm-ms");
			this.runTime.exec("gpio pwmc 192");
			this.runTime.exec("gpio pwmr 2000");
			this.runTime.exec("gpio pwm 1 130");
		} catch(Exception e) {
			this.log("[ERROR] Exception occured: " + e.getMessage());
			this.coms.sendEmail("AC Controller - Error Detected", "The AC controller encountered a  servo motor error.", this.coms.getToEmail());
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
		if(this.curTemp >= this.maxTemperature + 1.0) {
			this.tooHot++;

			if(this.tooHot == 2) {
				this.log("[ERROR] Temperature too hot. Correcting...");
				this.moveServo("gpio pwm 1 46", 500, "gpio pwm 1 130");
				this.coms.sendEmail("Temperature Above Max Value!", "The temperature in your room has exceded the limit you set.", this.coms.getToEmail());
				this.log("[NOTIF] Notification Sent.");
				this.tooHot = 0;
			}
		}

		else if(this.curTemp <= this.minTemperature - 1.0) {
			if(this.coldEmail == 0) {
				this.coms.sendEmail("Temperature Below Min Value!", "The temperature in your room is below the limit you set", this.coms.getToEmail());
				this.log("[NOTIF] Notification Sent.");
				this.coldEmail++;
			}
			else if(this.coldEmail == 10)
				this.coldEmail = 0;

				this.coldEmail++;
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

	/**
	* Returns current room humidity.
	* @param - String - Subject of email.
	* @param - String - Body of email.
	* @return - None
	*/
	public void sendEmail(String subject, String body) {
		this.coms.sendEmail(subject, body, this.coms.getToEmail());
	}

	/**
	* Shutdown AC Controller.
	* @param - None
	* @return - None
	*/
	public void shutdown() {
		if(this.servoStatus == true)
			switchAC();
		this.log("[SHUTDOWN] Shutting down...");
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
			this.log("[ERROR] Exception occured: " + e.getMessage() + "\nMinutes: " + minutes);
			this.coms.sendEmail("AC Controller - Sleep Method Error", "The sleep method in the Controller class encountered an error.", this.coms.getToEmail());
		}
	}

	/**
	* Sends info to servo for it to move.
	* @param - String - Servo info for move 1.
	* @param - int - How long it takes for the servo to move in miliseconds.
	* @param - String - Servo info for move 1.
	* @return - None
	*/
	public void moveServo(String move1, int sleep, String move2) {
		try {
			this.runTime.exec(move1); //Turn AC on/off.
			Thread.sleep(sleep);
			this.runTime.exec(move2); //Center Servo
		} catch(Exception e) {
			this.log("[ERROR] Exception occured: " + e.getMessage() + "\nMove 1: " + move1 + " Sleep: " + sleep + " Move 2: " + move2);
			this.coms.sendEmail("AC Controller - Servo Motor Error", "The moveServo method in the Controller class encountered an error.", this.coms.getToEmail());
		}
	}

	/**
	* Send a string to the logger to be logged in a text file.
	* @param - String - Data to be logged.
	* @return - None
	*/
	public void log(String info) {
		this.log.add(info);
	}

}
