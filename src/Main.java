public class Main {
	public static void main(String[] args) {

		Controller a = new Controller();
		a.alert("AC Controller Starting Up", "Your Raspberry Pi AC controller just started up.");

		do {
			a.temperatureUpdate();

			if (a.getTemperature() >= a.getMaxTemp()) {
				a.log("[AC ON]", "Turning AC on. Temperature: " + a.getTemperature());
				a.switchAC(); //Turn AC on.

				do {
					a.sleep(1);
					a.temperatureUpdate();
					a.log("[AC ON]", "Temperature: " + a.getTemperature() + " Humidity: " + a.getHumidity());
					a.tempWatch();

				} while (a.getTemperature() > a.getMinTemp());

				a.log("[AC OFF]", "Turning AC off.");
				a.switchAC(); //Turn AC off.

				a.sleep(1);
				do {
					a.temperatureUpdate();
					a.log("[INFO]","Temperature: " + a.getTemperature() + " Humidity: " + a.getHumidity());
					a.tempWatch();
					a.sleep(1);
				} while(a.getTemperature() < a.getMinTemp());

			}

			a.log("[INFO]","Temperature: " + a.getTemperature() + " Humidity: " + a.getHumidity());
			a.sleep(1);
		} while (true);
	}
}
