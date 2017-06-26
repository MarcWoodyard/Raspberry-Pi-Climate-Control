import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class ControllerController {

	private boolean servoStatus = false;
	private Runtime runTime = Runtime.getRuntime();
	private DHT11 tempSensor = new DHT11();
	private double curTemp;
	private double curHumidity;
	private double maxTemperature;
	private double minTemperature;
	private int tooHot = 0;
	private int tooCold = 0;
	private DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a yyyy/MM/dd");

	/**
	* Creates a Controller object.
	* @param double - Max Temperature.
	* @param double - Min Temperature.
	* @return None
	*/
	public Controller(double maxTemp, double minTemp) {
		System.out.println("Running V 6.25.17---5:10pm");

		try {
			System.out.println("[INFO] [" + this.dateFormat.format(new Date()) + "] Starting Controller module.");
			this.runTime.exec("gpio mode 1 pwm");
			this.runTime.exec("gpio pwm-ms");
			this.runTime.exec("gpio pwmc 192");
			this.runTime.exec("gpio pwmr 2000");
			this.runTime.exec("gpio pwm 1 130");
		} catch(Exception e) {
			System.out.println("[ERROR] [" + this.dateFormat.format(new Date()) + "] Exception occured: " + e.getMessage());
		}

		this.maxTemperature = maxTemp;
		this.minTemperature = minTemp;
	}

	/**
	* Turn's AC on/off.
	* @param None
	* @return None
	*/
	public void switchAC() {
		this.moveServo("gpio pwm 1 47", 500, "gpio pwm 1 130");

		if (this.servoStatus == true)
			this.servoStatus = false;
		else if (this.servoStatus == false)
			this.servoStatus = true;
	}

	/**
	* Updates room temperature and humidity values.
	* @param None
	* @return None
	*/
	public void temperatureUpdate() {
		this.tempSensor.updateTemperature(7);
		this.curTemp = this.tempSensor.getTemperature();
		this.curHumidity = this.tempSensor.gethumidity();
	}

	/**
	* Ensures room doesn't get too hot or cold.
	* @param None
	* @return None
	*/
	public void tempCheck() {
		//Room temperature is too hot.
		if(this.curTemp >= this.maxTemperature + 2.0) {
			this.tooHot++;

			if(this.tooHot == 2) {
				System.out.println("[ERROR] [" + this.dateFormat.format(new Date()) + "] Temperature too hot. Correcting...");
				this.moveServo("gpio pwm 1 47", 500, "gpio pwm 1 130");
				this.tooHot = 0;
			}

		}

		//Room temperature is too cold.
		if(this.curTemp <= this.minTemperature - 2.0) {
			this.tooCold++;

			if(this.tooCold == 2) {
				System.out.println("[ERROR] [" + this.dateFormat.format(new Date()) + "] Room temperature too cold. Correcting...");
				this.moveServo("gpio pwm 1 47", 500, "gpio pwm 1 130");
				this.tooCold = 0;
			}

		}
	}

	/**
	* Returns current servo status.
	* @param None
	* @return boolean - Return current servo status.
	*/
	public boolean getServoStatus() {
		return this.servoStatus;
	}

	/**
	* Returns current room temperature.
	* @param None
	* @return double - Current room temperature.
	*/
	public double getTemperature() {
		return this.curTemp;
	}

	/**
	* Returns current room humidity.
	* @param None
	* @return double - Current room humidity.
	*/
	public double getHumidity() {
		return this.curHumidity;
	}

	/**
	* Shutdown AC Controller.
	* @param None
	* @return None
	*/
	public void shutdown() {
		if(this.servoStatus == true)
			switchAC();
		System.out.println("[SHUTDOWN] Shutting down...");
		System.exit(0);
	}

	/**
	* Current thread sleeps for a set period of time.
	* @param int - Minutes that current thread should sleep for.
	* @return None
	*/
	public void sleep(int minutes) {
		try {
			Thread.sleep(minutes * 60 * 1000); //Minutes to sleep * seconds to a minute * miliseconds to a second.
		} catch(Exception e) {
			System.out.println("[ERROR] [" + dateFormat.format(new Date()) + "] Exception occured: " + e.getMessage());
		}
	}

	/**
	* Sends info to servo for it to move.
	* @param String - Servo info for move 1.
	* @param int - How long it takes for the servo to move in miliseconds.
	* @param String - Servo info for move 1.
	* @return None
	*/
	public void moveServo(String move1, int sleep, String move2) {
		try {
			this.runTime.exec(move1); //Turn AC on/off.
			Thread.sleep(sleep);
			this.runTime.exec(move2); //Center Servo
		} catch(Exception e) {
			System.out.println("[ERROR] [" + this.dateFormat.format(new Date()) + "] Exception occured: " + e.getMessage());
		}
	}

}
