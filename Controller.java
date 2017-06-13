public class Controller {

	private boolean servoStatus = false;
	private Runtime runTime = Runtime.getRuntime();
	private DHT11 tempSensor = new DHT11();
	private double temp;
	private double humidity;

	public Controller() {
		try {
			System.out.println("[INFO] Starting controller module.");
			this.runTime.exec("gpio mode 1 pwm");
			this.runTime.exec("gpio pwm-ms");
			this.runTime.exec("gpio pwmc 192");
			this.runTime.exec("gpio pwmr 2000");
			this.runTime.exec("gpio pwm 1 130");
		} catch(Exception e) {
			System.out.println("Exception occured: " + e.getMessage());
		}
		System.out.println("[INFO] Updating temperture data.");
		temperatureUpdate();
		System.out.println("[DONE] Initialization Complete.");
	}

	public void temperatureUpdate() {
		double tmpT;
		double tmpH;
		do {
			this.tempSensor.updateTemperature(7);
			tmpT = this.tempSensor.getTemperature();
			tmpH = this.tempSensor.gethumidity();
		} while (tmpT == Double.MAX_VALUE || tmpH == Double.MAX_VALUE);

		this.temp = tmpT;
		this.humidity = tmpH;
	}

	public void switchAC() {
		try {
			this.runTime.exec("gpio pwm 1 47"); //Turn AC on.
			Thread.sleep(500);
			this.runTime.exec("gpio pwm 1 130"); //Center Servo

			if (this.servoStatus == true) {
				System.out.println("[INFO] Controling AC. AC off.");
				this.servoStatus = false;
			}

			else if (this.servoStatus == false) {
				System.out.println("[INFO] Controling AC. AC on.");
				this.servoStatus = true;
			}

		} catch(Exception e) {
			System.out.println("Exception occured: " + e.getMessage());
		}

	}

	public boolean getServoStatus() {
		return this.servoStatus;
	}

	public double getTemperature() {
		return this.temp;
	}

	public double getHumidity() {
		return this.humidity;
	}

	public static void main(String[] args) {
		Controller a = new Controller();
		System.out.println("[INFO] Monitoring...");
		do {
			a.temperatureUpdate();
			System.out.println("[INFO] Temperature: " + a.getTemperature() + " Humidity: " + a.getHumidity());

			if (a.getTemperature() > 80.0) {
				a.switchAC(); //Turn AC on.

				try {
					Thread.sleep(10 * // minutes to sleep
					60 * // seconds to a minute
					1000); // milliseconds to a second
				} catch(Exception e) {
					System.out.println("Exception occured: " + e.getMessage());
				}

				a.switchAC(); //Turn AC off.
			}

			try {
				Thread.sleep(60000);
			} catch(Exception e) {
				System.out.println("Exception occured: " + e.getMessage());
			}
		} while ( 1 == 1 );

	}

}