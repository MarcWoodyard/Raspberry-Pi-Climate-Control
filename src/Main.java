import utils.ConfigImporter;
import utils.Controller;
import utils.Logger;

public class Main {

	private static Logger log = new Logger();
	private static ConfigImporter config = new ConfigImporter();
	private static Controller a = new Controller();

	private static int onCounter = 0;
	private static int offCounter = 0;

	public static void main(String[] args) {
		log.alert("AC Controller Starting Up", "Your Raspberry Pi AC controller just started up.");
		while (true) {
			verifyACOff();

			try {
				a.temperatureUpdate();

				if (a.getTemperature() >= a.getMaxTemp()) { // Room too hot.
					turnACOn();
					int rounds = 0;

					do {
						verifyACOn();
						logACOn();
						rounds++;

						if (rounds == 6)
							log.alert("Room Temperature Not Changing", "The room is not getting colder.");
						else if (rounds > 6)
							rounds = 0;
					} while (a.getTemperature() > a.getMinTemp()); // Room too cold.
					rounds = 0;

					log.add("[AC OFF]", "Turning AC off. Temperature: " + a.getTemperature());
					a.switchAC();
					verifyACOff();
					logACOff();
				}

				logACOff();
			} catch (Exception e) {
				log.alert("AC Controller ERROR!",
						"An error occured while the AC Controller was running. \n\nDebug Information:\n----------------\n\n"
								+ e.getStackTrace());
			}
		}
	}

	/**
	 * Logs & turns the AC on.
	 */
	public static void turnACOn() {
		log.add("[AC ON]", "Turning AC on. Temperature: " + a.getTemperature());
		a.switchAC();
	}

	/**
	 * Verifies that the AC is on.
	 */
	public static void verifyACOn() {
		boolean sendResolved = false;
		while (a.acStatus() == false) {
			a.switchAC();
			onCounter++;
			a.sleep(10000);

			if (onCounter > 2) {
				log.alert("[ERROR] Can't Turn AC On", "We're having trouble turning the AC on.");
				a.newServo();
				onCounter = 0;
				sendResolved = true;
			}
		}

		onCounter = 0;
		if (sendResolved == true)
			onOffResolved();
	}

	/**
	 * Verifies that the AC is off.
	 */
	public static void verifyACOff() {
		boolean sendResolved = false;
		while (a.acStatus() == true) {
			a.switchAC();
			offCounter++;
			a.sleep(10000);

			if (offCounter > 2) {
				log.alert("[ERROR] Can't Turn AC Off", "We're having trouble turning the AC off.");
				a.newServo();
				offCounter = 0;
				sendResolved = true;
			}
		}

		offCounter = 0;
		if (sendResolved == true)
			onOffResolved();
	}

	/**
	 * Logs the AC data when it is turned off.
	 */
	public static void logACOff() {
		log.add("[INFO]", "Temperature: " + a.getTemperature() + " Humidity: " + (int) a.getHumidity() + "%");
		a.sleep(config.getSleepTime());
	}

	/**
	 * Logs the AC data when it is turned off.
	 */
	public static void logACOn() {
		a.sleep(config.getSleepTime() / 2);
		a.temperatureUpdate();
		log.add("[AC ON]", "Temperature: " + a.getTemperature() + " Humidity: " + (int) a.getHumidity() + "%");
	}

	/**
	 * Sends an email alert to notifiy that AC issues have been resolved.
	 */
	public static void onOffResolved() {
		log.alert("[RESOLVED] AC On/Off Resolved",
				"We've resolved the AC on/off issue. Please" + "check the log for more details.");
	}
}